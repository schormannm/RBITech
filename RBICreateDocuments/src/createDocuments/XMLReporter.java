package createDocuments;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBContext;

import org.apache.commons.io.FilenameUtils;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

//
// This program takes a file containing the site data and photos all converted into a single XML file
// It combines this with a Word document template using Content Controls to merge the appropriate data
// into the correct places in the Word Template.  This mechanism is used in preference to the MailMerge
// way of doing it, as it allows the insertion of photographs directly.
//
public class XMLReporter
	{
	public static JAXBContext context = org.docx4j.jaxb.Context.jc;

	static String filepathprefix;

	/**
	 * @param args
	 */

	public static final String	BASE_PATH	= "";			// Used for testing
	public static final String	OUT_PATH	= "Reports/";

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

			process_files(input_filename, template_filename);

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
		String filename;

		filename = "";

		if (args.length >= 2)
			{
			String filePathString;

			for (int i = 0; i < 2; i++)
				{
				filename = args[i];

				filePathString = BASE_PATH + filename;

				File f = new File(filePathString);
				if (f.exists() && !f.isDirectory())
					{
					System.out.println("Input file specified -> [ " + filePathString + " ] exists");
					}
				else
					{
					System.out.println("Input file specified -> [ " + filePathString + " ] does not exist");
					files_exist = false; // Just takes one false to make it false
					}
				}
			}
		else
			{
			System.out.println("Number of arguments detected on command line : " + args.length);
			show_usage();
			}

		return files_exist;
		}

	private static void show_usage()
		{
		System.out.println("Usage: XMLReporter <input_filename> <template_filename>");
		}

	public static void process_files(String filename, String template) throws Exception
		{

		String input_DOCX = BASE_PATH + template;

		String input_XML = BASE_PATH + filename; // need to strip away the path for this.

		String template_file = FilenameUtils.getName(template);
		String code = template_file.substring(0, 2);

		String file_name_only = FilenameUtils.getName(filename);
		int pos1 = file_name_only.indexOf("-1.");
		filename = file_name_only.substring(0, pos1 + 1) + code + file_name_only.substring(pos1 + 1);

		// resulting docx
		// String OUTPUT_DOCX = System.getProperty("user.dir") + "/OUT_ContentControlsMergeXML.docx";
		String output_DOCX = BASE_PATH + getFilenameWithoutExtension(filename) + ".docx";
		System.out.println("Composite output filename : " + output_DOCX);

		File f = new File(output_DOCX);
		if (f.exists() && !f.isDirectory())
			{
			System.out.println("The output file exists already -> " + output_DOCX);
			System.out.println("<------- Skipping all further processing -------->");
			}
		else
			{
			// Load input_template.docx
			WordprocessingMLPackage wordMLPackage = Docx4J.load(new File(input_DOCX));

			// Open the xml stream
			FileInputStream xmlStream = new FileInputStream(new File(input_XML));

			// Do the binding:
			// FLAG_NONE means that all the steps of the binding will be done,
			// otherwise you could pass a combination of the following flags:
			// FLAG_BIND_INSERT_XML: inject the passed XML into the document
			// FLAG_BIND_BIND_XML: bind the document and the xml (including any OpenDope handling)
			// FLAG_BIND_REMOVE_SDT: remove the content controls from the document (only the content remains)
			// FLAG_BIND_REMOVE_XML: remove the custom xml parts from the document

			// Docx4J.bind(wordMLPackage, xmlStream, Docx4J.FLAG_NONE);
			// If a document doesn't include the Opendope definitions, eg. the XPathPart,
			// then the only thing you can do is insert the xml
			// the example document binding-simple.docx doesn't have an XPathPart....
			Docx4J.bind(wordMLPackage, xmlStream, Docx4J.FLAG_BIND_INSERT_XML | Docx4J.FLAG_BIND_BIND_XML);

			// Save the document
			Docx4J.save(wordMLPackage, new File(output_DOCX), Docx4J.FLAG_BIND_REMOVE_XML);
			System.out.println("Saved: " + output_DOCX);
			}
		}

	private static String getFilenameWithoutExtension(String input_filename)
		{
		String filename_sans_extension = input_filename.substring(0, input_filename.indexOf("."));
		// System.out.println("Filename without extension " + filename_sans_extension);
		return filename_sans_extension;
		}
	}