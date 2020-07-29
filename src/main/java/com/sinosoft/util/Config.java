package com.sinosoft.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * @author
 * @version 1.0
 */
public class Config {
    private static Properties prop = new Properties();
    private static Logger log = Logger.getLogger("Rms");

    /**
     * 载入默认properties配置文件
     */
    static {
    	InputStream in = null;
    	ClassLoader cl = null;
		try {
			cl = Config.class.getClassLoader();
            in = cl.getResourceAsStream("application-dev.properties");
			//in = new BufferedInputStream (new FileInputStream("/config.properties"));
			prop.load(in);     ///加载属性列表
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }

    /**
     * @param propKey String
     * @return String
     */
    public static String getProperty(String propKey) {
        String propValue = prop.getProperty(
            propKey.trim());
        log.info(propKey + ":" + propValue);
        return propValue;
    }

}
