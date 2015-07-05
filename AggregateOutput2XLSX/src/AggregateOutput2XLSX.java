
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

		filename = "";

		if (args.length >= 1)
			{
			String filePathString;
			filename = args[0];
			filePathString = BASE_PATH + filename + EXTENSION;

			File f = new File(filePathString);
			if (f.exists() && !f.isDirectory())
				{
				process_file(filename);
				}
			else
				System.out.println("Input file specified -> [ " + filePathString + " ] does not exist");
			}
		else
			show_usage();

		System.out.println("Program successfully completed.");
		}

	private static void show_usage()
		{
		System.out.println("Usage: AggregateOutput2XLSX <filename>");
		}

	private static void process_file(String filename)
		{
		ArrayList<String> hList = null;
		ArrayList<String> dList = null;
		ArrayList<RepeatBlock> table = new ArrayList<RepeatBlock>();
		String sourcefilename;

		sourcefilename = BASE_PATH + filename + EXTENSION;

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
			outputData(outputfilename, hList, dList, table);

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
					System.out.println("New block added to table.  Index = " + new_insert_index);

					dList.set(i, new_insert_index.toString()); // place index of
																// RepeatBlock
																// in table into
																// dList

					bIndex++; // block index - incremented for each repeat block
								// added

					System.out.println(hList.get(i));
					}
				else
					{
					String fake_insert_index = "-1";
					dList.set(i, fake_insert_index); // place index of
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

		// showDataBlock(hList, dList, b, uuidCol);

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
		// snippet = snippet.toLowerCase(); // actually not a good idea to mess
		// with the case
		subfilename = BASE_PATH + filename + "_" + snippet + EXTENSION;
		// subfilename = subfilename.toLowerCase();
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

	private static void outputData(String outputfilename, ArrayList<String> hList, ArrayList<String> dList,
			ArrayList<RepeatBlock> table)
		{
		Workbook wb = new XSSFWorkbook();
		FileOutputStream fileOut;
		int active_row = 1;
		int new_active_row = 1;
		int column_offset = 0;

		try
			{
			String full_path = BASE_PATH + outputfilename + EXTENSION;

			File yourFile = new File(full_path);
			if (!yourFile.exists())
				{
				yourFile.createNewFile();
				}
			fileOut = new FileOutputStream(yourFile, false);

			// fileOut = new FileOutputStream(full_path);

			System.out.println("Output filename : " + full_path);

			Sheet sheet1 = wb.createSheet("new sheet");
			Row row0 = sheet1.createRow((short) 0);
			active_row = 1;
			Row row1 = sheet1.createRow((short) active_row);

			int columns_added = 0;
			for (int i = 0; i < hList.size(); i++)
				{
				// Create a row and put some cells in it. Rows are 0 based.
				// Create a cell and put a value in it.
				int new_columns_added = 0;
				row0.createCell(columns_added).setCellValue(hList.get(i));

				String snippet = hList.get(i);
				// System.out.println("Processing output for : " + snippet);

				if (snippet.contains(REPEAT_INDICATOR))
					{
					String index_value = dList.get(i);
					int index_into_table = Integer.valueOf(index_value);
					if (index_into_table >= 0)
						{
						RepeatBlock b = table.get(index_into_table);
						column_offset = columns_added + 1;
						new_active_row = outputDataBlock(sheet1, b, row0, active_row, column_offset);
						new_columns_added = getUUIDColumn(b.hList) + 1;
						row1 = sheet1.createRow((short) new_active_row);
						}
					}
				else
					{
					if (active_row != new_active_row)
						{
						active_row = new_active_row;
						}

					String dValue = dList.get(i);
					row1.createCell(columns_added).setCellValue(dValue);
					new_columns_added = 1;
					// System.out.println("Col: " + i + " " + hList.get(i) + " "
					// + dValue);
					}

				columns_added = columns_added + new_columns_added;

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
		ArrayList<Row> rows = new ArrayList<Row>();

		int new_active_row = active_row + 1;

		int uuidCol = getUUIDColumn(b.hList);

		for (int i = 0; i < uuidCol; i++)
			{
			int new_col = i + column_offset;
			String heading = b.hList.get(i);
			row0.createCell(new_col).setCellValue(heading);

			ArrayList<String> column = b.block.get(i);
			int start_row = active_row;

			for (int j = 0; j < column.size(); j++)
				{
				if (i == 0) // in other words the first column
					{
					rows.add(sheet1.createRow((short) new_active_row));
					new_active_row++;
					}
				rows.get(j).createCell(new_col).setCellValue(column.get(j));
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

		// If site_inspection_date is not formatted correctly, or contains /
		// characters, then it needs to be fixed.
		System.out.println("Site inspection date raw: " + site_inspection_date);

		output_filename_string = site_name + "_" + site_number + "_" + site_inspection_date;
		output_filename_string = output_filename_string.toLowerCase();
		output_filename_string = site_region + "_" + output_filename_string;

		// Needs to be done for Linux
		output_filename_string = output_filename_string.replace("/", "-");
		System.out.println("Output filename string: " + output_filename_string);

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

	private static void showDataBlock(ArrayList<String> hList, ArrayList<String> dList, RepeatBlock b, int uuidCol)
		{
		String h, d, h2;
		ArrayList<String> column;
		int i;

		// for (i = 0; i < hList.size(); i++)
		// {
		// h = getArrayListElement(hList, i);
		// d = getArrayListElement(dList, i);
		// System.out.println(h + " : " + d);
		// }

		for (i = 0; i < uuidCol; i++)
			{
			String d2 = "";
			h2 = b.hList.get(i);
			column = b.block.get(i);
			for (int j = 0; j < column.size(); j++)
				{
				d2 = d2 + "," + column.get(j);
				}
			System.out.println("Column - " + i);
			System.out.println(h2 + " Values: " + d2);
			}
		System.out.println("================== End of routine - show data block ======================================");

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
		System.out.println("=================================== End of routine - ShowList ===========================");
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
		int number_of_rows;

		number_of_rows = sheet.getLastRowNum();
		System.out.println("Rows " + number_of_rows);

		String row_uuid;

		// System.out.println("Row uuid vs uuid : " + row_uuid + " : " +
		// uuid);

		for (int col = 0; col < uuidCol; col++)
			{
			ArrayList<String> cList = new ArrayList<String>();

			for (int current_row = 1; current_row < number_of_rows; current_row++)
				{
				row_uuid = getCell(sheet, current_row, uuidCol);

				if (row_uuid.contentEquals(uuid))
					{
					String cell_val;
					boolean not_duplicate = true;
					cell_val = getCell(sheet, current_row, col);
					// not_duplicate = check_not_duplicate(cList, cell_val);
					// if (not_duplicate)
					// System.out.println("Not duplicate - " + cell_val);
					// else
					// System.out.println("Duplicate - " + cell_val);

					cList.add(cell_val); // add the cell to the column list
											// if it is not a duplicate
					// System.out.println("Row added to column");
					}
				}
			// showList(cList);
			bList.add(cList); // add the column to the block
			// System.out.println("---> Column " + col + " added to Block");
			}

		return bList;

		}

	private static boolean check_not_duplicate(ArrayList<String> cList, String cell_val)
		{
		boolean not_duplicate = true;
		for (int i = 0; i < cList.size(); i++)
			{
			// System.out.println("Comparing - " + cell_val + " : " +
			// cList.get(i));
			if (cell_val.contains(cList.get(i)))
				not_duplicate = false;
			}
		return not_duplicate;
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

					if (HSSFDateUtil.isCellDateFormatted(cell)) // Using the
																// wrong type on
																// purpose
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

	// public static final String BASE_PATH = "C:\\RBI-Data\\Export\\"; // Used
	// for testing
	public static final String	BASE_PATH			= "";
	public static final String	EXTENSION			= ".xlsx";
	public static final String	PARENT_KEY			= "PARENT_KEY";
	public static final String	REPEAT_INDICATOR	= "SET-OF-";
	}
