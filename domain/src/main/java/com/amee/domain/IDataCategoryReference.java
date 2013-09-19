package com.amee.domain;

public interface IDataCategoryReference extends IAMEEEntityReference {

    String getName();

    String getPath();

    String getFullPath();

    String getWikiName();

    boolean isItemDefinitionPresent();
}
