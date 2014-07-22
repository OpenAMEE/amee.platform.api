package com.amee.platform.resource.search.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.search.v_3_2.SearchFilterValidationHelper_3_2_0;
import com.amee.platform.search.MultiFieldQueryParserEditor;
import com.amee.platform.search.ObjectTypesEditor;
import org.apache.lucene.search.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.0.0")
public class SearchFilterValidationHelper_3_0_0 extends SearchFilterValidationHelper_3_2_0 {

    /**
     * Hook for registering custom Spring editors.
     * <p/>
     * Version 3.0 does not support the tags and excTags parameters introduced in 3.2.
     *
     * @param dataBinder DataBinder to register editors with.
     */
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
    }

    /**
     * Returns an array of fields supported.
     * <p/>
     * Version 3.0 does not support the tags and excTags parameters introduced in 3.2.
     *
     * @return array of allowed fields
     */
    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("q");
            allowedFields.add("resultLimit");
            allowedFields.add("resultStart");
            allowedFields.add("types");
        }
        return allowedFields.toArray(new String[]{});
    }
}