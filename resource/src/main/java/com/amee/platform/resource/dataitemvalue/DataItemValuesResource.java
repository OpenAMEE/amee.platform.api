package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ValidationResult;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemValuesFilter;
import com.amee.domain.item.data.DataItem;

import java.util.Date;
import java.util.Map;

public interface DataItemValuesResource {

    interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, DataItem dataItem, DataItemValuesFilter filter);

        Renderer getRenderer(RequestWrapper requestWrapper);

        DataItemValueResource.Builder getDataItemValueBuilder(RequestWrapper requestWrapper);

        FilterValidator getValidator(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void newDataItemValue(DataItemValueResource.Renderer renderer);

        void setTruncated(boolean truncated);
    }

    interface FilterValidator {

        void initialise();

        boolean isValid(Map<String, String> queryParameters);

        DataItemValuesFilter getObject();

        void setObject(DataItemValuesFilter object);

        Date getDefaultStartDate();

        void setDefaultStartDate(Date defaultStartDate);

        ValidationResult getValidationResult();
    }

    static interface FormAcceptor extends ResourceAcceptor {

        @Override
        Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }
}
