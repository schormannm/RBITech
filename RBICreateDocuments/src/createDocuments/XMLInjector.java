package createDocuments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//
// This program takes a directory containing a file called submission.xml and then a sea of .jpg files that
// the .xml file refers to.  It generates an output file containing the original submission.xml
// with all references to the .jpg files replaced with Base64 encoded byte arrays of the images.
//

public class XMLInjector
	{

	public static void main(String[] args) throws Exception
		{
		String input_filename;
		String outpath;
		Boolean input_file_exists;
		Boolean outpath_exists;

		if (args.length < 2)
			{
			System.out.println("Number of arguments detected on command line is incorrect : " + args.length);
			show_usage();
			}
		else
			{
			String input_path;
			String mog_active;
			String mog_path = "";

			input_filename = args[0];
			outpath = args[1];

			String current = System.getProperty("user.dir");
			if (args.length == 3)
				{
				mog_active = args[2];
				mog_path = current + "/mog/";
				}
			else
				mog_path = current;

			System.out.println(mog_path);
			input_path = mog_path + input_filename;
			input_file_exists = check_file_exists(input_path);

			outpath_exists = check_path_exists(outpath);
			if (!outpath_exists)
				System.out.println("Output path does not exist " + outpath);

			if (input_file_exists && outpath_exists)
				{
				process_files(input_path, outpath);
				System.out.println("Program successfully completed.");
				}
			else
				{
				System.out.println("Program did not run successfully - error in arguments.");
				}
			}
		}

	private static Boolean check_file_exists(String filePathString)
		{
		Boolean file_exists = false;

		File f = new File(filePathString);
		if (f.exists() && !f.isDirectory())
			{
			file_exists = true;
			System.out.println("File exists ----> " + filePathString);
			}
		else
			{
			System.out.println("File does not exist -> " + filePathString);
			}
		return file_exists;
		}

	private static Boolean check_path_exists(String filePathString)
		{
		Boolean file_exists = false;

		File f = new File(filePathString);
		if (f.exists() && f.isDirectory())
			{
			file_exists = true;
			System.out.println("Path exists ----> " + filePathString);
			}
		else
			{
			System.out.println("Path does not exist -> " + filePathString);
			}
		return file_exists;
		}

	private static void show_usage()
		{
		System.out.println("Usage: XMLInjector <XML_input_filename> <output_path>");
		}

	public static void process_files(String filename, String outpath) throws Exception
		{

		String infile = filename;

		try
			{
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(new File(infile));
			NodeList nl = doc.getDocumentElement().getChildNodes();
			String output_filename = getOutPutFileName(doc);

			output_filename = outpath + output_filename + "-1.xml";

			File f = new File(output_filename);
			if (!f.exists())
				{

				for (int k = 0; k < nl.getLength(); k++)
					{
					processTags((Node) nl.item(k));
					}

				System.out.println("Processing of nodes done");

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				StreamResult result = new StreamResult(new StringWriter());
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);

				// String fnSans = getFilenameWithoutExtension(output_filename);
				Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_filename), "UTF-8"));

				String xmlOutput = result.getWriter().toString();
				output.write(xmlOutput);
				output.close();
				}
			else
				{
				System.out.println("Output file exists ----> skipping");
				}

			}
		catch (FileNotFoundException e)
			{
			System.out.println("Image not found" + e);
			}
		catch (IOException ioe)
			{
			System.out.println("Exception while reading the Image " + ioe);
			}
		catch (ParserConfigurationException e)
			{
			e.printStackTrace();
			}
		catch (SAXException e)
			{
			e.printStackTrace();
			}
		catch (TransformerConfigurationException e)
			{
			e.printStackTrace();
			}
		catch (TransformerFactoryConfigurationError e)
			{
			e.printStackTrace();
			}
		catch (TransformerException e)
			{
			e.printStackTrace();
			}

		}

	private static String getOutPutFileName(Document doc)
		{
		String output_filename;
		String site_name;
		String site_number;
		String region;
		String inspect_date;
		String type;

		site_name = doc.getElementsByTagName("site_name").item(0).getTextContent();
		site_number = doc.getElementsByTagName("site_number").item(0).getTextContent();
		region = doc.getElementsByTagName("region").item(0).getTextContent().toUpperCase();
		inspect_date = doc.getElementsByTagName("date_of_inspection").item(0).getTextContent();
		type = doc.getElementsByTagName("tower_type").item(0).getTextContent();

		output_filename = region + "_" + site_name + "_" + site_number + "_" + inspect_date + "_VC_PSEIA_" + type;
		output_filename = cleanString(output_filename);

		System.out.println("Outputfilename : " + output_filename);
		return output_filename;
		}

	private static String cleanString(String site_name)
		{
		String clean_string;

		clean_string = site_name.replace(" ", "_");
		clean_string = clean_string.replace(".", "_");

		return clean_string;
		}

	public static void processTags(Node nodes) throws IOException
		{
		if (nodes.hasChildNodes() || nodes.getNodeType() != 3) // Text node
			{
			// System.out.println(nodes.getNodeName() + " : " + nodes.getTextContent());

			NodeList nl = nodes.getChildNodes();

			if (nl.getLength() == 1)
				{
				if (nodes.getTextContent().contains(".jpg"))
					{
					System.out.println("Found a JPG node -> " + nodes.getNodeName() + " : " + nodes.getTextContent());
					String filename = nodes.getTextContent();

					String filePathString = filename;

					File f = new File(filePathString);
					if (f.exists() && !f.isDirectory())
						{
						System.out.println("Input file specified -> [ " + filePathString + " ] exists");

						// * Reading a Image file from file system
						FileInputStream imageInFile = new FileInputStream(f);
						byte imageData[] = new byte[(int) f.length()];
						imageInFile.read(imageData);

						// Converting Image byte array into Base64 String
						byte[] imageBytes = Base64.encodeBase64(imageData);

						String imageDataString = new String(imageBytes);

						nodes.setTextContent(imageDataString);

						imageInFile.close();
						}
					else
						{
						System.out.println("Input file specified -> [ " + filePathString + " ] does not exist");
						}
					}

				}

			for (int j = 0; j < nl.getLength(); j++)
				processTags(nl.item(j));
			}
		}

	private static String getFilenameWithoutExtension(String input_filename)
		{
		String filename_sans_extension = input_filename.substring(0, input_filename.indexOf("."));
		// System.out.println("Filename without extension " + filename_sans_extension);
		return filename_sans_extension;
		}

	/**
	 * Encodes the byte array into base64 string
	 * 
	 * @param imageByteArray
	 *            - byte array
	 * @return String a {@link java.lang.String}
	 */
	public static String encodeImage(byte[] imageByteArray)
		{
		return Base64.encodeBase64URLSafeString(imageByteArray);
		}

	/**
	 * Decodes the base64 string into byte array
	 * 
	 * @param imageDataString
	 *            - a {@link java.lang.String}
	 * @return byte array
	 */
	public static byte[] decodeImage(String imageDataString)
		{
		return Base64.decodeBase64(imageDataString);
		}

	}
