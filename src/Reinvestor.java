/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Reinvestor.java
 * Version  : 1.1.0
 * Author   : Zack Urben
 * Contact  : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * Support:
 * Motivation BTC       @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
 * Cex.io Referral      @ https://cex.io/r/0/kannibal3/0/
 * Scrypt Referral      @ http://scrypt.cc?ref=baaah
 * Cryptsy Trade Key    @ e5447842f0b6605ad45ced133b4cdd5135a4838c
 * Other donations accepted via email request.
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import utils.Config;
import zackurben.cex.data.Balance;
import zackurben.cex.data.Coin;
import zackurben.cex.data.Order;
import zackurben.cex.data.Ticker;
import zackurben.cex.data.Balance.Currency;

import com.google.gson.Gson;

public class Reinvestor extends CexAPI {
    protected final int MAX_API_CALLS = 400;
    protected ArrayList<Order> pending;
    protected long startTime, lastTime;
    protected int apiCalls;
    protected Balance balance;
    protected Coin BTC, NMC;
    protected InputThread input;
    protected ReinvestThread reinvest;
    protected Dashboard gui;
    protected boolean done, debug = false;
    private Config cfg =null;

    /**
     * Reinvestor constructor for Terminal/bash/cmd mode.
     * 
     * @param user
     *        Cex.io Username.
     * @param key
     *        Cex.io API Key.
     * @param secret
     *        Cex.io API Secret.
     */
    public Reinvestor(String user, String key, String secret) {
        super(user, key, secret);
        this.pending = new ArrayList<Order>();
        this.startTime = System.currentTimeMillis();
        this.lastTime = this.startTime;
        this.apiCalls = 0;
        this.BTC = new Coin(true, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, "GHS/BTC");
        this.NMC = new Coin(true, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, "GHS/NMC");
        this.done = false;
        this.input = new InputThread(this);
        this.gui = null;
    }

    /**
     * Reinvestor constructor for GUI mode.
     * 
     * @param user
     *        Cex.io Username.
     * @param key
     *        Cex.io API Key.
     * @param secret
     *        Cex.io API Secret.
     */
    public Reinvestor(String user, String key, String secret, Dashboard input) {
        super(user, key, secret);
        this.pending = new ArrayList<Order>();
        this.startTime = System.currentTimeMillis();
        this.lastTime = this.startTime;
        this.apiCalls = 0;
        this.BTC = new Coin(true, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, "GHS/BTC");
        this.NMC = new Coin(true, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, "GHS/NMC");
        this.done = false;
        this.input = new InputThread(this);
        this.gui = input;

        // update gui stuff
        if (this.gui != null) {
            this.gui.DISPLAY_API_CALLS.setText("0");
            this.gui.DISPLAY_CANCELED.setText("0");
            this.gui.DISPLAY_ORDERS.setText("0");
            this.gui.DISPLAY_PENDING.setText("0");
            this.gui.DISPLAY_START_TIME.setText(new Date(this.startTime)
                .toString());
            this.gui.NUM_START_TIME = this.startTime;
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
     * @param output
     *        String to output to display to console/terminal.
     */
    public void out(String output) {
        out(output, "");
    }

    /**
     * Output formatted string to console/terminal, with a prepended string.
     * 
     * @param output
     *        String to output to console/terminal.
     * @param prepend
     *        String to prepend to formatted string being displayed to
     *        console/terminal.
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
     * @param file
     *        Name of file to write contents to.
     * @param input
     *        Contents to write to file.
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
     * @param input
     *        Float to format.
     * @return String representation of the float, up to 8 decimal places,
     *         rounded down.
     */
    public String formatNumber(BigDecimal input) {
        input = input.setScale(8, RoundingMode.DOWN);
        return input.toPlainString();
    }

    /**
     * Format the account balance.
     * 
     * @param input
     *        JSON data of balance, from API call.
     * @return String representation of the formatted account balance.
     */
    public String formatBalance(String input) {
        String output = "";
        this.balance = new Gson().fromJson(input, Balance.class);

        String label[] = new String[] { "BTC ", "LTC ", "DOGE", "FTC ", "AUR ",
            "NMC ", "IXC ", "DVC ", "GHS " };
        Balance.Currency currency[] = new Balance.Currency[] {
            this.balance.BTC, this.balance.LTC, this.balance.DOGE,
            this.balance.FTC, this.balance.AUR, this.balance.NMC,
            this.balance.IXC, this.balance.DVC, this.balance.GHS };

        int formatted = 0;
        output += "Pair Available   Order";
        for (int a = 0; a < currency.length; a++) {
            output += "\n" + label[a] + " ";

            if (currency[a] != null
                && currency[a].available != null
                && currency[a].available.compareTo(BigDecimal.ZERO.setScale(8)) == 1) {
                output += formatNumber(currency[a].available);
                formatted++;
            } else {
                output += "0.00000000";
            }
            output += "  ";
            if (currency[a] != null
                && currency[a].orders != null
                && currency[a].orders.compareTo(BigDecimal.ZERO.setScale(8)) == 1) {
                output += formatNumber(currency[a].orders);
                formatted++;
            } else {
                output += "0.00000000";
            }
        }
        
        if(formatted == 0) {
            output = "There was a problem formatting the account balances, please wait until the next API call for an update.";
        }

        return output;
    }

    /**
     * Format the ticker data.
     * 
     * @param input
     *        JSON data of ticker, from API call.
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
            + formatNumber(data.ask) + "  " + formatNumber(data.volume) + "\n";
    }

    /**
     * Format the input time for a duration.
     * 
     * @param time
     *        Time subtract initial calculation.
     * @return String representation of formatted time passed since @param time
     */
    public String formatDuration(long time) {
        time = time - this.startTime;

        int h = (int) ((time / 1000) / 3600);
        int m = (int) (((time / 1000) / 60) % 60);
        int s = (int) ((time / 1000) % 60);

        DecimalFormat df = new DecimalFormat("####00");

        return df.format(h) + ":" + df.format(m) + ":" + df.format(s);
    }

    /**
     * Wrapper function for API calls, to ensure we do not exceed the API limit.
     * 
     * @param function
     *        Name of API function to invoke.
     * @param parameters
     *        String array of parameters required for API function.
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
                        Float.valueOf(parameters[2]), Float
                            .valueOf(parameters[3]));
                } else if (function == "open_orders") {
                    output = this.open_orders(parameters[0]);
                } else if (function == "cancel_order") {
                    output = this.cancel_order(Integer.valueOf(parameters[0]));
                } else if (function == "trade_history") {
                    output = this.trade_history(parameters[0], Integer
                        .valueOf(parameters[1]));
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
            long temp = System.currentTimeMillis();
            this.gui.DISPLAY_LAST_ACTIVITY.setText(new Date(temp).toString());
            this.gui.NUM_LAST_ACTIVITY = temp;
            this.gui.DISPLAY_DURATION.setText(this.formatDuration(temp));
        }

        return output;
    }

    /**
     * Load config
     */
    public void loadSettings() {
    	cfg = Config.getInstance();
        this.BTC.active = Boolean.valueOf(cfg.isBTCActive());
        this.BTC.reserve = cfg.getBTCReserve() ;
        this.BTC.max = cfg.getBTCMax();
        this.BTC.min = cfg.getBTCMin();
        this.NMC.active = Boolean.valueOf(cfg.isNMCActive());
        this.NMC.reserve = cfg.getNMCReserve();
        this.NMC.max = cfg.getNMCMax();
        this.NMC.min =cfg.getNMCMin();
        this.out("Settings loaded successfully!");
    }

    /**
     */
    public void saveSettings() {
    	cfg = Config.getInstance();
    	
    	
    	
    	
    	cfg.setUsername(this.username);
    	cfg.setAPIKey(this.apiKey);
    	cfg.setAPISecret(this.apiSecret);
    	
    	cfg.setBTCActive( this.BTC.active );
    	cfg.setBTCReserve(this.BTC.reserve);
    	cfg.setBTCMax(this.BTC.max);
    	cfg.setBTCMin(this.BTC.min);
    	
    	cfg.setNMCActive(this.NMC.active);
    	cfg.setNMCReserve(this.NMC.reserve);
    	cfg.setNMCMax(this.NMC.max);
    	cfg.setNMCMin(this.NMC.min);
 }

    /**
     * Thread wrapper to process user input, in a separate thread.
     */
    class InputThread extends Thread {
        protected Reinvestor user;

        /**
         * InputThread constructor, with reference to parent Reinvestor Object.
         * 
         * @param input
         *        Parent Reinvestor Object.
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
                            + this.user.formatBalance(this.user.execute(
                                "balance", new String[] {})), "\n");
                    } else if (temp.compareToIgnoreCase("1") == 0) {
                        out("\n"
                            + this.user.formatTicker(this.user.execute(
                                "ticker", (new String[] { "GHS/BTC" }))), "\n");
                    } else if (temp.compareToIgnoreCase("2") == 0) {
                        out("\n"
                            + this.user.formatTicker(this.user.execute(
                                "ticker", (new String[] { "GHS/NMC" }))), "\n");
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

                    // may need to yeild for terminal/bash/cmd mode
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
                + (Math.round(System.currentTimeMillis() - this.user.startTime) / 60000)
                + " minutes!", "\n");
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
         * @param input
         *        Parent Reinvestor Object.
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
         * requested.
         */
        public void run() {
            boolean trade_btc = false, trade_nmc = false;
            while (!this.user.done && !this.stop) {
                if (this.user.debug) {
                    this.user.out("[DBG] ReinvestThread:"
                        + Thread.currentThread().getId());
                }

                try {
                    this.user.balance = new Gson().fromJson(this.user.execute(
                        "balance", new String[] {}), Balance.class);

                    if (this.user.debug) {
                        this.user.out("[DBG] Determing trades..");
                    }

                    // active, balance != null, reserve < available
                    // active, pending
                    trade_btc = ((this.user.BTC.active)
                        && (this.user.balance != null) && (this.user.BTC.reserve
                        .compareTo(this.user.balance.BTC.available) <= -1))
                        || ((this.user.BTC.active) && (!this.user.pending
                            .isEmpty()));
                    trade_nmc = ((this.user.NMC.active)
                        && (this.user.balance != null) && (this.user.NMC.reserve
                        .compareTo(this.user.balance.NMC.available) <= -1))
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
                        if (this.user.debug) {
                            this.user
                                .out("[DBG] Waiting, insufficient funds to initiate new positions.");
                        }
                    }
                } catch (NullPointerException e) {
                    // error with api call
                    if (this.user.debug) {
                        e.printStackTrace();
                    }

                    this.user.nonce = Integer.valueOf((int) (System
                        .currentTimeMillis() / 1000));
                    out("Error 0x1: " + this.user.balance.toString());

                    StringWriter error = new StringWriter();
                    e.printStackTrace(new PrintWriter(error));
                    log("error", "Error 0x1:\n" + error.toString());
                } finally {
                    try {
                        if (this.user.debug) {
                            this.user.out("[DBG] Sleeping Reinvestor Thread.");
                        }

                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        if (this.user.debug) {
                            this.user
                                .out("[DBG] Sleeping Reinvestor Thread was interrupted.");
                        }
                    }
                }
            }
        }

        /**
         * Analyze the trade potential for a coin, with reference to Currency
         * and Coin Objects.
         * 
         * @param currency
         *        Parent Currency Object.
         * @param coin
         *        Parent Coin Object.
         */
        public void analyze(Currency currency, Coin coin) {
            // Determine if pending orders exist
            // and take appropriate action
            if (!this.user.pending.isEmpty()) {
                long temp = System.currentTimeMillis();

                if (this.user.debug) {
                    this.user
                        .out("[DBG] Analyzing the pending orders..! (Pending: "
                            + this.user.pending.size() + ", " + temp + ")");
                }

                for (int a = 0; a < this.user.pending.size(); a++) {
                    if (temp > (this.user.pending.get(a).time + 60000)) {
                        Order tempOrder = this.user.pending.get(a);

                        if (this.user.debug) {
                            this.user
                                .out("[DBG] Trying to cancel the pending order..!\n (Order: "
                                    + tempOrder.toString() + ")");
                        }

                        boolean canceled = Boolean.valueOf(this.user.execute(
                            "cancel_order", new String[] { String
                                .valueOf(tempOrder.id) }));

                        if (this.user.debug) {
                            this.user.out("[DBG] Canceled Status: " + canceled);
                        }

                        if (canceled) {
                            log("buy", "Pending order canceled:\n"
                                + tempOrder.toString());
                            out("Reinvestor: Canceled pending order (ID: "
                                + tempOrder.id + ", Terminated: "
                                + tempOrder.pending.toPlainString() + " GHS "
                                + tempOrder.price.toPlainString() + ")");
                            this.user.pending.remove(a);

                            // update gui display
                            if (this.user.gui != null) {
                                this.user.gui.DISPLAY_CANCELED
                                    .setText(String
                                        .valueOf(Integer
                                            .parseInt(this.user.gui.DISPLAY_CANCELED
                                                .getText()) + 1));
                            }
                        } else {
                            // error canceling order, completed already
                            if (this.user.debug) {
                                this.user
                                    .out("[DBG] Pending order has completed..!");
                            }

                            log("buy", "Pending order completed:\n"
                                + tempOrder.toString());
                            out("Reinvestor: Pending purchase order complete. (ID: "
                                + tempOrder.id
                                + ", Cost: "
                                + formatNumber(tempOrder.amount
                                    .multiply(tempOrder.price)) + ")");
                            this.user.pending.remove(a);

                            // update gui display
                            if (this.user.gui != null) {
                                this.user.gui.DISPLAY_CANCELED.setText(String
                                    .valueOf(Integer
                                        .parseInt(this.user.gui.DISPLAY_ORDERS
                                            .getText()) + 1));
                            }
                        }
                    } else {
                        if (this.user.debug) {
                            this.user.out("[DBG] Pending orders: "
                                + this.user.pending.toString());
                        }
                    }
                }

                // update gui display
                if (this.user.gui != null) {
                    this.user.gui.DISPLAY_PENDING.setText(String
                        .valueOf(this.user.pending.size()));
                }
            }

            this.user.balance = new Gson().fromJson(this.user.execute(
                "balance", new String[] {}), Balance.class);

            // Make purchases
            if (currency.available.compareTo(coin.reserve) == 1) {
                Ticker price = new Gson().fromJson(this.user.execute("ticker",
                    new String[] { coin.ticker }), Ticker.class);

                // if price range is within user specified limits: purchase
                if (((coin.max.compareTo(BigDecimal.ZERO) == 0) || (coin.max
                    .compareTo(price.last) == 1))
                    && ((coin.min.compareTo(BigDecimal.ZERO) == 0) || (price.last
                        .compareTo(coin.min)) == 1)) {
                    // calculate amount to buy
                    BigDecimal amt = (currency.available.subtract(coin.reserve))
                        .divide(price.last, 8, RoundingMode.DOWN);

                    // subtract 0.5% trading fee (accounting for the
                    // new fee increase)
                    BigDecimal fee = (amt.multiply(new BigDecimal("0.005"))
                        .multiply(price.last)).setScale(8, RoundingMode.UP);
                    amt = ((currency.available.subtract(coin.reserve))
                        .subtract(fee))
                        .divide(price.last, 8, RoundingMode.DOWN);

                    if (amt.compareTo(new BigDecimal(0.00000001)) == 1) {
                        Order order = new Gson().fromJson(this.user
                            .execute("place_order", new String[] { coin.ticker,
                                "buy", amt.toPlainString(),
                                price.last.toPlainString() }), Order.class);

                        // check if order contains pending values
                        if (order.error == "") {
                            if (order.pending.compareTo(BigDecimal.ZERO) == 0) {
                                log("buy", "Order complete:\n"
                                    + order.toString());
                                out("Reinvestor: Purchased "
                                    + formatNumber(order.amount)
                                    + " GHS @ "
                                    + formatNumber(order.price)
                                    + " "
                                    + "(Fee: "
                                    + formatNumber(fee)
                                    + ") "
                                    + coin.ticker
                                    + " (Cost: "
                                    + formatNumber(order.price
                                        .multiply(order.amount)) + ")");

                                // update gui display
                                if (this.user.gui != null) {
                                    this.user.gui.DISPLAY_ORDERS
                                        .setText(String
                                            .valueOf((Integer
                                                .parseInt(this.user.gui.DISPLAY_ORDERS
                                                    .getText()) + 1)));
                                }
                            } else {
                                // add to pending orders array list
                                this.user.pending.add(order);
                                log("buy", "Pending order:\n"
                                    + order.toString());
                                out("Reinvestor: Purchased "
                                    + formatNumber(order.amount) + " GHS @ "
                                    + formatNumber(order.price) + " "
                                    + "(Fee: " + formatNumber(fee) + ") "
                                    + coin.ticker + " (Pending: "
                                    + formatNumber(order.pending)
                                    + " GHS, ID: " + order.id + ")");

                                // update gui display
                                if (this.user.gui != null) {
                                    this.user.gui.DISPLAY_PENDING
                                        .setText(String
                                            .valueOf(this.user.pending.size()));
                                }
                            }
                        } else {
                            // tell purchase error @ coin value for x GHS
                            log("error", "Order error:\n" + order.toString());
                            out("Reinvestor: Order error: "
                                + order.error.toString());
                        }
                    }
                } else {
                    out("Reinvestor: The current price of a " + coin.ticker
                        + ", is outside your specified limits (Price: "
                        + price.last.toPlainString() + ", Range: "
                        + coin.min.toPlainString() + "-"
                        + coin.max.toPlainString() + ").");
                }
            } else if (currency.available.compareTo(coin.reserve) != 0) {
                out("Reinvestor: The coins available, is less than the allocated reserve limit (Coins: "
                    + this.user.formatNumber(currency.available)
                    + ", Reserve: "
                    + this.user.formatNumber(coin.reserve)
                    + ").");
            }
        }
    }
}
