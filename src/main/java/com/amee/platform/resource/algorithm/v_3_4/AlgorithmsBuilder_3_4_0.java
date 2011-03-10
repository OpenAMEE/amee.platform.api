package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import com.amee.platform.resource.algorithm.AlgorithmsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class AlgorithmsBuilder_3_4_0 implements AlgorithmsResource.Builder {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private AlgorithmsResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), itemDefinition);

        // Handle the ItemDefinition & Algorithms.
        handle(requestWrapper, itemDefinition);
        AlgorithmsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    protected void handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {

        // Start Renderer.
        AlgorithmsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add Algorithm.
        AlgorithmResource.Builder algorithmBuilder = getAlgorithmBuilder(requestWrapper);
        for (Algorithm algorithm : itemDefinition.getAlgorithms()) {
            algorithmBuilder.handle(requestWrapper, algorithm);
            renderer.newAlgorithm(algorithmBuilder.getRenderer(requestWrapper));
        }
    }

    public AlgorithmsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (AlgorithmsResource.Renderer) resourceBeanFinder.getRenderer(AlgorithmsResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }

    private AlgorithmResource.Builder getAlgorithmBuilder(RequestWrapper requestWrapper) {
        return (AlgorithmResource.Builder)
                resourceBeanFinder.getBuilder(AlgorithmResource.Builder.class, requestWrapper);
    }
}