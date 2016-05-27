package main;

import components.DefaultUI;
import components.JarInstaller;

public class Installer {
	
	public static void main(String [] args) {
		JarInstaller installer = new JarInstaller("textgame.jar");
		installer.setUI(new DefaultUI(installer));
	}
	
}
