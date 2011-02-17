package com.amee.platform.resource.dataitem;

import com.amee.base.resource.*;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface DataItemResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, DataItem dataItem);

        public DataItemResource.Renderer getRenderer(RequestWrapper requestWrapper);
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

        public void newValue(BaseItemValue itemValue);

        public Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    public static interface DataItemValidator {

        public boolean isValid(Map<String, String> queryParameters);

        public DataItem getObject();

        public void setObject(DataItem object);

        public ValidationResult getValidationResult();
    }
}
