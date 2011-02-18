package com.amee.platform.resource.tag.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.data.DataCategory;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.platform.resource.tag.TagValidationHelper;
import com.amee.platform.resource.tag.TagsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.invalidation.InvalidationService;
import com.amee.service.tag.TagService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class TagsFormAcceptor_3_0_0 implements TagsResource.FormAcceptor {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private DataService dataService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private TagValidationHelper validationHelper;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        Tag tag;
        // Get existing tag if it exists.
        Tag existingTag = tagService.getTagByTag(requestWrapper.getFormParameters().get("tag"));
        if (existingTag == null) {
            // Create new Tag.
            tag = new Tag();
            validationHelper.setTag(tag);
            if (validationHelper.isValid(requestWrapper.getFormParameters())) {
                // Save new Tag.
                log.debug("handle() Use new Tag.");
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForAccept(
                        requestWrapper.getAttributes().get("activeUserUid"));
                // Don't allow more than 1000 tags.
                // NOTE: See https://jira.amee.com/browse/PL-1761
                // Note: See https://jira.amee.com/browse/PL-1947
                if (tagService.getTagCount() >= 1000) {
                    throw new IllegalStateException("Only 1000 tags are allowed.");
                }
                // Save Tag.
                tagService.persist(tag);
            } else {
                throw new ValidationException(validationHelper.getValidationResult());
            }
        } else {
            // Use existing tag.
            log.debug("handle() Use existing Tag.");
            tag = existingTag;
        }
        // Deal with an entity if present.
        IAMEEEntityReference entity = tagResourceService.getEntity(requestWrapper);
        if (entity != null) {
            // TODO: Intention is to support entities other than DataCategory at some point.
            if (DataCategory.class.isAssignableFrom(entity.getClass())) {
                DataCategory dataCategory = (DataCategory) entity;
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForModify(
                        requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                // Find existing EntityTag.
                EntityTag entityTag = tagService.getEntityTag(dataCategory, tag.getTag());
                if (entityTag == null) {
                    // Create and save EntityTag.
                    log.debug("handle() Use new EntityTag.");
                    entityTag = new EntityTag(dataCategory, tag);
                    tagService.persist(entityTag);
                    // Need to invalidate the Data Category.
                    invalidationService.add(dataCategory);
                } else {
                    log.debug("handle() EntityTag already exists.");
                }
            } else {
                throw new IllegalStateException("Tags are currently only supported against DataCategories.");
            }
        } else {
            log.debug("handle() No entity.");
        }
        // Woo!
        return ResponseHelper.getOK(requestWrapper);
    }
}