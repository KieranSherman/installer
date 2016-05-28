package components;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import components.JarInstaller.InstallType;

/**
 * Class models a graphical installer.
 * 
 * @author kieransherman
 *
 */
public class GraphicalUI extends JarInstallerUI implements ActionListener {
	
	private Thread shutdownHook;
	
	private JButton finishButton;
	private JButton cancelButton;

	private JProgressBar progressBar;
	private JTextField progressField;
	
	private Font tahoma = new Font("Tahoma", Font.PLAIN, 13);
	
	private Color light_gold = new Color(255, 245, 104);
	private Color darker_blue = new Color(12, 152, 207);
	private Color lighter_blue = new Color(10, 160, 217);
	private Color gray = new Color(108, 110, 112);
	private Color dark_gray = new Color(45, 48, 51);
	
	private String extractionDir;
	private String extractionName;
	
	private int status = 0;
	private int selectY = 0;
	private int checkY = -40;
	private float checkOpacity = 0.0f;
	
	private int folderY = -40;
	private float folderOpacity = 0.0f;
	
	private float textOpacity = 0.0f;
	
	private BufferedImage check = loadImage("check.png");
	private BufferedImage folder = loadImage("folder.png");
	private BufferedImage jarfile = loadImage("jarfile.png");
	
	private BufferedImage loadImage(String filePath) {
		try {
			return ImageIO.read(getClass().getClassLoader().getResourceAsStream(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Creates a new GraphicalUI with a JarInstaller reference.
	 * 
	 * @param installer the JarInstaller.
	 */
	public GraphicalUI(JarInstaller installer) {
		super(installer);
	}

	/**
	 * Displays the GUI.
	 */
	@Override
	protected void load() {
		JLabel header = new JLabel("INSTALLER");
		header.setOpaque(true);
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setVerticalAlignment(JLabel.CENTER);
		header.setBorder(new EmptyBorder(10, 10, 7, 10));
		header.setFont(tahoma.deriveFont(16f));
		header.setBackground(dark_gray);
		header.setForeground(light_gold);
		
		JLabel progressHeader = new JLabel("progress");
		progressHeader.setOpaque(false);
		progressHeader.setFont(tahoma);
		progressHeader.setForeground(light_gold);
		
		progressBar = new JProgressBar();
		progressBar.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
		progressBar.setStringPainted(true);
		progressBar.setFont(tahoma);
		progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 35));
		progressBar.setEnabled(false);
		
		progressField = new JTextField("0%");
		progressField.setHorizontalAlignment(JTextField.CENTER);
		progressField.setForeground(Color.WHITE);
		progressField.setBackground(gray);
		progressField.setFont(tahoma);
		progressField.setFocusable(false);
		progressField.setEditable(false);
		progressField.setHighlighter(null);
		progressField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		progressField.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, light_gold));
		progressField.setEnabled(false);
		
		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.setBackground(dark_gray);
		progressPanel.setBorder(new EmptyBorder(0, 40, 10, 40));
		progressPanel.add(progressHeader, BorderLayout.NORTH);
		progressPanel.add(progressBar, BorderLayout.CENTER);
		progressPanel.add(progressField, BorderLayout.SOUTH);
		
		JLabel confirmHeader = new JLabel("final destination");
		confirmHeader.setOpaque(false);
		confirmHeader.setFont(tahoma);
		confirmHeader.setForeground(light_gold);
		
		JTextField confirmField = new JTextField("tbd");
		confirmField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		confirmField.setEditable(false);
		confirmField.setFont(tahoma.deriveFont(17f));
		confirmField.setForeground(dark_gray);
		confirmField.setBackground(darker_blue);
		confirmField.setMargin(new Insets(0, 10, 0, 10));
		confirmField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
		confirmField.setHorizontalAlignment(JTextField.CENTER);
		confirmField.setHighlighter(null);
		
		JButton confirmConfirm = new JButton("confirm");
		confirmConfirm.setForeground(light_gold);
		confirmConfirm.setBackground(dark_gray);
		confirmConfirm.setFont(tahoma);
		confirmConfirm.setFocusable(false);
		confirmConfirm.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		confirmConfirm.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		confirmConfirm.setEnabled(false);
		confirmConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmConfirm.setEnabled(false);
				confirmField.setEnabled(false);
				progressBar.setEnabled(true);
				progressField.setEnabled(true);
				
				new Thread() {
					public void run() {
						try {
							installer.setExtractionDir(extractionDir);
							installer.setExtractionName(extractionName);
							installer.install(InstallType.INCLUDE_ONLY, "files");
						} catch (Exception e) {
							installer.quit(e);
							System.exit(1);
						}
					}
				}.start();
				
				textOpacity = 0.0f;
				status++;
			}
		});
		
		JPanel confirmPanel = new JPanel(new BorderLayout());
		confirmPanel.setBackground(dark_gray);
		confirmPanel.setBorder(new EmptyBorder(0, 40, 10, 40));
		confirmPanel.add(confirmField, BorderLayout.CENTER);
		confirmPanel.add(confirmHeader, BorderLayout.NORTH);
		confirmPanel.add(confirmConfirm, BorderLayout.SOUTH);
		
		JLabel nameHeader = new JLabel("folder name");
		nameHeader.setOpaque(false);
		nameHeader.setFont(tahoma);
		nameHeader.setForeground(light_gold);
		
		JTextField nameField = new JTextField("src");
		nameField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		nameField.setEditable(false);
		nameField.setFont(tahoma.deriveFont(17f));
		nameField.setForeground(dark_gray);
		nameField.setBackground(lighter_blue);
		nameField.setMargin(new Insets(0, 10, 0, 10));
		nameField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
		nameField.setHorizontalAlignment(JTextField.CENTER);
		
		JButton nameConfirm = new JButton("confirm");
		nameConfirm.setForeground(light_gold);
		nameConfirm.setBackground(dark_gray);
		nameConfirm.setFont(tahoma);
		nameConfirm.setFocusable(false);
		nameConfirm.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		nameConfirm.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		nameConfirm.setEnabled(false);
		nameConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nameConfirm.setEnabled(false);
				nameField.setEnabled(false);
				confirmConfirm.setEnabled(true);
				
				extractionName = nameField.getText();
				confirmField.setText(extractionDir+extractionName);
				textOpacity = 0.0f;
				status++;
			}
		});
		
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBackground(dark_gray);
		namePanel.setBorder(new EmptyBorder(0, 40, 10, 40));
		namePanel.add(nameField, BorderLayout.CENTER);
		namePanel.add(nameHeader, BorderLayout.NORTH);
		namePanel.add(nameConfirm, BorderLayout.SOUTH);
		
		JLabel directoryHeader = new JLabel("installation directory");
		directoryHeader.setOpaque(false);
		directoryHeader.setFont(tahoma);
		directoryHeader.setForeground(light_gold);
		
		JTextField directoryField = new JTextField();
		directoryField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		directoryField.setEditable(false);
		directoryField.setFont(tahoma.deriveFont(17f));
		directoryField.setForeground(dark_gray);
		directoryField.setBackground(lighter_blue);
		directoryField.setMargin(new Insets(0, 10, 0, 10));
		directoryField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
		directoryField.setHorizontalAlignment(JTextField.CENTER);
		directoryField.setText(System.getProperty("user.home")+File.separator+"Desktop");
		directoryField.setCaretPosition(directoryField.getText().length());
		directoryField.setHighlighter(null);

		JButton directoryChange = new JButton("change");
		directoryChange.setForeground(lighter_blue);
		directoryChange.setBackground(dark_gray);
		directoryChange.setFont(tahoma);
		directoryChange.setFocusable(false);
		directoryChange.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		directoryChange.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		directoryChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				if(fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
					directoryField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		
		JButton directoryConfirm = new JButton("confirm");
		directoryConfirm.setForeground(light_gold);
		directoryConfirm.setBackground(dark_gray);
		directoryConfirm.setFont(tahoma);
		directoryConfirm.setFocusable(false);
		directoryConfirm.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		directoryConfirm.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		directoryConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directoryConfirm.setEnabled(false);
				directoryField.setEnabled(false);
				directoryChange.setEnabled(false);
				nameConfirm.setEnabled(true);
				nameField.setEditable(true);
				nameField.selectAll();
				
				extractionDir = directoryField.getText()+File.separator;
				textOpacity = 0.0f;
				status++;
			}
		});

		JPanel directoryButtons = new JPanel(new GridLayout(1, 2));
		directoryButtons.setOpaque(false);
		directoryButtons.add(directoryConfirm);
		directoryButtons.add(directoryChange);
		
		JPanel directoryPanel = new JPanel(new BorderLayout());
		directoryPanel.setBackground(dark_gray);
		directoryPanel.setBorder(new EmptyBorder(0, 40, 10, 40));
		directoryPanel.add(directoryField, BorderLayout.CENTER);
		directoryPanel.add(directoryHeader, BorderLayout.NORTH);
		directoryPanel.add(directoryButtons, BorderLayout.SOUTH);

		finishButton = new JButton("finish");
		finishButton.setFont(tahoma);
		finishButton.setBackground(dark_gray);
		finishButton.setForeground(light_gold);
		finishButton.setFocusable(false);
		finishButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, gray));
		finishButton.setEnabled(false);
		finishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runtime.getRuntime().removeShutdownHook(shutdownHook);
				window.dispose();
				
				if(!installer.finish())
					JOptionPane.showMessageDialog(null, "Did not finish installation cleanly.\nCheck directory for .installation.");
					
				installer.quit(null);
			}
		});
		finishButton.setVisible(false);
		
		cancelButton = new JButton("cancel");
		cancelButton.setFont(tahoma);
		cancelButton.setBackground(dark_gray);
		cancelButton.setForeground(lighter_blue);
		cancelButton.setFocusable(false);
		cancelButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, lighter_blue));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JPanel panel = new JPanel(new GridLayout(4, 1)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				g.setColor(light_gold);
				
				String str = status+"/3";
				FontMetrics metrics = g.getFontMetrics(tahoma);
				
				int rows = ((GridLayout)this.getLayout()).getRows();
				
				if(selectY < this.getHeight()/rows*status)
					selectY += ((this.getHeight()/rows*status)-selectY)/4+1;
				else
				if(selectY > this.getHeight()/rows*status)
					selectY -= (selectY-(this.getHeight()/rows*status))/4-1;
				
				g.drawLine(0, selectY, window.getWidth(), selectY);
				
				g.setColor(dark_gray);
				g.fillRect(0, 0, this.getWidth(), selectY-1);
				
				g.setColor(Color.WHITE);
				g.setFont(tahoma);
				
				if(status == 1) {
					header.setForeground(dark_gray);
					str = "Writing to: "+extractionDir+nameField.getText()+"\n"+str;
				} else if (status == 2) {
					str = "Everything look okay?\n"+str;
					
					if(folderOpacity < 0.7f)
						folderOpacity += 0.04f;
					
					if(folderY < 0)
						folderY += (0-folderY)/4;
					
					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, folderOpacity));
					g.drawImage(folder, 105-(folderY/2), 0, 150+folderY, 150+folderY, null);
				} else if(status == 3) {
					str = "Looks good! Just sit back and relax, we're installing your product now.";
					
					if(folderY < 50)
						folderY += (50-folderY)/4;
					
					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)progressBar.getPercentComplete()));
					g.drawImage(jarfile, 500-(int)(progressBar.getPercentComplete()*360), 40, 135, 135, null);

					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					g.drawImage(folder, 105-(folderY/2), 0, 150+folderY, 150+folderY, null);
				} else if (status == 4) {
					str = "All done!";
					
					if(checkOpacity < 0.7f)
						checkOpacity += 0.04f;
					
					if(checkY < 0)
						checkY += (0-checkY)/4;
					
					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f));
					g.setColor(light_gold);
					g.fillOval(205-(checkY/4), checkY+10-(checkY/4), 280+(checkY/2), 280+(checkY/2));
					g.setColor(Color.WHITE);
					g.drawOval(205-(checkY/4), checkY+10-(checkY/4), 280+(checkY/2), 280+(checkY/2));
					
					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, checkOpacity));
					g.drawImage(check, 195-(checkY/2), checkY-(checkY/2), 300+checkY, 300+checkY, null);
					
					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					finishButton.setBackground(new Color(dark_gray.getRed()-(int)(checkOpacity*100)/4,
							dark_gray.getBlue()-(int)(checkOpacity*100)/4, dark_gray.getGreen()-(int)(checkOpacity*100)/4+10));
					
					cancelButton.setBackground(new Color(dark_gray.getRed()-(int)(checkOpacity*100)/4,
							dark_gray.getBlue()-(int)(checkOpacity*100)/4, dark_gray.getGreen()-(int)(checkOpacity*100)/4+10));
				}
				
				if(textOpacity < 0.7f)
					textOpacity += 0.04f;
				
				g.setColor(Color.WHITE);
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textOpacity));
				String [] split = str.split("\n");
				for(int i = 0; i < split.length; i++)
					g.drawString(split[i], (this.getWidth() - metrics.stringWidth(split[i])) / 2, selectY-(20*split.length)+20*i);
				
				g.dispose();
			}
		};
		panel.setBackground(dark_gray);
		panel.add(directoryPanel);
		panel.add(namePanel);
		panel.add(confirmPanel);
		panel.add(progressPanel);
		panel.setBorder(new EmptyBorder(10, 0, 0, 0));
		panel.setPreferredSize(new Dimension(700, 400));
		
		JPanel buttonPanel = new JPanel(null);
		buttonPanel.setOpaque(false);
		cancelButton.setBounds(40, 10, 620, 40);
		buttonPanel.add(cancelButton);
		buttonPanel.add(finishButton);
		buttonPanel.setPreferredSize(new Dimension(700, 65));
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(dark_gray);
		mainPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		super.window = new JFrame("");
		super.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.window.setLayout(new BorderLayout());
		super.window.setPreferredSize(new Dimension(700, 460));
		super.window.pack();
		super.window.setLocationRelativeTo(null);
		super.window.add(header, BorderLayout.NORTH);
		super.window.add(mainPanel, BorderLayout.CENTER);
		super.window.setResizable(false);
		super.window.setVisible(true);
		
		Timer render = new Timer(20, this);
		render.start();
	}
	
	/**
	 * Sets the shutdown hook.
	 */
	@Override
	protected void setShutdownHook(Thread shutdownHook) {
		this.shutdownHook = shutdownHook;
	}
	
	/**
	 * Returns true because there are no conflicts.
	 */
	@Override
	protected boolean display() {
		return true;
	}

	/**
	 * Sets the finish button to enable.
	 */
	@Override
	protected void setFinishable(boolean enabled) {
		new Thread() {
			public void run() {
				progressField.setForeground(light_gold);

				try {Thread.sleep(750);} catch (Exception e) {}
				finishButton.setEnabled(true);
				cancelButton.setBounds(10, 13, 330, 40);
				finishButton.setBounds(360, 13, 330, 40);
				finishButton.setVisible(true);
				finishButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
				cancelButton.setText("uninstall");
				status++;
			}
		}.start();
	}

	/**
	 * Logs a line of text.
	 */
	@Override
	protected void log(String line) {
		System.out.println(line);
		
		progressField.setText(line);
	}

	/**
	 * Sets the progress text field's text.
	 */
	@Override
	protected void setText(String line) {
		progressField.setText(line);
	}

	/**
	 * Sets the maximum progress of the progress bar.
	 */
	@Override
	protected void setMaximumProgress(int value) {
		progressBar.setMaximum(value);
	}

	/**
	 * Increments the progress bar by value.
	 */
	@Override
	protected void incrementProgress(int value) {
		progressBar.setValue(progressBar.getValue()+value);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		window.repaint();
	}

}
