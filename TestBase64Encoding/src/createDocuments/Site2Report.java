package createDocuments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger.OutputField;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * Example of how to process MERGEFIELD.
 * 
 * See http://webapp.docx4java.org/OnlineDemo/ecma376/WordML/MERGEFIELD.html
 *
 */
public class Site2Report
	{
	public static final String	BASE_PATH			= "C:/RBI-Data/Export/";	// Used for testing
	// public static final String BASE_PATH = "";
	public static final String	EXTENSION			= ".docx";
	public static final String	PARENT_KEY			= "PARENT_KEY";
	public static final String	REPEAT_INDICATOR	= "SET-OF-";

	public static void main(String[] args) throws Exception
		{

		if (files_exist(args))
			{
			String template_filename;
			String input_filename;

			input_filename = args[0];
			template_filename = args[1];

			process_file(input_filename, template_filename);

			System.out.println("Program successfully completed.");
			}
		else
			{
			System.out.println("Program did not run successfully.");
			}
		}

	private static Boolean files_exist(String[] args)
		{
		Boolean files_exist = true;
		String input_filename;

		input_filename = "";

		if (args.length >= 2)
			{
			String filePathString;

			for (int i = 0; i < 2; i++)
				{
				input_filename = args[i];

				filePathString = BASE_PATH + input_filename;

				File f = new File(filePathString);
				if (f.exists() && !f.isDirectory())
					{
					System.out.println("Input file specified -> [ " + filePathString + " ] exists");
					}
				else
					{
					System.out.println("Input file specified -> [ " + filePathString + " ] does not exist");
					files_exist = false; // Just take one false to make it false
					}
				}
			}
		else
			show_usage();

		return files_exist;
		}

	private static void show_usage()
		{
		System.out.println("Usage: Site2Report <input_filename> <template_filename>");
		}

	private static void process_file(String input_filename, String template_filename) throws Exception
		{
		// Whether to create a single output docx, or a docx per Map of input data.
		// Note: If you only have 1 instance of input data, then you can just invoke performMerge
		String inFilePath;
		String outFilePath;
		String templateFilePath;
		String input_filename_sans_ext;

		input_filename_sans_ext = getFilenameWithoutExtension(input_filename);

		templateFilePath = BASE_PATH + template_filename;
		inFilePath = BASE_PATH + input_filename;
		outFilePath = BASE_PATH + "OUT_" + input_filename_sans_ext + EXTENSION;

		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(templateFilePath));
		// System.getProperty("user.dir") + "/template.docx"));

		List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();

		XSSFSheet sheet;

		sheet = getWorksheet(inFilePath);

		Row cur_row = sheet.getRow(0);

		int last_column = cur_row.getLastCellNum();

		String row_0_val, row_1_val;
		int column_counter = 0;

		Map<DataFieldName, String> map = new HashMap<DataFieldName, String>();

		while (column_counter < last_column)
			{
			row_0_val = getCell(sheet, 0, column_counter);
			row_1_val = getCell(sheet, 1, column_counter);
			row_0_val = row_0_val.replace("-", "");

			map.put(new DataFieldName(row_0_val), row_1_val);
			System.out.println(column_counter + " , " + row_0_val + " , " + row_1_val);
			column_counter++;
			}

		data.add(map);

		org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

		org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, data.get(0), true);
		wordMLPackage.save(new java.io.File(outFilePath));

		// wordMLPackage.save(new java.io.File(BASE_PATH + "OUT_TestMailMerge_" + ".docx"));

		}

	private static String getFilenameWithoutExtension(String input_filename)
		{
		String filename_sans_extension = input_filename.substring(0, input_filename.indexOf("."));
		// System.out.println("Filename without extension " + filename_sans_extension);
		return filename_sans_extension;
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

					if (HSSFDateUtil.isCellDateFormatted(cell)) // Using the wrong type on purpose
						{
						// System.out.println("Row No.: " + cur_row.getRowNum() + " " + cell.getDateCellValue());
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

		// System.out.println("Row : " + row + "Col : " + column + " - Cell value : " + cell_value);

		return cell_value;

		}

	}

/*
 * // Instance 1
 * 
 * // map.put(new DataFieldName("site_groupsite_name"), "Daffy duck"); // map.put(new DataFieldName("site_groupsite_number"),
 * "345456789"); // map.put(new DataFieldName("site_groupregion"), "Mpumalanga"); // map.put(new DataFieldName("tower_type"),
 * "Lattice"); // map.put(new DataFieldName("site_groupmanufacture_year"), "2007"); // map.put(new
 * DataFieldName("tower_grouptop_height"), "2456800"); // map.put(new DataFieldName("site_groupgps_locationLongitude"), "24.56800");
 * // map.put(new DataFieldName("site_groupgps_locationLatitude"), "24.56800"); // map.put(new DataFieldName("inspector_name"),
 * "Minnie Mouse"); // map.put(new DataFieldName("site_groupjob_number"), "KK45333"); // map.put(new
 * DataFieldName("site_groupreport_number"), "RP4435"); // map.put(new DataFieldName("container_owner"), "Vodacom"); // map.put(new
 * DataFieldName("Vodacom_container_grouptransmission_typ"), "Fibre"); // map.put(new
 * DataFieldName("Vodacom_container_grouptransmission_typ1"), "Microwave"); // // map.put(new
 * DataFieldName("site_groupphysical_address"), "Easy Street"); // map.put(new DataFieldName("site_grouptower_owner"), "Vodacom");
 * // map.put(new DataFieldName("tower_groupexternal_coating_colour"), "Pink"); // map.put(new
 * DataFieldName("tower_groupexternal_coating_colour_cust"), "International Orange"); // map.put(new
 * DataFieldName("site_groupinfrastructure_contractor"), "Panavision"); // map.put(new DataFieldName("site_groupmanufacturer"),
 * "Dorbyl"); // map.put(new DataFieldName("site_groupspecial_access_arrangements"), "Come in via the gate and get a key");
 * 
 * // map.put(new DataFieldName("tower_type"), "Plutext");
 * 
 * // To get dates right, make sure you have docx4j property docx4j.Fields.Dates.DateFormatInferencer.USA // set to true or false as
 * appropriate. It defaults to non-US. // map.put(new DataFieldName("date_of_inspection"), "15/4/2013");
 * 
 * // How to treat the MERGEFIELD, in the output? // Need to keep the MERGEFIELDs. If you don't, you'd have to clone the docx, and
 * perform the // merge on the clone. For how to clone, see the MailMerger code, method getConsolidatedResultCrude //
 * org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);
 * 
 * // System.out.println(XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true));
 * 
 * // WordprocessingMLPackage output = org.docx4j.model.fields.merge.MailMerger.getConsolidatedResultCrude(wordMLPackage, data, //
 * true);
 * 
 * // System.out.println(XmlUtils.marshaltoString(output.getMainDocumentPart().getJaxbElement(), true, true));
 * 
 * // output.save(new java.io.File(outFilePath));
 */
