package com.amee.domain;

public interface IDataCategoryReference extends IAMEEEntityReference {

    public String getName();

    public String getPath();

    public String getFullPath();

    public String getWikiName();

    public boolean isItemDefinitionPresent();
}
