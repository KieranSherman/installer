package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import components.JarInstaller.InstallType;

/**
 * Class models a graphical installer.
 * 
 * @author kieransherman
 *
 */
public class GraphicalUI extends JarInstallerUI {
	
	private Thread shutdownHook;
	
	private JButton finishButton;
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
		progressBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		progressBar.setStringPainted(true);
		progressBar.setFont(tahoma);
		progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 35));
		progressBar.setBackground(Color.WHITE);
		progressBar.setForeground(Color.BLACK);
		progressBar.setEnabled(false);
		
		progressField = new JTextField("0%");
		progressField.setHorizontalAlignment(JTextField.CENTER);
		progressField.setForeground(Color.WHITE);
		progressField.setBackground(dark_gray);
		progressField.setFont(tahoma);
		progressField.setFocusable(false);
		progressField.setEditable(false);
		progressField.setHighlighter(null);
		progressField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		progressField.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.WHITE));
		progressField.setEnabled(false);
		
		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.setBackground(dark_gray);
		progressPanel.setBorder(new EmptyBorder(0, 70, 10, 70));
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
			}
		});
		
		JPanel confirmPanel = new JPanel(new BorderLayout());
		confirmPanel.setBackground(dark_gray);
		confirmPanel.setBorder(new EmptyBorder(0, 70, 10, 70));
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
			}
		});
		
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBackground(dark_gray);
		namePanel.setBorder(new EmptyBorder(0, 70, 10, 70));
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
		directoryField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
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
				nameConfirm.setEnabled(true);
				nameField.setEditable(true);
				nameField.selectAll();
				
				extractionDir = directoryField.getText()+File.separator;
			}
		});

		JPanel directoryPanel = new JPanel(new BorderLayout());
		directoryPanel.setBackground(dark_gray);
		directoryPanel.setBorder(new EmptyBorder(0, 70, 10, 70));
		directoryPanel.add(directoryField, BorderLayout.CENTER);
		directoryPanel.add(directoryHeader, BorderLayout.NORTH);
		directoryPanel.add(directoryConfirm, BorderLayout.SOUTH);

		finishButton = new JButton("finish");
		finishButton.setFont(tahoma);
		finishButton.setBackground(dark_gray);
		finishButton.setForeground(light_gold);
		finishButton.setFocusable(false);
		finishButton.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(30, 30, 30, 30),
				BorderFactory.createMatteBorder(1, 1, 1, 1, gray)));
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

		JButton cancelButton = new JButton("cancel");
		cancelButton.setFont(tahoma);
		cancelButton.setBackground(dark_gray);
		cancelButton.setForeground(lighter_blue);
		cancelButton.setFocusable(false);
		cancelButton.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(30, 30, 30, 30),
				BorderFactory.createMatteBorder(1, 1, 1, 1, lighter_blue)));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JPanel endPanel = new JPanel(new GridLayout(1, 2));
		endPanel.add(cancelButton);
		endPanel.add(finishButton);
		
		JPanel panel = new JPanel(new GridLayout(5, 1));
		panel.setBackground(gray);
		panel.add(directoryPanel);
		panel.add(namePanel);
		panel.add(confirmPanel);
		panel.add(progressPanel);
		panel.add(endPanel);
		
		super.window = new JFrame("");
		super.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.window.setLayout(new BorderLayout());
		super.window.setPreferredSize(new Dimension(700, 500));
		super.window.pack();
		super.window.setLocationRelativeTo(null);
		super.window.add(header, BorderLayout.NORTH);
		super.window.add(panel, BorderLayout.CENTER);
		super.window.setResizable(false);
		super.window.setVisible(true);
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
		progressField.setForeground(light_gold);
		finishButton.setEnabled(true);
		finishButton.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(30, 30, 30, 30),
				BorderFactory.createMatteBorder(1, 1, 1, 1, light_gold)));
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

}
