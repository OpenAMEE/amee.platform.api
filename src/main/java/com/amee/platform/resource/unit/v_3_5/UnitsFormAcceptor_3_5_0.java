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
import com.amee.platform.resource.unit.UnitsResource;
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
public class UnitsFormAcceptor_3_5_0 implements UnitsResource.FormAcceptor {

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

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForAccept(
                requestWrapper.getAttributes().get("activeUserUid"), unitType);

        // Create the new Unit.
        AMEEUnit unit = new AMEEUnit(unitType);
        unitService.persist(unit);
        return handle(requestWrapper, unit);
    }

    @Override
    public Object handle(RequestWrapper requestWrapper, AMEEUnit unit) {

        // Create Validator.
        UnitResource.UnitValidator validator = getValidator(requestWrapper);
        validator.setObject(unit);
        validator.initialise();

        // Is the Unit valid?
        if (validator.isValid(requestWrapper.getFormParameters())) {
            return ResponseHelper.getOK(requestWrapper);
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