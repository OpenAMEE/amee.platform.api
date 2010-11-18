package com.amee.platform.resource.datacategory;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DataCategoryResource {

    static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, DataCategory dataCategory);

        public DataCategoryResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    static interface Renderer extends ResourceRenderer {

        public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

        public void newDataCategory(DataCategory dataCategory);

        public void addBasic();

        public void addPath();

        public void addParent();

        public void addAudit();

        public void addAuthority();

        public void addWikiDoc();

        public void addProvenance();

        public void addItemDefinition(ItemDefinition id);

        public void startTags();

        public void newTag(Tag tag);
    }

    static interface FormAcceptor extends ResourceAcceptor {
    }
}
