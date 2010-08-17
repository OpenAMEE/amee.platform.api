package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.validation.ValidationException;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.service.definition.DefinitionService;
import com.amee.service.environment.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class ReturnValueDefinitionsAcceptor implements ResourceAcceptor {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    protected EnvironmentService environmentService;

    @Transactional(rollbackFor = {ValidationException.class})
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(
                    environmentService.getEnvironmentByName("AMEE"), itemDefinitionIdentifier);
            if (itemDefinition != null) {
                ReturnValueDefinition returnValueDefinition = new ReturnValueDefinition();
                returnValueDefinition.setItemDefinition(itemDefinition);
                returnValueDefinition.setEnvironment(itemDefinition.getEnvironment());
                return handle(requestWrapper, returnValueDefinition);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    protected abstract Object handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition);
}
