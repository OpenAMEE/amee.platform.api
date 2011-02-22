package com.amee.base.validation;

import com.amee.base.resource.ValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.beans.PropertyEditor;
import java.util.Map;

/**
 * A base class providing a number of utility functions to validate objects. A {@link MessageSource} and
 * a {@link DataBinder} are used internally, integrating into the Spring validation framework.
 * <p/>
 * TODO: Merge this with {@link BaseValidator}.
 */
public abstract class ValidationHelper {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private MessageSource messageSource;

    private DataBinder dataBinder;

    public ValidationHelper() {
        super();
    }

    /**
     * Validates the supplied form.
     * <p/>
     * TODO: Binder.setValidator() & Binder.validate() instead.
     *
     * @param values to validate
     * @return true if form is valid, otherwise false
     */
    public boolean isValid(Map<String, String> values) {
        log.debug("isValid()");
        DataBinder dataBinder = getDataBinder();
        prepareDataBinder(dataBinder);
        beforeBind(values);
        dataBinder.bind(createPropertyValues(values));
        Errors errors = dataBinder.getBindingResult();
        ValidationUtils.invokeValidator(getValidator(), dataBinder.getTarget(), errors);
        if (!errors.hasErrors()) {
            log.debug("isValid() - No validation errors.");
            return true;
        } else {
            log.debug("isValid() - Has validation errors.");
            return false;
        }
    }

    /**
     * Prepare the DataBinder before it is used for validation. Default implementation updates the DataBinder
     * allowedFields and calls registerCustomEditors with the DataBinder.
     *
     * @param dataBinder to be prepared
     */
    protected void prepareDataBinder(DataBinder dataBinder) {
        dataBinder.setAllowedFields(getAllowedFields());
        registerCustomEditors(dataBinder);
    }

    /**
     * Hook for registering custom PropertyEditors into the DataBinder. Default implementation does
     * nothing. Extending classes should override this method to supply required PropertyEditors to
     * the DataBinder.
     *
     * @param dataBinder to set editors on
     */
    protected void registerCustomEditors(DataBinder dataBinder) {
        // do nothing
    }

    /**
     * Register a custom editor to the DataBinder.
     *
     * @param requiredType   the type of the property
     * @param field          the field name of the property
     * @param propertyEditor a {@link PropertyEditor} implementation
     */
    protected void add(Class requiredType, String field, PropertyEditor propertyEditor) {
        getDataBinder().registerCustomEditor(requiredType, field, propertyEditor);
    }

    /**
     * Create a new {@link DataBinder}.
     *
     * @return the new {@link DataBinder}
     */
    protected DataBinder createDataBinder() {
        return new DataBinder(getObject(), getName());
    }

    /**
     * Hook for pre-processing the Form before binding happens.
     *
     * @param values to be validated
     */
    protected void beforeBind(Map<String, String> values) {
        // do nothing
    }

    /**
     * Renames all parameters in the Form that match oldName to newName. Useful for re-implementations of beforeBind.
     *
     * @param values  to consider for renaming
     * @param oldName old name for value
     * @param newName new name for value
     */
    protected void renameValue(Map<String, String> values, String oldName, String newName) {
        String value = values.remove(oldName);
        if (value != null) {
            values.put(newName, value);
        }
    }

    protected PropertyValues createPropertyValues(Map<String, String> values) {
        return new MutablePropertyValues(values);
    }

    protected Validator getValidator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a DataBinder for the current validator. If a DataBinder has not already been created
     * a new one will be created and cached for subsequent calls.
     *
     * @return the DataBinder
     */
    public DataBinder getDataBinder() {
        if (dataBinder == null) {
            dataBinder = createDataBinder();
        }
        return dataBinder;
    }

    /**
     * Convenience method to get the BindingResult (errors) from the current DataBinder.
     *
     * @return Errors object
     */
    public Errors getErrors() {
        return getDataBinder().getBindingResult();
    }

    public Object getObject() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public String[] getAllowedFields() {
        return new String[]{};
    }

    public ValidationResult getValidationResult() {
        // We always want a new ValidationResult.
        ValidationResult validationResult = new ValidationResult(messageSource);
        // Add the errors.
        validationResult.setErrors(getErrors());
        // Add the values.
        // NOTE: This is not required and was commented out 2010/08/18 by DB. Left here for reference.
        // NOTE: https://jira.amee.com/browse/PL-3275
        // for (String field : getAllowedFields()) {
        //     Object value = getErrors().getFieldValue(field);
        //     if ((value != null) && (value instanceof String)) {
        //         validationResult.addValue(field, (String) value);
        //     }
        // }
        return validationResult;
    }
}
