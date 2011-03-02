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
        addValueDefintion();
        addName();
        addPath();
        addValue();
        addChoices();
        addUnit();
        addPerUnit();
        addWikiDoc();
    }

    @Override
    public boolean supports(Class clazz) {
        return ItemValueDefinition.class.isAssignableFrom(clazz);
    }

    private void addValueDefintion() {
        add(new ValidationSpecification()
            .setName("valueDefinition")
            .setUid(true)
            .setAllowEmpty(false)
        );
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
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure ItemValueDefinition path is unique amongst peers.
                                ItemValueDefinition itemValueDefinition = (ItemValueDefinition) object;
                                if (itemValueDefinition.getItemDefinition() != null) {
                                    for (ItemValueDefinition ivd : itemValueDefinition.getItemDefinition().getItemValueDefinitions()) {
                                        if (!itemValueDefinition.equals(ivd) && itemValueDefinition.getPath().equalsIgnoreCase(ivd.getPath())) {
                                            errors.rejectValue("path", "duplicate");
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

    private void addUnit() {
        add(new ValidationSpecification()
            .setName("unit")
            .setMaxSize(ItemValueDefinition.UNIT_MAX_SIZE)
            .setAllowEmpty(true));
    }

    private void addPerUnit() {
        add(new ValidationSpecification()
            .setName("perUnit")
            .setMaxSize(ItemValueDefinition.PER_UNIT_MAX_SIZE)
            .setAllowEmpty(true));
    }

    private void addChoices() {
        add(new ValidationSpecification()
            .setName("choices")
            .setMaxSize(ItemValueDefinition.CHOICES_MAX_SIZE)
            .setAllowEmpty(true));
    }

    private void addValue() {
        add(new ValidationSpecification()
            .setName("value")
            .setMaxSize(ItemValueDefinition.VALUE_MAX_SIZE)
            .setAllowEmpty(true));
    }
}