package com.amee.platform.resource.tag;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.tag.Tag;

public interface TagResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, Tag tag);

        public TagResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        public void newTag(Tag tag);

        public void addBasic();

        public Object getObject();
    }

    interface Remover extends ResourceRemover {
    }
}
