package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionRemover_3_1_0 implements ReturnValueDefinitionResource.Remover {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ValidationException.class})
    public Object handle(RequestWrapper requestWrapper) {
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(itemDefinitionIdentifier);
            if (itemDefinition != null) {
                String returnValueDefinitionIdentifier = requestWrapper.getAttributes().get("returnValueDefinitionIdentifier");
                if (returnValueDefinitionIdentifier != null) {
                    ReturnValueDefinition returnValueDefinition = definitionService.getReturnValueDefinitionByUid(itemDefinition, returnValueDefinitionIdentifier);
                    if (returnValueDefinition != null) {
                        // Authorized?
                        resourceAuthorizationService.ensureAuthorizedForRemove(
                                requestWrapper.getAttributes().get("activeUserUid"), returnValueDefinition);
                        // Handle ReturnValueDefinition removal.
                        definitionService.remove(returnValueDefinition);
                        definitionService.invalidate(returnValueDefinition.getItemDefinition());
                        return ResponseHelper.getOK(requestWrapper);
                    } else {
                        throw new NotFoundException();
                    }
                } else {
                    throw new MissingAttributeException("returnValueDefinitionIdentifier");
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }
}
