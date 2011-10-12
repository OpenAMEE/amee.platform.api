package com.amee.platform.resource.algorithm;

import com.amee.base.resource.*;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

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

        public Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    interface AlgorithmValidator {

        public boolean isValid(Map<String, String> queryParameters);

        public Algorithm getObject();

        public void setObject(Algorithm object);

        public ValidationResult getValidationResult();
    }

    interface Remover extends ResourceRemover {
    }
}
