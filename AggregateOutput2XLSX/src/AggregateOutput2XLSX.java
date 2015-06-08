
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author mark schormann
 *
 */

public class AggregateOutput2XLSX
	{

	public enum dataFormatType
		{
		SINGLE, HREPEAT, VREPEAT
		};

	public static void main(String[] args)
		{
		String filename;

		if (args.length >= 1)
			filename = args[0];
		else
			filename = "Lattice 2015_1_5";

		process_file(filename);

		System.out.println("Program successfully completed.");
		}

	private static void process_file(String filename)
		{
		ArrayList<String> hList = null;
		ArrayList<String> dList = null;
		ArrayList<RepeatBlock> table = new ArrayList<RepeatBlock>();
		String sourcefilename;

		sourcefilename = PATH + filename + EXTENSION;

		int number_of_rows = getNumberOfRows(sourcefilename);

		hList = getHeadings(sourcefilename);
		// showList(hList);

		for (int i = 1; i < number_of_rows + 1; i++)
			{
			dList = getRowData(sourcefilename, i, hList.size());
			// showList(dList);
			// showData(hList, dList);
			table = getSubFileData(filename, hList, dList);

			String outputfilename = getOutputFilenameString(hList, dList);
			outputData(hList, dList, table, outputfilename);

			System.out.println("Outputfilename:  " + outputfilename);
			}
		}

	//
	// This routine gets the data out of the sub-files containing the repeat
	// block data
	// It works out the location of each of the columns containing the phrase
	// "SET-OF" and passes this
	// through to the routine getRepeatBlock to get the data out of the file
	// indicated at that index
	//
	private static ArrayList<RepeatBlock> getSubFileData(String filename, ArrayList<String> hList, ArrayList<String> dList)
		{
		ArrayList<RepeatBlock> table = new ArrayList<RepeatBlock>();
		RepeatBlock b = new RepeatBlock();
		int bIndex = 0;

		for (int i = 0; i < hList.size(); i++)
			{
			String snippet = hList.get(i);
			if (snippet.contains(REPEAT_INDICATOR))
				{
				if (dList.get(i).length() > 2)
					{
					b = getRepeatBlock(filename, hList, dList, i, bIndex);
					table.add(b);
					Integer new_insert_index = table.size() - 1;
					dList.set(i, new_insert_index.toString()); // place index of
																// RepeatBlock
																// in table into
																// dList

					bIndex++; // block index - incremented for each repeat block
								// added

					System.out.println(hList.get(i));
					}
				}
			}

		return table;
		}

	//
	// This routine uses the UUID from the main file to locate the appropriate
	// records in the subfiles
	//
	private static RepeatBlock getRepeatBlock(String filename, ArrayList<String> hList, ArrayList<String> dList, int index,
			int bIndex)
		{
		RepeatBlock b = new RepeatBlock();
		String subfilename = getSubFilename(filename, dList, index);
		String uuid = getUUID(dList, index);

		System.out.println(subfilename);
		System.out.println(":" + uuid);

		// b.hList.add(hList.get(index));

		b.hList = getHeadings(subfilename);
		int uuidCol = getUUIDColumn(b.hList);
		if (uuidCol == 0)
			{
			System.out.println("We have a problem finding the UUID column");
			}

		b.block = getRepeats(subfilename, uuidCol, uuid);

		showDataBlock(hList, dList, b);

		return b;
		}

	private static int getUUIDColumn(ArrayList<String> hList)
		{
		int uuidCol = 0;
		process_list: for (int i = 0; i < hList.size(); i++)
			{
			if (hList.get(i).contains(PARENT_KEY))
				{
				uuidCol = i;
				break process_list;
				}
			}
		return uuidCol;
		}

	private static ArrayList<ArrayList<String>> getRepeats(String subfilename, int uuidCol, String uuid)
		{
		XSSFSheet sheet;
		ArrayList<ArrayList<String>> block = null;

		sheet = getWorksheet(subfilename);

		block = fillStringBlock(sheet, uuidCol, uuid);

		return block;
		}

	private static String getUUID(ArrayList<String> dList, int index)
		{
		String snippet;
		String uuid;
		snippet = dList.get(index);
		int pos_of_slash = snippet.indexOf("/");
		uuid = snippet.substring(0, pos_of_slash);

		return uuid;
		}

	/**
	 * @param filename
	 * @param dList
	 * @param i
	 * @return
	 */
	private static String getSubFilename(String filename, ArrayList<String> dList, int index)
		{
		String snippet;
		String subfilename;

		snippet = dList.get(index);
		int pos_of_slash = snippet.indexOf("/");
		snippet = snippet.substring(pos_of_slash + 1);
		snippet = snippet.replace("-", "_");
		subfilename = PATH + filename + "_" + snippet + EXTENSION;
		return subfilename;
		}

	private static int getNumberOfRows(String sourcefilename)
		{
		XSSFSheet sheet;
		int number_of_rows = 0;

		sheet = getWorksheet(sourcefilename);
		number_of_rows = sheet.getLastRowNum();

		return number_of_rows;
		}

	private static void outputData(ArrayList<String> hList, ArrayList<String> dList, ArrayList<RepeatBlock> table,
			String outputfilename)
		{
		Workbook wb = new XSSFWorkbook();
		FileOutputStream fileOut;
		int active_row = 1;
		int new_active_row = 1;
		int column_offset = 0;

		try
			{
			fileOut = new FileOutputStream(PATH + outputfilename + EXTENSION);

			Sheet sheet1 = wb.createSheet("new sheet");
			Row row0 = sheet1.createRow((short) 0);
			active_row = 1;
			Row row1 = sheet1.createRow((short) active_row);

			for (int i = 0; i < hList.size(); i++)
				{
				// Create a row and put some cells in it. Rows are 0 based.
				// Create a cell and put a value in it.
				row0.createCell(i).setCellValue(hList.get(i));

				String snippet = hList.get(i);
				if (snippet.contains(REPEAT_INDICATOR))
					{
					int index_into_table = Integer.valueOf(dList.get(i));
					RepeatBlock b = table.get(index_into_table);
					new_active_row = outputDataBlock(sheet1, b, row0, active_row, column_offset);
					column_offset = column_offset + b.block.get(Integer.valueOf(dList.get(i))).size();
					}
				else
					{
					if (active_row != new_active_row)
						{
						row1 = sheet1.createRow((short) new_active_row);
						active_row = new_active_row;
						}
					row1.createCell(i).setCellValue(dList.get(i));
					}
				}

			wb.write(fileOut);
			fileOut.close();
			}
		catch (FileNotFoundException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (IOException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		}

	private static int outputDataBlock(Sheet sheet1, RepeatBlock b, Row row0, int active_row, Integer column_offset)
		{
		int new_active_row = active_row;

		for (int i = 0; i < b.hList.size(); i++)
			{
			row0.createCell(i + column_offset).setCellValue(b.hList.get(i));
			ArrayList<String> column = b.block.get(i);

			for (int j = 0; j < column.size(); j++)
				{
				Row row1 = sheet1.createRow((short) new_active_row);
				row1.createCell(i + column_offset).setCellValue(column.get(j));
				new_active_row++;
				}
			}

		return new_active_row;
		}

	private static String getOutputFilenameString(ArrayList<String> hList, ArrayList<String> dList)
		{
		String site_name;
		String site_number;
		String site_inspection_date;
		String site_region;
		String output_filename_string;

		site_name = getValueByToken(hList, dList, "site_group-site_name");
		site_number = getValueByToken(hList, dList, "site_group-site_number");
		site_inspection_date = getValueByToken(hList, dList, "date_of_inspection");
		site_region = getValueByToken(hList, dList, "site_group-region");

		output_filename_string = site_region + "_" + site_name + "_" + site_number + "_" + site_inspection_date;

		return output_filename_string;
		}

	private static String getValueByToken(ArrayList<String> hList, ArrayList<String> dList, String token)
		{
		String value = null;
		int index = 1;

		while (index < hList.size())
			{
			if (hList.get(index) != null && hList.get(index).contentEquals(token))
				{
				value = dList.get(index);
				}
			index++;
			}
		return value;
		}

	private static void showData(ArrayList<String> hList, ArrayList<String> dList)
		{
		String h, d;
		for (int i = 0; i < hList.size(); i++)
			{
			h = getArrayListElement(hList, i);
			d = getArrayListElement(dList, i);
			System.out.println(h + " : " + d);
			}
		System.out.println("================================================================================");

		}

	private static void showDataBlock(ArrayList<String> hList, ArrayList<String> dList, RepeatBlock b)
		{
		String h, d, h2;
		String d2 = "";
		ArrayList<String> column;

		for (int i = 0; i < hList.size(); i++)
			{
			h = getArrayListElement(hList, i);
			d = getArrayListElement(dList, i);
			System.out.println(h + " : " + d);
			h2 = b.hList.get(i);
			column = b.block.get(i);
			for (int j = 0; j < column.size(); j++)
				{
				d2 = d2 + "," + column.get(j);
				}
			System.out.println(h2 + " Values: " + d2);
			}
		System.out.println("================================================================================");

		}

	/**
	 * @param cList
	 * 
	 *            Just a short method to print out the Heading list to the
	 *            console
	 */
	private static void showList(ArrayList<String> cList)
		{
		String s;
		for (int i = 0; i < cList.size(); i++)
			{
			s = getArrayListElement(cList, i);
			System.out.println(s + ":");
			}
		System.out.println("================================================================================");
		}

	private static String getArrayListElement(ArrayList<String> cList, int index)
		{
		String s;
		s = cList.get(index);
		return s;
		}

	private static ArrayList<String> getHeadings(String Stringfilename)
		{
		XSSFSheet sheet;
		ArrayList<String> cList = null;

		sheet = getWorksheet(Stringfilename);

		cList = fillStringList(sheet, 0);

		return cList;
		}

	private static ArrayList<String> getRowData(String Stringfilename, int row, int size)
		{
		XSSFSheet sheet;
		ArrayList<String> dList = null;

		sheet = getWorksheet(Stringfilename);

		dList = fillStringList(sheet, row, size);

		return dList;
		}

	// Overloaded method
	// This one uses iterators on the rows and the cells (columns)
	// It is used to fill the header array List.
	//
	private static ArrayList<String> fillStringList(XSSFSheet sheet, int rowIndex)
		{
		ArrayList<String> cList = new ArrayList<String>();

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();

		Row row = null;

		if (rowIterator.hasNext())
			{ // kill header row
			row = rowIterator.next();
			}

		row = sheet.getRow(rowIndex);
		int column = 0;

		// For each row, iterate through each columns
		Iterator<Cell> cellIterator = row.cellIterator();

		while (cellIterator.hasNext())
			{
			String cell_val;
			int columnIndex;

			Cell cell = cellIterator.next();

			columnIndex = cell.getColumnIndex();

			cell_val = getCell(sheet, rowIndex, columnIndex);
			cList.add(cell_val);

			column++;
			}

		return cList;
		}

	// Overloaded method
	// This one uses iterators on the rows and NOT the cells (columns)
	// It is used to fill the data array List.
	//
	private static ArrayList<String> fillStringList(XSSFSheet sheet, int rowIndex, int maxCols)
		{
		ArrayList<String> cList = new ArrayList<String>();

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();

		Row row = null;

		if (rowIterator.hasNext())
			{
			row = rowIterator.next();
			}

		row = sheet.getRow(rowIndex);
		int col = 0;

		// For each row, iterate through each columns
		Iterator<Cell> cellIterator = row.cellIterator();

		for (col = 0; col < maxCols; col++)
			{
			String cell_val;
			cell_val = getCell(sheet, rowIndex, col);
			cList.add(cell_val);
			}

		return cList;
		}

	// Overloaded method
	// This one uses iterators on the rows and NOT the cells (columns)
	// It is used to fill the data array List.
	//
	private static ArrayList<ArrayList<String>> fillStringBlock(XSSFSheet sheet, int uuidCol, String uuid)
		{
		ArrayList<ArrayList<String>> bList = new ArrayList<ArrayList<String>>();
		ArrayList<String> cList = new ArrayList<String>();
		int number_of_rows;

		number_of_rows = sheet.getLastRowNum();
		System.out.println("Rows " + number_of_rows);

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();

		Row row = null;

		row = rowIterator.next(); // skip the first row

		while (rowIterator.hasNext())
			{
			row = rowIterator.next();
			String row_uuid;
			int current_row = row.getRowNum();

			row_uuid = getCell(sheet, current_row, uuidCol);

			// System.out.println("Row uuid vs uuid : " + row_uuid + " : " +
			// uuid);
			if (row_uuid.contentEquals(uuid))
				{
				for (int col = 0; col < uuidCol; col++)
					{
					String cell_val;
					cell_val = getCell(sheet, current_row, col);
					cList.add(cell_val); // add the cell to the column list
					}
				bList.add(cList); // add the row to the row list
				// System.out.println("Row added");
				}

			}

		return bList;
		}

	private static XSSFSheet getWorksheet(String StringFullFilename)
		{
		XSSFSheet sheet = null;

		try
			{
			FileInputStream file = new FileInputStream(new File(StringFullFilename));

			XSSFWorkbook workbook = new XSSFWorkbook(file);
			sheet = workbook.getSheetAt(0);
			workbook.close();

			}
		catch (FileNotFoundException e)
			{
			e.printStackTrace();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}

		return sheet;
		}

	private static String getCell(XSSFSheet sheet, int row, int column)
		{
		String cell_value = " ";

		Row cur_row = sheet.getRow(row);

		Cell cell = cur_row.getCell(column);

		// System.out.println("Looking at Row: " + row + " column: " + column);

		if (cell != null)
			{
			switch (cell.getCellType())
				{
				case Cell.CELL_TYPE_BLANK:
					cell_value = " ";
					break;

				case Cell.CELL_TYPE_STRING:
					cell_value = cell.getRichStringCellValue().getString();
					if (cell_value.isEmpty())
						cell_value = " - ";
					// cell_value = cell_value.trim();
					// if (cell_value.equals("-"))
					// cell_value = " ";
					break;

				case Cell.CELL_TYPE_NUMERIC:

					if (HSSFDateUtil.isCellDateFormatted(cell))
						{
						// System.out.println("Row No.: " + cur_row.getRowNum()
						// + " " + cell.getDateCellValue());
						Date date = cell.getDateCellValue();
						String dateFmt = cell.getCellStyle().getDataFormatString();
						cell_value = new CellDateFormatter(dateFmt).format(date);
						}
					else
						{

						cell_value = Double.toString(cell.getNumericCellValue());
						int int_cell_value = (int) cell.getNumericCellValue();
						if (int_cell_value == cell.getNumericCellValue())
							cell_value = Integer.toString(int_cell_value);
						}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					System.out.println(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					System.out.println(cell.getCellFormula());
					break;
				default:
					System.out.println("What the hell!!");
				}
			}
		else
			cell_value = " ";

		// System.out.println("Row : " + row + "Col : " + column + " - Cell
		// value : " + cell_value);

		return cell_value;

		}

	public static final String	PATH				= "C:\\My Documents\\Special projects\\RBI Tech database project\\Development files\\Briefcase\\Exports\\";
	public static final String	EXTENSION			= ".XLSX";
	public static final String	PARENT_KEY			= "PARENT_KEY";
	public static final String	REPEAT_INDICATOR	= "SET-OF-";
	}
