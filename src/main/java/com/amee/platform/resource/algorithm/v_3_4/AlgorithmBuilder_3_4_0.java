package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class AlgorithmBuilder_3_4_0 implements AlgorithmResource.Builder {

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private AlgorithmResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Get Algorithm.
        Algorithm algorithm = resourceService.getAlgorithm(requestWrapper, itemDefinition);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), algorithm);

        // Handle the Algorithm.
        handle(requestWrapper, algorithm);
        AlgorithmResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    public void handle(RequestWrapper requestWrapper, Algorithm algorithm) {

        AlgorithmResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean content = requestWrapper.getMatrixParameters().containsKey("content");
        boolean itemDefinition = requestWrapper.getMatrixParameters().containsKey("itemDefinition");

        // New Algorithm & basic.
        renderer.newAlgorithm(algorithm);
        renderer.addBasic();

        // Optional attributes.
        if (audit || full) {
            renderer.addAudit();
        }
        if (name || full) {
            renderer.addName();
        }
        if (content || full) {
            renderer.addContent();
        }
        if ((itemDefinition || full) && (algorithm.getItemDefinition() != null)) {
            renderer.addItemDefinition(algorithm.getItemDefinition());
        }
    }

    public AlgorithmResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (AlgorithmResource.Renderer) resourceBeanFinder.getRenderer(AlgorithmResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
