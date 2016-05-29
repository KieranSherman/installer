package components;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

public abstract class JarInstallerUI {
	
	protected JFrame window;
	protected Thread shutdownHook;
	protected JarInstaller installer;
	
	protected Color light_gold = new Color(255, 245, 104);
	protected Color darker_blue = new Color(12, 152, 207);
	protected Color lighter_blue = new Color(10, 160, 217);
	protected Color gray = new Color(108, 110, 112);
	protected Color dark_gray = new Color(45, 48, 51);
	
	protected Font tahoma = new Font("Tahoma", Font.PLAIN, 13);
	
	public enum InstallationUI {
		GRAPHICAL, 
		DEFAULT;
	}
	
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
	
	protected BufferedImage loadImage(String filePath) {
		try {
			return ImageIO.read(getClass().getClassLoader().getResourceAsStream(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected abstract boolean display();
	protected abstract void load();
	protected abstract void setShutdownHook(Thread shutdownHook);
	protected abstract void setFinishable(boolean enabled);
	protected abstract void log(String line);
	protected abstract void setText(String line);
	protected abstract void setMaximumProgress(int value);
	protected abstract void incrementProgress(int value);

}
