package main;

import javax.swing.UIManager;

public class Installer {
	
	public static void main(String [] args) {
		System.out.println(System.getProperty("os.name"));
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String extractionDir = getModifiedFilePath(System.getProperty("user.home")+"/Desktop/");
		JarInstaller installer = new JarInstaller("textgame.jar", extractionDir, "textgame");
		
		installer.setUI(new GraphicalUI(installer));
		
//		try {
//			installer.install(InstallType.INCLUDE_ONLY, "files");
//		} catch (Exception e) {
//			installer.quit(e);
//		}
	}
	
	public static String getModifiedFilePath(String filePath) {
		if(!System.getProperty("os.name").toLowerCase().contains("mac"))
			return filePath.replaceAll("[/]", "\\\\");
		
		return filePath;
	}

}
