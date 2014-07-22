package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ItemDefinitionAcceptor {

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    protected ItemDefinitionResource.ItemDefinitionValidator getValidator(RequestWrapper requestWrapper) {
        return (ItemDefinitionResource.ItemDefinitionValidator)
                resourceBeanFinder.getBaseValidator(ItemDefinitionResource.ItemDefinitionValidator.class, requestWrapper);
    }
}
