package com.amee.platform.resource.tag;

import com.amee.base.resource.Renderer;
import com.amee.domain.tag.Tag;

public interface TagsRenderer extends Renderer {

    public void newTag(Tag tag);

    public Object getObject();
}
