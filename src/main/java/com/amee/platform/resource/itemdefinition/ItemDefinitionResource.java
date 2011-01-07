package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.data.ItemDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface ItemDefinitionResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

        public ItemDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newItemDefinition(ItemDefinition itemDefinition);

        public void addBasic();

        public void addAudit();

        public void addName();

        public void addDrillDown();

        public void addUsages();

        public Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }
}