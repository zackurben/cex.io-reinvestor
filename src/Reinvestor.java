/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Reinvestor.java
 * Version : 1.0.1
 * Author : Zack Urben
 * Contact : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * Support:
 * Motivation BTC @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
 * Cex.io Referral @ https://cex.io/r/0/kannibal3/0/
 * Cryptsy Trade Key @ e5447842f0b6605ad45ced133b4cdd5135a4838c
 * Other donations accepted via email request.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import zackurben.cex.data.*;
import zackurben.cex.data.Balance.Currency;
import com.google.gson.Gson;

public class Reinvestor extends CexAPI {
	protected final int MAX_API_CALLS = 400;
	protected ArrayList<Order> pending;
	protected long startTime, lastTime;
	protected int apiCalls;
	protected Balance balance;
	protected Coin BTC, NMC;
	protected boolean done;
	protected InputThread input;
	protected ReinvestThread reinvest;
	protected Dashboard gui;
	protected boolean debug = false;

	/**
	 * Reinvestor constructor for Terminal/bash/cmd mode.
	 * 
	 * @param user (String) - Cex.io Username.
	 * @param key (String) - Cex.io API Key.
	 * @param secret (String) - Cex.io API Secret.
	 */
	public Reinvestor(String user, String key, String secret) {
		super(user, key, secret);
		this.pending = new ArrayList<Order>();
		this.startTime = System.currentTimeMillis();
		this.lastTime = this.startTime;
		this.apiCalls = 0;
		this.BTC = new Coin(true, 0f, 0f, 0f, "GHS/BTC");
		this.NMC = new Coin(true, 0f, 0f, 0f, "GHS/NMC");
		this.done = false;
		this.input = new InputThread(this);
		this.gui = null;
	}

	/**
	 * Reinvestor constructor for GUI mode.
	 * 
	 * @param user (String) - Cex.io Username.
	 * @param key (String) - Cex.io API Key.
	 * @param secret (String) - Cex.io API Secret.
	 */
	public Reinvestor(String user, String key, String secret, Dashboard input) {
		super(user, key, secret);
		this.pending = new ArrayList<Order>();
		this.startTime = System.currentTimeMillis();
		this.lastTime = this.startTime;
		this.apiCalls = 0;
		this.BTC = new Coin(true, 0f, 0f, 0f, "GHS/BTC");
		this.NMC = new Coin(true, 0f, 0f, 0f, "GHS/NMC");
		this.done = false;
		this.input = new InputThread(this);
		this.gui = input;

		// update gui stuff
		if (this.gui != null) {
			this.gui.DISPLAY_API_CALLS.setText("0");
			this.gui.DISPLAY_CANCELED.setText("0");
			this.gui.DISPLAY_ORDERS.setText("0");
			this.gui.DISPLAY_PENDING.setText("0");
			this.gui.DISPLAY_START_TIME.setText(new Date(System
					.currentTimeMillis()).toString());
			this.gui.DISPLAY_STATUS.setText("Idle");
			this.gui.DISPLAY_USERNAME.setText(user);
		} else {
			// serious error here, should never occur.
			out("Error 0x7.");
			log("error", "Error 0x7.");
		}
	}

	/**
	 * Add Reinvestor thread to program, with a reference to the invoked
	 * Reinvestor object.
	 */
	public void addReinvestorThread() {
		this.reinvest = new ReinvestThread(this);
		this.reinvest.start();

		// update gui display
		if (this.gui != null) {
			this.gui.DISPLAY_STATUS.setText("Reinvesting");
			this.gui.DISPLAY_LOG.append("["
					+ new Date(System.currentTimeMillis()).toString()
					+ "] Reinvesting.\n");
		}
	}

	/**
	 * Start the Input thread.
	 */
	public void start() {
		this.input.start();
	}

	/**
	 * Alias of: out(String, String);
	 * 
	 * @param output (String) - String to output to display to console/terminal.
	 */
	public void out(String output) {
		out(output, "");
	}

	/**
	 * Output formatted string to console/terminal, with a prepended string.
	 * 
	 * @param output (String) - String to output to console/terminal.
	 * @param prepend (String) - String to prepend to formatted string being
	 *        displayed to console/terminal.
	 */
	public void out(String output, String prepend) {
		String temp = prepend + "[" + new Date(System.currentTimeMillis())
				+ "] " + output + "\n";

		// determine how to display output, based on which
		// version of the Reinvestor is currently active.
		// (terminal/bash/cmd or GUI)
		if (this.gui != null) {
			this.gui.DISPLAY_LOG.append(temp);
		} else {
			System.out.print(temp);
		}
	}

	/**
	 * Write contents to a log, relative to executed script.
	 * 
	 * @param file (String) - Name of file to write contents to.
	 * @param input (String) - Contents to write to file.
	 */
	public void log(String file, String input) {
		PrintWriter write = null;
		try {
			write = new PrintWriter(new BufferedWriter(new FileWriter(file
					+ ".txt", true)));
			write.println(input);
			write.println();
		} catch (IOException e) {
			out("Error 0x6.");
			log("error", "Error 0x6:\n" + e.getMessage());
		} finally {
			if (write != null) {
				write.close();
			}
		}
	}

	/**
	 * Format the display of floats (removes scientific notation).
	 * 
	 * @param input (Float) - Float to format.
	 * @return String representation of the float, up to 8 decimal places,
	 *         rounded down.
	 */
	public String formatNumber(float input) {
		DecimalFormat format = new DecimalFormat("###0.00000000");
		format.setRoundingMode(RoundingMode.HALF_DOWN);
		format.setDecimalFormatSymbols(DecimalFormatSymbols
				.getInstance(Locale.US));
		return format.format(input);
	}

	/**
	 * Format the account balance.
	 * 
	 * @param input (String) - JSON data of balance, from API call.
	 * @return String representation of the formatted account balance.
	 */
	public String formatBalance(String input) {
		this.balance = new Gson().fromJson(input, Balance.class);

		return "Pair Available   Order" + "\nBTC  "
				+ formatNumber(balance.BTC.available) + "  "
				+ formatNumber(balance.BTC.orders) + "\nGHS  "
				+ formatNumber(balance.GHS.available) + "  "
				+ formatNumber(balance.GHS.orders) + "\nIXC  "
				+ formatNumber(balance.IXC.available) + "  "
				+ formatNumber(balance.IXC.orders) + "\nDVC  "
				+ formatNumber(balance.DVC.available) + "  "
				+ formatNumber(balance.DVC.orders) + "\nNMC  "
				+ formatNumber(balance.NMC.available) + "  "
				+ formatNumber(balance.NMC.orders) + "\n";
	}

	/**
	 * Format the ticker data.
	 * 
	 * @param input (String) - JSON data of ticker, from API call.
	 * @return String representation of formatted ticker data.
	 */
	public String formatTicker(String input) {
		Ticker data = new Gson().fromJson(input, Ticker.class);
		return "Last        High        Low         Bid         Ask         Volume\n"
				+ formatNumber(data.last)
				+ "  "
				+ formatNumber(data.high)
				+ "  "
				+ formatNumber(data.low)
				+ "  "
				+ formatNumber(data.bid)
				+ "  "
				+ formatNumber(data.ask)
				+ "  "
				+ formatNumber(data.volume) + "\n";
	}

	/**
	 * Wrapper function for API calls, to ensure we do not exceed the API limit.
	 * 
	 * @param function (String) - Name of API function to invoke.
	 * @param parameters (String Array) - String array of parameters required
	 *        for API function.
	 * @return JSON String from Cex.io API call.
	 */
	public String execute(String function, String parameters[]) {
		boolean done = false;
		String output = "";
		if (System.currentTimeMillis() > (this.lastTime + 600000)) {
			this.apiCalls = 0;
			this.lastTime = System.currentTimeMillis();
		}

		while (!done) {
			if (this.apiCalls < this.MAX_API_CALLS) {
				if (function == "balance") {
					output = this.balance();

					if (this.gui != null) {
						this.gui.DISPLAY_BALANCE.setText(formatBalance(output));
					}
				} else if (function == "ticker") {
					output = this.ticker(parameters[0]);
				} else if (function == "order_book") {
					output = this.order_book(parameters[0]);
				} else if (function == "place_order") {
					output = this.place_order(parameters[0], parameters[1],
							Float.valueOf(parameters[2]),
							Float.valueOf(parameters[3]));
				} else if (function == "open_orders") {
					output = this.open_orders(parameters[0]);
				} else if (function == "cancel_order") {
					output = this.cancel_order(Integer.valueOf(parameters[0]));
				} else if (function == "trade_history") {
					output = this.trade_history(parameters[0],
							Integer.valueOf(parameters[1]));
				}

				this.apiCalls++;
				done = true;
			} else {
				if (System.currentTimeMillis() > (this.lastTime + 600000)) {
					this.apiCalls = 0;
					this.lastTime = System.currentTimeMillis();
				} else {
					out("API Limit reached, waiting 30 seconds to try again.\n");

					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						out("Error 0x5." + e.getMessage());
						log("error", "Error 0x5:\n" + e.getMessage());
					}
				}
			}
		}

		// update gui display
		if (this.gui != null) {
			this.gui.DISPLAY_API_CALLS.setText(String.valueOf(this.apiCalls));
		}

		return output;
	}

	/**
	 * Load settings from 'settings.txt' file, if it exists.
	 * 
	 * settings.txt Example
	 * username,apiKey,apiSecret,btcActive,btcReserve,btcMax,btcMin,nmcActive,
	 * nmcReserve,nmcMax,nmcMin
	 */
	public void loadSettings() {
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

				this.BTC.active = Boolean.valueOf(temp[3]);
				this.BTC.reserve = Float.valueOf(temp[4]);
				this.BTC.max = Float.valueOf(temp[5]);
				this.BTC.min = Float.valueOf(temp[6]);
				this.NMC.active = Boolean.valueOf(temp[7]);
				this.NMC.reserve = Float.valueOf(temp[8]);
				this.NMC.max = Float.valueOf(temp[9]);
				this.NMC.min = Float.valueOf(temp[10]);
				this.out("Settings loaded successfully!");
			} catch (FileNotFoundException e) {
				this.out("Error 0xB.");
				this.log("error", "Error 0xB:\n" + e.getMessage());
			}
		}
	}

	/**
	 * Write user settings to a file, 'settings.txt', which will load the last
	 * settings upon next program start-up.
	 */
	public void saveSettings() {
		PrintWriter write = null;
		try {
			write = new PrintWriter(new BufferedWriter(new FileWriter(
					"settings.txt", false)));
			String temp = this.username + "," + this.apiKey + ","
					+ this.apiSecret + "," + this.BTC.active + ","
					+ this.formatNumber(this.BTC.reserve) + ","
					+ this.formatNumber(this.BTC.max) + ","
					+ this.formatNumber(this.BTC.min) + "," + this.NMC.active
					+ "," + this.formatNumber(this.NMC.reserve) + ","
					+ this.formatNumber(this.NMC.max) + ","
					+ this.formatNumber(this.NMC.min);
			write.write(temp);
			this.out("Settings saved successfully!");
		} catch (IOException e) {
			this.out("Error 0xA.");
			this.log("error", "Error 0xA:\n" + e.getMessage());
		} finally {
			if (write != null) {
				write.close();
			}
		}
	}

	/**
	 * Thread wrapper to process user input, in a separate thread.
	 */
	class InputThread extends Thread {
		protected Reinvestor user;

		/**
		 * InputThread constructor, with reference to parent Reinvestor Object.
		 * 
		 * @param input (Reinvestor) - Parent Reinvestor Object.
		 */
		public InputThread(Reinvestor input) {
			this.user = input;

			if (this.user.gui != null) {
				this.setPriority(Thread.MIN_PRIORITY);
			} else {
				this.setPriority(Thread.MAX_PRIORITY);
			}
		}

		/**
		 * Display input controls for user, if not running in GUI mode.
		 */
		public void prompt() {
			if (this.user.gui == null) {
				out("\n[B]alance | [1] Display 'GHS/BTC' | [2] Display 'GHS/NMC' | [E]xit\n"
						+ "[R]einvest | [S]top Reinvest | [L]oad Settings | [W]rite Settings");
			}
		}

		/**
		 * Invoke the InputThread to run until program exit is requested.
		 */
		public void run() {
			Scanner input = new Scanner(System.in);
			String temp = null;
			while (!this.user.done) {
				if (this.user.debug) {
					this.user.out("[DBG] InputThread:"
							+ Thread.currentThread().getId());
				}

				this.prompt();
				try {
					temp = input.nextLine().substring(0, 1);
					if (temp.compareToIgnoreCase("b") == 0) {
						out("\n"
								+ this.user.formatBalance("\n"
										+ this.user.execute("balance",
												new String[] {})), "\n");
					} else if (temp.compareToIgnoreCase("1") == 0) {
						out("\n"
								+ this.user
										.formatTicker(this.user.execute(
												"ticker",
												(new String[] { "GHS/BTC" }))),
								"\n");
					} else if (temp.compareToIgnoreCase("2") == 0) {
						out("\n"
								+ this.user
										.formatTicker(this.user.execute(
												"ticker",
												(new String[] { "GHS/NMC" }))),
								"\n");
					} else if (temp.compareToIgnoreCase("e") == 0) {
						this.user.done = true;
					} else if (temp.compareToIgnoreCase("r") == 0) {
						this.user.addReinvestorThread();
					} else if (temp.compareToIgnoreCase("s") == 0) {
						this.user.reinvest.interrupt();
						this.user.reinvest.stop = true;
						this.user.reinvest.join();
					} else if (temp.compareToIgnoreCase("l") == 0) {
						this.user.loadSettings();
					} else if (temp.compareToIgnoreCase("w") == 0) {
						this.user.saveSettings();
					}

					// may need for terminal/bash/cmd mode
					// Thread.yield();
					// Thread.currentThread().interrupt();
					Thread.sleep(500);
				} catch (IndexOutOfBoundsException e) {
					// no input was typed before hitting enter ignore the
					// indexoutofbounds and retry with fresh buffer
					out("Error 0x9.");
					log("error", "Error 0x9:\n" + e.getMessage());
				} catch (InterruptedException e) {
					out("Error 0x3: ");
					log("error", "Error 0x3:\n" + e.getMessage());
				}
			}

			// save last settings
			this.user.saveSettings();
			// tightguy spam
			out("\nThanks for using my Cex.io Reinvestment tool.\n"
					+ "Motivation BTC: 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ\n"
					+ "Reinvestment ran for "
					+ (Math.round(System.currentTimeMillis()
							- this.user.startTime) / 60000) + " minutes!", "\n");
		}
	}

	/**
	 * Thread wrapper to run Reinvestment, in a separate thread.
	 */
	class ReinvestThread extends Thread {
		protected Reinvestor user;
		protected boolean stop;

		/**
		 * ReinvestThread constructor, with reference to parent Reinvestor
		 * Object.
		 * 
		 * @param input (Reinvestor) - Parent Reinvestor Object.
		 */
		public ReinvestThread(Reinvestor input) {
			this.user = input;
			this.stop = false;

			if (this.user.gui != null) {
				this.setPriority(Thread.MAX_PRIORITY);
			} else {
				this.setPriority(Thread.NORM_PRIORITY);
			}
		}

		/**
		 * Invoke the ReinvestThread to run until the halt of reinvestment is
		 * requiested.
		 */
		public void run() {
			boolean trade_btc = false, trade_nmc = false;
			while (!this.user.done && !this.stop) {
				if (this.user.debug) {
					this.user.out("[DBG] ReinvestThread:"
							+ Thread.currentThread().getId());
				}

				try {
					this.user.balance = new Gson().fromJson(
							this.user.execute("balance", new String[] {}),
							Balance.class);

					if (this.user.debug) {
						this.user.out("[DBG] Determing trades..");
					}

					// active, balance != null, reserve < available
					// active, pending
					trade_btc = ((this.user.BTC.active)
							&& (this.user.balance != null) && (this.user.BTC.reserve < this.user.balance.BTC.available))
							|| ((this.user.BTC.active) && (!this.user.pending
									.isEmpty()));
					trade_nmc = ((this.user.NMC.active)
							&& (this.user.balance != null) && (this.user.NMC.reserve < this.user.balance.NMC.available))
							|| ((this.user.NMC.active) && (!this.user.pending
									.isEmpty()));

					if (trade_btc || trade_nmc) {
						if (this.user.debug) {
							this.user.out("[DBG] trade_btc:trade_nmc {"
									+ trade_btc + ":" + trade_nmc + "}");
						}

						if (trade_btc) {
							this.analyze(this.user.balance.BTC, this.user.BTC);
						}
						if (trade_nmc) {
							this.analyze(this.user.balance.NMC, this.user.NMC);
						}
					} else {
						// wait till next call, no trades available
						// remove due to spam?
						out("Reinvestor: Waiting, insufficient funds to initiate new positions.");
					}
				} catch (NullPointerException e) {
					// trade_btc and trade_nmc can trigger a null pointer, but
					// I'm not sure how atm.
					// I believe this is a problem with Gson, but cant confirm
					// due to the random nature of the problem.
					out("Error 0x1.");
					// + this.user.balance.toString());
					log("error", "Error 0x1:\n" + this.user.BTC.toString()
							+ "\n" + this.user.NMC.toString());
					// + this.user.balance.toString());
				} finally {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						// Reinvestment thread is stopped
						// Remove output, due to spam.
						out("Error 0x2.");
						log("error", "Error 0x2:\n" + e.getMessage());
					}
				}

				// remove due to unneeded.
				trade_btc = false;
				trade_nmc = false;
			}
		}

		/**
		 * Analyze the trade potential for a coin, with reference to Currency
		 * and Coin Objects.
		 * 
		 * @param currency (Currency) - Parent Currency Object.
		 * @param coin (Coin) - Parent Coin Object.
		 */
		public void analyze(Currency currency, Coin coin) {
			// Determine if pending orders exist
			// and take appropriate action
			if (!this.user.pending.isEmpty()) {
				long temp = System.currentTimeMillis();

				if (this.user.debug) {
					this.user.out("[DBG] Analyzing the pending orders..! ("
							+ this.user.pending.size() + ", " + temp + ")");
				}

				for (int a = 0; a < this.user.pending.size(); a++) {
					if (temp > (this.user.pending.get(a).time + 60000)) {
						Order tempOrder = this.user.pending.get(a);
						// if (tempOrder.pending == 0) {
						//
						// } else {
						// cancel order
						if (this.user.debug) {
							this.user
									.out("[DBG] Trying to cancel the pending order..!\n"
											+ tempOrder.toString());
						}

						// boolean canceled = new Gson().fromJson(this.user
						// .execute("cancel_order",
						// new String[] { String
						// .valueOf(tempOrder.id) }),
						// Boolean.class);

						boolean canceled = Boolean.valueOf(this.user.execute(
								"cancel_order",
								new String[] { String.valueOf(tempOrder.id) }));

						if (this.user.debug) {
							this.user.out("[DBG] Canceled: " + canceled);
						}

						if (canceled) {
							log("buy",
									"Pending order canceled:\n"
											+ tempOrder.toString());
							out("Reinvestor: Canceled pending order (ID: "
									+ tempOrder.id + ", Terminated: "
									+ formatNumber(tempOrder.pending) + " GHS "
									+ tempOrder.price + ")");
							this.user.pending.remove(a);
						} else {
							// error canceling order, completed already
							if (this.user.debug) {
								this.user
										.out("[DBG] Pending order has completed..!");
							}

							log("buy",
									"Pending order completed:\n"
											+ tempOrder.toString());
							out("Reinvestor: Purchase order complete; (ID: "
									+ tempOrder.id
									+ ", Cost: "
									+ formatNumber(tempOrder.amount
											* tempOrder.price) + ")");
							this.user.pending.remove(a);
						}

						// update gui display
						if (this.user.gui != null) {
							this.user.gui.DISPLAY_CANCELED
									.setText(String.valueOf(Integer
											.parseInt(this.user.gui.DISPLAY_CANCELED
													.getText()) + 1));
						}
						// }
					} else {
						if (this.user.debug) {
							this.user.out("[DBG] Pending orders: ("
									+ this.user.pending.toString() + ")");
						}
					}
				}

				// update gui display
				if (this.user.gui != null) {
					this.user.gui.DISPLAY_PENDING.setText(String
							.valueOf(this.user.pending.size()));
				}
			}

			this.user.balance = new Gson().fromJson(
					this.user.execute("balance", new String[] {}),
					Balance.class);

			out("Reinvestor: Current " + coin.ticker.split("/")[1]
					+ " balance: " + formatNumber(currency.available));

			// Make purchases
			if (currency.available > coin.reserve) {
				Ticker price;
				price = new Gson().fromJson(this.user.execute("ticker",
						new String[] { coin.ticker }), Ticker.class);

				// if price range is within user specified limits, initiate
				// purchase
				if (((coin.max == 0) || (coin.max >= price.last))
						&& ((coin.min == 0) || (price.last >= coin.min))) {
					// calculate amount to buy
					float calc = ((currency.available - coin.reserve) / price.last);
					float amt = Float.valueOf(formatNumber(calc));
					if (amt > 0.00000001) {
						Order order = new Gson().fromJson(
								this.user.execute(
										"place_order",
										new String[] { coin.ticker, "buy",
												String.valueOf(amt),
												String.valueOf(price.last) }),
								Order.class);

						// check if order contains pending values
						if (order.error == "") {
							if (order.pending == 0) {
								log("buy",
										"Order complete:\n" + order.toString());
								out("Reinvestor: Purchased "
										+ formatNumber(order.amount)
										+ " GHS @ "
										+ formatNumber(order.price)
										+ " "
										+ coin.ticker
										+ " (Cost: "
										+ formatNumber(order.price
												* order.amount) + ")");

								// update gui display
								if (this.user.gui != null) {
									this.user.gui.DISPLAY_ORDERS
											.setText(String.valueOf((Integer
													.parseInt(this.user.gui.DISPLAY_ORDERS
															.getText()) + 1)));
								}
							} else {
								// add to pending orders array list
								this.user.pending.add(order);
								log("buy",
										"Pending order:\n" + order.toString());
								out("Reinvestor: Purchased "
										+ formatNumber(order.amount)
										+ " GHS @ " + formatNumber(order.price)
										+ " " + coin.ticker + " (Pending: "
										+ formatNumber(order.pending)
										+ " GHS, ID: " + order.id + ")");

								// update gui display
								if (this.user.gui != null) {
									this.user.gui.DISPLAY_PENDING
											.setText(String
													.valueOf(this.user.pending
															.size()));
								}
							}
						} else {
							// tell purchase error @ coin value for x GHS
							log("error", "Order error:\n" + order.toString());
							out("Reinvestor: Order error: "
									+ order.error.toString());
						}
					} else {
						// remove due to spam?
						out("Reinvestor: "
								+ coin.ticker.split("/")[1]
								+ " Balance is too low to place the minimum order.");
					}
				} else {
					out("Reinvestor: The current price of a " + coin.ticker
							+ ", is outside your specified limits (Price: "
							+ price.last + ", Range: " + coin.min + "-"
							+ coin.max + ").");
				}
			} else {
				out("Reinvestor: The coins available, is less than the allocated reserve limit (Coins: "
						+ this.user.formatNumber(currency.available)
						+ ", Reserve: "
						+ this.user.formatNumber(coin.reserve)
						+ ").");
			}
		}
	}
}
