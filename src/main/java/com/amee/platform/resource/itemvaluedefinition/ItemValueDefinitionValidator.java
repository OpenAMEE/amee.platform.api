package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.ItemValueDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
public class ItemValueDefinitionValidator implements Validator {

    // Alpha numerics & underscore.
    private final static String PATH_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    private ValidationSpecification nameSpec;
    private ValidationSpecification pathSpec;
    private ValidationSpecification wikiDocSpec;

    public ItemValueDefinitionValidator() {
        super();
        // name
        nameSpec = new ValidationSpecification();
        nameSpec.setName("name");
        nameSpec.setMinSize(ItemValueDefinition.NAME_MIN_SIZE);
        nameSpec.setMaxSize(ItemValueDefinition.NAME_MAX_SIZE);
        // path
        pathSpec = new ValidationSpecification();
        pathSpec.setName("path");
        pathSpec.setMinSize(ItemValueDefinition.PATH_MIN_SIZE);
        pathSpec.setMaxSize(ItemValueDefinition.PATH_MAX_SIZE);
        pathSpec.setFormat(PATH_PATTERN_STRING);
        pathSpec.setCustomValidation(new ValidationSpecification.CustomValidation() {
            @Override
            public int validate(Object o, Errors e) {
                // Ensure ItemValueDefinition path is unique amongst peers. 
                ItemValueDefinition itemValueDefinition = (ItemValueDefinition) o;
                for (ItemValueDefinition ivd : itemValueDefinition.getItemDefinition().getItemValueDefinitions()) {
                    if ((itemValueDefinition != ivd) && itemValueDefinition.getPath().equalsIgnoreCase(ivd.getPath())) {
                        e.rejectValue("path", "duplicate");
                        break;
                    }
                }
                return ValidationSpecification.CONTINUE;
            }
        });
        // wikiDoc
        wikiDocSpec = new ValidationSpecification();
        wikiDocSpec.setName("wikiDoc");
        wikiDocSpec.setMaxSize(ItemValueDefinition.WIKI_DOC_MAX_SIZE);
        wikiDocSpec.setAllowEmpty(true);
    }

    public boolean supports(Class clazz) {
        return ItemValueDefinition.class.isAssignableFrom(clazz);
    }

    public void validate(Object o, Errors e) {
        nameSpec.validate(o, e);
        pathSpec.validate(o, e);
        wikiDocSpec.validate(o, e);
    }
}