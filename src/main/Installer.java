package main;

import components.JarInstaller;
import components.JarInstallerUI.InstallationUI;

public class Installer {
	
	public static void main(String [] args) {
		JarInstaller jarInstaller = new JarInstaller("textgame.jar");
		jarInstaller.startInstallation(InstallationUI.DEFAULT);
	}
	
}
