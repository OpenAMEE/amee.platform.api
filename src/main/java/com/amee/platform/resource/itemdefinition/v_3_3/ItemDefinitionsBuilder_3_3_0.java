package com.amee.platform.resource.itemdefinition.v_3_3;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import com.amee.platform.resource.itemdefinition.ItemDefinitionsFilterValidationHelper;
import com.amee.platform.resource.itemdefinition.ItemDefinitionsResource;
import com.amee.platform.search.ItemDefinitionsFilter;
import com.amee.service.definition.DefinitionService;
import com.amee.service.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Scope("prototype")
@Since("3.3.0")
public class ItemDefinitionsBuilder_3_3_0 implements ItemDefinitionsResource.Builder {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ItemDefinitionsFilterValidationHelper validationHelper;

    private ItemDefinitionsResource.Renderer itemDefinitionsRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Setup filter and validate.
        ItemDefinitionsFilter filter = new ItemDefinitionsFilter();
        validationHelper.setItemDefinitionFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {

            // Get ItemDefinitions.
            List<ItemDefinition> itemDefinitions = definitionService.getItemDefinitions();

            // Load Metadatas if needed (for usages).
            if (requestWrapper.getMatrixParameters().containsKey("usages")) {
                metadataService.loadMetadatasForItemDefinitions(itemDefinitions);
            }

            // Start Renderer.
            ItemDefinitionsResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.start();

            // Add ItemDefinitions.
            ItemDefinitionResource.Builder itemDefinitionBuilder = getItemDefinitionBuilder(requestWrapper);
            for (ItemDefinition itemDefinition : itemDefinitions) {
                itemDefinitionBuilder.handle(requestWrapper, itemDefinition);
                renderer.newItemDefinition(itemDefinitionBuilder.getRenderer(requestWrapper));
            }

            // Success!
            renderer.ok();
            return renderer.getObject();

        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    public ItemDefinitionsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (itemDefinitionsRenderer == null) {
            itemDefinitionsRenderer = (ItemDefinitionsResource.Renderer) resourceBeanFinder.getRenderer(ItemDefinitionsResource.Renderer.class, requestWrapper);
        }
        return itemDefinitionsRenderer;
    }

    private ItemDefinitionResource.Builder getItemDefinitionBuilder(RequestWrapper requestWrapper) {
        return (ItemDefinitionResource.Builder)
                resourceBeanFinder.getBuilder(ItemDefinitionResource.Builder.class, requestWrapper);
    }
}