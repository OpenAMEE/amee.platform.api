package com.amee.platform.resource.profileitemvalue;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.profile.BaseProfileItemValue;

public interface ProfileItemValueResource {

    static interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, BaseProfileItemValue itemValue);

        ProfileItemValueResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    static interface Renderer extends ResourceRenderer {

        void newProfileItemValue(BaseProfileItemValue profileItemValue);

        void addBasic();

        void addAudit();

        void addItemValueDefinition(ItemValueDefinition itemValueDefinition);

        void addProfileItem();

        void addDataCategory();
    }
}
