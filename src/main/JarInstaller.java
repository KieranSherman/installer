package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

/**
 * Class models an installer that extracts contents from a jar file and moves them to a directory.
 * 
 * @author kieransherman
 *
 */
public class JarInstaller {
	
	private String jarFilePath;
	private Thread shutdownHook;
	private GUI gui;
	
	private volatile ArrayList<Thread> installerThreads;
	
	/**
	 * Creates a new installer with a location to the .jar file to install and a destination directory.
	 * 
	 * @param jarFilePath the path of the jar file.
	 * @param destDir the path of the destination directroy.
	 */
	public JarInstaller(String jarFilePath, String destDir) {
		shutdownHook = new Thread() {
			public void run() {
				if(removeDirectory(new File(destDir+"textgame")))
					System.out.println("Installation aborted cleanly");
				else
					System.err.println("Installation did not abort cleanly");
			}
		};
		shutdownHook.setDaemon(false);
		
		this.jarFilePath = jarFilePath;
		this.gui = new GUI(shutdownHook);
	}

	/**
	 * Enum models installation types.
	 * 
	 * @author kieransherman
	 *
	 */
	public enum InstallType {
		/**
		 * Includes only the specified files starting with a filepath.
		 */
		INCLUDE_ONLY,
		
		/**
		 * Excludes only the specified files starting with a filepath.
		 */
		EXCLUDE,
		
		/**
		 * Writes all files.
		 */
		ALL;
	}
	
	/**
	 * Opens a .jar file and extracts its files based on the {@link #InstallType} to a directory.  Then,
	 * the .jar file itself is copied to the same directory.
	 * 
	 * @param destDir the directory to extract to.
	 * @param folder the folder name to extract to.
	 * @param installType the installation type.
	 * @param modifier the installation type modifier.
	 * @throws Exception something goes wrong with the installation.
	 */
	public void install(String destDir, String folder, InstallType installType, String modifier) throws Exception {
		if(!gui.display(this)) {
			quit(null);
			return;
		}
		
		installerThreads = new ArrayList<Thread>();
		gui.log("fileDir: "+Installer.getModifiedFilePath(destDir+"textgame/"+folder));

		Runtime.getRuntime().addShutdownHook(shutdownHook);
		
		if(getClass().getResource(jarFilePath) == null)
			throw new Exception("Missing files for instalation.");

		File jarFile = new File(getClass().getResource(jarFilePath).getFile());
		JarFile jar = new JarFile(jarFile.getPath());
		Enumeration<JarEntry> jarContents = jar.entries();
		
		while(jarContents.hasMoreElements()) {
			JarEntry file = (JarEntry)jarContents.nextElement();
			String fileName = file.getName();
			
			if(installType == InstallType.INCLUDE_ONLY && !fileName.startsWith(modifier))
				continue;
			else
			if(installType == InstallType.EXCLUDE && fileName.startsWith(modifier))
				continue;
			
			createFileSystem(Installer.getModifiedFilePath(destDir+"textgame/"+folder+fileName));
			
			installerThreads.add(queueFile(jar, file, Installer.getModifiedFilePath(destDir+"textgame/"+folder), fileName));
			
			gui.log("[Queueing: "+file.getName()+"]");
		}
		
		gui.progress.setMaximum(installerThreads.size());
		
		synchronized(installerThreads) {
			for(Thread install : installerThreads)
				install.start();
		}
		
		if(!installerThreads.isEmpty()) {
			synchronized(this) {
				this.wait();
			}
		}
		
		jar.close();
		
		Files.copy(jarFile.toPath(), new File(Installer.getModifiedFilePath(destDir+"textgame/run.jar")).toPath());
		
		gui.log("Installation Finished");
		gui.finish.setEnabled(true);
	}
	
	/**
	 * Quits the installer with an exception.
	 */
	public void quit(Exception e) {
		gui.dispose();
		
		if(e != null)
			JOptionPane.showMessageDialog(null, "There was a problem with the installation.\n\n"+
					"Error: "+e.getMessage(), "Installation Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Creates a directory file system, provided it does not already exist.
	 */
	private void createFileSystem(String filePath) {
		String[] fileSystem = filePath.split(File.separator);
		
		String directories = "";
		
		for(int i = 0; i < fileSystem.length-1; i++)
			directories += fileSystem[i]+File.separator;
		
		File f = new File(directories);
		if(!f.exists()) {
			f.mkdirs();
			gui.log("** Created directory: "+f.getPath());
		}
	}
	
	/**
	 * Recursively removes a directory and all of its subfolders and files.
	 */
	private boolean removeDirectory(File directory) {
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
	 * Returns a thread which upon execution, writes a file from a *.jar to a directory.
	 */
	private Thread queueFile(JarFile jar, JarEntry file, String fileDir, String fileName) {
		JarInstaller obj = this;
		
		Thread writerThread = new Thread(fileName) {
			public void run() {
				try {
					File toWrite = new File(fileDir+fileName);
					gui.log("Starting: "+toWrite.getPath());
					
					if(!toWrite.exists())
						toWrite.createNewFile();
					
					InputStream is = jar.getInputStream(file);
					FileOutputStream fos = new FileOutputStream(new File(fileDir+fileName));
					
					while(is.available() > 0)
						fos.write(is.read());
					
					fos.close();
					is.close();  
					
					gui.log("INSTALLING "+this.getName());
				} catch (IOException e) {
					quit(e);
				}
				
				synchronized(installerThreads) {
					if(installerThreads.size() == 1) {
						synchronized(obj) {
							gui.log("finishing up");
							obj.notifyAll();
						}
					}
					
					installerThreads.remove(this);
					gui.progress.setValue(gui.progress.getValue()+1);
				}
			}
		};
		
		return writerThread;
	}

}