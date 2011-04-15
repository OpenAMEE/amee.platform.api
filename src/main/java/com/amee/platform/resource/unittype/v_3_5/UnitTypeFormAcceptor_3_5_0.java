package com.amee.platform.resource.unittype.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unittype.UnitTypeResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.unit.UnitService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypeFormAcceptor_3_5_0 implements UnitTypeResource.FormAcceptor {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get entities.
        AMEEUnitType unitType = resourceService.getUnitType(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForAccept(
                requestWrapper.getAttributes().get("activeUserUid"), unitType);

        // Create Validator.
        UnitTypeResource.UnitTypeValidator validator = getValidator(requestWrapper);
        validator.setObject(unitType);
        validator.initialise();

        // Is the UnitType valid?
        if (validator.isValid(requestWrapper.getFormParameters())) {
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    @Override
    public UnitTypeResource.UnitTypeValidator getValidator(RequestWrapper requestWrapper) {
        return (UnitTypeResource.UnitTypeValidator)
                resourceBeanFinder.getValidator(
                        UnitTypeResource.UnitTypeValidator.class, requestWrapper);
    }
}