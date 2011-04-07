package com.amee.platform.resource.unit;

import com.amee.base.resource.*;
import com.amee.domain.unit.AMEEUnit;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

public interface UnitResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, AMEEUnit unit);

        public UnitResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newUnit(AMEEUnit unit);

        public void addBasic();

        public void addAudit();

        public void addSymbols();

        public void addUnitType();

        public void addInternalUnit();

        public Object getObject();
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public UnitResource.UnitValidator getValidator(RequestWrapper requestWrapper);
    }

    public static interface UnitValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public AMEEUnit getObject();

        public void setObject(AMEEUnit object);

        public ValidationResult getValidationResult();
    }

    /**
     * A {@link com.amee.base.resource.ResourceRemover} implementation for this resource.
     */
    public static interface Remover extends ResourceRemover {
    }
}
