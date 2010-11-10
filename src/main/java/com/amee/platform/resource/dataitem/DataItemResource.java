package com.amee.platform.resource.dataitem;

import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValue;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DataItemResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newDataItem(DataItem dataItem);

        public void addBasic();

        public void addName();

        public void addLabel();

        public void addPath();

        public void addParent();

        public void addAudit();

        public void addWikiDoc();

        public void addProvenance();

        public void addItemDefinition(ItemDefinition id);

        public void startValues();

        public void newValue(ItemValue itemValue);

        public Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }
}
