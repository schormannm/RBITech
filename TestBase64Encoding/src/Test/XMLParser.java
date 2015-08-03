package Test;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser
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
				printTags((Node) nl.item(k));
				}
			}
		catch (Exception e)
			{
			/* err handling */}
		}

	public static void printTags(Node nodes)
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
					}
				else
					{
					System.out.println("Input file specified -> [ " + filePathString + " ] does not exist");
					// files_exist = false; // Just take one false to make it false
					}

				}

			for (int j = 0; j < nl.getLength(); j++)
				printTags(nl.item(j));
			}
		}
	}