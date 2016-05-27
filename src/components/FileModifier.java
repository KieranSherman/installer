package components;

import java.io.File;

public class FileModifier {
	
	/**
	 * Recursively removes a directory and all of its subfolders and files.
	 */
	public static boolean removeDirectory(File directory) {
		if(directory == null)
			return false;
		if(!directory.exists())
			return true;
		if(!directory.isDirectory())
			return false;

		String[] list = directory.list();
		if(list != null) {
			for(int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);
				
				if(entry.isDirectory()) {
					if(!removeDirectory(entry))
						return false;
				} else {
					if(!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}
	
	/**
	 * Creates a directory file system, provided it does not already exist.
	 */
	public static void createFileSystem(String filePath) throws Exception {
		String[] fileSystem = filePath.split(getFileSeparator());
		
		String directories = "";
		
		for(int i = 0; i < fileSystem.length-1; i++)
			directories += fileSystem[i]+File.separator;
		
		File f = new File(directories);
		if(!f.exists())
			f.mkdirs();
	}
	
	/**
	 * Returns a String containing the operating system-specific file separator.
	 */
	protected static String getFileSeparator() throws Exception {
		String os = System.getProperty("os.name").toLowerCase();

		if(os.contains("mac")) {
			return "/";
		} else
		if(os.contains("nix") || os.contains("nux")) {
			return "/";
		} else
		if(os.contains("windows")) {
			return "\\\\";
		} else
			throw new Exception("Unsupported operating system.");
	}
	
	/**
	 * Returns a modified filepath containing operating system-specific file separators.
	 */
	protected static String getModifiedFilePath(String filePath) throws Exception {
		String os = System.getProperty("os.name").toLowerCase();
		
		if(os.contains("mac")) {
			return filePath;
		} else
		if(os.contains("nix") || os.contains("nux")) {
			return filePath;
		} else
		if(os.contains("windows")) {
			return filePath.replaceAll("[/]", "\\\\");
		} else
			throw new Exception("Unsupported operating system.");
	}

}
