package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.DataCategory;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profile.ProfileResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileBuilder_3_6_0 implements ProfileResource.Builder {

    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;
    
    private ProfileResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get resource entities for this request.
        Profile profile = resourceService.getProfile(requestWrapper);

        // Authorised for this Profile?
        resourceAuthorizationService.ensureAuthorizedForBuild(
            requestWrapper.getAttributes().get("activeUserUid"), profile);

        // Handle the Profile.
        handle(requestWrapper, profile);
        ProfileResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public void handle(RequestWrapper requestWrapper, Profile profile) {

        // Get the renderer.
        ProfileResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Collect rendering options from matrix parameters
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean categories = requestWrapper.getMatrixParameters().containsKey("categories");

        // New Profile + basic
        renderer.newProfile(profile);
        renderer.addBasic();

        // Optionals
        if (audit || full) {
            renderer.addAudit();
        }
        if (categories || full) {
            renderer.startCategories();
            for (DataCategory category : profileService.getProfileDataCategories(profile)) {
                renderer.newCategory(category);
            }
        }
    }

    @Override
    public ProfileResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (ProfileResource.Renderer) resourceBeanFinder.getRenderer(ProfileResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
