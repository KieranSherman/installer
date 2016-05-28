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
	
	private JLabel header;
	
	private JButton endFinishButton;
	private JButton endCancelButton;
	
	private JButton finalConfirmButton;
	private JButton finalBackButton;
	
	private JButton nameConfirmButton;
	private JButton nameBackButton;
	
	private JButton directoryConfirmButton;
	private JButton directoryChangeButton;

	private JProgressBar progressBar;
	private JTextField progressField;
	private JTextField nameField;
	private JTextField finalField;
	
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
	
	private JPanel getDirectoryPanel() {
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

		directoryChangeButton = new JButton("change");
		directoryChangeButton.setForeground(lighter_blue);
		directoryChangeButton.setBackground(dark_gray);
		directoryChangeButton.setFont(tahoma);
		directoryChangeButton.setFocusable(false);
		directoryChangeButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		directoryChangeButton.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		directoryChangeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				if(fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
					directoryField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		
		directoryConfirmButton = new JButton("confirm");
		directoryConfirmButton.setForeground(light_gold);
		directoryConfirmButton.setBackground(dark_gray);
		directoryConfirmButton.setFont(tahoma);
		directoryConfirmButton.setFocusable(false);
		directoryConfirmButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		directoryConfirmButton.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		directoryConfirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directoryConfirmButton.setEnabled(false);
				directoryChangeButton.setEnabled(false);
				
				nameConfirmButton.setEnabled(true);
				nameBackButton.setEnabled(true);
				nameField.setEditable(true);
				nameField.setEnabled(true);
				
				extractionDir = directoryField.getText()+File.separator;
				textOpacity = 0.0f;
				status++;
			}
		});

		JPanel directoryButtons = new JPanel(new GridLayout(1, 2));
		directoryButtons.setOpaque(false);
		directoryButtons.add(directoryConfirmButton);
		directoryButtons.add(directoryChangeButton);
		
		JPanel directoryPanel = new JPanel(new BorderLayout());
		directoryPanel.setBackground(dark_gray);
		directoryPanel.setBorder(new EmptyBorder(0, 40, 10, 40));
		directoryPanel.add(directoryField, BorderLayout.CENTER);
		directoryPanel.add(directoryHeader, BorderLayout.NORTH);
		directoryPanel.add(directoryButtons, BorderLayout.SOUTH);
		
		return directoryPanel;
	}
	
	private JPanel getNamePanel() {
		JLabel nameHeader = new JLabel("folder name");
		nameHeader.setOpaque(false);
		nameHeader.setFont(tahoma);
		nameHeader.setForeground(light_gold);
		
		nameField = new JTextField("src");
		nameField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		nameField.setEnabled(false);
		nameField.setFont(tahoma.deriveFont(17f));
		nameField.setForeground(dark_gray);
		nameField.setBackground(lighter_blue);
		nameField.setMargin(new Insets(0, 10, 0, 10));
		nameField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
		nameField.setHorizontalAlignment(JTextField.CENTER);
		
		nameConfirmButton = new JButton("confirm");
		nameConfirmButton.setForeground(light_gold);
		nameConfirmButton.setBackground(dark_gray);
		nameConfirmButton.setFont(tahoma);
		nameConfirmButton.setFocusable(false);
		nameConfirmButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		nameConfirmButton.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		nameConfirmButton.setEnabled(false);
		nameConfirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nameConfirmButton.setEnabled(false);
				nameBackButton.setEnabled(false);
				nameField.setEnabled(false);
				
				finalConfirmButton.setEnabled(true);
				finalBackButton.setEnabled(true);
				
				extractionName = nameField.getText();
				finalField.setText(extractionDir+extractionName);
				finalField.setEnabled(true);
				textOpacity = 0.0f;
				status++;
			}
		});
		
		nameBackButton = new JButton("back");
		nameBackButton.setForeground(lighter_blue);
		nameBackButton.setBackground(dark_gray);
		nameBackButton.setFont(tahoma);
		nameBackButton.setFocusable(false);
		nameBackButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		nameBackButton.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		nameBackButton.setEnabled(false);
		nameBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nameBackButton.setEnabled(false);
				nameConfirmButton.setEnabled(false);
				nameField.setEnabled(false);
				
				directoryConfirmButton.setEnabled(true);
				directoryChangeButton.setEnabled(true);
				
				textOpacity = 0.0f;
				status--;
			}
		});
		
		JPanel nameButtons = new JPanel(new GridLayout(1, 2));
		nameButtons.setOpaque(false);
		nameButtons.add(nameConfirmButton);
		nameButtons.add(nameBackButton);
		
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBackground(dark_gray);
		namePanel.setBorder(new EmptyBorder(0, 40, 10, 40));
		namePanel.add(nameField, BorderLayout.CENTER);
		namePanel.add(nameHeader, BorderLayout.NORTH);
		namePanel.add(nameButtons, BorderLayout.SOUTH);
		
		return namePanel;
	}
	
	private JPanel getFinalPanel() {
		JLabel confirmHeader = new JLabel("final destination");
		confirmHeader.setOpaque(false);
		confirmHeader.setFont(tahoma);
		confirmHeader.setForeground(light_gold);
		
		finalField = new JTextField("tbd");
		finalField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		finalField.setEnabled(false);
		finalField.setFont(tahoma.deriveFont(17f));
		finalField.setForeground(dark_gray);
		finalField.setBackground(darker_blue);
		finalField.setMargin(new Insets(0, 10, 0, 10));
		finalField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
		finalField.setHorizontalAlignment(JTextField.CENTER);
		finalField.setHighlighter(null);
		
		finalConfirmButton = new JButton("confirm");
		finalConfirmButton.setForeground(light_gold);
		finalConfirmButton.setBackground(dark_gray);
		finalConfirmButton.setFont(tahoma);
		finalConfirmButton.setFocusable(false);
		finalConfirmButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		finalConfirmButton.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		finalConfirmButton.setEnabled(false);
		finalConfirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finalConfirmButton.setEnabled(false);
				finalField.setEnabled(false);
				
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
		
		finalBackButton = new JButton("back");
		finalBackButton.setForeground(lighter_blue);
		finalBackButton.setBackground(dark_gray);
		finalBackButton.setFont(tahoma);
		finalBackButton.setFocusable(false);
		finalBackButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		finalBackButton.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		finalBackButton.setEnabled(false);
		finalBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finalConfirmButton.setEnabled(false);
				finalBackButton.setEnabled(false);
				finalField.setEnabled(false);
				
				nameConfirmButton.setEnabled(true);
				nameBackButton.setEnabled(true);
				nameField.setEnabled(true);
				
				status--;
			}
		});
		
		JPanel finalButtons = new JPanel(new GridLayout(1, 2));
		finalButtons.setOpaque(false);
		finalButtons.add(finalConfirmButton);
		finalButtons.add(finalBackButton);
		
		JPanel confirmPanel = new JPanel(new BorderLayout());
		confirmPanel.setBackground(dark_gray);
		confirmPanel.setBorder(new EmptyBorder(0, 40, 10, 40));
		confirmPanel.add(finalField, BorderLayout.CENTER);
		confirmPanel.add(confirmHeader, BorderLayout.NORTH);
		confirmPanel.add(finalButtons, BorderLayout.SOUTH);
		
		return confirmPanel;
	}

	private JPanel getProgressPanel() {
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
		
		return progressPanel;
	}
	
	private JPanel getHeaderPanel() {
		header = new JLabel("INSTALLER");
		header.setOpaque(false);
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setVerticalAlignment(JLabel.CENTER);
		header.setBorder(new EmptyBorder(10, 10, 7, 10));
		header.setFont(tahoma.deriveFont(16f));
		header.setForeground(light_gold);
		
		JPanel panel = new JPanel();
		panel.setBackground(dark_gray);
		panel.add(header);
		
		return panel;
	}

	/**
	 * Displays the GUI.
	 */
	@Override
	protected void load() {
		endFinishButton = new JButton("finish");
		endFinishButton.setFont(tahoma);
		endFinishButton.setBackground(dark_gray);
		endFinishButton.setForeground(light_gold);
		endFinishButton.setFocusable(false);
		endFinishButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, gray));
		endFinishButton.setEnabled(false);
		endFinishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runtime.getRuntime().removeShutdownHook(shutdownHook);
				window.dispose();
				
				if(!installer.finish())
					JOptionPane.showMessageDialog(null, "Did not finish installation cleanly.\nCheck directory for .installation.");
					
				installer.quit(null);
			}
		});
		endFinishButton.setVisible(false);
		
		endCancelButton = new JButton("cancel");
		endCancelButton.setFont(tahoma);
		endCancelButton.setBackground(dark_gray);
		endCancelButton.setForeground(lighter_blue);
		endCancelButton.setFocusable(false);
		endCancelButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, lighter_blue));
		endCancelButton.addActionListener(new ActionListener() {
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
				
				if(status == 0) {
					str = "";
				} else if(status == 1) {
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
					endFinishButton.setBackground(new Color(dark_gray.getRed()-(int)(checkOpacity*100)/4,
							dark_gray.getBlue()-(int)(checkOpacity*100)/4, dark_gray.getGreen()-(int)(checkOpacity*100)/4+10));
					
					endCancelButton.setBackground(new Color(dark_gray.getRed()-(int)(checkOpacity*100)/4,
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
		panel.add(getDirectoryPanel());
		panel.add(getNamePanel());
		panel.add(getFinalPanel());
		panel.add(getProgressPanel());
		panel.setBorder(new EmptyBorder(10, 0, 0, 0));
		panel.setPreferredSize(new Dimension(700, 400));
		
		JPanel buttonPanel = new JPanel(null);
		buttonPanel.setOpaque(false);
		endCancelButton.setBounds(40, 10, 620, 40);
		buttonPanel.add(endCancelButton);
		buttonPanel.add(endFinishButton);
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
		super.window.add(getHeaderPanel(), BorderLayout.NORTH);
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
				endFinishButton.setEnabled(true);
				endCancelButton.setBounds(10, 13, 330, 40);
				endFinishButton.setBounds(360, 13, 330, 40);
				endFinishButton.setVisible(true);
				endFinishButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold));
				endCancelButton.setText("uninstall");
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
