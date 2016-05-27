package main;

import javax.swing.JFrame;

public abstract class JarInstallerUI {
	
	protected JFrame window;
	protected Thread shutdownHook;
	protected JarInstaller installer;
	
	public JarInstallerUI(JarInstaller installer) {
		this.installer = installer;
	}
	
	protected abstract boolean display();
	protected abstract void load();
	protected abstract void setShutdownHook(Thread shutdownHook);
	protected abstract void setEnabled(boolean enabled);
	protected abstract void log(String line);
	protected abstract void setText(String line);
	protected abstract void setMaximumProgress(int value);
	protected abstract void incrementProgress(int value);
	protected abstract void dispose();

}
