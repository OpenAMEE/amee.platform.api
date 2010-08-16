package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.Renderer;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface ReturnValueDefinitionRenderer extends Renderer {

    public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    public void newReturnValueDefinition(ReturnValueDefinition returnValueDefinition);

    public void addBasic();

    public void addItemDefinition(ItemDefinition id);

    public void addType();

    public void addUnits();

    public void addFlags();

    public Object getObject();
}
