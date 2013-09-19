package com.amee.platform.resource.algorithm;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceValidator;

public interface AlgorithmResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, Algorithm algorithm);

        public AlgorithmResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        public void newAlgorithm(Algorithm algorithm);

        public void addBasic();

        public void addAudit();

        public void addName();

        public void addContent();

        public void addItemDefinition(ItemDefinition itemDefinition);
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    interface AlgorithmValidator extends ResourceValidator<Algorithm> {
    }

    interface Remover extends ResourceRemover {
    }
}
