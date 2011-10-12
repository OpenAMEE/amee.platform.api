package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.utils.ThreadBeanHolder;
import com.amee.base.validation.ValidationException;
import com.amee.domain.auth.User;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.profile.ProfilesResource;
import com.amee.service.auth.AuthenticationService;
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
public class ProfilesFormAcceptor_3_6_0 implements ProfilesResource.FormAcceptor {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForAcceptProfile(
            requestWrapper.getAttributes().get("activeUserUid"));

        // Create the new Profile
        User user = resourceAuthorizationService.getActiveUser();
        return handle(requestWrapper, new Profile(user));
    }

    @Override
    public Object handle(RequestWrapper requestWrapper, Profile profile) {

        // Validation is not required because profiles are created using a 'dummy' profile=true parameter
        // just so the POST body has some content. This parameter is not used.
        profileService.persist(profile);
        String location = "/" + requestWrapper.getVersion() + "/profiles/" + profile.getFullPath();
        return ResponseHelper.getOK(requestWrapper, location);
    }
}
