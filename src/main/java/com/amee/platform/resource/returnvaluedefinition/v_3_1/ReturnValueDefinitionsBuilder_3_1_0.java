package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.*;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionsResource;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionsBuilder_3_1_0 implements ReturnValueDefinitionsResource.Builder {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ReturnValueDefinitionBuilder_3_1_0 returnValueDefinitionBuilder;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private ReturnValueDefinitionsResource.Renderer returnValueDefinitionsRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(itemDefinitionIdentifier);
            if (itemDefinition != null) {
                // Handle the ItemDefinition.
                handle(requestWrapper, itemDefinition);
                ReturnValueDefinitionsResource.Renderer renderer = getRenderer(requestWrapper);
                renderer.ok();
                return renderer.getObject();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    protected void handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {

        // Start Renderer.
        ReturnValueDefinitionsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add ReturnValueDefinition.
        for (ReturnValueDefinition returnValueDefinition : itemDefinition.getActiveReturnValueDefinitions()) {
            returnValueDefinitionBuilder.handle(requestWrapper, returnValueDefinition);
            renderer.newReturnValueDefinition(returnValueDefinitionBuilder.getRenderer(requestWrapper));
        }
    }

    public ReturnValueDefinitionsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (returnValueDefinitionsRenderer == null) {
            returnValueDefinitionsRenderer = (ReturnValueDefinitionsResource.Renderer) rendererBeanFinder.getRenderer(ReturnValueDefinitionsResource.Renderer.class, requestWrapper);
        }
        return returnValueDefinitionsRenderer;
    }
}