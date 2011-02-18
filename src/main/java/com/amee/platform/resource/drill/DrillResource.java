package com.amee.platform.resource.drill;

import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.sheet.Choice;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DrillResource {

    public static interface Builder extends ResourceBuilder {
    }

    public static interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void startSelections();

        public void newSelection(Choice selection);

        public void startChoices(String name);

        public void newChoice(Choice choice);
    }
}
