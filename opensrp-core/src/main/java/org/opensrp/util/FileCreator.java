/**
 * Contributors: muhammad.ahmed@ihsinformatics.com
 */
package org.opensrp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class FileCreator {

	// private static String directory=null;//= System.getProperty("user.home");

	// public FileCreator() {
	// directory = System.getProperty("user.home");
	//
	// }

	private void createFile(String filename, String directory, byte[] content)
			throws FileNotFoundException, IOException {

		String s = osDirectorySet(osDirectorySet(directory));
		//System.out.println(s);
		FileOutputStream fos2 = new FileOutputStream(s + filename);
		fos2.write(content);
		fos2.close();

	}

	public String createDirectory(String directory) {

		File file = new File(osDirectorySet(directory) );
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}

		return file.getAbsolutePath();
	}

	public boolean createFormFiles(String directory, String formId,
			byte[] form, byte[] model, byte[] formjson) {
	
		try {
		//	System.out.println("before creating files "+directory);
			directory = createDirectory(directory);
			createFile("form.xml", directory, form);
			createFile("model.xml", directory, model);
			createFile("form.json", directory, formjson);
		//	System.out.println("before creating files "+directory);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createTextFile(String directory, byte[] context,
			String formId) {
		try {
			directory = createDirectory(directory);
			createFile(formId + ".txt", directory, context);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createModelFile(String directory, String formId,
			byte[] context) {
		try {
			directory = createDirectory(directory);
			createFile("model.xml", directory, context);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public boolean createFormFile(String directory, String formId,
			byte[] context) {
		try {
			directory = createDirectory(directory);
			createFile("form.xml", directory, context);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createFormJsonFile(String directory, String formId,
			byte[] context) {
		try {
			directory = createDirectory(directory);
			createFile("form.json", directory, context);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String osDirectorySet(String name) {

		if (name.startsWith("/")) {
			name += "/";
			// directory += "/"+name+"/";
		} else {
			// directory += "\\"+name+"\\";
			name += "\\";
		}
		return name;
	}

	public  String prettyFormat(String input, int indent) {
		try {
			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			// This statement works with JDK 6
			transformerFactory.setAttribute("indent-number", indent);

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Throwable e) {
			// You'll come here if you are using JDK 1.5
			// you are getting an the following exeption
			// java.lang.IllegalArgumentException: Not supported: indent-number
			// Use this code (Set the output property in transformer.
			try {
				Source xmlInput = new StreamSource(new StringReader(input));
				StringWriter stringWriter = new StringWriter();
				StreamResult xmlOutput = new StreamResult(stringWriter);
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount",
						String.valueOf(indent));
				transformer.transform(xmlInput, xmlOutput);
				return xmlOutput.getWriter().toString();
			} catch (Throwable t) {
				return input;
			}
		}
	}

	public  String prettyFormat(String input) {
		return prettyFormat(input, 3);
	}

}
