package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsFormAcceptor_3_6_0 implements ProfileItemsResource.FormAcceptor {
    
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForAccept(
            requestWrapper.getAttributes().get("activeUserId"), profile);

        ProfileItem profileItem = new ProfileItem(profile)
    }

    protected Object handle(RequestWrapper requestWrapper, ProfileItem profileItem) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
