package com.amee.platform.resource.dataitem;

import com.amee.base.resource.*;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValue;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class DataItemBuilder implements ResourceBuilder {

    @Autowired
    private DataService dataService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private DataItemRenderer dataItemRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get DataCategory identifier.
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
            if (dataCategory != null) {
                // Get DataItem identifier.
                String dataItemIdentifier = requestWrapper.getAttributes().get("itemIdentifier");
                if (dataItemIdentifier != null) {
                    // Get DataItem.
                    DataItem dataItem = dataService.getDataItemByIdentifier(dataCategory, dataItemIdentifier);
                    if (dataItem != null) {
                        // Authorized?
                        resourceAuthorizationService.ensureAuthorizedForBuild(
                                requestWrapper.getAttributes().get("activeUserUid"), dataItem);
                        // Handle the DataItem.
                        this.handle(requestWrapper, dataItem);
                        DataItemRenderer renderer = getDataItemRenderer(requestWrapper);
                        renderer.ok();
                        return renderer.getObject();
                    } else {
                        throw new NotFoundException();
                    }
                } else {
                    throw new MissingAttributeException("itemIdentifier");
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("categoryIdentifier");
        }
    }

    public void handle(
            RequestWrapper requestWrapper,
            DataItem dataItem) {

        DataItemRenderer renderer = getDataItemRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean label = requestWrapper.getMatrixParameters().containsKey("label");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean parent = requestWrapper.getMatrixParameters().containsKey("parent");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean wikiDoc = requestWrapper.getMatrixParameters().containsKey("wikiDoc");
        boolean provenance = requestWrapper.getMatrixParameters().containsKey("provenance");
        boolean itemDefinition = requestWrapper.getMatrixParameters().containsKey("itemDefinition");
        boolean values = requestWrapper.getMatrixParameters().containsKey("values");

        // New DataItem & basic.
        renderer.newDataItem(dataItem);
        renderer.addBasic();

        // Optionals.
        if (name || full) {
            renderer.addName();
        }
        if (label || full) {
            renderer.addLabel();
        }
        if (path || full) {
            renderer.addPath();
        }
        if (parent || full) {
            renderer.addParent();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if (wikiDoc || full) {
            renderer.addWikiDoc();
        }
        if (provenance || full) {
            renderer.addProvenance();
        }
        if ((itemDefinition || full) && (dataItem.getItemDefinition() != null)) {
            ItemDefinition id = dataItem.getItemDefinition();
            renderer.addItemDefinition(id);
        }
        if (values || full) {
            renderer.startValues();
            for (ItemValue itemValue : dataItem.getItemValues()) {
                renderer.newValue(itemValue);
            }
        }
    }

    public DataItemRenderer getDataItemRenderer(RequestWrapper requestWrapper) {
        if (dataItemRenderer == null) {
            dataItemRenderer = (DataItemRenderer) rendererBeanFinder.getRenderer(DataItemRenderer.class, requestWrapper);
        }
        return dataItemRenderer;
    }
}