package components;

import javax.swing.JFrame;
import javax.swing.UIManager;

public abstract class JarInstallerUI {
	
	protected JFrame window;
	protected Thread shutdownHook;
	protected JarInstaller installer;
	
	/**
	 * Creates a new JarInstallerUI object with a JarInstaller reference.  Also
	 * sets the look and feel to MetalLookAndFeel.
	 * 
	 * @param installer
	 */
	public JarInstallerUI(JarInstaller installer) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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

}
