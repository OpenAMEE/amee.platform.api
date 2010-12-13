package com.amee.platform.resource.tag;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.data.DataCategory;
import com.amee.service.data.DataService;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagResourceService {

    @Autowired
    private DataService dataService;

    @Autowired
    private TagService tagService;

    /**
     * Get the entity that Tags should belong to.
     * <p/>
     * TODO: This is currently only works with Data Categories.
     *
     * @param requestWrapper the active RequestWrapper
     * @return IAMEEEntityReference entity reference
     */
    public IAMEEEntityReference getEntity(RequestWrapper requestWrapper) {
        if (requestWrapper.getAttributes().containsKey("categoryIdentifier")) {
            String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
            if (dataCategoryIdentifier != null) {
                DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
                if (dataCategory != null) {
                    return dataCategory;
                } else {
                    throw new NotFoundException();
                }
            } else {
                throw new MissingAttributeException("No value for the categoryIdentifier attribute.");
            }
        }
        return null;
    }
}
