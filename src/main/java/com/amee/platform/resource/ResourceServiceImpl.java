package com.amee.platform.resource;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ValidationResult;
import com.amee.base.utils.UidGen;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IDataItemService;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.*;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.science.StartEndDate;
import com.amee.service.data.DataService;
import com.amee.service.definition.DefinitionService;
import com.amee.service.item.DataItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private DataService dataService;

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public DataCategory getDataCategory(RequestWrapper requestWrapper) {
        // Get DataCategory identifier.
        String categoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (categoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(categoryIdentifier);
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
        String itemIdentifier = requestWrapper.getAttributes().get("itemIdentifier");
        if (itemIdentifier != null) {
            // Get DataItem.
            DataItem dataItem = dataItemService.getDataItemByIdentifier(dataCategory, itemIdentifier);
            if (dataItem != null) {
                return dataItem;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemIdentifier");
        }
    }

    /**
     * Gets the {@link BaseDataItemValue} for the current resource.
     * <p/>
     * TODO: This method is not designed for large amounts of DIVHs.
     * TODO: See https://jira.amee.com/browse/PL-2685.
     *
     * @param requestWrapper      the current {@link RequestWrapper}
     * @param dataItem            the current {@link DataItem}
     * @param itemValueDefinition the current {@link ItemValueDefinition}
     * @return
     */
    @Override
    public BaseDataItemValue getDataItemValue(RequestWrapper requestWrapper, DataItem dataItem, ItemValueDefinition itemValueDefinition) {
        BaseDataItemValue dataItemValue;
        // Get DataItemValue identifier.
        String itemValueIdentifier = requestWrapper.getAttributes().get("itemValueIdentifier");
        if (itemValueIdentifier != null) {
            // Parse itemValueIdentifier.
            ItemValueMap itemValueMap = dataItemService.getItemValuesMap(dataItem);
            if (itemValueIdentifier.equals("CURRENT")) {
                // Current date.
                dataItemValue =
                        (BaseDataItemValue) itemValueMap.get(
                                itemValueDefinition.getPath(),
                                new Date());
            } else if (itemValueIdentifier.equals("FIRST")) {
                // First possible date.
                dataItemValue =
                        (BaseDataItemValue) itemValueMap.get(
                                itemValueDefinition.getPath(),
                                IDataItemService.EPOCH);
            } else if (itemValueIdentifier.equals("LAST")) {
                // Use the last possible date.
                dataItemValue =
                        (BaseDataItemValue) itemValueMap.get(
                                itemValueDefinition.getPath(),
                                IDataItemService.Y2038);
            } else if (UidGen.INSTANCE_12.isValid(itemValueIdentifier)) {
                // Treat identifier as a UID.
                dataItemValue = (BaseDataItemValue) dataItemService.getByUid(dataItem, itemValueIdentifier);
                if (dataItemValue != null) {
                    dataItemValue.setHistoryAvailable(itemValueMap.getAll(itemValueDefinition.getPath()).size() > 1);
                }
            } else {
                // Try to parse identifier as a date.
                try {
                    dataItemValue =
                            (BaseDataItemValue) itemValueMap.get(
                                    itemValueDefinition.getPath(),
                                    new StartEndDate(itemValueIdentifier));
                } catch (IllegalArgumentException e) {
                    // Could not parse date.
                    throw new ValidationException(new ValidationResult(messageSource, "itemValueIdentifier", "typeMismatch"));
                }
            }
            // Got BaseDataItemValue?
            if (dataItemValue != null) {
                return dataItemValue;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemValueIdentifier");
        }
    }

    @Override
    public ItemDefinition getItemDefinition(RequestWrapper requestWrapper) {
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(itemDefinitionIdentifier);
            if (itemDefinition != null) {
                return itemDefinition;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    @Override
    public ItemValueDefinition getItemValueDefinition(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {
        // Get ItemValueDefinition identifier.
        String itemValueDefinitionIdentifier = requestWrapper.getAttributes().get("itemValueDefinitionIdentifier");
        if (itemValueDefinitionIdentifier != null) {
            // Get ItemValueDefinition.
            ItemValueDefinition itemValueDefinition = definitionService.getItemValueDefinitionByUid(itemDefinition, itemValueDefinitionIdentifier);
            if (itemValueDefinition != null) {
                return itemValueDefinition;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemValueDefinitionIdentifier");
        }
    }

    @Override
    public ItemValueDefinition getItemValueDefinition(RequestWrapper requestWrapper, DataItem dataItem) {
        // Get ItemValueDefinition path.
        String valuePath = requestWrapper.getAttributes().get("valuePath");
        if (valuePath != null) {
            // Get ItemValueDefinition.
            ItemValueDefinition itemValueDefinition = dataItem.getItemDefinition().getItemValueDefinition(valuePath);
            if ((itemValueDefinition != null) && itemValueDefinition.isFromData()) {
                return itemValueDefinition;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("valuePath");
        }
    }

    @Override
    public ReturnValueDefinition getReturnValueDefinition(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {
        // Get ReturnValueDefinition identifier.
        String returnValueDefinitionIdentifier = requestWrapper.getAttributes().get("returnValueDefinitionIdentifier");
        if (returnValueDefinitionIdentifier != null) {
            // Get ReturnValueDefinition.
            ReturnValueDefinition returnValueDefinition = definitionService.getReturnValueDefinitionByUid(itemDefinition, returnValueDefinitionIdentifier);
            if (returnValueDefinition != null) {
                return returnValueDefinition;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("returnValueDefinitionIdentifier");
        }
    }

    @Override
    public Algorithm getAlgorithm(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {
        // Get Algorithm identifier.
        String algorithmIdentifier = requestWrapper.getAttributes().get("algorithmIdentifier");
        if (algorithmIdentifier != null) {
            // Get Algorithm.
            Algorithm algorithm = definitionService.getAlgorithmByUid(itemDefinition, algorithmIdentifier);
            if (algorithm != null) {
                return algorithm;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("algorithmIdentifier");
        }
    }
}