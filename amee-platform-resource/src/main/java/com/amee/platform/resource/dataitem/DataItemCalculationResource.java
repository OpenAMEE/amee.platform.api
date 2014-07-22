package com.amee.platform.resource.dataitem;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choices;
import com.amee.platform.science.ReturnValues;

import java.util.List;
import java.util.Map;

public interface DataItemCalculationResource {

    interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, DataItem dataItem);

        DataItemCalculationResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void addReturnValues(ReturnValues returnValues);

        void addValues(DataItem data, Choices userValues, Map<String, List<BaseItemValue>> dataItemValues);
    }
}
