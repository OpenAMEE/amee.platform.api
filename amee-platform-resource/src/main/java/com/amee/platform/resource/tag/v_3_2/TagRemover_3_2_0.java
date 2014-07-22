package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import com.amee.domain.data.DataCategory;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.tag.TagResource;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.invalidation.InvalidationService;
import com.amee.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This Remover removes either Tags or EntityTags depending on context. If an entity is present
 * in the resource path then an EntityTag is being removed otherwise the Tag itself is being removed.
 */
@Service
@Scope("prototype")
@Since("3.2.0")
public class TagRemover_3_2_0 implements TagResource.Remover {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private InvalidationService invalidationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {

        // Get Tag.
        Tag tag = resourceService.getTag(requestWrapper);

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
                    resourceAuthorizationService.ensureAuthorizedForModify(
                            requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                    // Remove the EntityTag.
                    log.debug("handle() Remove EntityTag.");
                    tagService.remove(entityTag);
                    // Need to invalidate the Data Category.
                    invalidationService.add(dataCategory);
                    // Woo!
                    return ResponseHelper.getOK(requestWrapper, null, tag.getUid());
                } else {
                    log.debug("handle() EntityTag does not exist.");
                    throw new NotFoundException();
                }
            } else {
                throw new IllegalStateException("Tags are currently only supported against DataCategories.");
            }
        } else {
            // No entity. This means we're removing the Tag itself.
            // Authorized?
            resourceAuthorizationService.ensureAuthorizedForRemove(
                    requestWrapper.getAttributes().get("activeUserUid"), tag);
            // Handle Tag removal.
            tagService.remove(tag);
            // Invalidation.
            for (EntityTag entityTag : tagService.getEntityTagsForTag(ObjectType.DC, tag)) {
                invalidationService.add(entityTag.getEntityReference());
            }
            // Woo!
            return ResponseHelper.getOK(requestWrapper, null, tag.getUid());
        }
    }
}
