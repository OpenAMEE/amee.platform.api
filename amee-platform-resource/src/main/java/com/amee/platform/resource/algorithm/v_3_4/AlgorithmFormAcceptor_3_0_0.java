package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class AlgorithmFormAcceptor_3_0_0 implements AlgorithmResource.FormAcceptor {

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Get Algorithm.
        Algorithm algorithm = resourceService.getAlgorithm(requestWrapper, itemDefinition);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), algorithm);

        // Handle Algorithm.
        return handle(requestWrapper, algorithm);
    }

    protected Object handle(RequestWrapper requestWrapper, Algorithm algorithm) {

        // Get Validator.
        AlgorithmResource.AlgorithmValidator validator = getValidator(requestWrapper);
        validator.setObject(algorithm);

        // Do the validation.
        if (validator.isValid(requestWrapper.getFormParameters())) {
            return ResponseHelper.getOK(requestWrapper, null, algorithm.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    protected AlgorithmResource.AlgorithmValidator getValidator(RequestWrapper requestWrapper) {
        return (AlgorithmResource.AlgorithmValidator)
                resourceBeanFinder.getBaseValidator(AlgorithmResource.AlgorithmValidator.class, requestWrapper);
    }
}