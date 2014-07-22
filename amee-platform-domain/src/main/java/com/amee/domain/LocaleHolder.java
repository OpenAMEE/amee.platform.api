package com.amee.domain;

import com.amee.base.utils.ThreadBeanHolder;

import java.util.Locale;

/**
 * A thread local helper for retrieving the locale string of the request
 */
public class LocaleHolder extends ThreadBeanHolder {

    /**
     * Does the current request thread have the default locale.
     *
     * @return - true if the current request has the default locale, otherwise false
     *
     * {@see LocaleName.DEFAULT_LOCALE}
     */
    public static boolean isDefaultLocale() {
        Locale locale = get(Locale.class);
        return LocaleConstants.DEFAULT_LOCALE.equals(locale);
    }

    /**
     * Get the locale of the current request thread. This will be the {@link com.amee.domain.auth.User} locale or a
     * locale specified as a request override.  
     *  
     * @return - the locale string of the current request thread.
     */
    public static String getLocale() {
        Locale currentLocale = get(Locale.class);
        if (currentLocale != null) {
            return currentLocale.toString();
        }
        return null;
    }

    /**
     * Stores a Locale object for the given country.
     *
     * @param country the country to set the locale for.
     */
    public static void setLocale(String country) {
        set(Locale.class, new Locale(country));
    }
}
