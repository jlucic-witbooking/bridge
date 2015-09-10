package apibridge

import com.witbooking.middleware.model.CurrencyExchange
import com.witbooking.middleware.model.Reservation
import com.witbooking.middleware.model.TransactionStatus
import com.witbooking.middleware.model.TransactionType

// import com.witbooking.withotel.resources.Dispatcher

class WithotelService {
    static transaction = false
    def cache
    private Dispatcher dispatcher = Dispatcher.getDispatcher()

    def insertConversionQuery(final String hotelTicker, final String clientIp, final Date checkInDate,
                              final Date checkOutDate, final Integer rooms, final int adults,
                              final int children, final int infants, final String language,
                              final boolean isSoldOut, final boolean isChain,
                              final String channelID,final String trackingID){
        dispatcher.executeService(dispatcher.insertConversionQuery, [ hotelTicker,   clientIp,   checkInDate,
                                                                      checkOutDate,   rooms,   adults,
                                                                      children,   infants,   language,
                                                                      isSoldOut,   isChain, channelID,trackingID ])
    }

    def insertReservation(final String hotelTicker, final Reservation reservation){
        dispatcher.executeService(dispatcher.insertReservation, [ hotelTicker,   reservation])
    }

    def saveTransactions(String hotelTicker, Reservation reservation, String ticket, TransactionType type, TransactionStatus status){
        dispatcher.executeService(dispatcher.saveTransactions, [ hotelTicker, reservation, ticket, type, status ])
    }


    def getPaymentForm(String ticker, String returnUrl, String language, Float amount) {
        return dispatcher.executeService(dispatcher.getPaymentForm,[ticker,returnUrl,language,amount] )
    }



    def getConversionRate(final String defaultCurrency, final String previousCurrency, final String newCurrency){

        Map<String,Object> currencyData=cache.getConversionRate(defaultCurrency);

        double minutesDelta =  ( new Date().time - (currencyData.timestamp as Date).time ) / 1000.0 / 60.0  as double;

        currencyData=cache.getConversionRate(defaultCurrency,minutesDelta);

        if (currencyData==null){
            return [error:"Middleware Error"]
        }
        return (currencyData.conversionRate as CurrencyExchange).prices
    }


    def getCountriesByLanguage(String language){

        def countries=null;
        try{
            countries= cache.getCountriesByLanguage(language,null);
        }catch(Exception e){
            e.printStackTrace()
            cache.resetCountriesByLanguage(language)
        }finally{
            if (!countries){
                cache.resetCountriesByLanguage(language)
            }
            return countries;
        }
    }

    def addCache(cacheServiceInstance){
        cache=cacheServiceInstance;
    }
    def testFrontEndServices() {
        return cache.testFrontEndServices()
    }


    def getAccommodations(hotelTicker, locale) {
        // return dispatcher.executeService(dispatcher.getAccommodations,[hotelTicker, locale])
        return getHotelInfo(hotelTicker).accommodations[locale]
    }

    def getDiscounts(hotelTicker, locale) {
        // return dispatcher.executeService(dispatcher.getDiscounts,[hotelTicker, locale])
        return getHotelInfo(hotelTicker).discounts[locale]
    }

    def getServices(hotelTicker, locale) {
        // return dispatcher.executeService(dispatcher.getServices,[hotelTicker, locale])
        return getHotelInfo(hotelTicker).services[locale]
    }

    def getLanguages(hotelTicker) {
        // return dispatcher.executeService(dispatcher.getLanguages,[hotelTicker])
        return getHotelInfo(hotelTicker).languages
    }

    def getLocationPoints(hotelTicker, locale) {
        // return dispatcher.executeService(dispatcher.getLocationPoints,[hotelTicker, locale])
        return getHotelInfo(hotelTicker).locationPoints[locale]
    }

    def getCurrencies(hotelTicker) {
        // return dispatcher.executeService(dispatcher.getCurrencies,[hotelTicker])
        return getHotelInfo(hotelTicker).currencies
    }

    def getConfigurations(hotelTicker) {
        // return dispatcher.executeService(dispatcher.getConfigurations, [hotelTicker])
        return getHotelInfo(hotelTicker).configurations
    }

    def subscribeNewsletter(hotelTicker, mail, language){
        // return dispatdispatcher.executeService(dispatcher.subscribeNewsletter, [hotelTicker, locale, mail])
        return cache.subscribeNewsletter(hotelTicker, mail, language)
    }

    def getComments(hotelTicker, locale){
        return getHotelInfo(hotelTicker).comments[locale]
    }
    def getStaticData(hotelTicker, locale){
        return getEstablishmentInfo(hotelTicker).comments[locale]
    }


    def resetHotelData(hotelTicker) {
        return cache.resetHotelData(hotelTicker)
    }

    def resetEstablishmentData(hotelTicker) {
        return cache.resetEstablishmentData(hotelTicker)
    }

    def resetEstablishmentInfo(key) {
        return cache.resetEstablishmentInfo(key)
    }

    def getEstablishmentByLanguage(key,propertyNames) {
        return cache.getEstablishmentByLanguage(key,propertyNames)
    }

    def getAri(String hotelTicker, List<String> inventoryTickers,
               Date start, Date end,
               String currency, List<String> promotionalCodes,String country) {
        return cache.getAri( hotelTicker, inventoryTickers,
                start,  end,
                currency,  promotionalCodes,country)
    }


    def getEstablishmentTest(ticker, int basura) {
        return cache.getEstablishmentTest(ticker, basura)
    }

    def saveEstablishmentByLanguageTest(establishment) {
        cache.saveEstablishmentByLanguageTest(establishment)
    }

    def getHotelInfo(String hotelTicker){
        def hotelInfo = [:]
        try{
            hotelInfo = cache.getHotelInfo(hotelTicker)
        }catch(Exception e){
            resetHotelData(hotelTicker)
        }finally{
            return hotelInfo
        }
    }
    def getEstablishment(String hotelTicker, List<String> propertyNames){
        def establishmentInfo = [:]
        try{
            establishmentInfo = cache.getEstablishment(hotelTicker, propertyNames)
        }catch(Exception e){
            /*TODO: errorCacheService*/
            e.printStackTrace()
            resetEstablishmentData(hotelTicker)
        }finally{
            if ((establishmentInfo as Map).containsKey("error")){
                cache.resetAllCache(hotelTicker)
            }
            return establishmentInfo
        }
    }

    def getEstablishmentInfo(String hotelTicker, String locale){
        def establishmentInfo = [:]
        try{
            establishmentInfo = cache.getEstablishmentInfo(hotelTicker, locale)
        }catch(Exception e){
            e.printStackTrace()
            resetEstablishmentData(hotelTicker)
        }finally{
            return establishmentInfo
        }
    }

}