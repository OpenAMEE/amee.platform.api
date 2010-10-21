package com.amee.platform.resource.tag;

import com.amee.base.resource.RendererBeanFinder;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.Tag;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class TagsBuilder implements ResourceBuilder {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private TagsRenderer tagsRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        handle(requestWrapper, tagResourceService.getEntity(requestWrapper));
        TagsRenderer renderer = getTagsRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    protected void handle(RequestWrapper requestWrapper, IAMEEEntityReference entity) {
        TagsRenderer renderer = getTagsRenderer(requestWrapper);
        renderer.start();
        for (Tag tag : tagService.getTags(entity)) {
            renderer.newTag(tag);
        }
    }

    public TagsRenderer getTagsRenderer(RequestWrapper requestWrapper) {
        if (tagsRenderer == null) {
            tagsRenderer = (TagsRenderer) rendererBeanFinder.getRenderer(TagsRenderer.class, requestWrapper);
        }
        return tagsRenderer;
    }
}