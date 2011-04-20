package com.amee.platform.resource.itemvaluedefinition.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.ApiVersionSetEditor;
import com.amee.platform.resource.PerUnitEditor;
import com.amee.platform.resource.UnitEditor;
import com.amee.platform.resource.ValueDefinitionEditor;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.0.0")
public class ItemValueDefinitionValidator_3_0_0 extends BaseValidator implements ItemValueDefinitionResource.ItemValueDefinitionValidator {

    // Alpha numerics & underscore.
    private final static String PATH_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    @Autowired
    private ValueDefinitionEditor valueDefinitionEditor;

    @Autowired
    private ApiVersionSetEditor apiVersionSetEditor;

    private ItemValueDefinition itemValueDefinition;
    private Set<String> allowedFields = new HashSet<String>();

    public ItemValueDefinitionValidator_3_0_0() {
        super();
    }

    @Override
    public void initialise() {
        addValueDefintion();
        addName();
        addPath();
        addValue();
        addChoices();
        addUnit();
        addPerUnit();
        addWikiDoc();
        addApiVersions();
        allowedFields.add("fromProfile");
        allowedFields.add("fromData");
    }

    private void addApiVersions() {
        allowedFields.add("apiVersions");
        add(Set.class, "apiVersions", apiVersionSetEditor);
    }

    private void addValueDefintion() {
        allowedFields.add("valueDefinition");
        add(ValueDefinition.class, "valueDefinition", valueDefinitionEditor);
        add(new ValidationSpecification()
                .setName("valueDefinition")
                .setUid(true)
                .setAllowEmpty(false)
        );
    }

    private void addName() {
        allowedFields.add("name");
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(ItemValueDefinition.NAME_MIN_SIZE)
                .setMaxSize(ItemValueDefinition.NAME_MAX_SIZE)
        );
    }

    private void addPath() {
        allowedFields.add("path");
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
        allowedFields.add("wikiDoc");
        add(new ValidationSpecification()
                .setName("wikiDoc")
                .setMaxSize(ItemValueDefinition.WIKI_DOC_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    private void addUnit() {
        allowedFields.add("unit");
        add(AmountUnit.class, "unit", new UnitEditor());
        add(new ValidationSpecification()
                .setName("unit")
                .setMaxSize(ItemValueDefinition.UNIT_MAX_SIZE)
                .setAllowEmpty(true));
    }

    private void addPerUnit() {
        allowedFields.add("perUnit");
        add(AmountPerUnit.class, "perUnit", new PerUnitEditor());
        add(new ValidationSpecification()
                .setName("perUnit")
                .setMaxSize(ItemValueDefinition.PER_UNIT_MAX_SIZE)
                .setAllowEmpty(true));
    }

    private void addChoices() {
        allowedFields.add("choices");
        add(new ValidationSpecification()
                .setName("choices")
                .setMaxSize(ItemValueDefinition.CHOICES_MAX_SIZE)
                .setAllowEmpty(true));
    }

    private void addValue() {
        allowedFields.add("value");
        add(new ValidationSpecification()
                .setName("value")
                .setMaxSize(ItemValueDefinition.VALUE_MAX_SIZE)
                .setAllowEmpty(true));
    }

    @Override
    public String getName() {
        return "itemValueDefinition";
    }

    @Override
    public boolean supports(Class clazz) {
        return ItemValueDefinition.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public ItemValueDefinition getObject() {
        return itemValueDefinition;
    }

    @Override
    public void setObject(ItemValueDefinition itemValueDefinition) {
        this.itemValueDefinition = itemValueDefinition;
    }

    @Override
    protected void beforeBind(Map<String, String> values) {
        this.renameValue(values, "versions", "apiVersions");
    }
}