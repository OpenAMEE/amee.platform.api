package com.amee.platform.resource.tag;

import com.amee.base.resource.*;
import com.amee.domain.tag.Tag;

public interface TagResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, Tag tag);

        public TagResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newTag(Tag tag);

        public void addBasic();

        public Object getObject();
    }

    public static interface FormAcceptor extends ResourceAcceptor {
    }

    public static interface Remover extends ResourceRemover {
    }
}
