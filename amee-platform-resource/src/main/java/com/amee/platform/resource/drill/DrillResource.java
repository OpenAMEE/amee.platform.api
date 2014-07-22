package com.amee.platform.resource.drill;

import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.sheet.Choice;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DrillResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        void startSelections();

        void newSelection(Choice selection);

        void startChoices(String name);

        void newChoice(Choice choice);
    }
}
