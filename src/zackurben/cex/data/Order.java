/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Order.java
 * Version  : 1.0.4
 * Author   : Zack Urben
 * Contact  : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * This is the Order data type, used to parse JSON to a Java Object.
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
import java.math.RoundingMode;

public class Order {
    public int id;
    public long time;
    public BigDecimal pending = new BigDecimal("0.00000000"),
        amount = new BigDecimal("0.00000000"), price = new BigDecimal(
            "0.00000000");
    public String type, error = "";

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
     * Overide the default toString method to give basic object data dump.
     */
    public String toString() {
        if (error.compareToIgnoreCase("") == 0) {
            return "{" + this.type + ":" + this.id + ":" + this.time + ":"
                + formatNumber(this.pending) + ":" + formatNumber(this.amount)
                + ":" + formatNumber(this.price) + "}";
        } else {
            return "{error:" + this.error + "}";
        }
    }
}
