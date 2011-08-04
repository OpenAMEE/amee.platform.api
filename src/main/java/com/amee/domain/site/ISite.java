package com.amee.domain.site;

public interface ISite {

    public String getActiveSkinPath();

    public boolean isSecureAvailable();

    public boolean isCheckRemoteAddress();

    public String getAuthCookieDomain();

    public Long getMaxAuthDuration();

    public Long getMaxAuthIdle();
}
