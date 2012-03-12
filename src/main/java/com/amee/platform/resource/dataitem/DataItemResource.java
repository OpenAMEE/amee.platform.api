package com.amee.platform.resource.dataitem;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.DataItemService;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceValidator;
import com.amee.platform.resource.dataitemvalue.DataItemValuesResource;

public interface DataItemResource {

    interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, DataItem dataItem);

        DataItemResource.Renderer getRenderer(RequestWrapper requestWrapper);

        DataItemValuesResource.FilterValidator getValidator(RequestWrapper requestWrapper);
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
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    interface DataItemValidator extends ResourceValidator<DataItem> {

        void initialise();

        void setDataItemService(DataItemService dataItemService);
    }

    interface Remover extends ResourceRemover {
    }
}
