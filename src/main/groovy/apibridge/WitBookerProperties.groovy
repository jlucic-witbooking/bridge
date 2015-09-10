/*
 *  IntegrationProperties.groovy
 * 
 * Copyright(c) 2013 Witbooking.com. All Rights Reserved.
 * This software is the proprietary information of Witbooking.com
 * 
 */

package apibridge

/**
 *
 * @author Christian Delgado
 */
import org.apache.log4j.Logger;
import grails.util.Environment

class WitBookerProperties {

    /**
     * Internal flow logger for ApplicationProperties
     */
    static final Logger logger = Logger.getLogger(WitBookerProperties.class);

    /**
     * Configuration file Absolute Path
     */
    private static String CONFIG_FILE = "witbooking.properties";
    private static String CONFIG_FILE_DEVEL = "witbooking.properties";
    private static String CONFIG_FILE_TEST = "/var/www/vhosts/witbooking.com/private/configFiles/witbooking.properties";
    private static String CONFIG_FILE_PRODUC = "/var/www/vhosts/witbooking.com/private/configFiles/witbooking.properties";

    /**
     * Instance of WitBookerProperties for the singleton class
     */
    private static WitBookerProperties _instance = new WitBookerProperties();

    //Properties:

    /**
     * IP for the Middleware services
     */
    private static String MIDDLEWARE_IP;
    /**
     * File path for the external folder accesible
     */
    private static String CORBA_PORT;
    /**
     * JNDI name for the EJB service for WitHotel
     */
    private static String WITBOOKER_SERVICE_JNDI;


    private static String STATIC_ROOT_URL;
    private static String URL_WITBOOKER_V6;

    private static int SIPAY_TIMEOUT_INTERVAL=300;


    /**
     * Boolean that define is if this is Production environment
     */
    /**
     * Variable used for Throwing a unchecked Exception
     */
    private static final String STRING_NULL = null;


    private WitBookerProperties() {

        logger.error("Working Directory = " +System.getProperty("user.dir"));

        Environment.executeForCurrentEnvironment {
            development {
                CONFIG_FILE = CONFIG_FILE_DEVEL
                println 'This is the direction for development ' + CONFIG_FILE
            }
            luke {
                CONFIG_FILE = CONFIG_FILE_DEVEL
                println 'This is the direction for development ' + CONFIG_FILE
            }
            test {
                CONFIG_FILE = CONFIG_FILE_TEST
                println 'This is the direction for test ' + CONFIG_FILE
            }
            production {
                CONFIG_FILE = CONFIG_FILE_PRODUC
                println 'This is the direction for production ' + CONFIG_FILE

            }
        }
        logger.debug("Loading the properties file: " + CONFIG_FILE);
        try {
            this.configure();
        } catch (Exception e) {
            logger.error("Error loading the Properties File: " + e.getMessage());
            System.out.println("Error loading the Properties File: " + e.getMessage());
            //Throwing a unchecked Exception to stop the deployment
            STRING_NULL.trim();
        }
        logger.info("Properties file successfully loaded.");
    }

    public static WitBookerProperties getWitHotelProperties() {
        return _instance;
    }


    def configure() {

        Properties properties = new Properties();
        String value = null;
        String param = null;

        //-----------------------------------------------------------------
        // Opening configuration file
        //-----------------------------------------------------------------
        try {

            logger.debug("Loading the Property file for the WitHotel Application");
            File file = new File(CONFIG_FILE);
            System.out.println("Property file location: " + file.getAbsolutePath());
            logger.debug("Property file location: " + file.getAbsolutePath());
            // Loads the Properties file
            properties.load(new FileInputStream(CONFIG_FILE));

        } catch (Exception e) {
            System.out.println("Configuration file not found: " + e.getMessage());
            throw e;
            //return null;
        }
        try {

            //-----------------------------------------------------------------
            // Setting properties object from configuration file
            //-----------------------------------------------------------------
            param = "MIDDLEWARE_IP";
            if ((value = properties.getProperty(param)) == null) {
                throw new Exception(param + ": No encontrado en el archivo de configuracion");
            }
            MIDDLEWARE_IP = value.trim();
            logger.debug(" MIDDLEWARE_IP: " + MIDDLEWARE_IP);

            param = "CORBA_PORT";
            if ((value = properties.getProperty(param)) == null) {
                throw new Exception(param + ": No encontrado en el archivo de configuracion");
            }
            CORBA_PORT = value.trim();
            logger.debug(" CORBA_PORT: " + CORBA_PORT);

            param = "WITBOOKER_SERVICE_JNDI";
            if ((value = properties.getProperty(param)) == null) {
                throw new Exception(param + ": No encontrado en el archivo de configuracion");
            }
            WITBOOKER_SERVICE_JNDI = value.trim();
            logger.debug(" WITBOOKER_SERVICE_JNDI: " + WITBOOKER_SERVICE_JNDI);

            param = "STATIC_ROOT_URL";
            if ((value = properties.getProperty(param)) == null) {
                throw new Exception(param + ": No encontrado en el archivo de configuracion");
            }
            STATIC_ROOT_URL = value.trim();
            logger.debug(" STATIC_ROOT_URL: " + STATIC_ROOT_URL);

            param = "URL_WITBOOKER_V6";
            if ((value = properties.getProperty(param)) == null) {
                throw new Exception(param + ": No encontrado en el archivo de configuracion");
            }
            URL_WITBOOKER_V6 = value.trim();
            logger.debug(" URL_WITBOOKER_V6: " + URL_WITBOOKER_V6);

            param = "SIPAY_TIMEOUT_INTERVAL";
            if ((value = properties.getProperty(param)) == null) {
                throw new Exception(param + ": No encontrado en el archivo de configuracion");
            }
            try {
                SIPAY_TIMEOUT_INTERVAL = Integer.parseInt(value.trim());
            } catch (Exception ex) {
                logger.error("INVALID SIPAY TIMEOUT INTERVAL "+value)
            }

            logger.debug(" SIPAY_TIMEOUT_INTERVAL: " + SIPAY_TIMEOUT_INTERVAL);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
            e.printStackTrace();
        }
    }

    public static String getMIDDLEWARE_IP() {
        return MIDDLEWARE_IP;
    }

    public static String getCORBA_PORT() {
        return CORBA_PORT;
    }

    public static String getWITBOOKER_SERVICE_JNDI() {
        return WITBOOKER_SERVICE_JNDI;
    }

    static String getSTATIC_ROOT_URL() {
        return STATIC_ROOT_URL
    }

    static int getSIPAY_TIMEOUT_INTERVAL() {
        return SIPAY_TIMEOUT_INTERVAL
    }
}
