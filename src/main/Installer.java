package main;

import javax.swing.JOptionPane;

import components.JarInstaller;
import components.JarInstallerUI.InstallationUI;

public class Installer {
	
	public static void main(String [] args) {
		InstallationUI ui = null;

		for(String s : args)
			if(s.contains("skip"))
				ui = InstallationUI.GRAPHICAL;
		
		if(ui == null) {
			int installer = JOptionPane.showOptionDialog(null, "Choose your installer", "Installer", 
					JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Cancel", "Graphical", "Default"}, null);
			
			
			if(installer == 0) {
				System.exit(0);
			}
			else
			if(installer == 1) {
				ui = InstallationUI.GRAPHICAL;
			}
			else
			if(installer == 2) {
				ui = InstallationUI.DEFAULT;
			}
		}
		
		JarInstaller jarInstaller = new JarInstaller("textgame.jar");
		jarInstaller.startInstallation(ui);
	}
	
}
