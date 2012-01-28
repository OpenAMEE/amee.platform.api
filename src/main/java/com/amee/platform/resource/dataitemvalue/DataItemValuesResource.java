package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemValuesFilter;
import com.amee.domain.item.data.DataItem;

import java.util.Date;
import java.util.Map;

public interface DataItemValuesResource {

    static interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, DataItem dataItem, DataItemValuesFilter filter);

        DataItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper);

        DataItemValueResource.Builder getDataItemValueBuilder(RequestWrapper requestWrapper);

        DataItemValuesResource.DataItemValuesFilterValidator getValidator(RequestWrapper requestWrapper);
    }

    /**
     * Note that this Renderer is also used by DataItemValueHistoryResource.
     */
    static interface Renderer extends ResourceRenderer {

        void newDataItemValue(DataItemValueResource.Renderer renderer);

        void setTruncated(boolean truncated);
    }

    static interface DataItemValuesFilterValidator {

        void initialise();

        boolean isValid(Map<String, String> queryParameters);

        DataItemValuesFilter getObject();

        void setObject(DataItemValuesFilter object);

        Date getDefaultStartDate();

        void setDefaultStartDate(Date defaultStartDate);

        ValidationResult getValidationResult();
    }

    static interface FormAcceptor extends ResourceAcceptor {

        Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }
}
