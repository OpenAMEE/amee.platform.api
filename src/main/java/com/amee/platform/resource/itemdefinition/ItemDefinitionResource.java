package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.*;
import com.amee.domain.data.ItemDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface ItemDefinitionResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

        public ItemDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newItemDefinition(ItemDefinition itemDefinition);

        public void addBasic();

        public void addAudit();

        public void addName();

        public void addDrillDown();

        public void addUsages();

        public Object getObject();
    }

    public static interface FormAcceptor extends ResourceAcceptor {
    }

    public static interface ItemDefinitionValidator {

        public boolean isValid(Map<String, String> queryParameters);

        public ItemDefinition getObject();

        public void setObject(ItemDefinition object);

        public ValidationResult getValidationResult();
    }
}