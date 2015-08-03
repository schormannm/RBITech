package Test;

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

public class XMLInjector
	{

	public static final String BASE_DIR = "c:/rbi-data/merge/uuid/";

	public static void main(String[] args)
		{

		String input_filename = "submission.xml";
		String infile = BASE_DIR + input_filename;

		try
			{
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(new File(infile));
			NodeList nl = doc.getDocumentElement().getChildNodes();

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
			Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SeeOfXML.xml"), "UTF-8"));

			String xmlOutput = result.getWriter().toString();
			output.write(xmlOutput);
			output.close();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (SAXException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (TransformerConfigurationException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (TransformerFactoryConfigurationError e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (TransformerException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		}

	public static void processTags(Node nodes) throws IOException
		{
		if (nodes.hasChildNodes() || nodes.getNodeType() != 3)
			{
			// System.out.println(nodes.getNodeName() + " : " + nodes.getTextContent());

			NodeList nl = nodes.getChildNodes();

			if (nodes.getTextContent().contains(".jpg") && nl.getLength() == 1)
				{
				System.out.println("Found a JPG node -> " + nodes.getNodeName() + " : " + nodes.getTextContent());
				String filename = nodes.getTextContent();

				String filePathString = BASE_DIR + filename;

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
					}
				else
					{
					System.out.println("Input file specified -> [ " + filePathString + " ] does not exist");
					// files_exist = false; // Just take one false to make it false
					}

				}

			for (int j = 0; j < nl.getLength(); j++)
				processTags(nl.item(j));
			}
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
