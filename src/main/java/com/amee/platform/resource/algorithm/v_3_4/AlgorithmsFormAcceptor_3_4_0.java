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
import com.amee.platform.resource.algorithm.AlgorithmsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class AlgorithmsFormAcceptor_3_4_0 implements AlgorithmsResource.FormAcceptor {

    @Autowired
    private DefinitionService definitionService;

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

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), itemDefinition);

        // Handle the ItemDefinition submission.
        Algorithm algorithm = new Algorithm(itemDefinition, "");
        return handle(requestWrapper, algorithm);
    }

    public Object handle(RequestWrapper requestWrapper, Algorithm algorithm) {

        // Get Validator.
        AlgorithmResource.AlgorithmValidator validator = getValidator(requestWrapper);
        validator.setObject(algorithm);

        // Do the validation.
        if (validator.isValid(requestWrapper.getFormParameters())) {

            // Save the Algorithm.
            definitionService.persist(algorithm);

            String location = "/" + requestWrapper.getVersion() +
                    "/definitions/" + requestWrapper.getAttributes().get("itemDefinitionIdentifier") +
                    "/algorithms/" + algorithm.getUid();
            return ResponseHelper.getOK(requestWrapper, location);
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    protected AlgorithmResource.AlgorithmValidator getValidator(RequestWrapper requestWrapper) {
        return (AlgorithmResource.AlgorithmValidator)
                resourceBeanFinder.getValidationHelper(AlgorithmResource.AlgorithmValidator.class, requestWrapper);
    }
}
