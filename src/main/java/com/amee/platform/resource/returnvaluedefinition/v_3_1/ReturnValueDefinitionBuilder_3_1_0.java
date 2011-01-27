package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionBuilder_3_1_0 implements ReturnValueDefinitionResource.Builder {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private ReturnValueDefinitionResource.Renderer returnValueDefinitionRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(itemDefinitionIdentifier);
            if (itemDefinition != null) {
                // Get ReturnValueDefinition identifier.
                String returnValueDefinitionIdentifier = requestWrapper.getAttributes().get("returnValueDefinitionIdentifier");
                if (returnValueDefinitionIdentifier != null) {
                    // Get ReturnValueDefinition.
                    ReturnValueDefinition returnValueDefinition = definitionService.getReturnValueDefinitionByUid(
                            itemDefinition, returnValueDefinitionIdentifier);
                    if (returnValueDefinition != null) {
                        // Authorized?
                        resourceAuthorizationService.ensureAuthorizedForBuild(
                                requestWrapper.getAttributes().get("activeUserUid"), returnValueDefinition);
                        // Handle the ReturnValueDefinition.
                        handle(requestWrapper, returnValueDefinition);
                        ReturnValueDefinitionResource.Renderer renderer = getRenderer(requestWrapper);
                        renderer.ok();
                        return renderer.getObject();
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

    public void handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition) {

        ReturnValueDefinitionResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean itemDefinition = requestWrapper.getMatrixParameters().containsKey("itemDefinition");
        boolean valueDefinition = requestWrapper.getMatrixParameters().containsKey("valueDefinition");
        boolean type = requestWrapper.getMatrixParameters().containsKey("type");
        boolean units = requestWrapper.getMatrixParameters().containsKey("units");
        boolean flags = requestWrapper.getMatrixParameters().containsKey("flags");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");

        // New ReturnValueDefinition & basic.
        renderer.newReturnValueDefinition(returnValueDefinition);
        renderer.addBasic();

        // Optional attributes.
        if ((itemDefinition || full) && (returnValueDefinition.getItemDefinition() != null)) {
            renderer.addItemDefinition(returnValueDefinition.getItemDefinition());
        }
        if ((valueDefinition || full) && (returnValueDefinition.getValueDefinition() != null)) {
            renderer.addValueDefinition(returnValueDefinition.getValueDefinition());
        }
        if (units || full) {
            renderer.addUnits();
        }
        if (type || full) {
            renderer.addType();
        }
        if (flags || full) {
            renderer.addFlags();
        }
        if (audit || full) {
            renderer.addAudit();
        }
    }

    public ReturnValueDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (returnValueDefinitionRenderer == null) {
            returnValueDefinitionRenderer = (ReturnValueDefinitionResource.Renderer) resourceBeanFinder.getRenderer(ReturnValueDefinitionResource.Renderer.class, requestWrapper);
        }
        return returnValueDefinitionRenderer;
    }
}
