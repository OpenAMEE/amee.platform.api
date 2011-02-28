package com.amee.platform.resource;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.service.data.DataService;
import com.amee.service.item.DataItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private DataService dataService;

    @Autowired
    private DataItemService dataItemService;

    @Override
    public DataCategory getDataCategory(RequestWrapper requestWrapper) {
        // Get DataCategory identifier.
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
            if (dataCategory != null) {
                return dataCategory;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("categoryIdentifier");
        }
    }

    @Override
    public DataCategory getDataCategoryWhichHasItemDefinition(RequestWrapper requestWrapper) {
        DataCategory dataCategory = getDataCategory(requestWrapper);
        if (dataCategory.isItemDefinitionPresent()) {
            return dataCategory;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public DataItem getDataItem(RequestWrapper requestWrapper, DataCategory dataCategory) {
        // Get DataItem identifier.
        String dataItemIdentifier = requestWrapper.getAttributes().get("itemIdentifier");
        if (dataItemIdentifier != null) {
            // Get DataItem.
            DataItem dataItem = dataItemService.getDataItemByIdentifier(dataCategory, dataItemIdentifier);
            if (dataItem != null) {
                return dataItem;
            } else {
                throw new MissingAttributeException("itemIdentifier");
            }
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public BaseDataItemValue getDataItemValue(RequestWrapper requestWrapper, DataItem dataItem) {
        // Get DataItemValue identifier.
        String dataItemValueIdentifier = requestWrapper.getAttributes().get("itemValueIdentifier");
        if (dataItemValueIdentifier != null) {
            // Get BaseDataItemValue.
            // TODO: Does this really get the correct DIV?
            BaseDataItemValue dataItemValue = (BaseDataItemValue) dataItemService.getItemValue(dataItem, dataItemValueIdentifier);
            if (dataItemValue != null) {
                return dataItemValue;
            } else {
                throw new MissingAttributeException("itemValueIdentifier");
            }
        } else {
            throw new NotFoundException();
        }
    }
}