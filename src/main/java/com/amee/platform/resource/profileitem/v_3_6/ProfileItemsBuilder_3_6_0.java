package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.ProfileItemService;
import com.amee.domain.ProfileItemsFilter;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.platform.resource.profileitem.ProfileItemsFilterValidationHelper;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsBuilder_3_6_0 implements ProfileItemsResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ProfileItemService profileItemService;
    
    @Autowired
    private ResourceBeanFinder resourceBeanFinder;
    
    @Autowired
    private ProfileItemsFilterValidationHelper validationHelper;

    private ProfileItemsResource.Renderer profileItemsRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForBuild(
            requestWrapper.getAttributes().get("activeUserUid"), profile);

        // Set up filter and validate
        ProfileItemsFilter filter = new ProfileItemsFilter();
        validationHelper.setProfileItemsFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
            handle(requestWrapper, profile, filter);
            ProfileItemsResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    public void handle(RequestWrapper requestWrapper, Profile profile, ProfileItemsFilter filter) {
        // Start renderer
        ProfileItemsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add Profile Items to Renderer and build.
        ResultsWrapper<ProfileItem> resultsWrapper = profileItemService.getProfileItems(profile, filter);
        renderer.setTruncated(resultsWrapper.isTruncated());
        ProfileItemResource.Builder profileItemBuilder = getProfileItemBuilder(requestWrapper);
        for (ProfileItem profileItem : resultsWrapper.getResults()) {
            profileItemBuilder.handle(requestWrapper, profileItem);
            renderer.newProfileItem(profileItemBuilder.getRenderer(requestWrapper));
        }
    }

    @Override
    public ProfileItemsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (profileItemsRenderer == null) {
            profileItemsRenderer = (ProfileItemsResource.Renderer) resourceBeanFinder.getRenderer(
                ProfileItemsResource.Renderer.class, requestWrapper);
        }
        return profileItemsRenderer;
    }

    private ProfileItemResource.Builder getProfileItemBuilder(RequestWrapper requestWrapper) {
        return (ProfileItemResource.Builder)
            resourceBeanFinder.getBuilder(ProfileItemResource.Builder.class, requestWrapper);
    }
}
