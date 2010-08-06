package com.amee.platform.resource.search;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.resource.RendererHelper;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.validation.ValidationException;
import com.amee.domain.AMEEEntity;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.platform.resource.datacategory.DataCategoryBuilder;
import com.amee.platform.resource.datacategory.DataCategoryDOMRenderer;
import com.amee.platform.resource.datacategory.DataCategoryJSONRenderer;
import com.amee.platform.resource.datacategory.DataCategoryRenderer;
import com.amee.platform.resource.dataitem.DataItemBuilder;
import com.amee.platform.resource.dataitem.DataItemDOMRenderer;
import com.amee.platform.resource.dataitem.DataItemJSONRenderer;
import com.amee.platform.resource.dataitem.DataItemRenderer;
import com.amee.platform.search.SearchFilter;
import com.amee.platform.search.SearchService;
import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("prototype")
public class SearchBuilder implements ResourceBuilder {

    private final static Map<String, Class> RENDERERS = new HashMap<String, Class>() {
        {
            put("application/json", SearchJSONRenderer.class);
            put("application/xml", SearchDOMRenderer.class);
        }
    };

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchFilterValidationHelper validationHelper;

    @Autowired
    private DataCategoryBuilder dataCategoryBuilder;

    @Autowired
    private DataItemBuilder dataItemBuilder;

    private SearchRenderer renderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        renderer = new RendererHelper<SearchRenderer>().getRenderer(requestWrapper, RENDERERS);
        SearchFilter filter = new SearchFilter();
        filter.setLoadDataItemValues(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("values"));
        filter.setLoadMetadatas(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("authority") ||
                        requestWrapper.getMatrixParameters().containsKey("wikiDoc") ||
                        requestWrapper.getMatrixParameters().containsKey("provenance"));
        validationHelper.setSearchFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
            handle(requestWrapper, filter, renderer);
            renderer.ok();
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
        return renderer.getObject();
    }

    protected void handle(
            RequestWrapper requestWrapper,
            SearchFilter filter,
            SearchRenderer renderer) {
        ResultsWrapper<AMEEEntity> resultsWrapper = searchService.getEntities(filter);
        renderer.setTruncated(resultsWrapper.isTruncated());
        for (AMEEEntity entity : resultsWrapper.getResults()) {
            switch (entity.getObjectType()) {
                case DC:
                    dataCategoryBuilder.handle(requestWrapper, (DataCategory) entity, renderer.getDataCategoryRenderer());
                    renderer.newDataCategory();
                    break;
                case DI:
                    dataItemBuilder.handle(requestWrapper, (DataItem) entity, renderer.getDataItemRenderer());
                    renderer.newDataItem();
                    break;
            }
        }
    }

    public interface SearchRenderer {

        public void ok();

        public void start();

        public void newDataCategory();

        public void newDataItem();

        public void setTruncated(boolean truncated);

        public DataCategoryRenderer getDataCategoryRenderer();

        public DataItemRenderer getDataItemRenderer();

        public Object getObject();
    }

    public static class SearchJSONRenderer implements SearchRenderer {

        private DataCategoryJSONRenderer dataCategoryRenderer;
        private DataItemJSONRenderer dataItemRenderer;
        private JSONObject rootObj;
        private JSONArray resultsArr;

        public SearchJSONRenderer() {
            super();
            this.dataCategoryRenderer = new DataCategoryJSONRenderer(false);
            this.dataItemRenderer = new DataItemJSONRenderer(false);
            start();
        }

        public void start() {
            rootObj = new JSONObject();
            resultsArr = new JSONArray();
            put(rootObj, "results", resultsArr);
        }

        public void ok() {
            put(rootObj, "status", "OK");
        }

        public void newDataCategory() {
            resultsArr.put(dataCategoryRenderer.getDataCategoryJSONObject());
        }

        public void newDataItem() {
            resultsArr.put(dataItemRenderer.getDataItemJSONObject());
        }

        public void setTruncated(boolean truncated) {
            put(rootObj, "resultsTruncated", truncated);
        }

        public DataCategoryRenderer getDataCategoryRenderer() {
            return dataCategoryRenderer;
        }

        public DataItemRenderer getDataItemRenderer() {
            return dataItemRenderer;
        }

        protected JSONObject put(JSONObject o, String key, Object value) {
            try {
                return o.put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
            }
        }

        public Object getObject() {
            return rootObj;
        }
    }

    public static class SearchDOMRenderer implements SearchRenderer {

        private DataCategoryDOMRenderer dataCategoryRenderer;
        private DataItemDOMRenderer dataItemRenderer;
        private Element rootElem;
        private Element resultsElem;

        public SearchDOMRenderer() {
            super();
            this.dataCategoryRenderer = new DataCategoryDOMRenderer(false);
            this.dataItemRenderer = new DataItemDOMRenderer(false);
            start();
        }

        public void start() {
            rootElem = new Element("Representation");
            resultsElem = new Element("Results");
            rootElem.addContent(resultsElem);
        }

        public void ok() {
            rootElem.addContent(new Element("Status").setText("OK"));
        }

        public void newDataCategory() {
            resultsElem.addContent(dataCategoryRenderer.getDataCategoryElement());
        }

        public void newDataItem() {
            resultsElem.addContent(dataItemRenderer.getDataItemElement());
        }

        public void setTruncated(boolean truncated) {
            resultsElem.setAttribute("truncated", "" + truncated);
        }

        public DataCategoryRenderer getDataCategoryRenderer() {
            return dataCategoryRenderer;
        }

        public DataItemRenderer getDataItemRenderer() {
            return dataItemRenderer;
        }

        public Document getObject() {
            return new Document(rootElem);
        }
    }
}