package com.amee.platform.resource.tag;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.tag.Tag;
import com.amee.service.tag.TagsFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Scope("prototype")
public class TagsFilterValidator extends BaseValidator {

    private static class TagListValidation implements ValidationSpecification.CustomValidation {

        private final static Pattern TAG_PATTERN = Pattern.compile(TagValidator.TAG_PATTERN_STRING);

        private String name;

        public TagListValidation(String name) {
            super();
            this.name = name;
        }

        @Override
        public int validate(Object object, Object value, Errors errors) {
            // Ensure each Tag value is valid.
            List<String> tags = (List<String>) value;
            if (tags != null) {
                for (String tag : tags) {
                    if (tag.length() < Tag.TAG_MIN_SIZE) {
                        errors.rejectValue(name, "short");
                    }
                    if (tag.length() > Tag.TAG_MAX_SIZE) {
                        errors.rejectValue(name, "long");
                    }
                    if (!TAG_PATTERN.matcher(tag).matches()) {
                        errors.rejectValue(name, "format");
                    }
                }
            }
            return ValidationSpecification.CONTINUE;
        }
    }

    public TagsFilterValidator() {
        super();
        addTags("incTags");
        addTags("excTags");
    }

    @Override
    public boolean supports(Class clazz) {
        return TagsFilter.class.isAssignableFrom(clazz);
    }

    private void addTags(String name) {
        add(new ValidationSpecification()
                .setName(name)
                .setAllowEmpty(true)
                .setCustomValidation(new TagListValidation(name))
        );
    }
}
