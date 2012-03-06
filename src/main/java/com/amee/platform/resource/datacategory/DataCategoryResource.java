package com.amee.platform.resource.datacategory;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.ResourceValidator;
import com.amee.service.data.DataService;

public interface DataCategoryResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, DataCategory dataCategory);

        public DataCategoryResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newDataCategory(DataCategory dataCategory);

        public void addBasic();

        public void addName();

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

    public static interface DataCategoryValidator extends ResourceValidator<DataCategory> {

        public void initialise();

        public void setDataService(DataService dataService);
    }

    public static interface Remover extends ResourceRemover {
    }
}
