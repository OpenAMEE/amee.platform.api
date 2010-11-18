package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface ItemValueDefinitionResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition);

        public ItemValueDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

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

    interface DOMAcceptor {
    }

    interface FormAcceptor {
    }
}
