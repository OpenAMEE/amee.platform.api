package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionsBuilder_3_1_0 implements ItemValueDefinitionsResource.Builder {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private ItemValueDefinitionsResource.Renderer itemValueDefinitionsRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(itemDefinitionIdentifier);
            if (itemDefinition != null) {
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForBuild(
                        requestWrapper.getAttributes().get("activeUserUid"), itemDefinition);
                // Handle the ItemDefinition & ItemValueDefinitions.
                handle(requestWrapper, itemDefinition);
                ItemValueDefinitionsResource.Renderer renderer = getRenderer(requestWrapper);
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
        ItemValueDefinitionsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add ItemValueDefinition.
        ItemValueDefinitionResource.Builder itemValueDefinitionBuilder = getItemValueDefinitionBuilder(requestWrapper);
        for (ItemValueDefinition itemValueDefinition : itemDefinition.getActiveItemValueDefinitions()) {
            itemValueDefinitionBuilder.handle(requestWrapper, itemValueDefinition);
            renderer.newItemValueDefinition(itemValueDefinitionBuilder.getRenderer(requestWrapper));
        }
    }

    public ItemValueDefinitionsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (itemValueDefinitionsRenderer == null) {
            itemValueDefinitionsRenderer = (ItemValueDefinitionsResource.Renderer) resourceBeanFinder.getRenderer(ItemValueDefinitionsResource.Renderer.class, requestWrapper);
        }
        return itemValueDefinitionsRenderer;
    }

    private ItemValueDefinitionResource.Builder getItemValueDefinitionBuilder(RequestWrapper requestWrapper) {
        return (ItemValueDefinitionResource.Builder)
                resourceBeanFinder.getBuilder(ItemValueDefinitionResource.Builder.class, requestWrapper);
    }
}