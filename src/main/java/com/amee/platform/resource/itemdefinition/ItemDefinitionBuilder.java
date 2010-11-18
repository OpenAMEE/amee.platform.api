package com.amee.platform.resource.itemdefinition;

import com.amee.base.domain.Since;
import com.amee.base.resource.*;
import com.amee.domain.data.ItemDefinition;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemDefinitionBuilder implements ResourceBuilder {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private ItemDefinitionRenderer itemDefinitionRenderer;

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
                // Handle the ItemDefinition.
                handle(requestWrapper, itemDefinition);
                ItemDefinitionRenderer renderer = getItemDefinitionRenderer(requestWrapper);
                renderer.ok();
                return renderer.getObject();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    protected void handle(
            RequestWrapper requestWrapper,
            ItemDefinition itemDefinition) {

        ItemDefinitionRenderer renderer = getItemDefinitionRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean drillDown = requestWrapper.getMatrixParameters().containsKey("drillDown");
        boolean usages = requestWrapper.getMatrixParameters().containsKey("usages");

        // New ItemValueDefinition & basic.
        renderer.newItemDefinition(itemDefinition);
        renderer.addBasic();

        // Optional attributes.
        if (name || full) {
            renderer.addName();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if (drillDown || full) {
            renderer.addDrillDown();
        }
        if (usages || full) {
            renderer.addUsages();
        }
    }

    public ItemDefinitionRenderer getItemDefinitionRenderer(RequestWrapper requestWrapper) {
        if (itemDefinitionRenderer == null) {
            itemDefinitionRenderer = (ItemDefinitionRenderer) rendererBeanFinder.getRenderer(ItemDefinitionRenderer.class, requestWrapper);
        }
        return itemDefinitionRenderer;
    }
}
