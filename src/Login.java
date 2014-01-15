/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Login.java
 * Version : 1.0.3
 * Author : Zack Urben
 * Contact : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * This is Login GUI for the Reinvestor, when run in GUI mode.
 * 
 * Support:
 * Motivation BTC @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
 * Cex.io Referral @ https://cex.io/r/0/kannibal3/0/
 * Cryptsy Trade Key @ e5447842f0b6605ad45ced133b4cdd5135a4838c
 * Other donations accepted via email request.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class Login {
	private JFrame FRAME_LOGIN;
	private JTextField INPUT_USERNAME;
	private JTextField INPUT_API_KEY;
	private JTextField INPUT_API_SECRET;
	private JPanel PANEL;
	private JLabel LABEL_USERNAME;
	private JLabel LABEL_API_KEY;
	private JLabel LABEL_API_SECRET;
	private JLabel LABEL_VERSION;
	private JLabel LABEL_VERSION_NUMBER;
	private JButton BUTTON_LOGIN;

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
		FRAME_LOGIN.setVisible(true);
		loadSettings();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		FRAME_LOGIN = new JFrame();
		FRAME_LOGIN.setTitle("Cex.io Reinvestor - By Zack Urben");
		FRAME_LOGIN.setBounds(100, 100, 450, 235);
		FRAME_LOGIN.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FRAME_LOGIN.getContentPane().setLayout(null);

		PANEL = new JPanel();
		PANEL.setBounds(6, 6, 438, 200);
		PANEL.setLayout(null);
		FRAME_LOGIN.getContentPane().add(PANEL);

		LABEL_USERNAME = new JLabel("Username");
		LABEL_USERNAME.setHorizontalAlignment(SwingConstants.RIGHT);
		LABEL_USERNAME.setBounds(6, 12, 100, 16);
		PANEL.add(LABEL_USERNAME);

		LABEL_API_KEY = new JLabel("API Key");
		LABEL_API_KEY.setHorizontalAlignment(SwingConstants.RIGHT);
		LABEL_API_KEY.setBounds(6, 52, 100, 16);
		PANEL.add(LABEL_API_KEY);

		LABEL_API_SECRET = new JLabel("API Secret");
		LABEL_API_SECRET.setHorizontalAlignment(SwingConstants.RIGHT);
		LABEL_API_SECRET.setBounds(6, 92, 100, 16);
		PANEL.add(LABEL_API_SECRET);

		LABEL_VERSION = new JLabel("Version");
		LABEL_VERSION.setEnabled(false);
		LABEL_VERSION.setBounds(312, 167, 47, 16);
		PANEL.add(LABEL_VERSION);

		LABEL_VERSION_NUMBER = new JLabel("1.0.3");
		LABEL_VERSION_NUMBER.setEnabled(false);
		LABEL_VERSION_NUMBER.setBounds(371, 167, 61, 16);
		PANEL.add(LABEL_VERSION_NUMBER);

		INPUT_USERNAME = new JTextField();
		INPUT_USERNAME.setHorizontalAlignment(SwingConstants.RIGHT);
		INPUT_USERNAME.setBounds(118, 6, 275, 28);
		INPUT_USERNAME.setColumns(10);
		PANEL.add(INPUT_USERNAME);

		INPUT_API_KEY = new JTextField();
		INPUT_API_KEY.setHorizontalAlignment(SwingConstants.RIGHT);
		INPUT_API_KEY.setBounds(118, 46, 275, 28);
		INPUT_API_KEY.setColumns(10);
		PANEL.add(INPUT_API_KEY);

		INPUT_API_SECRET = new JTextField();
		INPUT_API_SECRET.setHorizontalAlignment(SwingConstants.RIGHT);
		INPUT_API_SECRET.setBounds(118, 86, 275, 28);
		INPUT_API_SECRET.setColumns(10);
		PANEL.add(INPUT_API_SECRET);

		BUTTON_LOGIN = new JButton("Login");
		BUTTON_LOGIN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// spawn Dashboard GUI
				new Dashboard(INPUT_USERNAME.getText(),
						INPUT_API_KEY.getText(), INPUT_API_SECRET.getText());
				// remove login GUI
				FRAME_LOGIN.dispose();
			}
		});
		BUTTON_LOGIN.setBounds(118, 126, 117, 29);
		PANEL.add(BUTTON_LOGIN);
	}

	/**
	 * Load settings from 'settings.txt' file, if it exists.
	 * 
	 * settings.txt Example
	 * username,apiKey,apiSecret,btcActive,btcReserve,btcMax,btcMin,nmcActive,
	 * nmcReserve,nmcMax,nmcMin
	 */
	private void loadSettings() {
		File file = new File("settings.txt");
		if (file.exists() && file.isFile()) {
			try {
				Scanner input = new Scanner(file).useDelimiter(",");
				String temp[] = new String[3];

				for (int a = 0; a < temp.length; a++) {
					temp[a] = input.next();
				}

				this.INPUT_USERNAME.setText(temp[0]);
				this.INPUT_API_KEY.setText(temp[1]);
				this.INPUT_API_SECRET.setText(temp[2]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
