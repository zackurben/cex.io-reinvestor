/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Balance.java
 * Version  : 1.1.0
 * Author   : Zack Urben
 * Contact  : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * This is the Balance data type, used to parse JSON to a Java Object.
 * 
 * Support:
 * Motivation BTC       @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
 * Cex.io Referral      @ https://cex.io/r/0/kannibal3/0/
 * Scrypt Referral      @ http://scrypt.cc?ref=baaah
 * Cryptsy Trade Key    @ e5447842f0b6605ad45ced133b4cdd5135a4838c
 * Other donations accepted via email request.
 */

package zackurben.cex.data;

import java.math.BigDecimal;

public class Balance {
    public long timestamp;
    public String username;
    public Currency BTC, LTC, DOGE, FTC, AUR, NMC, IXC, DVC, GHS;
    public String error = "";
    

    /**
     * 
     * @return
     */
    public boolean isValid() {
    	boolean retval = false;
    	retval = this.BTC != null && this.NMC != null && this.BTC.available != null && this.NMC.available != null && this.username!= null ;
    	
    	Currency currency[] = new Currency[] { this.BTC, this.LTC,
                this.DOGE, this.FTC, this.AUR, this.NMC, this.IXC, this.DVC,
                this.GHS };

            for (Currency curr : currency) {
                retval = retval && curr != null;
            }    	
    	
    	return retval;
    }

    /**
     * Overide the default toString method to give basic object data dump.
     */
    public String toString() {
        if (error.compareToIgnoreCase("") == 0) {
            String output = "{" + this.timestamp + ":" + this.username;

            Currency currency[] = new Currency[] { this.BTC, this.LTC,
                this.DOGE, this.FTC, this.AUR, this.NMC, this.IXC, this.DVC,
                this.GHS };

            for (Currency curr : currency) {
                output += ":" + curr.toString();
            }

            output += "}";
            return output;
        } else {
            return "{error:" + this.error + "}";
        }
    }

    /**
     * Internal Currency class for parsing input JSON data to additional
     * objects.
     */
    public class Currency {
        public BigDecimal available = new BigDecimal("0.00000000");
        public BigDecimal orders = new BigDecimal("0.00000000");

        /**
         * Overide the default toString method to give basic object data dump.
         */
        public String toString() {
            String output = "[" + this.available.toPlainString();

            if (this.orders != null) {
                output += ":" + this.orders.toPlainString();
            } else {
                output += ":0.00000000";
            }
            output += "]";

            return output;
        }
    }
}
