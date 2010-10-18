package com.amee.platform.resource.datacategory;

import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValue;
import com.amee.domain.tag.Tag;
import com.amee.platform.search.SearchService;
import com.amee.service.data.DataService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataCategoryEcospoldRenderer implements DataCategoryRenderer {

    private final Namespace XSI_NS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    private final Namespace NS = Namespace.getNamespace("http://www.EcoInvent.org/EcoSpold01");
    private final String SCHEMA_LOCATION = "http://www.EcoInvent.org/EcoSpold01 EcoSpold01Dataset.xsd";

    private DataCategory dataCategory;
    private Element rootElem;
    private Element datasetElem;
    private Element flowDataElem;

    @Autowired
    private SearchService searchService;

    @Autowired
    private DataService dataService;

    public void start() {
        rootElem = new Element("ecoSpold", NS);
        rootElem.addNamespaceDeclaration(XSI_NS);
        rootElem.setAttribute("schemaLocation", "http://www.EcoInvent.org/EcoSpold01 EcoSpold01Dataset.xsd", XSI_NS);
    }

    public void ok() {
        // Not implemented for ecospold.
    }

    public void newDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;

        datasetElem = new Element("dataset", NS);
        if (rootElem != null) {
            rootElem.addContent(datasetElem);
        }

        // TODO: Add the dataset attributes from metadata
    }

    public void addBasic() {

        // Add the metainformation element
        datasetElem.addContent(new Element("metaInformation", NS).setText(dataCategory.getEcoinventMetaInformation()));

        // Add the flowData (data items)
        flowDataElem = new Element("flowData", NS);
        if (datasetElem != null) {
            datasetElem.addContent(flowDataElem);
        }

        // For each data item, add each item value definition name and data item value
        for (DataItem dataItem : dataService.getDataItems(dataCategory)) {

            Element exchangeElem = new Element("exchange", NS);

            for (ItemValue itemValue : dataItem.getItemValues()) {

                // Convert group to category and subGroup to subCategory
                String name = itemValue.getName();
                if (name.equals("group")) {
                    name = "category";
                } else if (name.equals("subGroup")) {
                    name = "subCategory";
                }

                // The outputGroup and inputGroup values are displayed as child elements not attributes
                // Only display the element if it is non-empty.
                if ((name.equals("outputGroup") || name.equals("inputGroup")) && !itemValue.getValue().isEmpty()) {
                    exchangeElem.addContent(new Element(name, NS).setText(itemValue.getValue()));
                } else {
                    exchangeElem.setAttribute(name, itemValue.getValue());
                }
                
            }

            flowDataElem.addContent(exchangeElem);
        }
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
