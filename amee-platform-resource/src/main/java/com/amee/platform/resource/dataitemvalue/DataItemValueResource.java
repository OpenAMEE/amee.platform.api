package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.*;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface DataItemValueResource {

    static interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, BaseDataItemValue itemValue);

        DataItemValueResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    static interface Renderer extends ResourceRenderer {

        void newDataItemValue(BaseDataItemValue dataItemValue);

        void addBasic();

        void addPath();

        void addDataCategory();

        void addDataItem();

        void addAudit();

        void addItemValueDefinition(ItemValueDefinition itemValueDefinition);
    }

    static interface FormAcceptor extends ResourceAcceptor {

        Object handle(RequestWrapper requestWrapper, BaseDataItemValue dataItemValue);
    }

    static interface DataItemValueValidator {

        void initialise();

        boolean isValid(Map<String, String> queryParameters);

        BaseDataItemValue getObject();

        void setObject(BaseDataItemValue object);

        ValidationResult getValidationResult();
    }

    /**
     * A {@link ResourceRemover} implementation for this resource.
     */
    static interface Remover extends ResourceRemover {
    }
}
