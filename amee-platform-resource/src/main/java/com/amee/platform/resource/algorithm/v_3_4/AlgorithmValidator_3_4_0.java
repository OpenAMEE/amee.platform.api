package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.algorithm.AbstractAlgorithm;
import com.amee.domain.algorithm.Algorithm;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.4.0")
public class AlgorithmValidator_3_4_0 extends BaseValidator implements AlgorithmResource.AlgorithmValidator {

    private Algorithm object;
    private Set<String> allowedFields;

    public AlgorithmValidator_3_4_0() {
        super();
        addName();
        addContent();
    }

    private void addName() {
        add(new ValidationSpecification()
                .setName("name")
                .setMaxSize(AbstractAlgorithm.NAME_MAX_SIZE)
        );
    }

    private void addContent() {
        add(new ValidationSpecification()
                .setName("content")
                .setMaxSize(AbstractAlgorithm.CONTENT_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    @Override
    public String getName() {
        return "algorithm";
    }

    @Override
    public boolean supports(Class clazz) {
        return Algorithm.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("content");
        }
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public Algorithm getObject() {
        return object;
    }

    @Override
    public void setObject(Algorithm object) {
        this.object = object;
    }
}