package com.amee.platform.resource.search.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.validation.ValidationHelper;
import com.amee.platform.resource.search.SearchFilterValidator;
import com.amee.platform.resource.search.SearchResource;
import com.amee.platform.search.MultiFieldQueryParserEditor;
import com.amee.platform.search.ObjectTypesEditor;
import com.amee.platform.search.QueryParserEditor;
import com.amee.platform.search.SearchFilter;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.2.0")
public class SearchFilterValidationHelper_3_2_0 extends ValidationHelper implements SearchResource.SearchFilterValidationHelper {

    @Autowired
    protected SearchFilterValidator validator;

    protected SearchFilter searchFilter;
    protected Set<String> allowedFields;

    @Override
    protected void registerCustomEditors(DataBinder dataBinder) {
        String[] fields = {
                "name",
                "wikiName",
                "path",
                "provenance",
                "authority",
                "wikiDoc",
                "itemDefinitionName",
                "label",
                "tags"};
        Map<String, Float> boosts = new HashMap<String, Float>();
        boosts.put("wikiName", 10.0f);
        boosts.put("tags", 10.0f);
        dataBinder.registerCustomEditor(Query.class, "q", new MultiFieldQueryParserEditor(fields, boosts));
        dataBinder.registerCustomEditor(Set.class, "types", new ObjectTypesEditor());
        dataBinder.registerCustomEditor(Query.class, "tags", QueryParserEditor.getTagQueryParserEditor("tags"));
        dataBinder.registerCustomEditor(Query.class, "excTags", QueryParserEditor.getTagQueryParserEditor("tags"));
    }

    @Override
    public Object getObject() {
        return searchFilter;
    }

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Override
    public String getName() {
        return "searchFilter";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("q");
            allowedFields.add("resultLimit");
            allowedFields.add("resultStart");
            allowedFields.add("types");
            allowedFields.add("tags");
            allowedFields.add("excTags");
        }
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public SearchFilter getSearchFilter() {
        return searchFilter;
    }

    @Override
    public void setSearchFilter(SearchFilter searchFilter) {
        this.searchFilter = searchFilter;
    }
}