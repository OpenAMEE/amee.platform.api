package com.amee.domain.site;

public interface ISite {

    String getActiveSkinPath();

    boolean isSecureAvailable();

    boolean isCheckRemoteAddress();

    String getAuthCookieDomain();

    Long getMaxAuthDuration();

    Long getMaxAuthIdle();
}
