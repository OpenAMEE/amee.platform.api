package com.amee.platform.resource.tag;

import com.amee.base.resource.*;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.Tag;
import com.amee.service.tag.TagsFilter;

import java.util.Map;

public interface TagsResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, IAMEEEntityReference entity);

        public TagsResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newTag(TagResource.Renderer renderer);

        @Deprecated
        public void newTag(Tag tag);
    }

    public static interface FormAcceptor extends ResourceAcceptor {
    }

    public static interface TagsFilterValidationHelper {

        public TagsFilter getTagsFilter();

        public void setTagsFilter(TagsFilter tagsFilter);

        public boolean isValid(Map<String, String> queryParameters);

        public ValidationResult getValidationResult();
    }
}