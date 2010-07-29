package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.resource.*;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.environment.Environment;
import com.amee.service.definition.DefinitionService;
import com.amee.service.environment.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public abstract class ItemValueDefinitionBuilder implements ResourceBuilder {

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private DefinitionService definitionService;

    private ItemValueDefinitionRenderer renderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get Renderer.
        renderer = new RendererHelper<ItemValueDefinitionRenderer>().getRenderer(requestWrapper, getRenderers());
        // Get Environment.
        Environment environment = environmentService.getEnvironmentByName("AMEE");
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(
                    environment, itemDefinitionIdentifier);
            if (itemDefinition != null) {
                // Get ItemValueDefinition identifier.
                String itemValueDefinitionIdentifier = requestWrapper.getAttributes().get("itemValueDefinitionIdentifier");
                if (itemValueDefinitionIdentifier != null) {
                    // Get ItemValueDefinition.
                    ItemValueDefinition itemValueDefinition = definitionService.getItemValueDefinitionByUid(
                            itemDefinition, itemValueDefinitionIdentifier);
                    if (itemValueDefinition != null) {
                        // Handle the ItemValueDefinition.
                        handle(requestWrapper, itemValueDefinition, renderer);
                        renderer.ok();
                    } else {
                        throw new NotFoundException();
                    }
                } else {
                    throw new MissingAttributeException("itemValueDefinitionIdentifier");
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
        return renderer.getObject();
    }

    protected void handle(
            RequestWrapper requestWrapper,
            ItemValueDefinition itemValueDefinition,
            ItemValueDefinitionRenderer renderer) {

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean wikiDoc = requestWrapper.getMatrixParameters().containsKey("wikiDoc");
        boolean itemDefinition = requestWrapper.getMatrixParameters().containsKey("itemDefinition");
        boolean usages = requestWrapper.getMatrixParameters().containsKey("usages");

        // New ItemValueDefinition & basic.
        renderer.newItemValueDefinition(itemValueDefinition);
        renderer.addBasic();

        // Optional attributes.
        if (name || full) {
            renderer.addName();
        }
        if (path || full) {
            renderer.addPath();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if (wikiDoc || full) {
            renderer.addWikiDoc();
        }
        if ((itemDefinition || full) && (itemValueDefinition.getItemDefinition() != null)) {
            ItemDefinition id = itemValueDefinition.getItemDefinition();
            renderer.addItemDefinition(id);
        }
        if (usages || full) {
            renderer.addUsages();
        }
    }

    public abstract Map<String, Class> getRenderers();
}
