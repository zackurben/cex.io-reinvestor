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
	private Properties pConfig;

	
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
			this.pConfig = new Properties();
			pConfig.load(file);
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		}
    	}
    	else {
    		this.pConfig = new Properties();
    	}
    }
    
    public Object clone() throws CloneNotSupportedException {
    	throw new CloneNotSupportedException(); 
    }
    
    
	public Boolean isBTCActive() {
		return Boolean.valueOf(pConfig.getProperty("BTC.active", "true"));
	}

	public Boolean isNMCActive() {
		return Boolean.valueOf(pConfig.getProperty("NMC.active", "true"));
	}

	public BigDecimal getBTCReserve() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("BTC.reserve", "0.0"))));
	}

	public BigDecimal getNMCReserve() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("NMC.reserve", "0.0"))));
	}

	public BigDecimal getBTCMax() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("BTC.max", "0.0"))));
	}


	public BigDecimal getNMCMax() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("NMC.max", "0.0"))));
	}

	
	public BigDecimal getBTCMin() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("BTC.min", "0.0"))));
	}

	
	public BigDecimal getNMCMin() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("NMC.min", "0.0"))));
	}

	
	public void setBTCActive(boolean active) {
		FileOutputStream out; 
		try {
			pConfig.setProperty("BTC.active", Boolean.valueOf(active).toString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }
	}

	public void setNMCActive(boolean active) {
		FileOutputStream out; 
		try {
			pConfig.setProperty("NMC.active", Boolean.valueOf(active).toString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }		
	}

	public void setBTCReserve(BigDecimal reserve) {
		FileOutputStream out; 
		try {
			pConfig.setProperty("BTC.reserve", reserve.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }		
	}


	public void setBTCMin(BigDecimal min) {
		FileOutputStream out; 

		try {
			pConfig.setProperty("BTC.min", min.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }
	}

	
	public void setBTCMax(BigDecimal max) {
		FileOutputStream out; 
		
		try {
			pConfig.setProperty("BTC.max", max.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }	
	}

	public void setNMCReserve(BigDecimal reserve) {
		FileOutputStream out; 
		try {
			pConfig.setProperty("NMC.reserve", reserve.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }	
	}

	public void setNMCMax(BigDecimal max) {
		FileOutputStream out; 
		try {
			pConfig.setProperty("NMC.max", max.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }
	}


	public void setNMCMin(BigDecimal min) {
		FileOutputStream out; 
		
		try {
			pConfig.setProperty("NMC.min", min.toPlainString());
			File f = new File("reinvestor.properties");
			out = new FileOutputStream( f );
			pConfig.store(out, "reinvestor.properties");
			}
		    catch (Exception e ) {
		        e.printStackTrace();
		    }		
	}
	
	
	public String getAPISecret() {
		return pConfig.getProperty("api_secret", "");
	}
	
	public void setAPISecret(String api_secret) {
		FileOutputStream out;
		try {
			pConfig.setProperty("api_secret", api_secret);
			File f = new File("reinvestor.properties");
			out = new FileOutputStream(f);
			pConfig.store(out, "reinvestor.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getAPIKey() {
		return pConfig.getProperty("api_key", "");
	}

	public void setAPIKey(String api_key) {
		FileOutputStream out;

		pConfig.setProperty("api_key", api_key);
		File f = new File("reinvestor.properties");
		try {
			out = new FileOutputStream(f);
			pConfig.store(out, "reinvestor.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public int getThreadSleep() {
		return Integer.valueOf(pConfig.getProperty("Thread.Sleep", "30"));
	}
	
	
	public BigDecimal getBTCMinOrder() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("BTC.min_order", "0.0"))));
	}

	public BigDecimal getNMCMinOrder() {
		return BigDecimal.valueOf((Double.valueOf(pConfig.getProperty("NMC.min_order", "0.0"))));
	}

	public String getUsername() {
		return pConfig.getProperty("username", "");
	}
	
	public void setUsername(String username) {
		FileOutputStream out;
		try {
			pConfig.setProperty("username", username);
			File f = new File("reinvestor.properties");
			out = new FileOutputStream(f);
			pConfig.store(out, "reinvestor.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
