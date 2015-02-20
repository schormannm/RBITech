import java.io.File;

/**
 * @author mark
 *
 */
public class ListFiles {

	public void walk(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath()); // This is where the recursion
											// happens
				// System.out.println("Dir:" + f.getAbsoluteFile());
			} else {
				String fullpath = f.getAbsoluteFile().toString();
				String fixed = fullpath.replace("\\", "\\\\");
				System.out.println(fixed);
			}
		}
	}

	public static void main(String[] args) {
		ListFiles lf = new ListFiles();

		lf.walk(PATH);

	}

	public static final String PATH = "C:\\My Documents\\Special projects\\RBI Tech database project\\Vodacom SA Excel Data";

}

// static File mainFolder = new File(
// "C:\\My Documents\\Special projects\\RBI Tech database project\\RBI Reference docs\\Vodacom SA Excel Data");
//
// public static void main(String[] args) {
// ListFiles lf = new ListFiles();
// // lf.getFiles(lf.mainFolder);
//
// // long fileSize = mainFolder.length();
// // System.out.println("mainFolder size in bytes is: " + fileSize);
// // System.out.println("File size in KB is : " + (double) fileSize /
// // 1024);
// // System.out.println("File size in MB is :" + (double) fileSize
// // / (1024 * 1024));
//
// listFile("C:\\My Documents\\Special projects\\RBI Tech database project\\RBI Reference docs\\Vodacom SA Excel Data");
// }
//
// public static void listFile(String pathname) {
// File f = new File(pathname);
// File[] listfiles = f.listFiles();
// for (int i = 0; i < listfiles.length; i++) {
// if (listfiles[i].isDirectory()) {
// File[] internalFile = listfiles[i].listFiles();
// for (int j = 0; j < internalFile.length; j++) {
// System.out.println(internalFile[j]);
// if (internalFile[j].isDirectory()) {
// String name = internalFile[j].getAbsolutePath();
// listFile(name);
// }
//
// }
// } else {
// System.out.println(listfiles[i]);
// }
//
// }
//
// }
//
// public void getFiles(File f) {
// File files[];
// if (f.isFile())
// System.out.println(f.getAbsolutePath());
// else {
// files = f.listFiles();
// for (int i = 0; i < files.length; i++) {
// getFiles(files[i]);
// }
// }
// }

// }
