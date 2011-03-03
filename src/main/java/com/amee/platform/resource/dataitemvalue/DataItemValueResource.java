package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.*;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface DataItemValueResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, BaseDataItemValue itemValue);

        public DataItemValueResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newDataItemValue(BaseDataItemValue dataItemValue);

        public void addBasic();

        public void addPath();

        public void addDataCategory();

        public void addDataItem();

        public void addAudit();

        public void addItemValueDefinition(ItemValueDefinition itemValueDefinition);

        public Object getObject();
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public Object handle(RequestWrapper requestWrapper, BaseDataItemValue dataItemValue);
    }

    public static interface DataItemValueValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public BaseDataItemValue getObject();

        public void setObject(BaseDataItemValue object);

        public ValidationResult getValidationResult();
    }

    /**
     * A {@link ResourceRemover} implementation for this resource.
     */
    public static interface Remover extends ResourceRemover {
    }
}
