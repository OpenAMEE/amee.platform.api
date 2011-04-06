package com.amee.platform.resource.unit;

import com.amee.base.resource.*;
import com.amee.domain.unit.AMEEUnitType;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface UnitTypeResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, AMEEUnitType unitType);

        public UnitTypeResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newUnitType(AMEEUnitType unitType);

        public void addBasic();

        public void addAudit();

        public Object getObject();
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public UnitTypeResource.UnitTypeValidator getValidator(RequestWrapper requestWrapper);
    }

    public static interface UnitTypeValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public AMEEUnitType getObject();

        public void setObject(AMEEUnitType object);

        public ValidationResult getValidationResult();
    }

    /**
     * A {@link com.amee.base.resource.ResourceRemover} implementation for this resource.
     */
    public static interface Remover extends ResourceRemover {
    }
}
