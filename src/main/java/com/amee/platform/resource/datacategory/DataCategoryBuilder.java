package com.amee.platform.resource.datacategory;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.AMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.auth.AccessSpecification;
import com.amee.domain.auth.AuthorizationContext;
import com.amee.domain.auth.PermissionEntry;
import com.amee.domain.auth.User;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.EntityFilter;
import com.amee.platform.resource.EntityFilterValidationHelper;
import com.amee.service.auth.AuthenticationService;
import com.amee.service.auth.AuthorizationService;
import com.amee.service.auth.GroupService;
import com.amee.service.data.DataService;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class DataCategoryBuilder implements ResourceBuilder {

    @Autowired
    private DataService dataService;

    @Autowired
    private TagService tagService;

    @Autowired
    private EntityFilterValidationHelper validationHelper;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private GroupService groupService;

    private DataCategory dataCategory;
    private DataCategoryRenderer dataCategoryRenderer;
    private User user;
    private AuthorizationContext authorizationContext;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get the User.
        user = authenticationService.getUserByUid(requestWrapper.getAttributes().get("activeUserUid"));
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
                    if (isAuthorized(getGetAccessSpecifications())) {
                        // Handle the DataCategory.
                        handleDataCategory(requestWrapper);
                        DataCategoryRenderer renderer = getDataCategoryRenderer(requestWrapper);
                        renderer.ok();
                        return renderer.getObject();
                    } else {
                        throw new NotAuthorizedException();
                    }
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

    /**
     * Returns true if the request is authorized, otherwise false. AuthorizationService is used to
     * do the authorization based on the supplied AccessSpecifications and the principals from GetPrincipals.
     *
     * @param accessSpecifications to use for authorization
     * @return true if the request is authorized, otherwise false
     */
    public boolean isAuthorized(List<AccessSpecification> accessSpecifications) {
        authorizationContext = new AuthorizationContext();
        authorizationContext.addPrincipals(getPrincipals());
        authorizationContext.addAccessSpecifications(accessSpecifications);
        boolean authorized = authorizationService.isAuthorized(authorizationContext);
        if (authorized && isRequireSuperUser() && !authorizationContext.isSuperUser()) {
            authorized = false;
        }
        return authorized;
    }

    /**
     * Returns a list of principals involved in authorization. Permissions from these principals will
     * be compared against AccessSpecifications to determine if a request is authorized.
     *
     * @return a list of principals
     */
    public List<AMEEEntity> getPrincipals() {
        List<AMEEEntity> principals = new ArrayList<AMEEEntity>();
        principals.addAll(groupService.getGroupsForPrincipal(getActiveUser()));
        principals.add(getActiveUser());
        return principals;
    }

    /**
     * Get the AccessSpecifications for GET requests. Creates an AccessSpecification for each entity
     * from getEntities with VIEW as the PermissionEntry. This specifies that principals must have VIEW permissions
     * for all the entities.
     *
     * @return AccessSpecifications for GET requests
     */
    public List<AccessSpecification> getGetAccessSpecifications() {
        List<AccessSpecification> accessSpecifications = new ArrayList<AccessSpecification>();
        for (IAMEEEntityReference entity : getDistinctEntities()) {
            accessSpecifications.add(new AccessSpecification(entity, PermissionEntry.VIEW));
        }
        return accessSpecifications;
    }

    /**
     * Returns a de-duped version of the list from getEntities().
     *
     * @return list of entities required for authorization
     */
    public List<IAMEEEntityReference> getDistinctEntities() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        for (IAMEEEntityReference entity : getEntities()) {
            if (!entities.contains(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    /**
     * Returns a list of entities required for authorization for the current resource. The list is
     * in hierarchical order, from general to more specific (e.g., category -> sub-category -> item).
     *
     * @return list of entities required for authorization
     */
    public List<IAMEEEntityReference> getEntities() {
        return dataCategory.getHierarchy();
    }

    /**
     * Get the current active signed-in User.
     *
     * @return the current active signed-in User
     */
    public User getActiveUser() {
        return user;
    }

    public boolean isRequireSuperUser() {
        return false;
    }
}