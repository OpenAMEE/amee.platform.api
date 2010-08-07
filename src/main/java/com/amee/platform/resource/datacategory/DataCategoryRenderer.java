package com.amee.platform.resource.datacategory;

import com.amee.base.resource.Renderer;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.path.PathItem;
import com.amee.domain.tag.Tag;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface DataCategoryRenderer extends Renderer {

    public final static DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    public void newDataCategory(DataCategory dataCategory);

    public void addBasic();

    public void addPath(PathItem pathItem);

    public void addParent();

    public void addAudit();

    public void addAuthority();

    public void addWikiDoc();

    public void addProvenance();

    public void addItemDefinition(ItemDefinition id);

    public void startTags();

    public void newTag(Tag tag);
}
