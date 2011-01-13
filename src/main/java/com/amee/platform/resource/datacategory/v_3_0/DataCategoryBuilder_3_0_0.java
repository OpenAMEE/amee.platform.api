package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.EntityFilter;
import com.amee.platform.resource.EntityFilterValidationHelper;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoryBuilder_3_0_0 implements DataCategoryResource.Builder {

    @Autowired
    private DataService dataService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private EntityFilterValidationHelper validationHelper;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private DataCategoryResource.Renderer renderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get the DataCategory identifier.
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            // Validate.
            EntityFilter filter = new EntityFilter();
            validationHelper.setEntityFilter(filter);
            if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
                // Get DataCategory (filter by status).
                DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier, filter.getStatus());
                if (dataCategory != null) {
                    // Authorized?
                    resourceAuthorizationService.ensureAuthorizedForBuild(
                            requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                    // Handle the DataCategory.
                    this.handle(requestWrapper, dataCategory);
                    DataCategoryResource.Renderer renderer = getRenderer(requestWrapper);
                    renderer.ok();
                    return renderer.getObject();
                } else {
                    throw new NotFoundException();
                }
            } else {
                throw new ValidationException(validationHelper.getValidationResult());
            }
        } else {
            throw new MissingAttributeException("categoryIdentifier");
        }
    }

    public void handle(RequestWrapper requestWrapper, DataCategory dataCategory) {

        DataCategoryResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean parent = requestWrapper.getMatrixParameters().containsKey("parent");
        boolean authority = requestWrapper.getMatrixParameters().containsKey("authority");
        boolean history = requestWrapper.getMatrixParameters().containsKey("history");
        boolean wikiDoc = requestWrapper.getMatrixParameters().containsKey("wikiDoc");
        boolean provenance = requestWrapper.getMatrixParameters().containsKey("provenance");
        boolean itemDefinition = requestWrapper.getMatrixParameters().containsKey("itemDefinition");
        boolean tags = requestWrapper.getMatrixParameters().containsKey("tags");

        // New DataCategory & basic.
        renderer.newDataCategory(dataCategory);
        renderer.addBasic();

        // Optionals.
        if (path || full) {
            renderer.addPath();
        }
        if (parent || full) {
            renderer.addParent();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if (authority || full) {
            renderer.addAuthority();
        }
        if (history || full) {
            renderer.addHistory();
        }
        if (wikiDoc || full) {
            renderer.addWikiDoc();
        }
        if (provenance || full) {
            renderer.addProvenance();
        }
        if ((itemDefinition || full) && (dataCategory.getItemDefinition() != null)) {
            ItemDefinition id = dataCategory.getItemDefinition();
            renderer.addItemDefinition(id);
        }
        if (tags || full) {
            renderer.startTags();
            for (Tag tag : tagService.getTags(dataCategory)) {
                renderer.newTag(tag);
            }
        }
    }

    public DataCategoryResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DataCategoryResource.Renderer) resourceBeanFinder.getRenderer(DataCategoryResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}