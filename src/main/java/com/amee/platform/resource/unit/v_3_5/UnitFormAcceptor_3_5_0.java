package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.unit.AMEEUnit;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unit.UnitResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.unit.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitFormAcceptor_3_5_0 implements UnitResource.FormAcceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

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
        AMEEUnit unit = resourceService.getUnit(requestWrapper, unitType);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForAccept(
                requestWrapper.getAttributes().get("activeUserUid"), unit);

        // Create Validator.
        UnitResource.UnitValidator validator = getValidator(requestWrapper);
        validator.setObject(unit);
        validator.initialise();

        // Is the Unit valid?
        if (validator.isValid(requestWrapper.getFormParameters())) {
            return ResponseHelper.getOK(requestWrapper, null, unit.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    @Override
    public UnitResource.UnitValidator getValidator(RequestWrapper requestWrapper) {
        return (UnitResource.UnitValidator)
                resourceBeanFinder.getValidator(
                        UnitResource.UnitValidator.class, requestWrapper);
    }
}
