package com.amee.platform.resource.dataitem;

import com.amee.base.resource.Renderer;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValue;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DataItemRenderer extends Renderer {

    public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    public void newDataItem(DataItem dataItem);

    public void addBasic();

    public void addName();

    // TODO: Implement in v_3.2.
    public void addLabel();

    public void addPath();

    public void addParent();

    public void addAudit();

    public void addWikiDoc();

    public void addProvenance();

    public void addItemDefinition(ItemDefinition id);

    public void startValues();

    public void newValue(ItemValue itemValue);

    public Object getObject();
}
