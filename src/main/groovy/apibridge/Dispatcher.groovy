/*
 *  Dispatcher.groovy
 * 
 * Copyright(c) 2013 Witbooking.com. All Rights Reserved.
 * This software is the proprietary information of Witbooking.com
 * 
 */

package apibridge

import com.witbooking.middleware.beans.ServerInformationBeanRemote
import com.witbooking.middleware.exceptions.MiddlewareException
import com.witbooking.middleware.exceptions.RemoteServiceException
import grails.util.Environment
import org.apache.log4j.Logger

import javax.naming.Context
import javax.naming.InitialContext

/**
 * Insert description here
 *
 * @author Christian Delgado
 */
class Dispatcher {
    /**
     * Internal flow logger for ApplicationProperties
     */
    static final Logger logger = Logger.getLogger(Dispatcher.class);

    ServerInformationBeanRemote witBookerBean = null;
    //WitHotelBeanRemote witBookerBean = null;

    InitialContext initialContext = null;
//    def ejbWitHotelBean;

    private static Dispatcher _instance;

    private def newBean() {
        try {
            if (Environment.current == Environment.DEVELOPMENT) {
//                Properties ejbClientProperties = new Properties();
//                ejbClientProperties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
//                ejbClientProperties.put("remote.connections", "default");
//                ejbClientProperties.put("remote.connection.default.port", "8080");
//                ejbClientProperties.put("remote.connection.default.host", "localhost");
//                ejbClientProperties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
//                ejbClientProperties.put("remote.connection.default.username", "admin");
//                ejbClientProperties.put("remote.connection.default.password", "admin");
//                EJBClientContext.setSelector(new ConfigBasedEJBClientContextSelector(new PropertiesBasedEJBClientConfiguration(ejbClientProperties)));

                final Hashtable<String,Object> jndiProperties = new Hashtable<String,Object>();
                jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
                jndiProperties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
                jndiProperties.put("remote.connections", "default");
                jndiProperties.put("remote.connection.default.port", MiddlewareProperties.LIBRARY_PORT);
                jndiProperties.put("remote.connection.default.host", MiddlewareProperties.LIBRARY_SERVER);
                jndiProperties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
                jndiProperties.put("remote.connection.default.username", MiddlewareProperties.JBOSS_USER);
                jndiProperties.put("remote.connection.default.password", MiddlewareProperties.JBOSS_CREDENTIALS);
                jndiProperties.put("org.jboss.ejb.client.scoped.context", true);
            }

            Properties jndiProperties = new Properties();
            jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");

            final Context initialContext = new InitialContext(jndiProperties);
            def newBean = (ServerInformationBeanRemote)initialContext.lookup("ejb:witbooking-api-ear/witbooking-api-ejb/ServerInformationBean!"+ServerInformationBeanRemote.class.getName())
            return newBean
        } catch (Exception ex) {
//            ex.printStackTrace()
            logger.error("Error Dispatcher WitBookerV7:"+ex.getMessage())
            throw new RemoteServiceException(ex)
        }
    }

    private closeContext(){
        if (initialContext != null) {
            initialContext.close()
            initialContext=null
            witBookerBean=null
        }
    }

    def Dispatcher() {
        try {
            witBookerBean = newBean();
        } catch (RemoteServiceException ex) {
            closeContext()
        }
    }

    public static synchronized Dispatcher getDispatcher() {
        if (_instance == null) {
            _instance = new Dispatcher()
        }
        return _instance;
    }

    private testConnection(host, port) {
        Socket socket = null
        try {
            logger.debug("Testing Socket Request to " + host + ":" + port)
            socket = new Socket(host, port as Integer)
            socket.close()
            logger.debug "Connection successful."
            print "Connection successful."
            return true
        } catch (Exception ex) {
            // The remote host is not listening on this port
            logger.error("Server is not listening on port " + port + " of " + host)
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
            return false
        }
    }

    def executeService(Closure c, args) {
        if (witBookerBean == null) {
            try {
                witBookerBean = newBean();
            } catch (RemoteServiceException ex) {
                closeContext()
                throw ex
            }
        }
        try {
            return c.call(args)
        }
        catch (javax.ejb.EJBException ex) {
            logger.error("Error in Call: ["+c+"] - "+ex.getMessage())
//            logger.error(ex.getCausedByException().getMessage())
            try {
                witBookerBean = newBean();
                return c.call(args)
            } catch (RemoteServiceException exRemote) {
                closeContext()
                throw ex
            }catch (MiddlewareException midEx) {
                throw midEx
            }
        }catch (MiddlewareException midEx) {
            throw midEx
        }
    }

    def testFrontEndServices = {
        witBookerBean.testFrontEndServices()
    }


    def getAccommodations = {
        args -> witBookerBean.getAccommodations(args[0], args[1])
    }

    def getDiscounts = {
        args -> witBookerBean.getDiscounts(args[0], args[1])
    }

    def getServices = {
        args -> witBookerBean.getServices(args[0], args[1])
    }

    def getLocationPoints = {
        args -> witBookerBean.getLocationPoints(args[0], args[1])
    }

    def getReviews = {
        args -> witBookerBean.getReviews(args[0], args[1])
    }

    def getLanguages = {
        args -> witBookerBean.getLanguages(args[0])
    }

    def getCurrencies = {
        args -> witBookerBean.getCurrencies(args[0])
    }

    def getConfigurations = {
        args -> witBookerBean.getConfigurations(args[0])
    }


    def subscribeNewsletter = {
        args -> witBookerBean.subscribeNewsletter(args[0], args[1], args[2])
    }

    def getEstablishments = {
        args -> witBookerBean.getEstablishmentInfo(args[0],args[1])
    }

    def getEstablishment = {
        args -> witBookerBean.getWitBookerVisualRepresentation(args[0],args[1])
    }
    def getAri = {
        args -> witBookerBean.getARI(args[0],args[1],args[2],args[3],args[4],args[5],args[6])
    }

    def insertConversionQuery = {
        args -> witBookerBean.insertConversionQuery(args[0],args[1],args[2],args[3],args[4],args[5],
                args[6],args[7],args[8],args[9],args[10],args[11],args[12])
    }

    def insertReservation = {
        args -> witBookerBean.insertReservation(args[0],args[1])
    }

    def getCountriesMap = {
        args -> witBookerBean.getCountriesMap()
    }

    def getCurrencyConversionRate = {
        args -> witBookerBean.getCurrencyExchange(args[0])
    }

    def getPaymentForm = {
        args -> witBookerBean.generatePaymentsForm(args[0],args[1],args[2],args[3])
    }

    def saveTransactions = {
        args -> witBookerBean.saveTransactions(args[0],args[1],args[2],args[3],args[4])
    }

}
