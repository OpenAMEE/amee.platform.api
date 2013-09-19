package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionResource;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionsBuilder_3_1_0 implements ReturnValueDefinitionsResource.Builder {

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private ReturnValueDefinitionsResource.Renderer returnValueDefinitionsRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), itemDefinition);

        // Handle the ItemDefinition & ReturnValueDefinitions.
        handle(requestWrapper, itemDefinition);
        ReturnValueDefinitionsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    protected void handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {

        // Start Renderer.
        ReturnValueDefinitionsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add ReturnValueDefinition.
        ReturnValueDefinitionResource.Builder returnValueDefinitionBuilder = getReturnValueDefinitionBuilder(requestWrapper);
        for (ReturnValueDefinition returnValueDefinition : itemDefinition.getActiveReturnValueDefinitions()) {
            returnValueDefinitionBuilder.handle(requestWrapper, returnValueDefinition);
            renderer.newReturnValueDefinition(returnValueDefinitionBuilder.getRenderer(requestWrapper));
        }
    }

    public ReturnValueDefinitionsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (returnValueDefinitionsRenderer == null) {
            returnValueDefinitionsRenderer = (ReturnValueDefinitionsResource.Renderer) resourceBeanFinder.getRenderer(ReturnValueDefinitionsResource.Renderer.class, requestWrapper);
        }
        return returnValueDefinitionsRenderer;
    }

    private ReturnValueDefinitionResource.Builder getReturnValueDefinitionBuilder(RequestWrapper requestWrapper) {
        return (ReturnValueDefinitionResource.Builder)
                resourceBeanFinder.getBuilder(ReturnValueDefinitionResource.Builder.class, requestWrapper);
    }
}