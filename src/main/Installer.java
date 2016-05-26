package main;

import java.io.File;

import javax.swing.UIManager;

import main.JarInstaller.InstallType;

public class Installer {
	
	public static void main(String [] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String destDir = getModifiedFilePath(System.getProperty("user.home")+"/Desktop/");
		String folder = getModifiedFilePath("src/");
		JarInstaller installer = new JarInstaller(getModifiedFilePath("/jarfiles/textgame.jar"), destDir);
		
		try {
			installer.install(destDir, folder, InstallType.INCLUDE_ONLY, "files");
		} catch (Exception e) {
			installer.quit(e);
		}
	}
	
	public static String getModifiedFilePath(String filePath) {
		return filePath.replace("/", File.separator);
	}

}
