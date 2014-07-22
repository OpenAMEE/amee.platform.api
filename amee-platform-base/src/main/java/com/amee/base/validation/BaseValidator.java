package com.amee.base.validation;

import com.amee.base.resource.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A base class for Validator implementations providing a mechanism to validate objects with {@link ValidationSpecification}s.
 */
public abstract class BaseValidator implements Validator {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * A {@link List} of {@link ValidationSpecification}s for validating an object against.
     */
    private List<ValidationSpecification> specifications = new ArrayList<ValidationSpecification>();

    /**
     * A {@link MessageSource} for detailed validation error messages.
     */
    @Autowired
    private MessageSource messageSource;
    /**
     * The {@link DataBinder} used to bind incoming parameters to target bean fields.
     */
    private DataBinder dataBinder;

    /**
     * A constructor.
     */
    public BaseValidator() {
        super();
    }

    /**
     * Add a {@link ValidationSpecification} to the {@link List} or specifications.
     * 
     * @param validationSpecification
     *            a {@link ValidationSpecification} to add the list
     */
    public void add(ValidationSpecification validationSpecification) {
        getSpecifications().add(validationSpecification);
    }

    /**
     * Validate the supplied object and update the supplied Errors object with any failures. This iterates over all the current {@link ValidationSpecification}s
     * and validates the object against each.
     * 
     * @param object
     *            to validate
     * @param errors
     *            to store validation errors
     */
    @Override
    public void validate(Object object, Errors errors) {
        for (ValidationSpecification validationSpecification : getSpecifications()) {
            if (validationSpecification.validate(object, errors) == ValidationSpecification.STOP) {
                break;
            }
        }
    }

    /**
     * Get the {@link ValidationSpecification}s.
     * 
     * @return the {@link ValidationSpecification}s
     */
    public List<ValidationSpecification> getSpecifications() {
        return specifications;
    }

    /**
     * Set the {@link ValidationSpecification}s.
     * 
     * @param specifications
     *            the {@link ValidationSpecification}s
     */
    public void setSpecifications(List<ValidationSpecification> specifications) {
        this.specifications.clear();
        if (specifications != null) {
            this.specifications.addAll(specifications);
        }
    }

    /**
     * Get the current {@link Validator} implementation.
     * 
     * @return the current {@link Validator} implementation
     */
    protected Validator getValidator() {
        return this;
    }

    /**
     * Validates the supplied map of parameters. The parameters, name / value pairs, typically arrive in a {@link com.amee.base.resource.RequestWrapper} as part
     * of an HTTP GET, POST or PUT request.
     * <p/>
     * TODO: Consider using Binder.setValidator() & Binder.validate() instead.
     * 
     * @param values
     *            to validate
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
     * Prepare the DataBinder before it is used for validation. Default implementation updates the DataBinder allowedFields and calls registerCustomEditors with
     * the DataBinder.
     * 
     * @param dataBinder
     *            to be prepared
     */
    protected void prepareDataBinder(DataBinder dataBinder) {
        dataBinder.setAllowedFields(getAllowedFields());
        registerCustomEditors(dataBinder);
    }

    /**
     * Hook for registering custom PropertyEditors into the DataBinder. Default implementation does nothing. Extending classes should override this method to
     * supply required PropertyEditors to the DataBinder.
     * 
     * @param dataBinder
     *            to set editors on
     */
    protected void registerCustomEditors(DataBinder dataBinder) {
        // do nothing
    }

    /**
     * Register a custom editor to the DataBinder.
     * 
     * @param requiredType
     *            the type of the property
     * @param field
     *            the field name of the property
     * @param propertyEditor
     *            a {@link PropertyEditor} implementation
     */
    protected void addCustomEditor(Class requiredType, String field, PropertyEditor propertyEditor) {
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
     * @param values
     *            to be validated
     */
    protected void beforeBind(Map<String, String> values) {
        // do nothing
    }

    /**
     * Renames all parameters in the Form that match oldName to newName. Useful for re-implementations of beforeBind.
     * 
     * @param values
     *            to consider for renaming
     * @param oldName
     *            old name for value
     * @param newName
     *            new name for value
     */
    protected void renameValue(Map<String, String> values, String oldName, String newName) {
        String value = values.remove(oldName);
        if (value != null) {
            values.put(newName, value);
        }
    }

    private PropertyValues createPropertyValues(Map<String, String> values) {
        return new MutablePropertyValues(values);
    }

    /**
     * Get a DataBinder for the current validator. If a DataBinder has not already been created a new one will be created and cached for subsequent calls.
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

    /**
     * Get the object that is being validated.
     * 
     * @return the object that is being validated
     */
    public Object getObject() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the friendly name of the object that is being validated.
     * 
     * @return the friendly name of the object that is being validated
     */
    public String getName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the array of allowed fields. Only fields that match names in this array will be used by the {@link DataBinder}.
     * 
     * @return the array of allowed fields
     */
    public String[] getAllowedFields() {
        return new String[] {};
    }

    /**
     * Get a {@link ValidationResult} representing any validation errors. Pulls in details from the {@link Errors} and {@link MessageSource} objects.
     * 
     * @return a {@link ValidationResult} representing any validation errors
     */
    public ValidationResult getValidationResult() {
        // We always want a new ValidationResult.
        ValidationResult validationResult = new ValidationResult(messageSource);
        // Add the errors.
        validationResult.setErrors(getErrors());
        // Add the values.
        // NOTE: This is not required and was commented out 2010/08/18 by DB. Left here for reference.
        // NOTE: https://jira.amee.com/browse/PL-3275
        // for (String field : getAllowedFields()) {
        // Object value = getErrors().getFieldValue(field);
        // if ((value != null) && (value instanceof String)) {
        // validationResult.addValue(field, (String) value);
        // }
        // }
        return validationResult;
    }

    /**
     * Get a ValidationResult using the given Errors object.
     *
     * @param errors
     * @return
     */
    public ValidationResult getValidationResult(Errors errors) {
        ValidationResult validationResult = new ValidationResult(messageSource);
        validationResult.setErrors(errors);
        return validationResult;
    }
}
