package com.amee.platform.resource.profileitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.item.profile.BaseProfileItemValue;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitemvalue.ProfileItemValueResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValueBuilder_3_6_0 implements ProfileItemValueResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private ProfileItemValueResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Direct get requests are not supported
        return null;

    }

    @Override
    public void handle(RequestWrapper requestWrapper, BaseProfileItemValue profileItemValue) {

        // Get the renderer
        ProfileItemValueResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Collect rendering options from matrix parameters
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean category = requestWrapper.getMatrixParameters().containsKey("category");
        boolean item = requestWrapper.getMatrixParameters().containsKey("item");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean itemValueDefinition = requestWrapper.getMatrixParameters().containsKey("itemValueDefinition");

        // New Profile Item and basic
        renderer.newProfileItemValue(profileItemValue);
        renderer.addBasic();

        // Optionals.
        if (category || full) {
            renderer.addDataCategory();
        }
        if (item || full) {
            renderer.addProfileItem();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if (itemValueDefinition || full) {
            renderer.addItemValueDefinition(profileItemValue.getItemValueDefinition());
        }
    }

    @Override
    public ProfileItemValueResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (ProfileItemValueResource.Renderer) resourceBeanFinder.getRenderer(
                ProfileItemValueResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
