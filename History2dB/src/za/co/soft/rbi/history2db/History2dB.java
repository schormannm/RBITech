/**
 * 
 */
package za.co.soft.rbi.history2db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author mark
 *
 */
public class History2dB {

	public static void main(String[] args) throws InvalidFormatException,
			IOException {
		// TODO Auto-generated method stub

		ArrayList<String> xlsFileName = getFilenames();
		processFileList(xlsFileName);
	}

	/**
	 * @param xlsFileName
	 */
	private static void processFileList(ArrayList<String> xlsFileName) {

		int number_of_files = xlsFileName.size();

		for (int i = 0; i < number_of_files; i++) {
			// for (int i = 0; i < 10; i++) {

			System.out.println("Name of XLS file to process next: "
					+ xlsFileName.get(i));

			ArrayList<String> list = new ArrayList<String>();

			String manifestfilename;
			String assetType = "";

			try {
				String sFileName = xlsFileName.get(i);

				FileInputStream file = new FileInputStream(new File(sFileName));

				String extension = sFileName.substring(sFileName.indexOf('.'));
				// System.out.println("Extension: " + extension);

				if (extension.equals(".xls")) {
					HSSFWorkbook workbook = new HSSFWorkbook(file);
					HSSFSheet sheet = workbook.getSheetAt(0);
					list = getTypeInfoFromSheet(sheet);
					workbook.close();
				} else if (extension.equals(".xlsx")) {
					XSSFWorkbook workbook = new XSSFWorkbook(file);
					XSSFSheet sheet = workbook.getSheetAt(0);
					list = getTypeInfoFromSheet(sheet);
					workbook.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// System.out.println("");
			// System.out.println("Asset type: " + list.get(0));
			// System.out.println("Version string: " + list.get(1));

			manifestfilename = getConverterPath(list);

			// System.out.println("");
			System.out.println("Path to manifests: " + manifestfilename);
		}
	}

	/**
	 * @param sheet
	 * @param list
	 * @return
	 */
	private static String getPath2Converter(HSSFSheet sheet,
			ArrayList<String> list) {
		// TODO Auto-generated method stub
		String path2Converter = null;

		Iterator<Row> rowIterator = sheet.iterator();

		if (rowIterator.hasNext()) {
			Row row = rowIterator.next(); // skip over first row

			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				if (findTypeMatch(row, list) && findRevMatch(row, list)) {
					// System.out.println("Found a perfect match");
					path2Converter = row.getCell(3).getStringCellValue();
				}
			}

		}

		return path2Converter;
	}

	/**
	 * @param sheet
	 * @param list
	 * @return
	 */
	private static String getPath2Converter(XSSFSheet sheet,
			ArrayList<String> list) {
		// TODO Auto-generated method stub
		String path2Converter = null;

		Iterator<Row> rowIterator = sheet.iterator();

		if (rowIterator.hasNext()) {
			Row row = rowIterator.next(); // skip over first row

			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				if (findTypeMatch(row, list) && findRevMatch(row, list)) {
					// System.out.println("Found a perfect match");
					// System.out.println("SType:"
					// + row.getCell(1).getStringCellValue() + " SRev:"
					// + row.getCell(2).getStringCellValue());
					// System.out.println("MType:" + list.get(0) + " MRev:"
					// + list.get(1));
					// System.out.println("File:"
					// + row.getCell(3).getStringCellValue());

					path2Converter = row.getCell(3).getStringCellValue();
				}
			}

		}

		return path2Converter;
	}

	/**
	 * @param args
	 * @throws InvalidFormatException
	 * @throws IOException
	 * 
	 */

	private static ArrayList<String> getFilenames() {
		ArrayList<String> xlsfilename = new ArrayList<String>();

		String filename = PATH + "FilesList.txt";

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(
					filename)));
			System.out.println("File open successful!");
			String line = null;

			// while ((line = in.readLine()) != null) {
			// System.out.println(line);
			//
			// }
			while ((line = in.readLine()) != null) {
				xlsfilename.add(line);
			}

			in.close();

		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		return xlsfilename;
	}

	private static void showCellContents(Cell cell) {

		// System.out.println("In showCellContents ..." + cell.toString());

		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_NUMERIC:
			System.out.print(cell.getNumericCellValue() + "\t");
			break;
		case Cell.CELL_TYPE_STRING:
			System.out.print(cell.getStringCellValue() + "\t");
			break;
		}
	}

	/**
	 * @param list
	 * @return
	 */
	private static String getConverterPath(ArrayList<String> list) {
		// TODO Auto-generated method stub

		String path2Converter = null;

		String xlsFileName = PATH + "Source2Converters_manifest.xlsx";

		try {
			FileInputStream file = new FileInputStream(new File(xlsFileName));

			String extension = xlsFileName.substring(xlsFileName.indexOf('.'));
			// System.out.println("Extension: " + extension);

			if (extension.equals(".xls")) {
				HSSFWorkbook workbook = new HSSFWorkbook(file);
				HSSFSheet sheet = workbook.getSheetAt(0);
				path2Converter = getPath2Converter(sheet, list);
				// processSheet(sheet);
				workbook.close();
			} else if (extension.equals(".xlsx")) {
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				XSSFSheet sheet = workbook.getSheetAt(0);
				path2Converter = getPath2Converter(sheet, list);
				// processSheet(sheet);
				workbook.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return path2Converter;
	}

	/**
	 * @param row
	 * @param list
	 * @return
	 */
	private static boolean findRevMatch(Row row, ArrayList<String> list) {
		// TODO Auto-generated method stub
		// For each row, iterate through all the columns
		Boolean found_rev_match = Boolean.FALSE;

		String rev_compare = row.getCell(2).getStringCellValue();

		if (list.get(1).contains(rev_compare)) {
			found_rev_match = Boolean.TRUE;
		}

		return (found_rev_match);
	}

	/**
	 * @param row
	 * @param list
	 * @return
	 */
	private static boolean findTypeMatch(Row row, ArrayList<String> list) {
		// TODO Auto-generated method stub
		// For each row, iterate through all the columns
		Boolean found_type_match = Boolean.FALSE;

		String type_compare = row.getCell(1).getStringCellValue();

		if (list.get(0).equals(type_compare)) {
			found_type_match = Boolean.TRUE;
		}

		return found_type_match;
	}

	private static ArrayList<String> getTypeInfoFromSheet(HSSFSheet sheet) {

		String type = null;
		ArrayList<String> list = new ArrayList<String>();

		Iterator<Row> rowIterator = sheet.iterator();

		if (rowIterator.hasNext()) {
			// Row row = rowIterator.next(); // skip over first row

			Row row = rowIterator.next();

			// For each row, iterate through all the columns
			// Iterator<Cell> cellIterator = row.cellIterator();

			// while (cellIterator.hasNext()) {
			// Cell cell = cellIterator.next();

			for (int i = 0; i < 4; i++) {
				Cell cell1 = row.getCell(i);
				// showCellContents(cell1);

				if (cell1.getCellType() == Cell.CELL_TYPE_STRING) {
					type = cell1.getStringCellValue();
					list.add(type);
				}
			}

		}
		return list;
	}

	private static ArrayList<String> getTypeInfoFromSheet(XSSFSheet sheet) {

		String type = null;
		ArrayList<String> list = new ArrayList<String>();

		Iterator<Row> rowIterator = sheet.iterator();

		if (rowIterator.hasNext()) {
			// Row row = rowIterator.next(); // skip over first row

			Row row = rowIterator.next();

			// For each row, iterate through all the columns
			// Iterator<Cell> cellIterator = row.cellIterator();

			// while (cellIterator.hasNext()) {
			// Cell cell = cellIterator.next();

			for (int i = 0; i < 4; i++) {
				Cell cell1 = row.getCell(i);
				// showCellContents(cell1);

				if (cell1.getCellType() == Cell.CELL_TYPE_STRING) {
					type = cell1.getStringCellValue();
					list.add(type);
				}
			}

		}
		return list;
	}

	private static void processSheet(HSSFSheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		if (rowIterator.hasNext()) {
			// Row row = rowIterator.next(); // skip over first row

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				// For each row, iterate through all the columns
				// Iterator<Cell> cellIterator = row.cellIterator();

				// while (cellIterator.hasNext()) {
				// Cell cell = cellIterator.next();

				Cell cell1 = row.getCell(0);
				showCellContents(cell1);
				Cell cell2 = row.getCell(2);
				showCellContents(cell2);
				System.out.println("");
			}
		}
	}

	private static void processSheet(XSSFSheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		if (rowIterator.hasNext()) {
			// Row row = rowIterator.next(); // skip over first row

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				// For each row, iterate through all the columns
				// Iterator<Cell> cellIterator = row.cellIterator();

				// while (cellIterator.hasNext()) {
				// Cell cell = cellIterator.next();

				Cell cell1 = row.getCell(0);
				showCellContents(cell1);
				Cell cell2 = row.getCell(2);
				showCellContents(cell2);
				System.out.println("");
			}
		}
	}

	public static final String PATH = "C:\\My Documents\\Special projects\\RBI Tech database project\\Development files\\History2dB_processor\\";
}
