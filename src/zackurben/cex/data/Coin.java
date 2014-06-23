/**
 * This project is licensed under the terms of the MIT license, you can read
 * more in LICENSE.txt; this project utilized the Google GSON library, licensed
 * under the Apache V2 License, which can be found at: gson/LICENSE.txt
 * 
 * Coin.java
 * Version  : 1.0.4
 * Author   : Zack Urben
 * Contact  : zackurben@gmail.com
 * Creation : 12/31/13
 * 
 * This is the Coin data type, used to parse JSON to a Java Object.
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

public class Coin {
    public boolean active;
    public BigDecimal reserve = new BigDecimal("0.00000000"),
        max = new BigDecimal("0.00000000"), min = new BigDecimal(
	        "0.00000000"), min_order = new BigDecimal("0.00000000");
    public String ticker;

    /**
     * Default Coin constructor, used to edit reinvestment options.
     * 
     * @param active
     *        Determine if the coin is enabled for reinvestment.
     * @param reserve
     *        The amount to with-hold from reinvestment.
     * @param max
     *        The maximum amount allowed to pay for 1 GHS/COIN
     * @param min
     *        The minimum amount allowed to pay for 1 GHS/COIN
     * @param ticker
     *        The pair ticker for Cex.io
     */
    public Coin(Boolean active, BigDecimal reserve, BigDecimal max,
        BigDecimal min, BigDecimal min_order, String ticker) {
        this.active = active;
        this.reserve = reserve;
        this.max = max;
        this.min = min;
        this.ticker = ticker;
        this.min_order = min_order;
    }

    /**
     * Overide the default toString method to give basic object data dump.
     */
    public String toString() {
        return "{" + this.active + ":" + this.reserve.toPlainString() + ":"
            + this.max.toPlainString() + ":" + this.min.toPlainString() + ":"
            + this.ticker + "}";
    }
}
