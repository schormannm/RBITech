package createDocuments;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBContext;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class XMLReporter
	{
	public static JAXBContext context = org.docx4j.jaxb.Context.jc;

	static String filepathprefix;

	/**
	 * @param args
	 */

	public static final String	BASE_PATH			= "C:/RBI-Data/Merge/";	// Used for testing
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
			{
			System.out.println("Number of arguments detected on command line : " + args.length);
			show_usage();
			}

		return files_exist;
		}

	private static void show_usage()
		{
		System.out.println("Usage: XML2Report <input_filename> <template_filename>");
		}

	public static void process_files(String filename, String template) throws Exception
		{

		String input_DOCX = BASE_PATH + template;
		String input_XML = BASE_PATH + filename;

		// resulting docx
		// String OUTPUT_DOCX = System.getProperty("user.dir") + "/OUT_ContentControlsMergeXML.docx";
		String OUTPUT_DOCX = BASE_PATH + "OUT_" + template;

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
		Docx4J.save(wordMLPackage, new File(OUTPUT_DOCX), Docx4J.FLAG_BIND_REMOVE_XML);
		System.out.println("Saved: " + OUTPUT_DOCX);
		}

	}
