package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.data.DataCategory;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagBuilder_3_2_0 implements TagResource.Builder {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private TagService tagService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    private TagResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get Tag identifier.
        String tagIdentifier = requestWrapper.getAttributes().get("tagIdentifier");
        if (tagIdentifier != null) {
            // Get Tag.
            Tag tag = tagService.getTagByIdentifier(tagIdentifier);
            if (tag != null) {
                // Deal with an entity (if present).
                IAMEEEntityReference entity = tagResourceService.getEntity(requestWrapper);
                if (entity != null) {
                    // TODO: Intention is to support entities other than DataCategory at some point.
                    if (DataCategory.class.isAssignableFrom(entity.getClass())) {
                        DataCategory dataCategory = (DataCategory) entity;
                        // Find existing EntityTag.
                        EntityTag entityTag = tagService.getEntityTag(dataCategory, tag.getTag());
                        if (entityTag != null) {
                            // Authorized?
                            resourceAuthorizationService.ensureAuthorizedForBuild(
                                    requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                            // Handle the EntityTag.
                            // TODO: Any representation difference required for EntityTags over and above Tags?
                            handle(requestWrapper, tag);
                            TagResource.Renderer renderer = getRenderer(requestWrapper);
                            renderer.ok();
                            return renderer.getObject();
                        } else {
                            log.debug("handle() EntityTag does not exist.");
                            throw new NotFoundException();
                        }
                    } else {
                        throw new IllegalStateException("Tags are currently only supported against DataCategories.");
                    }
                } else {
                    // No entity. This means we're accessing the Tag itself.
                    // Authorized?
                    resourceAuthorizationService.ensureAuthorizedForBuild(
                            requestWrapper.getAttributes().get("activeUserUid"), tag);
                    // Handle the Tag.
                    handle(requestWrapper, tag);
                    TagResource.Renderer renderer = getRenderer(requestWrapper);
                    renderer.ok();
                    return renderer.getObject();
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("tagIdentifier");
        }
    }

    @Override
    public void handle(RequestWrapper requestWrapper, Tag tag) {

        TagResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // New Tag & basic.
        renderer.newTag(tag);
        renderer.addBasic();
    }

    @Override
    public TagResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (TagResource.Renderer) resourceBeanFinder.getRenderer(TagResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
