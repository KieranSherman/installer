package main;

import javax.swing.UIManager;

public class Installer {
	
	public static void main(String [] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JarInstaller installer = new JarInstaller("textgame.jar");
		installer.setUI(new DefaultUI(installer));
	}
	
	public static String getModifiedFilePath(String filePath) {
		if(!System.getProperty("os.name").toLowerCase().contains("mac"))
			return filePath.replaceAll("[/]", "\\\\");
		
		return filePath;
	}

}
