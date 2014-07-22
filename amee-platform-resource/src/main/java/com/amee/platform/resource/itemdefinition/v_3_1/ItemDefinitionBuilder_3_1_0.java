package com.amee.platform.resource.itemdefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
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
public class ItemDefinitionBuilder_3_1_0 implements ItemDefinitionResource.Builder {

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private ItemDefinitionResource.Renderer itemDefinitionRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), itemDefinition);

        // Handle the ItemDefinition.
        handle(requestWrapper, itemDefinition);
        ItemDefinitionResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    public void handle(
            RequestWrapper requestWrapper,
            ItemDefinition itemDefinition) {

        ItemDefinitionResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean drillDown = requestWrapper.getMatrixParameters().containsKey("drillDown");
        boolean usages = requestWrapper.getMatrixParameters().containsKey("usages");
        boolean algorithms = requestWrapper.getMatrixParameters().containsKey("algorithms");

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
        if (algorithms || full) {
            renderer.addAlgorithms();
        }
    }

    public ItemDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (itemDefinitionRenderer == null) {
            itemDefinitionRenderer = (ItemDefinitionResource.Renderer) resourceBeanFinder.getRenderer(ItemDefinitionResource.Renderer.class, requestWrapper);
        }
        return itemDefinitionRenderer;
    }
}
