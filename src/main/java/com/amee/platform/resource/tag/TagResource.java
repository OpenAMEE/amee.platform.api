package com.amee.platform.resource.tag;

import com.amee.base.resource.*;
import com.amee.domain.tag.Tag;

public interface TagResource {

    interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, Tag tag);

        TagResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void newTag(Tag tag);

        void addBasic();

        Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    interface Remover extends ResourceRemover {
    }
}
