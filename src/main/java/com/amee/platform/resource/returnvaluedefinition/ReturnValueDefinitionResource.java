package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.*;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

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

    public static interface FormAcceptor extends ResourceAcceptor {
    }

    public static interface ReturnValueDefinitionValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public ReturnValueDefinition getObject();

        public void setObject(ReturnValueDefinition object);

        public ValidationResult getValidationResult();
    }

    public static interface Remover extends ResourceRemover {
    }
}
