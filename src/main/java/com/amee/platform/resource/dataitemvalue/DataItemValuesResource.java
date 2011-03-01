package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;

import java.util.Map;

public interface DataItemValuesResource {

    public static interface Builder extends ResourceBuilder {
    }

    /**
     * Note that this Renderer is also used by DataItemValueHistoryResource.
     */
    public static interface Renderer extends ResourceRenderer {

        public void newDataItemValue(DataItemValueResource.Renderer renderer);
    }

    public static interface DataItemValuesFilterValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public DataItemValuesFilter getObject();

        public void setObject(DataItemValuesFilter object);

        public ValidationResult getValidationResult();
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }
}
