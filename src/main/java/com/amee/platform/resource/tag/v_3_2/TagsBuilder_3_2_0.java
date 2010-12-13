package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.platform.resource.tag.TagsResource;
import com.amee.service.tag.TagService;
import com.amee.service.tag.TagsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagsBuilder_3_2_0 implements TagsResource.Builder {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private TagsResource.Renderer tagsRenderer;

    @Override
    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        handle(requestWrapper, tagResourceService.getEntity(requestWrapper));
        TagsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public void handle(RequestWrapper requestWrapper, IAMEEEntityReference entity) {
        TagsFilter filter = new TagsFilter();
        TagsResource.TagsFilterValidationHelper validationHelper = getValidationHelper(requestWrapper);
        validationHelper.setTagsFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
            TagsResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.start();
            TagResource.Builder builder = getTagBuilder(requestWrapper);
            for (Tag tag : tagService.getTags(entity, filter.getIncTags(), filter.getExcTags())) {
                builder.handle(requestWrapper, tag);
                renderer.newTag(builder.getRenderer(requestWrapper));
            }
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    @Override
    public TagsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (tagsRenderer == null) {
            tagsRenderer = (TagsResource.Renderer) resourceBeanFinder.getRenderer(TagsResource.Renderer.class, requestWrapper);
        }
        return tagsRenderer;
    }

    private TagResource.Builder getTagBuilder(RequestWrapper requestWrapper) {
        return (TagResource.Builder)
                resourceBeanFinder.getBuilder(TagResource.Builder.class, requestWrapper);
    }

    private TagsResource.TagsFilterValidationHelper getValidationHelper(RequestWrapper requestWrapper) {
        return (TagsResource.TagsFilterValidationHelper)
                resourceBeanFinder.getValidationHelper(TagsResource.TagsFilterValidationHelper.class, requestWrapper);
    }
}