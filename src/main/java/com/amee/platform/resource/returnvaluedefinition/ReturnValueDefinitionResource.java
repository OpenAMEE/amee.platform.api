package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface ReturnValueDefinitionResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newReturnValueDefinition(ReturnValueDefinition returnValueDefinition);

        public void addBasic();

        public void addItemDefinition(ItemDefinition id);

        public void addValueDefinition(ValueDefinition vd);

        public void addType();

        public void addUnits();

        public void addFlags();

        public Object getObject();
    }

    interface FormAcceptor {
    }

    interface Remover extends ResourceRemover {
    }
}
