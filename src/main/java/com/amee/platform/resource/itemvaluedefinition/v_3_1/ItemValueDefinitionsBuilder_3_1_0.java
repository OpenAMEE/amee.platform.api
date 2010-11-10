package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.*;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsResource;
import com.amee.platform.resource.itemvaluedefinition.v_3_0.ItemValueDefinitionBuilder_3_0_0;
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
    private ItemValueDefinitionBuilder_3_0_0 itemValueDefinitionBuilder;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private ItemValueDefinitionsResource.Renderer itemValueDefinitionsRenderer;

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
        for (ItemValueDefinition itemValueDefinition : itemDefinition.getActiveItemValueDefinitions()) {
            itemValueDefinitionBuilder.handle(requestWrapper, itemValueDefinition);
            renderer.newItemValueDefinition(itemValueDefinitionBuilder.getRenderer(requestWrapper));
        }
    }

    public ItemValueDefinitionsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (itemValueDefinitionsRenderer == null) {
            itemValueDefinitionsRenderer = (ItemValueDefinitionsResource.Renderer) rendererBeanFinder.getRenderer(ItemValueDefinitionsResource.Renderer.class, requestWrapper);
        }
        return itemValueDefinitionsRenderer;
    }
}