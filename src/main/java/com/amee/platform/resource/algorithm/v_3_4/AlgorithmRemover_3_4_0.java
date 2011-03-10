package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.algorithm.AlgorithmResource;
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
public class AlgorithmRemover_3_4_0 implements AlgorithmResource.Remover {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceService resourceService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Get Algorithm.
        Algorithm algorithm = resourceService.getAlgorithm(requestWrapper, itemDefinition);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForRemove(
                requestWrapper.getAttributes().get("activeUserUid"), algorithm);

        // Handle Algorithm removal.
        definitionService.remove(algorithm);
        definitionService.invalidate(itemDefinition);
        return ResponseHelper.getOK(requestWrapper);
    }
}
