package com.amee.platform.resource.tag;

import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.tag.Tag;

public interface TagsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public void newTag(Tag tag);

        public Object getObject();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }
}
