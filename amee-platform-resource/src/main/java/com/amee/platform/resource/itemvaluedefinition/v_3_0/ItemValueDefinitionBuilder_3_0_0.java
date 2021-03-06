package com.amee.platform.resource.itemvaluedefinition.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class ItemValueDefinitionBuilder_3_0_0 implements ItemValueDefinitionResource.Builder {

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private ItemValueDefinitionResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Get ItemValueDefinition.
        ItemValueDefinition itemValueDefinition = resourceService.getItemValueDefinition(requestWrapper, itemDefinition);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), itemValueDefinition);

        // Handle the ItemValueDefinition.
        handle(requestWrapper, itemValueDefinition);
        ItemValueDefinitionResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    public void handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition) {

        ItemValueDefinitionResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean value = requestWrapper.getMatrixParameters().containsKey("value");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean wikiDoc = requestWrapper.getMatrixParameters().containsKey("wikiDoc");
        boolean itemDefinition = requestWrapper.getMatrixParameters().containsKey("itemDefinition");
        boolean valueDefinition = requestWrapper.getMatrixParameters().containsKey("valueDefinition");
        boolean usages = requestWrapper.getMatrixParameters().containsKey("usages");
        boolean choices = requestWrapper.getMatrixParameters().containsKey("choices");
        boolean units = requestWrapper.getMatrixParameters().containsKey("units");
        boolean flags = requestWrapper.getMatrixParameters().containsKey("flags");
        boolean versions = requestWrapper.getMatrixParameters().containsKey("versions");

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
        if (value || full) {
            renderer.addValue();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if (wikiDoc || full) {
            renderer.addWikiDoc();
        }
        if ((itemDefinition || full) && (itemValueDefinition.getItemDefinition() != null)) {
            renderer.addItemDefinition(itemValueDefinition.getItemDefinition());
        }
        if ((valueDefinition || full) && (itemValueDefinition.getValueDefinition() != null)) {
            renderer.addValueDefinition(itemValueDefinition.getValueDefinition());
        }
        if (usages || full) {
            renderer.addUsages();
        }
        if (choices || full) {
            renderer.addChoices();
        }
        if (units || full) {
            renderer.addUnits();
        }
        if (flags || full) {
            renderer.addFlags();
        }
        if (versions || full) {
            renderer.addVersions();
        }
    }

    public ItemValueDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (ItemValueDefinitionResource.Renderer) resourceBeanFinder.getRenderer(ItemValueDefinitionResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
