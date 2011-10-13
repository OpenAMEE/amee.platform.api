package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.auth.User;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.profile.ProfileResource;
import com.amee.platform.resource.profile.ProfilesResource;
import com.amee.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfilesBuilder_3_6_0 implements ProfilesResource.Builder {

    private ProfilesResource.Renderer profilesRenderer;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Start Renderer
        ProfilesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add Profiles
        int resultStart;
        int resultLimit;
        if (requestWrapper.getQueryParameters().containsKey("resultStart")) {
            resultStart = Integer.parseInt(requestWrapper.getQueryParameters().get("resultStart"));
        } else {
            resultStart = 0;
        }
        if (requestWrapper.getQueryParameters().containsKey("resultLimit")) {
            resultLimit = Integer.parseInt(requestWrapper.getQueryParameters().get("resultLimit"));
        } else {
            resultLimit = 0;
        }
        ResultsWrapper<Profile> profiles = profileService.getProfilesByUserUid(
            requestWrapper.getAttributes().get("activeUserUid"), resultStart, resultLimit);
        renderer.setTruncated(profiles.isTruncated());
        ProfileResource.Builder profileBuilder = getProfileBuilder(requestWrapper);
        for (Profile profile : profiles.getResults()) {
            profileBuilder.handle(requestWrapper, profile);
            renderer.newProfile(profileBuilder.getRenderer(requestWrapper));
        }

        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public ProfilesResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (profilesRenderer == null) {
            profilesRenderer = (ProfilesResource.Renderer) resourceBeanFinder.getRenderer(
                ProfilesResource.Renderer.class, requestWrapper);
        }
        return profilesRenderer;
    }

    private ProfileResource.Builder getProfileBuilder(RequestWrapper requestWrapper) {
        return (ProfileResource.Builder) resourceBeanFinder.getBuilder(ProfileResource.Builder.class, requestWrapper);
    }
}
