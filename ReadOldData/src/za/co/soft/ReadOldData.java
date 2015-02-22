package za.co.soft;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author mark
 *
 */
public class ReadOldData
	{

	public enum dataFormatType
		{
		SINGLE, HREPEAT, VREPEAT
		};

	public static void main(String[] args)
		{
		String converterfilename = "ConverterMap-2014-Lattice.xlsx";
		String sourcefilename = "Mayo Estates 14-15 VC MP 21566 REP-ST32512 Inspection Area 3 D1.xls";

		ArrayList<Converter> cList;

		cList = getConverterData(converterfilename);

		// showConverterList(cList); // Just a utility function to check out
		// what we are getting.

		processInspectionData(sourcefilename, cList);

		}

	/**
	 * @param cList
	 * 
	 *            Just a short method to print out the Converter list to the
	 *            console
	 */
	private static void showConverterList(ArrayList<Converter> cList)
		{
		Converter c;
		for (int i = 0; i < cList.size(); i++)
			{
			c = getConverter(cList, i);
			showConverter(c);
			}
		System.out.println("================================================================================");
		}

	/**
	 * @param c
	 */
	private static void showConverter(Converter c)
		{
		System.out.println(c.getSSFieldName() + " : " + c.getRow() + " : " + c.getColumn() + " : " + c.getSSFieldType());
		}

	private static ArrayList<Converter> getConverterData(String converterfilename)
		{
		XSSFSheet sheet;
		ArrayList<Converter> cList = null;

		String converterFullFilename = PATH + converterfilename;
		sheet = openConverterData(converterFullFilename);

		cList = fillConverterList(sheet);

		return cList;
		}

	private static ArrayList<Converter> fillConverterList(XSSFSheet sheet)
		{
		ArrayList<Converter> cList = new ArrayList<Converter>();

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();

		if (rowIterator.hasNext())
			{ // kill header row
			rowIterator.next();
			}

		while (rowIterator.hasNext())
			{

			Row row = rowIterator.next();

			int readRow = getCellValueInt(row, 0);
			int readColumn = getCellValueInt(row, 1);

			String readSSFieldName = getCellValueStr(row, 2);
			String readSSFieldType = getCellValueStr(row, 3);
			String readProcessing = getCellValueStr(row, 4);
			Integer readSort = getCellValueInt(row, 5);
			String readDTable = getCellValueStr(row, 6);
			String readDField = getCellValueStr(row, 7);
			String readDType = getCellValueStr(row, 8);
			String readLUT = getCellValueStr(row, 9);
			String readLUTField = getCellValueStr(row, 10);
			String readLUTKey = getCellValueStr(row, 11);

			Converter c = new Converter(readRow, readColumn, readSSFieldName, readSSFieldType, readProcessing, readSort,
					readDTable, readDField, readDType, readLUT, readLUTField, readLUTKey);

			cList.add(c);

			}

		return cList;
		}

	private static XSSFSheet openConverterData(String converterFullFilename)
		{
		XSSFSheet sheet = null;

		try
			{

			FileInputStream file = new FileInputStream(new File(converterFullFilename));

			XSSFWorkbook workbook = new XSSFWorkbook(file);
			sheet = workbook.getSheetAt(0);

			}
		catch (Exception e)
			{
			e.printStackTrace();
			}

		return sheet;
		}

	/*
	 * This is the main routine that processes the saved data. It uses the
	 * Converter list in cList to interpret what it should do with each field
	 * and where it should find the data in the original spreadsheet data files
	 */
	private static void processInspectionData(String sourcefilename, ArrayList<Converter> cList)
		{
		Connection con = null;

		String sourceFullFilename = DATAPATH + sourcefilename;
		HSSFSheet sheet = openInspectionSpreadsheet(sourceFullFilename);

		con = getConnection();

		if (con != null)
			storeDataInDB(con, cList, sheet);
		else
			System.out.println("Unable to create a connection - check if database is running!");

		closeConnection(con);

		}

	private static void storeDataInDB(Connection con, ArrayList<Converter> cList, HSSFSheet sheet)
		{
		int table_id = 1;
		int new_index;
		int index = 0;
		int max_table_id;
		Converter c;

		c = getConverter(cList, cList.size() - 2);
		max_table_id = c.Order;

		while (table_id < max_table_id + 1)
			{
			new_index = storeDataInTable(table_id, con, cList, index, sheet);
			index = new_index;
			table_id++;
			}
		}

	private static int storeDataInTable(int table_id, Connection con, ArrayList<Converter> cList, int index, HSSFSheet sheet)
		{
		int new_index;
		Integer number_of_values = 0;
		String sql = "";
		ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();

		sql = constructSQLInsertQuery(table_id, cList, index);

		// System.out.println(sql);

		number_of_values = getNumberOfTableItems(table_id, cList, index);

		getInspectionData(cList, index, sheet, number_of_values, table);

		try
			{
			PreparedStatement statement = con.prepareStatement(sql);

			int list_depth = table.get(0).size();

			for (int k = 1; k < list_depth + 1; k++)
				{
				for (int j = 1; j < number_of_values + 1; j++)
					{
					// ArrayList<String> arr = table.get(k - 1);
					String rawVal;
					Converter c;

					rawVal = table.get(j - 1).get(k - 1);

					rawVal = rawVal.trim();

					c = getConverter(cList, index + j - 1);

					showConverter(c);

					switch (c.DesinationType.toLowerCase())
						{
						case "lookup":
							int foreignKey = 0;
							if (!rawVal.isEmpty())
								foreignKey = getLUTIndex(con, c, rawVal);
							statement.setInt(j, foreignKey);
							break;
						case "bool":
							boolean bool = false;
							if (!rawVal.isEmpty())
								bool = Boolean.parseBoolean(rawVal);
							statement.setBoolean(j, bool);
							break;
						case "date":
							DateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
							if (rawVal.isEmpty())
								rawVal = "01-01-2000";
							java.util.Date theDate = simpleDateFormat.parse(rawVal);
							java.sql.Date sqlDate = new java.sql.Date(theDate.getTime());
							statement.setDate(j, sqlDate);
							break;
						case "varchar":
							statement.setString(j, rawVal);
							break;
						case "int":
							int intVal = 0;
							if (!rawVal.isEmpty())
								intVal = (int) Float.parseFloat(rawVal);
							statement.setInt(j, intVal);
							break;
						case "float":
							float fltVal = 0;
							if (!rawVal.isEmpty())
								fltVal = Float.parseFloat(rawVal);
							statement.setFloat(j, fltVal);
							break;
						case "checkbox":
							break;
						default:
						}

					}

				System.out.println(statement.toString());

				// int rowsInserted = statement.executeUpdate();
				// if (rowsInserted > 0)
				// {
				// System.out.println("A new record was inserted successfully!");
				// }
				}
			}
		catch (SQLException e)
			{
			e.printStackTrace();
			}
		catch (ParseException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		new_index = index + number_of_values;

		return new_index;
		}

	private static int getLUTIndex(Connection con, Converter c, String rawVal)
		{
		String sql;
		int retVal = 0;
		Statement stmt = null;

		sql = "SELECT " + " * FROM " + c.LUT + " WHERE " + c.LUTField + " = '" + rawVal + "'";
		System.out.println("SQL to find LUT ID : " + sql);

		try
			{
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				{
				int lutID = rs.getInt(c.LUTKey);
				System.out.println("LUT ID : " + lutID);
				retVal = lutID;
				}
			else
				{
				System.out.println("We need to insert something into the LUT");
				}

			// PreparedStatement statement =
			// con.prepareStatement(sql);

			// System.out.println(statement.toString());

			// int rowsFound = statement.executeUpdate();
			// if (rowsFound > 0)
			// {
			// System.out.println("A record was found and matched successfully!");
			// }

			}
		catch (SQLException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		return retVal;
		}

	/**
	 * @param cList
	 * @param index
	 * @param sheet
	 * @param number_of_values
	 * @param table
	 */
	private static void getInspectionData(ArrayList<Converter> cList, int index, HSSFSheet sheet, Integer number_of_values,
			ArrayList<ArrayList<String>> table)
		{
		Converter c;
		ArrayList<String> vals;
		c = getConverter(cList, index);
		dataFormatType dataFormat = getDataFormat(c);

		switch (dataFormat)
			{
			case SINGLE:
				System.out.println("Found a Single for " + c.DestinationTable);
				for (int i = 0; i < number_of_values; i++)
					{
					vals = getSingleValueFromSheet(sheet, cList, index + i);
					table.add(vals);
					}
				break;
			case HREPEAT: // Horizontal repeat - column headings along the side
				int max_hdepth = 0;
				max_hdepth = getHMultipleValueMaxDepth(sheet, cList, index);
				for (int i = 0; i < number_of_values; i++)
					{
					vals = getHMultipleValueFromSheet(sheet, cList, index + i, max_hdepth);
					table.add(vals);
					}
				System.out.println("Found a Horizontal Repeat for " + c.DestinationTable);
				break;
			case VREPEAT: // Vertical repeat - column headings at the top
				int max_vdepth = 0;
				max_vdepth = getVMultipleValueMaxDepth(sheet, cList, index);
				for (int i = 0; i < number_of_values; i++)
					{
					vals = getVMultipleValueFromSheet(sheet, cList, index + i, max_vdepth);
					table.add(vals);
					}
				System.out.println("Found a Vertical Repeat for " + c.DestinationTable);
				break;
			default:
				System.out.println("Did not find a match for " + c.DestinationTable);
				break;
			}
		}

	/**
	 * @param cList
	 * @param index
	 * @return Converter
	 * 
	 *         This will return a converter object from the cList ArrayList for
	 *         a given index
	 * 
	 */
	private static Converter getConverter(ArrayList<Converter> cList, int index)
		{
		Converter c;
		c = cList.get(index);
		return c;
		}

	private static dataFormatType getDataFormat(Converter c)
		{
		dataFormatType dataFormat = dataFormatType.SINGLE;

		if (c.Processing.equals("Single"))
			{
			dataFormat = dataFormatType.SINGLE;
			}
		else
			if (c.Processing.equals("HRepeatStart"))
				{
				dataFormat = dataFormatType.HREPEAT;
				}
			else
				if (c.Processing.equals("VRepeatStart"))
					{
					dataFormat = dataFormatType.VREPEAT;
					}

		return dataFormat;
		}

	/**
	 * @param table_id
	 * @param cList
	 * @param index
	 * @param sqlFieldList
	 * @return
	 */
	private static String constructSQLInsertQuery(int table_id, ArrayList<Converter> cList, int index)
		{
		Integer number_of_values;
		Converter c;
		String sqlPreamble = "";
		String sqlValueList = "";
		String sqlFieldList = "";

		String sql;

		c = getConverter(cList, index);
		sqlPreamble = constructSQLInsertPreamble(c);

		sqlFieldList = constructSQLFieldList(table_id, cList, index);

		number_of_values = getNumberOfTableItems(table_id, cList, index);
		sqlValueList = constructSQLValueList(number_of_values);

		sql = sqlPreamble + sqlFieldList + sqlValueList;
		return sql;
		}

	/**
	 * @param table_id
	 * @param cList
	 * @param index
	 * @param sqlFieldList
	 * @return
	 */
	private static String constructSQLFieldList(int table_id, ArrayList<Converter> cList, int index)
		{
		Converter c;
		String sqlFieldList = "";
		int inc_index = index;

		c = getConverter(cList, inc_index);
		while (c.Order == table_id && inc_index < (cList.size()) - 1)
			{
			c = getConverter(cList, inc_index);
			sqlFieldList = sqlFieldList + c.getDestinationField() + ", ";
			inc_index++;
			c = getConverter(cList, inc_index);
			}

		sqlFieldList = sqlFieldList.substring(0, sqlFieldList.length() - 2);
		return sqlFieldList;
		}

	/**
	 * @param table_id
	 * @param cList
	 * @param index
	 * @param number_of_values
	 * @return
	 */
	private static Integer getNumberOfTableItems(int table_id, ArrayList<Converter> cList, int index)
		{
		Integer number_of_values = 0;
		Converter c;
		int inc_index = index;
		c = getConverter(cList, inc_index);

		while (c.Order == table_id && inc_index < (cList.size()) - 1)
			{
			inc_index++;
			c = getConverter(cList, inc_index);
			number_of_values++;
			}

		return number_of_values;
		}

	/**
	 * @param c
	 * @return
	 */
	private static String constructSQLInsertPreamble(Converter c)
		{
		String sqlPreamble;
		sqlPreamble = "INSERT INTO " + c.getDestinationTable() + " (";
		return sqlPreamble;
		}

	/**
	 * @param number_of_values
	 * @return
	 */
	private static String constructSQLValueList(int number_of_values)
		{
		String sqlValueList;
		sqlValueList = ") VALUES (";

		for (int i = 0; i < number_of_values; i++)
			{
			sqlValueList = sqlValueList + "?,";
			}
		sqlValueList = sqlValueList.substring(0, sqlValueList.length() - 1);
		sqlValueList = sqlValueList + ")";
		return sqlValueList;
		}

	private static ArrayList<String> getSingleValueFromSheet(HSSFSheet sheet, ArrayList<Converter> cList, int index)
		{
		Converter c;
		String cell_val;
		ArrayList<String> vals_arr = new ArrayList<String>();

		c = getConverter(cList, index);

		cell_val = getInspectionCell(sheet, c.getRow(), c.getColumn(), c.getSSFieldType());

		vals_arr.add(cell_val);

		return vals_arr;
		}

	/*
	 * This function processes data that is in multiple rows. It returns an
	 * array of the data values in the column corresponding to the index (which
	 * refers to the converter) passed in to function
	 */
	private static ArrayList<String> getVMultipleValueFromSheet(HSSFSheet sheet, ArrayList<Converter> cList, int index,
			int max_depth)
		{
		Converter c;
		String cell_val;
		ArrayList<String> vals_arr = new ArrayList<String>();
		int data_row, data_column;

		c = getConverter(cList, index);
		data_row = c.getRow();
		data_column = c.getColumn();

		System.out.println("Max depth : " + max_depth);

		for (int i = 0; i < max_depth; i++)
			{
			cell_val = getInspectionCell(sheet, data_row, data_column, c.getSSFieldType());
			vals_arr.add(cell_val);
			data_column += 2;
			}

		return vals_arr;
		}

	/*
	 * This function processes data where the rows are stored in multiple rows.
	 * It returns the number of values that occur in the first column This is
	 * used as the maximum value when processing further columns.
	 */

	private static int getVMultipleValueMaxDepth(HSSFSheet sheet, ArrayList<Converter> cList, int index)
		{
		Converter c;
		String cell_val;
		int data_row, data_column;
		int max_depth = 0;

		c = getConverter(cList, index);
		data_row = c.getRow();
		data_column = c.getColumn();

		do
			{
			cell_val = getInspectionCell(sheet, data_row, data_column, c.getSSFieldType());
			max_depth++;
			data_column += 2;
			} while (!cell_val.trim().equals(""));

		return max_depth - 1;
		}

	/*
	 * This function processes data where the rows are stored in multiple
	 * columns. It returns an array of the data values in the row corresponding
	 * to the index (which refers to the converter) passed in to function
	 */
	private static ArrayList<String> getHMultipleValueFromSheet(HSSFSheet sheet, ArrayList<Converter> cList, int index,
			int max_depth)
		{
		Converter c;
		String cell_val;
		ArrayList<String> vals_arr = new ArrayList<String>();
		int data_row, data_column;

		c = getConverter(cList, index);
		data_row = c.getRow();
		data_column = c.getColumn();

		System.out.println("Max depth : " + max_depth);

		for (int i = 0; i < max_depth; i++)
			{
			cell_val = getInspectionCell(sheet, data_row, data_column, c.getSSFieldType());
			if (cell_val.trim().equals("-"))
				cell_val = "";
			vals_arr.add(cell_val);
			data_row++;
			}

		return vals_arr;
		}

	/*
	 * This function processes data where the rows are stored in multiple
	 * columns. It returns the number of values that occur in the first column
	 * This is used as the maximum value when processing further columns.
	 */

	private static int getHMultipleValueMaxDepth(HSSFSheet sheet, ArrayList<Converter> cList, int index)
		{
		Converter c;
		String cell_val;
		int data_row, data_column;
		int max_depth = 0;

		c = getConverter(cList, index);
		data_row = c.getRow();
		data_column = c.getColumn();

		do
			{
			cell_val = getInspectionCell(sheet, data_row, data_column, c.getSSFieldType());
			max_depth++;
			data_row++;
			} while (!cell_val.trim().equals(""));

		return max_depth - 1;
		}

	/**
	 * @param con
	 * @throws SQLException
	 */
	private static void showSQLVersion(Connection con) throws SQLException
		{
		Statement st;
		ResultSet rs;
		st = con.createStatement();
		rs = st.executeQuery("SELECT VERSION()");

		if (rs.next())
			{
			System.out.println(rs.getString(1));
			}
		}

	/**
	 * @param con
	 */
	private static void closeConnection(Connection con)
		{
		try
			{
			if (con != null)
				{
				con.close();
				}
			}
		catch (SQLException ex)
			{
			// Logger lgr =
			// Logger.getLogger(Version.class.getName());
			// lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

	private static Connection getConnection()
		{
		Connection con = null;

		String url = "jdbc:mysql://localhost:3306/rbi-towers";
		String user = "root";
		String password = "jelirkobiv";

		try
			{

			con = DriverManager.getConnection(url, user, password);
			if (con != null)
				{
				System.out.println("Connected");
				showSQLVersion(con);
				}
			}
		catch (SQLException ex)
			{
			// Logger lgr =
			// Logger.getLogger(Version.class.getName());
			// lgr.log(Level.SEVERE, ex.getMessage(), ex);
			}

		return con;
		}

	private static String getInspectionCell(HSSFSheet sheet, int row, int column, String ssFieldType)
		{
		String cell_value = "";

		Row cur_row = sheet.getRow(row - 1);

		Cell cell = cur_row.getCell(column - 1);

		if (cell != null)
			{
			switch (cell.getCellType())
				{
				case Cell.CELL_TYPE_STRING:
					cell_value = cell.getStringCellValue();
					// cell_value = cell_value.trim();
					// if (cell_value.equals("-"))
					// cell_value = " ";
					break;

				case Cell.CELL_TYPE_NUMERIC:
					cell_value = Double.toString(cell.getNumericCellValue());
					int int_cell_value = (int) cell.getNumericCellValue();
					if (int_cell_value == cell.getNumericCellValue())
						cell_value = Integer.toString(int_cell_value);
					break;
				}
			}

		// System.out.println("Cell value : " + cell_value);

		return cell_value;

		}

	private static HSSFSheet openInspectionSpreadsheet(String converterFullFilename)
		{
		HSSFSheet sheet = null;

		try
			{

			FileInputStream file = new FileInputStream(new File(converterFullFilename));

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			sheet = workbook.getSheetAt(0);

			}
		catch (Exception e)
			{
			e.printStackTrace();
			}

		return sheet;
		}

	private static String getCellValueStr(Row row, int index)
		{
		Cell cell;
		String readValue = "";

		cell = row.getCell(index);
		if (cell != null)
			{

			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				{
				readValue = cell.getStringCellValue();
				}
			}

		return readValue;
		}

	/**
	 * @param row
	 */
	private static int getCellValueInt(Row row, int index)
		{
		Cell cell;
		int readValue = 0;

		cell = row.getCell(index);
		if (cell != null)
			{
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
				{
				readValue = (int) cell.getNumericCellValue();
				}
			}
		return readValue;
		}

	public static final String	PATH		= "C:\\My Documents\\Special projects\\RBI Tech database project\\Development files\\History2dB_processor\\";
	public static final String	DATAPATH	= "C:\\My Documents\\Special projects\\RBI Tech database project\\Vodacom SA Excel Data\\MPUMALANGA\\2014\\Area 3\\";
	}
