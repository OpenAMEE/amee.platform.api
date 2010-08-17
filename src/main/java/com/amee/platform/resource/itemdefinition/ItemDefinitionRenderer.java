package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.Renderer;
import com.amee.domain.data.ItemDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface ItemDefinitionRenderer extends Renderer {

    public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    public void newItemDefinition(ItemDefinition itemDefinition);

    public void addBasic();

    public void addAudit();

    public void addName();

    public void addDrillDown();

    public void addUsages();

    public Object getObject();
}
