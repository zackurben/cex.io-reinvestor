/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Cex.java
 * Version  : 1.0.1
 * Author   : Zack Urben
 * Contact  : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * Requirements:
 * This program requires a free API Key from Cex.io, which can be
 * obtained here: https://cex.io/trade/profile
 * 
 * This API Key requires the following permissions:
 * Account Balance, Place Order, Cancel Order, Open Order
 * 
 * This program requires the free Cex.io Java library, which can be obtained
 * here: https://github.com/zackurben/cex.io-api-java/archive/master.zip
 * 
 * 
 * 
 * This is the core driver for Reinvestment. This determines if the Reinvestor
 * is run is GUI or Terminal mode.
 * 
 * Support:
 * Motivation BTC       @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
 * Cex.io Referral      @ https://cex.io/r/0/kannibal3/0/
 * Scrypt Referral      @ http://scrypt.cc?ref=baaah
 * Cryptsy Trade Key    @ e5447842f0b6605ad45ced133b4cdd5135a4838c
 * Other donations accepted via email request.
 */

public class Cex {
    public static void main(String args[]) {
        if (args.length == 0) {
            // run reinvestor in GUi mode
            new Login();
        } else if (args[0] != "" && args[1] != "" && args[2] != "") {
            // run reinvestor in terminal/bash/cmd mode
            new Reinvestor(args[0], args[1], args[2]).start();
        } else {
            System.out
                .println("\nTrading requires a Username, API Key, and API Secret.\n"
                    + "Please visit: Cex.io/api, if you do not have an API Key and Secret.\n"
                    + "Proper use is: \"java Reinvestor Username API_Key API_Secret\"\n"
                    + "Credentials given:\n"
                    + "Username: "
                    + args[0]
                    + "\n"
                    + "API_Key: "
                    + args[1]
                    + "\n"
                    + "API_Secret: "
                    + args[2]
                    + "\n");
        }
    }
}
