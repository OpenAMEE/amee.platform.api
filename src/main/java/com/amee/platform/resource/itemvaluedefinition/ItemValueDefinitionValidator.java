package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.ItemValueDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@Scope("prototype")
public class ItemValueDefinitionValidator extends BaseValidator {

    // Alpha numerics & underscore.
    private final static String PATH_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    public ItemValueDefinitionValidator() {
        super();
        addName();
        addPath();
        addWikiDoc();
    }

    public boolean supports(Class clazz) {
        return ItemValueDefinition.class.isAssignableFrom(clazz);
    }

    private void addName() {
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(ItemValueDefinition.NAME_MIN_SIZE)
                .setMaxSize(ItemValueDefinition.NAME_MAX_SIZE)
        );
    }

    private void addPath() {
        add(new ValidationSpecification()
                .setName("path")
                .setMinSize(ItemValueDefinition.PATH_MIN_SIZE)
                .setMaxSize(ItemValueDefinition.PATH_MAX_SIZE)
                .setFormat(PATH_PATTERN_STRING)
                .setCustomValidation(
                new ValidationSpecification.CustomValidation() {
                    @Override
                    public int validate(Object o, Errors e) {
                        // Ensure ItemValueDefinition path is unique amongst peers.
                        ItemValueDefinition itemValueDefinition = (ItemValueDefinition) o;
                        if (itemValueDefinition.getItemDefinition() != null) {
                            for (ItemValueDefinition ivd : itemValueDefinition.getItemDefinition().getItemValueDefinitions()) {
                                if ((itemValueDefinition != ivd) && itemValueDefinition.getPath().equalsIgnoreCase(ivd.getPath())) {
                                    e.rejectValue("path", "duplicate");
                                    break;
                                }
                            }
                        }
                        return ValidationSpecification.CONTINUE;
                    }
                })
        );
    }

    private void addWikiDoc() {
        add(new ValidationSpecification()
                .setName("wikiDoc")
                .setMaxSize(ItemValueDefinition.WIKI_DOC_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }
}