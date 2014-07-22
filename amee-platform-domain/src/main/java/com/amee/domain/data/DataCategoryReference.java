package com.amee.domain.data;

import com.amee.domain.AMEEEntityReference;
import com.amee.domain.IDataCategoryReference;

public class DataCategoryReference extends AMEEEntityReference implements IDataCategoryReference {

    private String name = "";
    private String path = "";
    private String fullPath = "";
    private String wikiName = "";
    private boolean itemDefinitionPresent = false;

    public DataCategoryReference() {
        super();
    }

    public DataCategoryReference(IDataCategoryReference dataCategoryReference) {
        super(dataCategoryReference);
        setName(dataCategoryReference.getName());
        setPath(dataCategoryReference.getPath());
        setFullPath(dataCategoryReference.getFullPath());
        setWikiName(dataCategoryReference.getWikiName());
        setItemDefinitionPresent(dataCategoryReference.isItemDefinitionPresent());
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path == null) {
            path = "";
        }
        this.path = path;
    }

    @Override
    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        if (fullPath == null) {
            fullPath = "";
        }
        this.fullPath = fullPath;
    }

    @Override
    public String getWikiName() {
        return wikiName;
    }

    public void setWikiName(String wikiName) {
        if (wikiName == null) {
            wikiName = "";
        }
        this.wikiName = wikiName;
    }

    @Override
    public boolean isItemDefinitionPresent() {
        return itemDefinitionPresent;
    }

    public void setItemDefinitionPresent(boolean itemDefinitionPresent) {
        this.itemDefinitionPresent = itemDefinitionPresent;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}