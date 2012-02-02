package com.amee.platform.resource.datacategory;

import com.amee.base.resource.*;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import com.amee.service.data.DataService;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.validation.Validator;

import java.util.Map;

public interface DataCategoryResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, DataCategory dataCategory);

        public DataCategoryResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newDataCategory(DataCategory dataCategory);

        public void addBasic();

        public void addPath();

        public void addParent();

        public void addAudit();

        public void addAuthority();

        public void addHistory();

        public void addWikiDoc();

        public void addProvenance();

        public void addItemDefinition(ItemDefinition id);

        public void startTags();

        public void newTag(Tag tag);
    }

    public static interface FormAcceptor extends ResourceAcceptor {
        public DataCategoryResource.DataCategoryValidator getValidator(RequestWrapper requestWrapper);
    }

    public static interface DataCategoryValidator extends Validator {

        public void initialise();

        public DataCategory getObject();

        public void setObject(DataCategory DataCategory);

        public boolean isValid(Map<String, String> queryParameters);

        public ValidationResult getValidationResult();

        public void setDataService(DataService dataService);
    }

    public static interface Remover extends ResourceRemover {
    }
}
