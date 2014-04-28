#Cex.io Java Reinvestor
The Java source files and executable for the Cex.io profit reinvestment. This is an opensource project under
the MIT license and utilized the Google Gson project licensed under the Apache V2 License, which can be found
at: gson/LICENSE.txt.

## Contact
* Author    : Zack Urben
* Contact   : zackurben@gmail.com

### Support
If you would like to support the development of this project, please spread the word and donate!

* Motivation BTC    @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
* Cex.io referral   @ https://cex.io/r/0/kannibal3/0/
* Scrypt Referral   @ http://scrypt.cc?ref=baaah
* Cryptsy Trade Key @ e5447842f0b6605ad45ced133b4cdd5135a4838c
* Other donations accepted via email request!

### Features
1. GUI/CLI version.
2. BTC/NMC Selection.
3. Reserve amounts.
4. Max/Min buy amounts.
5. Load/Save settings from/to file.
6. Buy/Error logging.
7. And more?

### Images
![](https://raw2.github.com/zackurben/cex.io-reinvestor/master/screenshots/Login.png)
![](https://raw2.github.com/zackurben/cex.io-reinvestor/master/screenshots/Settings.png)
![](https://raw2.github.com/zackurben/cex.io-reinvestor/master/screenshots/Information.png)
![](https://raw2.github.com/zackurben/cex.io-reinvestor/master/screenshots/Log.png)
![](https://raw2.github.com/zackurben/cex.io-reinvestor/master/screenshots/About.png)

##How to use (GUI):
1. Download the [latest release](https://github.com/zackurben/cex.io-reinvestor/releases/).
2. Generate a Cex.io API key and API secret (https://cex.io/trade/profile).
     This key needs the following permissions, to enable full functionality:
  * Account Balance
  * Open Order
  * Place Order
  * Cancel Order 
3. Run the executable and login.
4. Set your desired settings, and Toggle Reinvestment.

##How to use (CLI):
1. Download the executable (https://github.com/zackurben/cex.io-reinvestor/releases/).
2. Generate a Cex.io API key and API secret (https://cex.io/trade/profile).
    This key needs the following permissions, to enable full functionality:
  * Account Balance
  * Open Order
  * Place Order
  * Cancel Order 
3. Run the executable and give it your API information, then follow the prompt.

```
java -jar Reinvestor_v1.0.jar username api_key api_secret
``` 

## Available Tabs
The following are the 'Tabs' available in the GUI, and their settings/definitions.

### Settings
This is the settings tab, you can configure your desired Reinvestment options currently supported buy
this program.

```
Note, all numbers are limited to 6 places left of the decimal point, and 8 places right of the
decimal, as specified in the Cex.io Terms of Service (https://cex.io/tos). These values are not
validated, incorrect values may lead to program failure.
```

#### Coin

```
This checkbox, enables or disables each respective coin for use in the reinvestment program.
```

#### Reserve

```
The Reserve sets the limit for the untradeable amount of each respective coin. Reinvestment
will utilize all available funds that exceed the reserve, but leave the reserve uninvested.
```

#### Maximum

```
The Maximum sets the upper limit for what you are willing to pay for 1 GHS/COIN. If the
current price for 1 GHS/COIN is greater than your specified Maximum, no action will be taken.
```

#### Minimum

```
The Minimum sets the lower limit for what you are willing to pay for 1 GHS/COIN. If the
current price for 1 GHS/COIN is lower than your specified Minimum, no action will be taken.
```

#### Save Settings

```
Write your settings to a text file named 'settings.txt'. If this file exists upon program
start, it will load your credentials and setting from it. Note, when the Reinvestment is
started, the settings will be automatically saved.
```

### Information
This tab is mostly statistics and debug information, but its interesting to see, so I left it in.

#### Balance

```
Your account balance for every available currency.
```

```
Note, the balance will not show up until Reinvestment starts.
```

#### Start

```
The timestamp from when the program was started.
```

#### Orders

```
The total number of orders placed by the Reinvestment program.
```

#### Canceled

```
The total number of orders canceled by the Reinvestment program, due to time expirations.
```

```
Note, an order will be canceled, if not completed within ~60 seconds. This keeps funds
available for reinvestment, if an order is placed during a price spike, and a new order will
be placed at the updated price.
```

#### Pending

```
The total number of current pending orders. An order will be removed from pending if
completed, or canceled due to time.
```

### Log
This tab is the Reinvestment program output. Current reinvestment actions are displayed in
this temporary log.

### About
This tab is information about the reinvestment program. It includes the methods in which to
support the continuation of this project.
