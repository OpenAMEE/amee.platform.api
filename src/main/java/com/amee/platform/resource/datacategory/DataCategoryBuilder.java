package com.amee.platform.resource.datacategory;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.EntityFilter;
import com.amee.platform.resource.EntityFilterValidationHelper;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.tag.TagService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class DataCategoryBuilder implements ResourceBuilder {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private DataService dataService;

    @Autowired
    private TagService tagService;

    @Autowired
    private EntityFilterValidationHelper validationHelper;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    private DataCategory dataCategory;
    private DataCategoryRenderer dataCategoryRenderer;

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
                dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier, filter.getStatus());
                if (dataCategory != null) {
                    // Authorized?
                    resourceAuthorizationService.ensureAuthorizedForBuild(
                            requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                    // Handle the DataCategory.
                    handleDataCategory(requestWrapper);
                    DataCategoryRenderer renderer = getDataCategoryRenderer(requestWrapper);
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
        this.dataCategory = dataCategory;
        handleDataCategory(requestWrapper);
    }

    public void handleDataCategory(RequestWrapper requestWrapper) {

        DataCategoryRenderer renderer = getDataCategoryRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean parent = requestWrapper.getMatrixParameters().containsKey("parent");
        boolean authority = requestWrapper.getMatrixParameters().containsKey("authority");
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

    public DataCategoryRenderer getDataCategoryRenderer(RequestWrapper requestWrapper) {
        if (dataCategoryRenderer == null) {
            dataCategoryRenderer = (DataCategoryRenderer) rendererBeanFinder.getRenderer(DataCategoryRenderer.class, requestWrapper);
        }
        return dataCategoryRenderer;
    }
}