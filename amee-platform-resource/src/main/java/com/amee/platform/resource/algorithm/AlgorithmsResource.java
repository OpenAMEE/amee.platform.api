package com.amee.platform.resource.algorithm;

import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface AlgorithmsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        void newAlgorithm(AlgorithmResource.Renderer renderer);
    }

    interface FormAcceptor extends ResourceAcceptor {
    }
}
