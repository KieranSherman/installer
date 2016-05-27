package components;

import static java.nio.file.StandardCopyOption.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

import components.JarInstallerUI.InstallationUI;

/**
 * Class models an installer that extracts contents from a .jar file and moves them to a directory.
 * 
 * @author kieransherman
 * @version 1.06
 *
 */
public final class JarInstaller {
	
	public enum InstallType {
		INCLUDE_ONLY, EXCLUDE, ALL;
	}
	
	protected JarFile jarFile;
	
	protected String jarFilePath;
	protected String tempJarFileName;
	protected String tempJarFilePath;
	protected String extractionDirFilePath;
	protected String extractionDirFileName;
	protected String sourceFolderFileName;
	
	protected JarInstallerUI jarInstallerUI;
	
	protected volatile Thread shutdownHook;
	protected volatile Thread currentThread;
	
	private volatile ArrayList<Thread> threadList;
	
	/**
	 * Creates a new installer with a location to the .jar file to install.
	 * 
	 * @param jarFilePath the location of the .jar file to install.
	 */
	public JarInstaller(String jarFilePath) {
		this.jarFilePath = jarFilePath;
		this.threadList = new ArrayList<Thread>();

		setTempJarFileName(".installation");
		setSourceFolderName("src");
	}
	
	/**
	 * Set the source folder name of the extracted contents.
	 */
	public void setSourceFolderName(String srcFolderName) {
		this.sourceFolderFileName = srcFolderName.endsWith("/") ? srcFolderName : srcFolderName+"/";
	}
	
	/**
	 * Set the temporary jar's file name.
	 */
	public void setTempJarFileName(String tempJarFileName) {
		this.tempJarFileName = tempJarFileName;
	}
	
	/**
	 * Begins the installation using a specific installation user interface.
	 * 
	 * @param installationUI the UI used to install the .jar file.
	 */
	public void startInstallation(InstallationUI installationUI) {
		switch(installationUI) {
			case GRAPHICAL:
				jarInstallerUI = new GraphicalUI(this);
				break;
				
			case DEFAULT:
				jarInstallerUI = new DefaultUI(this);
				break;
				
			default:
				jarInstallerUI = new DefaultUI(this);
		}
		
		addShutdownHook();
		
		jarInstallerUI.load();
	}
	
	
	
	/**
	 * Opens a .jar file and extracts its files based on the {@link #InstallType} to a directory.  Then,
	 * the .jar file itself is copied to the same directory.
	 * 
	 * @param installType the installation type.
	 * @param modifier the installation type modifier.
	 * @throws Exception something goes wrong with the installation.
	 */
	protected void install(InstallType installType, String modifier) throws Exception {
		if(jarInstallerUI == null || !jarInstallerUI.display())
			throw new Exception("<NULL>");
		
		tempJarFilePath = extractionDirFilePath+tempJarFileName;
		File tempJarFile = new File(tempJarFilePath);
		
		convertFilePathsToOS();
		
		if(getClass().getClassLoader().getResourceAsStream(jarFilePath) == null)
			throw new Exception("Missing files required for installation.");
		
	    queueInstallerThreads(tempJarFile, installType, modifier);
		executeInstallerThreads();
		finishInstallation(tempJarFile);
	}
	
	/**
	 * Converts all the file paths to an operating system-specific format.
	 */
	private void convertFilePathsToOS() {
		try {
			jarFilePath = FileModifier.getModifiedFilePath(jarFilePath);
			tempJarFileName = FileModifier.getModifiedFilePath(tempJarFileName);
			tempJarFilePath = FileModifier.getModifiedFilePath(tempJarFilePath);
			extractionDirFileName = FileModifier.getModifiedFilePath(extractionDirFileName);
			extractionDirFilePath = FileModifier.getModifiedFilePath(extractionDirFilePath);
			sourceFolderFileName = FileModifier.getModifiedFilePath(sourceFolderFileName);
		} catch (Exception e) {
			quit(e);
		}
		
		System.out.println("j_fp:   "+jarFilePath);
		System.out.println("tj_fn:  "+tempJarFileName);
		System.out.println("tj_fp:  "+tempJarFilePath);
		System.out.println("ed_fn:  "+extractionDirFileName);
		System.out.println("ed_fp:  "+extractionDirFilePath);
		System.out.println("sf_fn:  "+sourceFolderFileName);
		System.out.println();
	}
	
	/**
	 * Creates and queues all the threads needed for installation.
	 */
	private void queueInstallerThreads(File tempJarFile, InstallType installType, String modifier) throws Exception {
	    Files.copy(getClass().getClassLoader().getResourceAsStream(jarFilePath), tempJarFile.toPath(), REPLACE_EXISTING);

		jarFile = new JarFile(tempJarFile.getPath());
		Enumeration<JarEntry> jarContents = jarFile.entries();
		
		while(jarContents.hasMoreElements()) {
			JarEntry file = (JarEntry)jarContents.nextElement();
			String fileName = FileModifier.getModifiedFilePath(file.getName());
			
			if(installType == InstallType.INCLUDE_ONLY && !fileName.startsWith(modifier))
				continue;
			else
			if(installType == InstallType.EXCLUDE && fileName.startsWith(modifier))
				continue;
			
			FileModifier.createFileSystem(extractionDirFilePath+extractionDirFileName+sourceFolderFileName+fileName);
			
			threadList.add(queueFile(jarFile, file, extractionDirFilePath+extractionDirFileName+sourceFolderFileName, fileName));
		}
		
		jarInstallerUI.setMaximumProgress(threadList.size()+1);
	}
	
	/**
	 * Executes all the installer threads.
	 */
	private void executeInstallerThreads() throws Exception {
		synchronized(threadList) {
			for(Thread thread : threadList) {
				currentThread = thread;
				thread.start();
				
				if(thread.isAlive())
					synchronized(this) {
						this.wait();
					}
			}
		}
		
		jarFile.close();
	}
	
	/**
	 * Finishes up the installation.
	 */
	private void finishInstallation(File tempJarFile) throws Exception {
		jarInstallerUI.log("INSTALLING JARFILE: "+tempJarFile.getName());
		
		String extractionFolder = extractionDirFilePath+extractionDirFileName;
		
		Files.copy(tempJarFile.toPath(), new File(extractionFolder+"/run.jar").toPath(), 
				StandardCopyOption.REPLACE_EXISTING);
		
		if(extractionDirFileName.startsWith("."))
			Files.move(new File(extractionFolder).toPath(), 
					new File(extractionDirFilePath+extractionDirFileName.substring(1)).toPath(), StandardCopyOption.REPLACE_EXISTING);
	
		jarInstallerUI.incrementProgress(1);
		
		jarInstallerUI.log("INSTALLATION FINISHED");
		jarInstallerUI.setFinishable(true);
	}
	
	/**
	 * Returns a thread which upon execution, writes a file from a *.jar to a directory.
	 */
	private Thread queueFile(JarFile jar, JarEntry file, String fileDir, String fileName) {
		JarInstaller obj = this;
		
		Thread writerThread = new Thread(fileName) {
			public void run() {
				try {
					String log = "INSTALLING "+this.getName();
					jarInstallerUI.log(log);

					File toWrite = new File(fileDir+fileName);

					if(!toWrite.exists())
						toWrite.createNewFile();
					
					InputStream is = jar.getInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(is);
					
					FileOutputStream fos = new FileOutputStream(new File(fileDir+fileName));
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					
					long bytesRead = 0;
					long fileSize = file.getSize();
					int update = 10000;
					
					while(bis.available() > 0 && !this.isInterrupted()) {
						bos.write(bis.read());
						bytesRead++;
						
						if(bytesRead%update == 0)
							jarInstallerUI.setText(log+" "+(bytesRead*100/fileSize)+"%");
					}
					
					bos.flush();
					bos.close();
					bis.close();
					
					if(this.isInterrupted()) {
						jarInstallerUI.log("CANCELLING "+this.getName());
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					quit(e);
				}
				
				synchronized(obj) {
					obj.notify();
				}
				
				jarInstallerUI.incrementProgress(1);
			}
					
		};
		
		return writerThread;
	}
	
	/**
	 * Adds the shutdown hook to the installer.
	 */
	protected void addShutdownHook() {
		shutdownHook = new Thread() {
			public void run() {
				if(currentThread != null) {
					currentThread.interrupt();
					try {
						currentThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if(jarFile != null) {
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(abort())
					System.out.println("INSTALLATION ABORTED CLEANLY");
				else {
					System.err.println("INSTALLATION DID NOT ABORT CLEANLY");
					System.err.println("CHECK: ["+extractionDirFilePath+"] FOR UNWANTED FILES.");

					JOptionPane.showMessageDialog(null, "Installation did not abort cleanly\nCheck: ["+extractionDirFilePath+"] for unwanted files.");
				}
			}
		};
		
		jarInstallerUI.setShutdownHook(shutdownHook);
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}

	/**
	 * Quits the installer with an exception.
	 */
	protected void quit(Exception e) {
		if(e != null && !e.getMessage().equals("<NULL>")) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null, "There was a problem with the installation.\n\n"+
					"Error:\n"+e.getMessage(), "Installation Error", JOptionPane.ERROR_MESSAGE);
		}
		
		System.exit(0);
	}
	
	/**
	 * Aborts the installation.
	 */
	protected boolean abort() {
		File dir = null;
		if(extractionDirFilePath != null && extractionDirFileName != null)
			dir = new File(extractionDirFilePath+extractionDirFileName);
		
		File tempJar = null;
		if(tempJarFilePath != null)
			tempJar = new File(tempJarFilePath);
		
		return ( tempJar == null || (!tempJar.exists() || tempJar.delete())) && 
				dir == null || ((!dir.exists() || FileModifier.removeDirectory(dir)) );
	}
	
	/**
	 * Sets the extraction folder's name.
	 */
	protected void setExtractionName(String extractionName) {
		this.extractionDirFileName = extractionName.endsWith("/") ? extractionName : extractionName+"/";
	}
	
	/**
	 * Sets the path to the extraction directory.
	 */
	protected void setExtractionDir(String extractionDir) {
		this.extractionDirFilePath = extractionDir.endsWith("/") ? extractionDir : extractionDir+"/";
	}
	
	/**
	 * Finishes the installation.
	 */
	protected boolean finish() {
		File tempJar = new File(tempJarFilePath);
		return (!tempJar.exists() || tempJar.delete());
	}
	
}