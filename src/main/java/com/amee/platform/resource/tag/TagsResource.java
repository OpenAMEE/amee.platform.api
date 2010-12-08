package com.amee.platform.resource.tag;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.Tag;

public interface TagsResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, IAMEEEntityReference entity);

        public TagsResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        public void newTag(TagResource.Renderer renderer);

        @Deprecated
        public void newTag(Tag tag);

        public Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }
}