package main;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

/**
 * Class models an installer that extracts contents from a jar file and moves them to a directory.
 * 
 * @author kieransherman
 * @version 1.03
 *
 */
public class JarInstaller {
	
	private String jarFilePath;
	private String extractionDir;
	private String extractionName;
	private String srcFolder;
	private String tempJarFilePath;
	
	private boolean hide;

	private GUI gui;
	
	private volatile Thread shutdownHook;
	
	private volatile ArrayList<Thread> installerThreads;
	
	/**
	 * Creates a new installer with a location to the .jar file to install and a destination directory.
	 * 
	 * @param jarFilePath the path of the jar file.
	 * @param extractionDir the path of the destination directroy.
	 * @param extractionName the name of the folder to extract to.
	 */
	public JarInstaller(String jarFilePath, String extractionDir, String extractionName) {
		this.hide = false;
		this.jarFilePath = Installer.getModifiedFilePath(jarFilePath);
		this.extractionDir = Installer.getModifiedFilePath(extractionDir);
		this.extractionName = Installer.getModifiedFilePath(hide ? "."+extractionName+File.separator : extractionName+File.separator);
		this.srcFolder = Installer.getModifiedFilePath("src"+File.separator);
		this.tempJarFilePath = Installer.getModifiedFilePath(extractionDir+(extractionName+"-loader"));

		gui = new GUI();
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
	 * @param extractionDir the directory to extract to.
	 * @param srcFolder the folder name to extract to.
	 * @param installType the installation type.
	 * @param modifier the installation type modifier.
	 * @throws Exception something goes wrong with the installation.
	 */
	public void install(InstallType installType, String modifier) throws Exception {
		System.out.println("will -r "+extractionDir+extractionName);
		System.out.println("will -r "+tempJarFilePath);
		
		shutdownHook = new Thread() {
			public void run() {
				synchronized(installerThreads) {
					for(Thread t : installerThreads)
						t.interrupt();
				}
				
				synchronized(installerThreads) {
					for(Thread t : installerThreads) {
						try {
							t.join(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				File dir = new File(extractionDir+extractionName);
				File tmpJar = new File(tempJarFilePath);
				
				System.out.println(tmpJar.getPath());

				if(removeDirectory(dir)) {
					try {
						tmpJar.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Installation aborted cleanly");
				} else
					System.err.println("Installation did not abort cleanly");
				
				gui.dispose();
				
			}
		};
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		
		gui.setShutdownHook(shutdownHook);
		
		if(!gui.display(this))
			throw new Exception("");
		
		if(getClass().getClassLoader().getResourceAsStream(jarFilePath) == null)
			throw new Exception("Missing files required for installation.");
		
		installerThreads = new ArrayList<Thread>();
		gui.log("fileDir: "+Installer.getModifiedFilePath(extractionDir+extractionName+srcFolder));
		
		File tempJarFile = new File(tempJarFilePath);

	    Files.copy(getClass().getClassLoader().getResourceAsStream(jarFilePath), tempJarFile.toPath(), REPLACE_EXISTING);
		
		JarFile jarFile = new JarFile(tempJarFile.getPath());
		Enumeration<JarEntry> jarContents = jarFile.entries();
		
		while(jarContents.hasMoreElements()) {
			JarEntry file = (JarEntry)jarContents.nextElement();
			String fileName = file.getName();
			
			if(installType == InstallType.INCLUDE_ONLY && !fileName.startsWith(modifier))
				continue;
			else
			if(installType == InstallType.EXCLUDE && fileName.startsWith(modifier))
				continue;
			
			createFileSystem(Installer.getModifiedFilePath(extractionDir+extractionName+srcFolder+fileName));
			
			installerThreads.add(queueFile(jarFile, file, Installer.getModifiedFilePath(extractionDir+extractionName+srcFolder), fileName));
			
			gui.log("[Queueing: "+Installer.getModifiedFilePath(file.getName())+"]");
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
		
		jarFile.close();
		
		Files.copy(tempJarFile.toPath(), new File(Installer.getModifiedFilePath(extractionDir+extractionName+"/run.jar")).toPath());
		Files.move(new File(Installer.getModifiedFilePath(extractionDir+extractionName)).toPath(), 
				new File(Installer.getModifiedFilePath(extractionDir+unHide(extractionName))).toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		gui.log("Installation Finished");
		gui.finish.setEnabled(true);
	}
	
	private String unHide(String hidden) {
		if(hidden.contains("."))
			return hidden.replace(".", "");
		
		return hidden;
	}
	
	/**
	 * Quits the installer with an exception.
	 */
	public void quit(Exception e) {
		if(e != null) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null, "There was a problem with the installation.\n\n"+
					"Error:\n"+e.getMessage(), "Installation Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Creates a directory file system, provided it does not already exist.
	 */
	private void createFileSystem(String filePath) {
		String[] fileSystem = filePath.split(System.getProperty("os.name").contains("mac") ? "/" : "\\\\");
		
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
					
					while(is.available() > 0 && !this.isInterrupted())
						fos.write(is.read());
					
					fos.flush();
					fos.close();
					is.close(); 
					
					if(this.isInterrupted()) {
						gui.log("EXITING "+Installer.getModifiedFilePath(this.getName()));
						return;
					}
					
					gui.log("INSTALLING "+Installer.getModifiedFilePath(this.getName()));
				} catch (IOException e) {
					e.printStackTrace();
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