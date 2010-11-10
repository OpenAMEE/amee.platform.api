package com.amee.platform.resource.tag;

import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.tag.Tag;

public interface TagsResource {
    
    interface TagsRenderer extends ResourceRenderer {

        public void newTag(Tag tag);

        public Object getObject();
    }
}
