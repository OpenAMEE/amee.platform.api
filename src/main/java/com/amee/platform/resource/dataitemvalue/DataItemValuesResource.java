package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.item.data.DataItem;
import com.amee.service.item.DataItemValuesFilter;

import java.util.Date;
import java.util.Map;

public interface DataItemValuesResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, DataItem dataItem, DataItemValuesFilter filter);

        public DataItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper);

        public DataItemValueResource.Builder getDataItemValueBuilder(RequestWrapper requestWrapper);

        public DataItemValuesResource.DataItemValuesFilterValidator getValidator(RequestWrapper requestWrapper);
    }

    /**
     * Note that this Renderer is also used by DataItemValueHistoryResource.
     */
    public static interface Renderer extends ResourceRenderer {

        public void newDataItemValue(DataItemValueResource.Renderer renderer);

        public void setTruncated(boolean truncated);
    }

    public static interface DataItemValuesFilterValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public DataItemValuesFilter getObject();

        public void setObject(DataItemValuesFilter object);

        public Date getDefaultStartDate();

        public void setDefaultStartDate(Date defaultStartDate);

        public ValidationResult getValidationResult();
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }
}
