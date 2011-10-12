package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.resource.*;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface ItemValueDefinitionResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition);

        public ItemValueDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newItemValueDefinition(ItemValueDefinition itemValueDefinition);

        public void addBasic();

        public void addName();

        public void addPath();

        public void addValue();

        public void addAudit();

        public void addWikiDoc();

        public void addItemDefinition(ItemDefinition id);

        public void addValueDefinition(ValueDefinition vd);

        public void addUsages();

        public void addChoices();

        public void addFlags();

        public void addUnits();

        public void addVersions();

        public Object getObject();
    }

    public static interface DOMAcceptor extends ResourceAcceptor {
    }

    public static interface FormAcceptor extends ResourceAcceptor {
    }

    public static interface ItemValueDefinitionValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public ItemValueDefinition getObject();

        public void setObject(ItemValueDefinition object);

        public ValidationResult getValidationResult();
    }

    public static interface ItemValueUsageValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public ItemValueUsage getObject();

        public void setObject(ItemValueUsage object);

        public ValidationResult getValidationResult();
    }

    public static interface Remover extends ResourceRemover {
    }
}
