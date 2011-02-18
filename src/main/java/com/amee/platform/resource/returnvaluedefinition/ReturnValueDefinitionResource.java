package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface ReturnValueDefinitionResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition);

        public ReturnValueDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newReturnValueDefinition(ReturnValueDefinition returnValueDefinition);

        public void addBasic();

        public void addItemDefinition(ItemDefinition id);

        public void addValueDefinition(ValueDefinition vd);

        public void addType();

        public void addUnits();

        public void addFlags();

        public void addAudit();

        public Object getObject();
    }

    public static interface FormAcceptor {
    }

    public static interface Remover extends ResourceRemover {
    }
}
