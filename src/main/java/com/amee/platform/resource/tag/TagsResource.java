package com.amee.platform.resource.tag;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.ResourceValidator;
import com.amee.service.tag.TagsFilter;

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

    public static interface TagsFilterValidationHelper extends ResourceValidator<TagsFilter> {
    }
}