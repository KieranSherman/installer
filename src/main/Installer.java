package main;

import javax.swing.JOptionPane;

import components.JarInstaller;
import components.JarInstallerUI.InstallationUI;

public class Installer {
	
	public static void main(String [] args) {
		int installer = JOptionPane.showOptionDialog(null, "Choose your installer", "Installer", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Graphical", "Default", "Cancel"}, null);
		
		InstallationUI ui = null;
		
		if(installer == 0) {
			ui = InstallationUI.GRAPHICAL;
		}
		else
		if(installer == 1) {
			ui = InstallationUI.DEFAULT;
		}
		else 
		if(installer == 2) {
			return;
		}
		
		JarInstaller jarInstaller = new JarInstaller("textgame.jar");
		jarInstaller.startInstallation(ui);
	}
	
}
