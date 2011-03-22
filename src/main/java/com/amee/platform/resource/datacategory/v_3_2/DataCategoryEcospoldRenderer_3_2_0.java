package com.amee.platform.resource.datacategory.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.MediaTypeNotSupportedException;
import com.amee.domain.IDataItemService;
import com.amee.domain.LocaleService;
import com.amee.domain.MetadataService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.service.data.DataService;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

@Service
@Scope("prototype")
@Since("3.2.0")
public class DataCategoryEcospoldRenderer_3_2_0 implements DataCategoryResource.Renderer {

    private final Namespace XSI_NS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    private final Namespace NS = Namespace.getNamespace("http://www.EcoInvent.org/EcoSpold01");
    private final String SCHEMA_LOCATION = "http://www.EcoInvent.org/EcoSpold01 EcoSpold01Dataset.xsd";

    private DataCategory dataCategory;
    private Element rootElem;
    private Element datasetElem;
    private Element flowDataElem;

    @Autowired
    private DataService dataService;

    @Autowired
    private IDataItemService dataItemService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private LocaleService localeService;

    public void start() {
        rootElem = new Element("ecoSpold", NS);
        rootElem.addNamespaceDeclaration(XSI_NS);
        rootElem.setAttribute("schemaLocation", SCHEMA_LOCATION, XSI_NS);
    }

    public void ok() {
        // Not implemented for ecospold.
    }

    public void newDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;

        // Only display ecoinvent data in ecospold format.
        if (dataCategory.getEcoinventMetaInformation().isEmpty() ||
                dataCategory.getEcoinventDatasetAttributes().isEmpty()) {
            throw new MediaTypeNotSupportedException();
        }
    }

    public void addBasic() {

        try {

            SAXBuilder builder = new SAXBuilder();
            Document doc;

            // Add the dataset element
            String ecoinventDatasetAttributes = dataCategory.getEcoinventDatasetAttributes();
            if (!StringUtils.isBlank(ecoinventDatasetAttributes)) {
                doc = builder.build(new StringReader(ecoinventDatasetAttributes));
                rootElem.addContent(doc.getRootElement().detach());
                datasetElem = rootElem.getChild("dataset", NS);
            } else {
                throw new IllegalStateException("The dataset Element could not be created.");
            }

            // Add the metainformation element
            String ecoinventMetaInformation = dataCategory.getEcoinventMetaInformation();
            if (!StringUtils.isBlank(ecoinventMetaInformation)) {
                doc = builder.build(new StringReader(ecoinventMetaInformation));
                datasetElem.addContent(doc.getRootElement().detach());
            } else {
                throw new IllegalStateException("The dataset Element could not be populated.");
            }

        } catch (JDOMException e) {
            throw new RuntimeException("Caught JDOMException: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
        }

        // Add the flowData (data items)
        flowDataElem = new Element("flowData", NS);
        if (datasetElem != null) {
            datasetElem.addContent(flowDataElem);
        }

        // Pre-cache metadata and locales for the Data Items.
        metadataService.loadMetadatasForItemValueDefinitions(dataCategory.getItemDefinition().getItemValueDefinitions());
        localeService.loadLocaleNamesForItemValueDefinitions(dataCategory.getItemDefinition().getItemValueDefinitions());

        // Fetch DataItems.
        List<DataItem> dataItems = dataItemService.getDataItems(dataCategory, false);

        // Pre-cache X for Data Items.
        metadataService.loadMetadatasForDataItems(dataItems);

        // For each data item, add each item value definition name and data item value
        for (DataItem dataItem : dataItems) {

            Element exchangeElem = new Element("exchange", NS);

            for (BaseItemValue itemValue : dataItemService.getItemValues(dataItem)) {

                // Convert group to category and subGroup to subCategory
                String name = itemValue.getName();
                if (name.equals("group")) {
                    name = "category";
                } else if (name.equals("subGroup")) {
                    name = "subCategory";
                }

                // The outputGroup and inputGroup values are displayed as child elements not attributes
                // Only display the element if it is non-empty.
                if (name.equals("outputGroup") || name.equals("inputGroup")) {
                    if (!itemValue.getValueAsString().isEmpty()) {
                        exchangeElem.addContent(new Element(name, NS).setText(itemValue.getValueAsString()));
                    }
                } else {
                    exchangeElem.setAttribute(name, itemValue.getValueAsString());
                }
            }
            flowDataElem.addContent(exchangeElem);
        }

        // Clear caches.
        metadataService.clearMetadatas();
        localeService.clearLocaleNames();
    }

    public void addPath() {
        // Not implemented for ecospold.
    }

    public void addParent() {
        // Not implemented for ecospold.
    }

    public void addAudit() {
        // Not implemented for ecospold.
    }

    public void addAuthority() {
        // Not implemented for ecospold.
    }

    public void addHistory() {
        // Not implemented for ecospold.
    }

    public void addWikiDoc() {
        // Not implemented for ecospold.
    }

    public void addProvenance() {
        // Not implemented for ecospold.
    }

    public void addItemDefinition(ItemDefinition id) {
        // Not implemented for ecospold.
    }

    public void startTags() {
        // Not implemented for ecospold.
    }

    public void newTag(Tag tag) {
        // Not implemented for ecospold.
    }

    public String getMediaType() {
        return "application/x.ecospold+xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
