package com.amee.platform.resource.dataitem;

import com.amee.base.resource.*;
import com.amee.domain.DataItemService;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitemvalue.DataItemValuesResource;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface DataItemResource {

    interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, DataItem dataItem);

        DataItemResource.Renderer getRenderer(RequestWrapper requestWrapper);

        DataItemValuesResource.DataItemValuesFilterValidator getValidator(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void newDataItem(DataItem dataItem);

        void addBasic();

        void addName();

        void addLabel();

        void addPath();

        void addParent();

        void addAudit();

        void addWikiDoc();

        void addProvenance();

        void addItemDefinition(ItemDefinition id);

        void startValues();

        void newValue(BaseItemValue itemValue);

        // TODO: remove this
        Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    interface DataItemValidator {

        void initialise();

        boolean isValid(Map<String, String> queryParameters);

        DataItem getObject();

        void setObject(DataItem object);

        ValidationResult getValidationResult();

        void setDataItemService(DataItemService dataItemService);
    }

    interface Remover extends ResourceRemover {
    }
}
