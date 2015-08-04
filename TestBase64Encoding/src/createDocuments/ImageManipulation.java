package createDocuments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

/**
 * @desc Image manipulation - Conversion
 * 
 * @filename ImageManipulation.java
 * @author <a href="mailto:mark@soft.co.za">Mark Schormann</a>
 * @copyright &copy; 2014-2015 www.soft.co.za
 */
public class ImageManipulation
	{

	public static final String BASE_DIR = "c:/rbi-data/merge/";

	/**
	 * @param args
	 */
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

				filePathString = BASE_DIR + input_filename;

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
		String infile = BASE_DIR + input_filename;

		File file = new File(infile);

		try
			{
			/*
			 * Reading a Image file from file system
			 */

			FileInputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);

			/*
			 * Converting Image byte array into Base64 String
			 */
			String imageDataString = encodeImage(imageData);

			byte[] imageBytes = Base64.encodeBase64(imageData);
			System.out.println("encodedBytes " + new String(imageBytes));

			// ... write to file
			FileOutputStream imageTxtOutFile = new FileOutputStream(BASE_DIR + "OUT64-" + input_filename + ".txt");
			imageTxtOutFile.write(imageBytes);

			System.out.println(imageDataString);

			/*
			 * Converting a Base64 String into Image byte array
			 */
			byte[] imageByteArray = decodeImage(imageDataString);

			/*
			 * Write a image byte array into file system
			 */
			FileOutputStream imageOutFile = new FileOutputStream(BASE_DIR + "OUT-" + input_filename);
			imageOutFile.write(imageByteArray);

			imageTxtOutFile.close();
			imageInFile.close();
			imageOutFile.close();

			System.out.println("Image Successfully Manipulated!");
			}
		catch (FileNotFoundException e)
			{
			System.out.println("Image not found" + e);
			}
		catch (IOException ioe)
			{
			System.out.println("Exception while reading the Image " + ioe);
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
