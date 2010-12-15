package com.amee.platform.resource.tag;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.tag.Tag;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@Scope("prototype")
public class TagValidator extends BaseValidator {

    // Alpha numerics & underscore.
    private final static String TAG_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    @Autowired
    private TagService tagService;

    public TagValidator() {
        super();
        addTag();
    }

    @Override
    public boolean supports(Class clazz) {
        return Tag.class.isAssignableFrom(clazz);
    }

    private void addTag() {
        add(new ValidationSpecification()
                .setName("tag")
                .setMinSize(Tag.TAG_MIN_SIZE)
                .setMaxSize(Tag.TAG_MAX_SIZE)
                .setFormat(TAG_PATTERN_STRING)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object o, Errors e) {
                                // Ensure Tag is unique.
                                Tag thisTag = (Tag) o;
                                if (thisTag != null) {
                                    Tag otherTag = tagService.getTagByTag(thisTag.getTag());
                                    if ((otherTag != null) && !otherTag.equals(thisTag)) {
                                        e.rejectValue("tag", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    /**
     * Setter used by unit tests.
     *
     * @param tagService a TagService (probably mocked)
     */
    protected void setTagService(TagService tagService) {
        this.tagService = tagService;
    }
}