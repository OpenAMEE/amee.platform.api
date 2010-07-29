package com.amee.platform.resource.itemvaluedefinition;

import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface ItemValueDefinitionRenderer {

    public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    public void start();

    public void ok();

    public void newItemValueDefinition(ItemValueDefinition itemValueDefinition);

    public void addBasic();

    public void addName();

    public void addPath();

    public void addAudit();

    public void addWikiDoc();

    public void addItemDefinition(ItemDefinition id);

    public void addUsages();

    public Object getObject();
}
