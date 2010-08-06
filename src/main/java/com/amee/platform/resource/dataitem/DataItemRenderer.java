package com.amee.platform.resource.dataitem;

import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValue;
import com.amee.domain.path.PathItem;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DataItemRenderer {

    public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    public void start();

    public void ok();

    public void newDataItem(DataItem dataItem);

    public void addBasic();

    public void addName();

    public void addPath(PathItem pathItem);

    public void addParent();

    public void addAudit();

    public void addWikiDoc();

    public void addProvenance();

    public void addItemDefinition(ItemDefinition id);

    public void startValues();

    public void newValue(ItemValue itemValue);

    public Object getObject();
}
