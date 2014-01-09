/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Dashboard.java
 * Version : 1.0.0
 * Author : Zack Urben
 * Contact : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * This is Dashboard GUI for the Reinvestor, when run in GUI mode.
 * 
 * Support:
 * Motivation BTC @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
 * Cex.io Referral @ https://cex.io/r/0/kannibal3/0/
 * Cryptsy Trade Key @ e5447842f0b6605ad45ced133b4cdd5135a4838c
 * Other donations accepted via email request.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JScrollPane;
import javax.swing.JButton;

public class Dashboard {
	protected JFrame FRAME_DASHBOARD;
	protected JTextField DISPLAY_USERNAME;
	protected JTextField DISPLAY_STATUS;
	protected JTextField INPUT_RESERVE_BTC;
	protected JTextField INPUT_RESERVE_NMC;
	protected JTextField INPUT_MAX_BTC;
	protected JTextField INPUT_MAX_NMC;
	protected JTextField INPUT_MIN_BTC;
	protected JTextField INPUT_MIN_NMC;
	protected JTextField DISPLAY_API_CALLS;
	protected JTextField DISPLAY_ORDERS;
	protected JTextField DISPLAY_CANCELED;
	protected JTextField DISPLAY_PENDING;
	protected JTextField DISPLAY_START_TIME;
	protected JPanel PANEL;
	protected JPanel TAB_SETTINGS;
	protected JPanel TAB_INFO;
	protected JPanel TAB_LOG;
	protected JTabbedPane PANEL_TAB;
	protected JLabel LABEL_USERNAME;
	protected JLabel LABEL_STATUS;
	protected JLabel LABEL_COINS;
	protected JLabel LABEL_MAX;
	protected JLabel LABEL_MIN;
	protected JLabel LABEL_RESERVE;
	protected JLabel LABEL_BALANCE;
	protected JLabel LABEL_API_CALLS;
	protected JLabel LABEL_ORDERS;
	protected JLabel LABEL_CANCELED;
	protected JLabel LABEL_PENDING;
	protected JLabel LABEL_START_TIME;
	protected JToggleButton BUTTON_TOGGLE_REINVESTOR;
	protected JCheckBox CHECKBOX_BTC;
	protected JCheckBox CHECKBOX_NMC;
	protected JTextPane DISPLAY_BALANCE;
	protected JTextArea DISPLAY_LOG;
	protected JPanel TAB_ABOUT;
	protected JTextPane TEXTPANE_ABOUT;
	protected JTextPane TEXTPANE_BTC;
	protected JTextPane TEXTPANE_CEX;
	protected JTextPane TEXTPANE_CRYPTSY;
	protected JScrollPane SCROLLPANE;
	protected JButton BUTTON_SAVE;
	protected Reinvestor user;

	/**
	 * Create the application.
	 */
	public Dashboard(String user, String apiKey, String apiSecret) {
		initialize();
		this.FRAME_DASHBOARD.setVisible(true);
		this.user = new Reinvestor(user, apiKey, apiSecret, this);
		this.user.start();
		loadSettings();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		FRAME_DASHBOARD = new JFrame();
		FRAME_DASHBOARD.setTitle("Cex.io Reinvestor - By Zack Urben");
		FRAME_DASHBOARD.setBounds(100, 100, 600, 400);
		FRAME_DASHBOARD.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FRAME_DASHBOARD.getContentPane().setLayout(null);

		PANEL = new JPanel();
		PANEL.setBounds(6, 6, 588, 366);
		PANEL.setLayout(null);
		FRAME_DASHBOARD.getContentPane().add(PANEL);

		LABEL_USERNAME = new JLabel("Username");
		LABEL_USERNAME.setHorizontalAlignment(SwingConstants.RIGHT);
		LABEL_USERNAME.setBounds(6, 12, 75, 16);
		PANEL.add(LABEL_USERNAME);

		DISPLAY_USERNAME = new JTextField();
		DISPLAY_USERNAME.setEditable(false);
		DISPLAY_USERNAME.setBounds(93, 6, 115, 28);
		DISPLAY_USERNAME.setColumns(10);
		PANEL.add(DISPLAY_USERNAME);

		BUTTON_TOGGLE_REINVESTOR = new JToggleButton("Toggle Reinvestor");
		BUTTON_TOGGLE_REINVESTOR.setBounds(377, 7, 205, 29);
		BUTTON_TOGGLE_REINVESTOR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (BUTTON_TOGGLE_REINVESTOR.isSelected()) {
					// update all values in obj from gui
					updateSettings();
					user.saveSettings();
					user.addReinvestorThread();
				} else {
					// button is deselected
					// stop reinvestment thread
					user.reinvest.interrupt();
					user.reinvest.stop = true;

					try {
						user.reinvest.join();
					} catch (InterruptedException e) {
						// Thread.currentThread().interrupt();
						// e.printStackTrace();
						user.out("Error 0x3.");
						user.log("error", "Error 0x3:\n" + e.getMessage());
					}

					DISPLAY_STATUS.setText("Idle");
					DISPLAY_LOG.append("["
							+ new Date(System.currentTimeMillis()).toString()
							+ "] Idle.\n");
				}
			}
		});
		PANEL.add(BUTTON_TOGGLE_REINVESTOR);

		LABEL_STATUS = new JLabel("Status:");
		LABEL_STATUS.setBounds(220, 12, 48, 16);
		PANEL.add(LABEL_STATUS);

		DISPLAY_STATUS = new JTextField();
		DISPLAY_STATUS.setEditable(false);
		DISPLAY_STATUS.setBounds(265, 6, 100, 28);
		DISPLAY_STATUS.setColumns(10);
		PANEL.add(DISPLAY_STATUS);

		PANEL_TAB = new JTabbedPane(JTabbedPane.TOP);
		PANEL_TAB.setBounds(0, 40, 588, 326);
		PANEL.add(PANEL_TAB);

		TAB_SETTINGS = new JPanel();
		TAB_SETTINGS.setLayout(null);
		PANEL_TAB.addTab("Settings", null, TAB_SETTINGS, null);

		LABEL_COINS = new JLabel("Coins");
		LABEL_COINS
				.setToolTipText("Select which coins to enable reinvestment with.");
		LABEL_COINS.setBounds(6, 6, 70, 16);
		TAB_SETTINGS.add(LABEL_COINS);

		CHECKBOX_BTC = new JCheckBox("BTC");
		CHECKBOX_BTC.setSelected(true);
		CHECKBOX_BTC.setBounds(6, 34, 70, 23);
		TAB_SETTINGS.add(CHECKBOX_BTC);

		CHECKBOX_NMC = new JCheckBox("NMC");
		CHECKBOX_NMC.setSelected(true);
		CHECKBOX_NMC.setBounds(6, 69, 70, 23);
		TAB_SETTINGS.add(CHECKBOX_NMC);

		LABEL_RESERVE = new JLabel("Reserve");
		LABEL_RESERVE.setToolTipText("Set an amount to reserve from trades.");
		LABEL_RESERVE.setBounds(88, 6, 61, 16);
		TAB_SETTINGS.add(LABEL_RESERVE);

		INPUT_RESERVE_BTC = new JTextField();
		INPUT_RESERVE_BTC.setText("0.00000000");
		INPUT_RESERVE_BTC.setBounds(88, 32, 134, 28);
		INPUT_RESERVE_BTC.setColumns(10);
		TAB_SETTINGS.add(INPUT_RESERVE_BTC);

		INPUT_RESERVE_NMC = new JTextField();
		INPUT_RESERVE_NMC.setText("0.00000000");
		INPUT_RESERVE_NMC.setBounds(88, 67, 134, 28);
		INPUT_RESERVE_NMC.setColumns(10);
		TAB_SETTINGS.add(INPUT_RESERVE_NMC);

		INPUT_MAX_BTC = new JTextField();
		INPUT_MAX_BTC.setText("0.00000000");
		INPUT_MAX_BTC.setBounds(244, 32, 134, 28);
		INPUT_MAX_BTC.setColumns(10);
		TAB_SETTINGS.add(INPUT_MAX_BTC);

		INPUT_MAX_NMC = new JTextField();
		INPUT_MAX_NMC.setText("0.00000000");
		INPUT_MAX_NMC.setBounds(244, 67, 134, 28);
		INPUT_MAX_NMC.setColumns(10);
		TAB_SETTINGS.add(INPUT_MAX_NMC);

		LABEL_MAX = new JLabel("Maximum");
		LABEL_MAX
				.setToolTipText("Set the maximum value you would like to pay for 1 GHS/COIN.");
		LABEL_MAX.setBounds(244, 6, 100, 16);
		TAB_SETTINGS.add(LABEL_MAX);

		LABEL_MIN = new JLabel("Minimum");
		LABEL_MIN
				.setToolTipText("Set the minimum value you would like to pay for 1 GHS/COIN.");
		LABEL_MIN.setBounds(400, 6, 61, 16);
		TAB_SETTINGS.add(LABEL_MIN);

		INPUT_MIN_BTC = new JTextField();
		INPUT_MIN_BTC.setText("0.00000000");
		INPUT_MIN_BTC.setBounds(400, 32, 134, 28);
		INPUT_MIN_BTC.setColumns(10);
		TAB_SETTINGS.add(INPUT_MIN_BTC);

		INPUT_MIN_NMC = new JTextField();
		INPUT_MIN_NMC.setText("0.00000000");
		INPUT_MIN_NMC.setBounds(400, 67, 134, 28);
		INPUT_MIN_NMC.setColumns(10);
		TAB_SETTINGS.add(INPUT_MIN_NMC);

		BUTTON_SAVE = new JButton("Save Settings");
		BUTTON_SAVE.setBounds(6, 245, 134, 29);
		BUTTON_SAVE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateSettings();
				user.saveSettings();
			}
		});
		TAB_SETTINGS.add(BUTTON_SAVE);

		TAB_INFO = new JPanel();
		TAB_INFO.setLayout(null);
		PANEL_TAB.addTab("Information", null, TAB_INFO, null);

		DISPLAY_BALANCE = new JTextPane();
		DISPLAY_BALANCE.setBackground(SystemColor.window);
		DISPLAY_BALANCE.setFont(new Font("Monospaced", Font.PLAIN, 13));
		DISPLAY_BALANCE.setEditable(false);
		DISPLAY_BALANCE.setBounds(6, 34, 256, 120);
		TAB_INFO.add(DISPLAY_BALANCE);

		LABEL_BALANCE = new JLabel("Balance");
		LABEL_BALANCE.setBounds(6, 6, 61, 16);
		TAB_INFO.add(LABEL_BALANCE);

		LABEL_API_CALLS = new JLabel("API Calls");
		LABEL_API_CALLS.setBounds(274, 12, 61, 16);
		TAB_INFO.add(LABEL_API_CALLS);

		DISPLAY_API_CALLS = new JTextField();
		DISPLAY_API_CALLS.setHorizontalAlignment(SwingConstants.LEFT);
		DISPLAY_API_CALLS.setEditable(false);
		DISPLAY_API_CALLS.setBounds(347, 6, 214, 28);
		DISPLAY_API_CALLS.setColumns(10);
		TAB_INFO.add(DISPLAY_API_CALLS);

		LABEL_ORDERS = new JLabel("Orders");
		LABEL_ORDERS.setBounds(274, 92, 61, 16);
		TAB_INFO.add(LABEL_ORDERS);

		DISPLAY_ORDERS = new JTextField();
		DISPLAY_ORDERS.setHorizontalAlignment(SwingConstants.LEFT);
		DISPLAY_ORDERS.setEditable(false);
		DISPLAY_ORDERS.setBounds(347, 86, 214, 28);
		DISPLAY_ORDERS.setColumns(10);
		TAB_INFO.add(DISPLAY_ORDERS);

		DISPLAY_CANCELED = new JTextField();
		DISPLAY_CANCELED.setHorizontalAlignment(SwingConstants.LEFT);
		DISPLAY_CANCELED.setEditable(false);
		DISPLAY_CANCELED.setBounds(347, 126, 214, 28);
		DISPLAY_CANCELED.setColumns(10);
		TAB_INFO.add(DISPLAY_CANCELED);

		LABEL_CANCELED = new JLabel("Canceled");
		LABEL_CANCELED.setBounds(274, 132, 61, 16);
		TAB_INFO.add(LABEL_CANCELED);

		DISPLAY_PENDING = new JTextField();
		DISPLAY_PENDING.setHorizontalAlignment(SwingConstants.LEFT);
		DISPLAY_PENDING.setEditable(false);
		DISPLAY_PENDING.setBounds(347, 166, 214, 28);
		DISPLAY_PENDING.setColumns(10);
		TAB_INFO.add(DISPLAY_PENDING);

		LABEL_PENDING = new JLabel("Pending");
		LABEL_PENDING.setBounds(274, 172, 61, 16);
		TAB_INFO.add(LABEL_PENDING);

		DISPLAY_START_TIME = new JTextField();
		DISPLAY_START_TIME.setHorizontalAlignment(SwingConstants.LEFT);
		DISPLAY_START_TIME.setEditable(false);
		DISPLAY_START_TIME.setBounds(347, 46, 214, 28);
		DISPLAY_START_TIME.setColumns(10);
		TAB_INFO.add(DISPLAY_START_TIME);

		LABEL_START_TIME = new JLabel("Start");
		LABEL_START_TIME.setBounds(274, 52, 61, 16);
		TAB_INFO.add(LABEL_START_TIME);

		TAB_LOG = new JPanel();
		TAB_LOG.setLayout(null);
		PANEL_TAB.addTab("Log", null, TAB_LOG, null);

		SCROLLPANE = new JScrollPane();
		SCROLLPANE.setBounds(0, 0, 567, 280);
		TAB_LOG.add(SCROLLPANE);

		DISPLAY_LOG = new JTextArea();
		SCROLLPANE.setViewportView(DISPLAY_LOG);
		DISPLAY_LOG.setFont(new Font("Monospaced", Font.PLAIN, 12));
		DISPLAY_LOG.setWrapStyleWord(true);
		DISPLAY_LOG.setLineWrap(true);
		DISPLAY_LOG.setEditable(false);

		TAB_ABOUT = new JPanel();
		TAB_ABOUT.setLayout(null);
		PANEL_TAB.addTab("About", null, TAB_ABOUT, null);

		TEXTPANE_ABOUT = new JTextPane();
		TEXTPANE_ABOUT.setEditable(false);
		TEXTPANE_ABOUT.setBackground(SystemColor.window);
		TEXTPANE_ABOUT.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		TEXTPANE_ABOUT
				.setText("This program was created to automate profit reinvestment from the Cex.io cloud mining platform. This is freeware distributed under the MIT open-source license. If this program has helped you at all, please donate to motivate me to continue with development. All feature/update suggestions are welcome on the projects Github page (https://github.com/zackurben/cex.io-reinvestor).");
		TEXTPANE_ABOUT.setBounds(6, 6, 555, 90);
		TAB_ABOUT.add(TEXTPANE_ABOUT);

		TEXTPANE_BTC = new JTextPane();
		TEXTPANE_BTC.setEditable(false);
		TEXTPANE_BTC.setBackground(SystemColor.window);
		TEXTPANE_BTC.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		TEXTPANE_BTC
				.setText("Motivation BTC @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ");
		TEXTPANE_BTC.setBounds(6, 108, 555, 18);
		TAB_ABOUT.add(TEXTPANE_BTC);

		TEXTPANE_CEX = new JTextPane();
		TEXTPANE_CEX.setEditable(false);
		TEXTPANE_CEX.setBackground(SystemColor.window);
		TEXTPANE_CEX.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		TEXTPANE_CEX
				.setText("Cex.io Referral @ https://cex.io/r/0/kannibal3/0/");
		TEXTPANE_CEX.setBounds(6, 138, 555, 18);
		TAB_ABOUT.add(TEXTPANE_CEX);

		TEXTPANE_CRYPTSY = new JTextPane();
		TEXTPANE_CRYPTSY.setEditable(false);
		TEXTPANE_CRYPTSY.setBackground(SystemColor.window);
		TEXTPANE_CRYPTSY
				.setText("Cryptsy Trade   @ e5447842f0b6605ad45ced133b4cdd5135a4838c");
		TEXTPANE_CRYPTSY.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		TEXTPANE_CRYPTSY.setBounds(6, 168, 555, 18);
		TAB_ABOUT.add(TEXTPANE_CRYPTSY);
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
				String temp[] = new String[11];

				// ignore first 3 inputs
				// username,apiKey,apiSecret
				for (int a = 0; a < temp.length; a++) {
					temp[a] = input.next();
				}

				this.CHECKBOX_BTC.setSelected(Boolean.valueOf(temp[3]));
				this.INPUT_RESERVE_BTC.setText(temp[4]);
				this.INPUT_MAX_BTC.setText(temp[5]);
				this.INPUT_MIN_BTC.setText(temp[6]);
				this.CHECKBOX_NMC.setSelected(Boolean.valueOf(temp[7]));
				this.INPUT_RESERVE_NMC.setText(temp[8]);
				this.INPUT_MAX_NMC.setText(temp[9]);
				this.INPUT_MIN_NMC.setText(temp[10]);
				this.user.out("Settings loaded successfully!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Write settings from GUI to user Reinvestor Object.
	 */
	private void updateSettings() {
		this.user.BTC.active = CHECKBOX_BTC.isEnabled();
		this.user.BTC.reserve = Float.valueOf(INPUT_RESERVE_BTC.getText());
		this.user.BTC.max = Float.valueOf(INPUT_MAX_BTC.getText());
		this.user.BTC.min = Float.valueOf(INPUT_MIN_BTC.getText());

		this.user.NMC.active = CHECKBOX_NMC.isEnabled();
		this.user.NMC.reserve = Float.valueOf(INPUT_RESERVE_NMC.getText());
		this.user.NMC.max = Float.valueOf(INPUT_MAX_NMC.getText());
		this.user.NMC.min = Float.valueOf(INPUT_MIN_NMC.getText());
	}
}
