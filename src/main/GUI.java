package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import main.JarInstaller;

/**
 * Class shows a visual representation of a {@link JarInstaller}'s progress.
 * 
 * @author kieransherman
 *
 */
public class GUI {
	
	protected volatile JProgressBar progress;
	protected volatile JLabel info;
	protected JButton finish;
	protected JFrame window;
	protected Font verdana = new Font("Verdana", Font.PLAIN, 11);
	protected Thread shutdownHook;
	
	/**
	 * Creates a new GUI object with a shutdown hook.
	 */
	protected GUI(Thread shutdownHook) {
		this.shutdownHook = shutdownHook;
	}
	
	/**
	 * Displays a GUI representation of the progress.
	 */
	protected boolean display(JarInstaller obj) {
		progress = new JProgressBar();
		progress.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		progress.setStringPainted(true);
		progress.setFont(verdana);
		progress.setPreferredSize(new Dimension(Integer.MAX_VALUE, 35));
		progress.setBackground(new Color(40, 190, 230, 180));
		progress.setForeground(Color.BLACK);
		
		JLabel header = new JLabel("INSTALLER");
		header.setOpaque(true);
		header.setFont(verdana);
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setVerticalAlignment(JLabel.CENTER);
		header.setBackground(Color.WHITE);
		header.setForeground(Color.BLACK);
		header.setPreferredSize(new Dimension(Integer.MAX_VALUE, 20));

		info = new JLabel("STARTING INSTALLATION");
		info.setOpaque(true);
		info.setFont(verdana);
		info.setHorizontalAlignment(JLabel.CENTER);
		info.setVerticalAlignment(JLabel.CENTER);
		info.setBackground(Color.WHITE);
		info.setForeground(Color.BLACK);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(30, 30, 30));
		panel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 5, 10), BorderFactory.createLineBorder(Color.WHITE)));
		panel.add(info, BorderLayout.CENTER);
		panel.add(progress, BorderLayout.SOUTH);
		
		JButton cancel = new JButton("CANCEL");
		cancel.setPreferredSize(new Dimension(214, 30));
		cancel.setFont(verdana);
		cancel.setFocusable(false);
		cancel.setForeground(Color.WHITE);
		cancel.setBackground(Color.BLACK);
		cancel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		finish = new JButton("FINISH");
		finish.setPreferredSize(new Dimension(215, 30));
		finish.setFont(verdana);
		finish.setFocusable(false);
		finish.setForeground(Color.WHITE);
		finish.setBackground(Color.BLACK);
		finish.setEnabled(false);
		finish.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
		finish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runtime.getRuntime().removeShutdownHook(shutdownHook);
				window.dispose();
			}
		});
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
		buttonPanel.setBackground(new Color(30, 30, 30));
		buttonPanel.add(cancel, BorderLayout.WEST);
		buttonPanel.add(finish, BorderLayout.EAST);
		buttonPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 10, 10, 10), BorderFactory.createLineBorder(Color.WHITE)));
		
		window = new JFrame();
		window.setUndecorated(true);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setPreferredSize(new Dimension(450, 155));
		window.add(header, BorderLayout.NORTH);
		window.add(panel, BorderLayout.CENTER);
		window.add(buttonPanel, BorderLayout.SOUTH);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		return JOptionPane.showConfirmDialog(window, "Begin installation?", null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	/**
	 * Logs a line of text.
	 */
	protected synchronized void log(String line) {
		System.out.println(line);
		
		if(info != null)
			info.setText((line.length() > 55 ? line.substring(0, 53)+"..." : line).toUpperCase());
	}
	
	/**
	 * Disposes the window.
	 */
	protected void dispose() {
		window.dispose();
	}
	
}
