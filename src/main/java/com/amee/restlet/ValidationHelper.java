package com.amee.restlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public abstract class ValidationHelper {

    private final Log log = LogFactory.getLog(getClass());

    private DataBinder dataBinder;

    public ValidationHelper() {
        super();
    }

    /**
     * Validates the supplied form.
     *
     * @param form to validate
     * @return true if form is valid, otherwise false
     */
    public boolean isValid(Form form) {
        log.debug("isValid()");
        dataBinder = createDataBinder();
        prepareDataBinder(dataBinder);
        beforeBind(form);
        dataBinder.bind(createPropertyValues(form));
        Errors errors = getErrors();
        if (!errors.hasErrors()) {
            ValidationUtils.invokeValidator(getValidator(), dataBinder.getTarget(), errors);
            if (!errors.hasErrors()) {
                log.debug("isValid() - No validation errors.");
                return true;
            }
        } else {
            log.debug("isValid() - Has binding errors.");
        }
        log.debug("isValid() - Has validation errors.");
        return false;
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

    protected DataBinder createDataBinder() {
        return new DataBinder(getTarget(), getName());
    }

    /**
     * Hook for pre-processing the Form before binding happens.
     *
     * @param form the Form to be validated
     * @return the Form
     */
    protected void beforeBind(Form form) {
        // do nothing
    }

    /**
     * Renames all parameters in the Form that match oldName to newName. Useful for re-implementations of beforeBind.
     *
     * @param form    containing parameters
     * @param oldName old name for form parameters
     * @param newName new name for form parameters
     */
    protected void renameFormParameters(Form form, String oldName, String newName) {
        for (Parameter p : form) {
            if (p.getName().equals(oldName)) {
                p.setName(newName);
            }
        }
    }

    protected PropertyValues createPropertyValues(Form form) {
        return new MutablePropertyValues(form.getValuesMap());
    }

    protected Validator getValidator() {
        throw new UnsupportedOperationException();
    }

    public DataBinder getDataBinder() {
        return dataBinder;
    }

    public Errors getErrors() {
        return dataBinder.getBindingResult();
    }

    public Object getTarget() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public String[] getAllowedFields() {
        return new String[]{};
    }
}
