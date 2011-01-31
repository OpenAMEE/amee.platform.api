package com.amee.platform.resource.dataitem;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.NuDataItem;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DataItemResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, NuDataItem dataItem);

        public DataItemResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newDataItem(NuDataItem dataItem);

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

        public void newValue(BaseItemValue itemValue);

        public Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }
}
