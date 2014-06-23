package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * 
 * @author quimicefa
 */
public class Config {
	
	private static Config INSTANCE = null;
	
    private String USERNAME;
    private String API_KEY;
    private String API_SECRET;
	private Boolean BTC_active;
	private Boolean NMC_active;
	private BigDecimal BTC_reserve;
	private BigDecimal NMC_reserve;
	private BigDecimal BTC_max;
	private BigDecimal NMC_max;
	private BigDecimal BTC_min;
	private BigDecimal NMC_min;
	private BigDecimal BTC_min_order;
	private BigDecimal NMC_min_order;

	private int threadSleep = 30; // 30 seconds by default between thread loops.
	
	private Properties p;

	
    public static Config getInstance() {
        if (INSTANCE == null) {
            synchronized(Config.class) {
                if (INSTANCE == null) { 
                    INSTANCE = new Config();
                }
            }
        }
        
        return INSTANCE;
    }
    
    private Config() {
    	loadConfig();
    }
    
    private void loadConfig() {
    	File f = new File ("reinvestor.properties");
    	if ( f.exists() && f.isFile()) {
        try {
			FileInputStream file = new FileInputStream(f);
			this.p = new Properties();
			p.load(file);
            this.USERNAME = p.getProperty("username", "");
			this.API_KEY = p.getProperty("api_key", "");
			this.API_SECRET = p.getProperty("api_secret", "");
            
            this.BTC_active = Boolean.valueOf(p.getProperty("BTC.active", "true"));
            this.BTC_reserve = BigDecimal.valueOf((Double.valueOf(p.getProperty("BTC.reserve", "0.0"))));
			this.BTC_max = BigDecimal.valueOf((Double.valueOf(p.getProperty("BTC.max", "0.0"))));
			this.BTC_min = BigDecimal.valueOf((Double.valueOf(p.getProperty("BTC.min", "0.0"))));
            this.NMC_active = Boolean.valueOf(p.getProperty("NMC.active", "true"));
            this.NMC_reserve = BigDecimal.valueOf((Double.valueOf(p.getProperty("NMC.reserve", "0.0"))));
			this.NMC_max = BigDecimal.valueOf((Double.valueOf(p.getProperty("NMC.max", "0.0"))));
			this.NMC_min = BigDecimal.valueOf((Double.valueOf(p.getProperty("NMC.min", "0.0"))));
            
			this.threadSleep = Integer.valueOf(p.getProperty("Thread.Sleep", "30"));
			
			this.BTC_min_order = BigDecimal.valueOf((Double.valueOf(p.getProperty("BTC.min_order", "0.0"))));
			this.NMC_min_order = BigDecimal.valueOf((Double.valueOf(p.getProperty("NMC.min_order", "0.0"))));
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		}
    	}
    }
    
    public Object clone() throws CloneNotSupportedException {
    	throw new CloneNotSupportedException(); 
    }
    
    
  
	public String getUsername() {
		return USERNAME;
	}


	public String getAPIKey() {
		return API_KEY;
	}


	public String getAPISecret() {
		return API_SECRET;
	}


	public Boolean isBTCActive() {
		return BTC_active;
	}

	public Boolean isNMCActive() {
		return NMC_active;
	}

	public BigDecimal getBTCReserve() {
		return BTC_reserve;
	}

	public BigDecimal getNMCReserve() {
		return NMC_reserve;
	}

	public BigDecimal getBTCMax() {
		return BTC_max;
	}

	public BigDecimal getNMCMax() {
		return NMC_max;
	}

	public BigDecimal getBTCMin() {
		return BTC_min;
	}

	public BigDecimal getNMCMin() {
		return NMC_min;
	}

	public void setBTCActive(boolean active) {
		FileOutputStream out; 
		this.BTC_active = active;
		
		try {
			p.setProperty("BTC_active", Boolean.valueOf(active).toString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }
	}

	public void setNMCActive(boolean active) {
		FileOutputStream out; 
		this.NMC_active = active;
		
		try {
			p.setProperty("NMC_active", Boolean.valueOf(active).toString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }		
	}

	public void setBTCReserve(BigDecimal reserve) {
		FileOutputStream out; 
		this.BTC_reserve = reserve;
		
		try {
			p.setProperty("BTC_reserve", reserve.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }		
		
	}

	public void setBTCMin(BigDecimal min) {
		FileOutputStream out; 
		this.BTC_min = min;

		try {
			p.setProperty("BTC_min", min.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }
	}

	public void setBTCMax(BigDecimal max) {
		FileOutputStream out; 
		this.BTC_max = max;
		
		try {
			p.setProperty("BTC_max", max.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }	
	}

	public void setNMCReserve(BigDecimal reserve) {
		FileOutputStream out; 
		this.NMC_reserve = reserve;
		try {
			p.setProperty("NMC_reserve", reserve.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }	
	}

	public void setNMCMax(BigDecimal max) {
		FileOutputStream out; 
		this.NMC_max = max;
		
		try {
			p.setProperty("NMC_max", max.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }		
	}

	public void setNMCMin(BigDecimal min) {
		FileOutputStream out; 
		this.NMC_min = min;
		
		try {
			p.setProperty("NMC_min", min.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			p.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }		
	}
	
	public void setAPISecret(String api_secret) {
		API_SECRET = api_secret;
		FileOutputStream out;

		try {
			p.setProperty("api_secret", api_secret);
			File f = new File("reinvestor.properties");
			out = new FileOutputStream(f);
			p.store(out, "reinvestor.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setAPIKey(String api_key) {
		API_KEY = api_key;
		FileOutputStream out;
		p.setProperty("api_key", api_key);
		File f = new File("reinvestor.properties");
		try {
			out = new FileOutputStream(f);
			p.store(out, "reinvestor.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUsername(String username) {
		USERNAME = username;
		FileOutputStream out;

		try {
			p.setProperty("username", username);
			File f = new File("reinvestor.properties");
			out = new FileOutputStream(f);
			p.store(out, "reinvestor.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getThreadSleep() {
		return this.threadSleep;
	}
	
	public BigDecimal getBTCMinOrder() {
		return BTC_min_order;
	}

	public BigDecimal getNMCMinOrder() {
		return NMC_min_order;
	}

	

}
