package com.amee.platform.resource;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ValidationResult;
import com.amee.base.utils.UidGen;
import com.amee.base.validation.ValidationException;
import com.amee.domain.AMEEStatus;
import com.amee.domain.DataItemService;
import com.amee.domain.ProfileItemService;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.auth.User;
import com.amee.domain.data.*;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.domain.tag.Tag;
import com.amee.domain.unit.AMEEUnit;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.science.StartEndDate;
import com.amee.service.auth.AuthenticationService;
import com.amee.service.auth.AuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.definition.DefinitionService;
import com.amee.service.profile.ProfileService;
import com.amee.service.tag.TagService;
import com.amee.service.unit.UnitService;
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
    private ProfileItemService profileItemService;
    
    @Autowired
    private TagService tagService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public DataCategory getDataCategory(RequestWrapper requestWrapper) {
        return getDataCategory(requestWrapper, AMEEStatus.ACTIVE);
    }

    @Override
    public DataCategory getDataCategory(RequestWrapper requestWrapper, AMEEStatus status) {
        // Get DataCategory identifier.
        String categoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (categoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(categoryIdentifier, status);
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
                                DataItemService.EPOCH);
            } else if (itemValueIdentifier.equals("LAST")) {
                // Use the last possible date.
                dataItemValue =
                        (BaseDataItemValue) itemValueMap.get(
                                itemValueDefinition.getPath(),
                                DataItemService.Y2038);
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

    @Override
    public Tag getTag(RequestWrapper requestWrapper) {
        // Get Tag identifier.
        String tagIdentifier = requestWrapper.getAttributes().get("tagIdentifier");
        if (tagIdentifier != null) {
            // Get Tag.
            Tag tag = tagService.getTagByIdentifier(tagIdentifier);
            if (tag != null) {
                return tag;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("tagIdentifier");
        }
    }

    @Override
    public AMEEUnitType getUnitType(RequestWrapper requestWrapper) {
        return getUnitType(requestWrapper, false);
    }

    @Override
    public AMEEUnitType getUnitType(RequestWrapper requestWrapper, boolean allowMissingUnitType) {
        // Get Unit Type identifier.
        String unitTypeIdentifier = requestWrapper.getAttributes().get("unitTypeIdentifier");
        if (unitTypeIdentifier != null) {
            // Get Unit Type.
            AMEEUnitType unitType = unitService.getUnitTypeByIdentifier(unitTypeIdentifier);
            if (unitType != null) {
                return unitType;
            } else {
                throw new NotFoundException();
            }
        } else {
            if (!allowMissingUnitType) {
                throw new MissingAttributeException("unitTypeIdentifier");
            } else {
                return null;
            }
        }
    }

    @Override
    public AMEEUnit getUnit(RequestWrapper requestWrapper, AMEEUnitType unitType) {
        // Get Unit identifier.
        String unitIdentifier = requestWrapper.getAttributes().get("unitIdentifier");
        if (unitIdentifier != null) {
            // Get Unit.
            AMEEUnit unit = unitService.getUnitByIdentifier(unitIdentifier);
            if ((unit != null) && unit.getUnitType().equals(unitType)) {
                return unit;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("unitTypeIdentifier");
        }
    }

    @Override
    public Profile getProfile(RequestWrapper requestWrapper) {

        // Get the profile identifier
        String profileIdentifier = requestWrapper.getAttributes().get("profileIdentifier");
        if (profileIdentifier != null) {
            Profile profile = profileService.getProfileByUid(profileIdentifier);
            if (profile != null) {
                return profile;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("profileIdentifier");
        }
    }

    @Override
    public ProfileItem getProfileItem(RequestWrapper requestWrapper, Profile profile) {

        // Get the profile item identifier
        String itemIdentifier = requestWrapper.getAttributes().get("itemIdentifier");
        if (itemIdentifier != null) {

            // Get ProfileItem.
            ProfileItem profileItem = profileItemService.getItemByUid(itemIdentifier);
            if (profileItem != null && profileItem.getProfile().equals(profile)) {
                return profileItem;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemIdentifier");
        }
    }

    @Override
    public User getCurrentUser(RequestWrapper requestWrapper) {
        return authenticationService.getUserByUid(requestWrapper.getAttributes().get("activeUserUid"));
    }
}