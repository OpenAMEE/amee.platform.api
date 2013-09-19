package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.4.0")
public class AlgorithmDOMRenderer_3_4_0 implements AlgorithmResource.Renderer {

    protected Algorithm algorithm;
    protected Element rootElem;
    protected Element algorithmElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public void newAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        algorithmElem = new Element("Algorithm");
        if (rootElem != null) {
            rootElem.addContent(algorithmElem);
        }
    }

    @Override
    public void addBasic() {
        algorithmElem.setAttribute("uid", algorithm.getUid());
    }

    @Override
    public void addAudit() {
        algorithmElem.setAttribute("status", algorithm.getStatus().getName());
        algorithmElem.setAttribute("created", DATE_FORMAT.print(algorithm.getCreated().getTime()));
        algorithmElem.setAttribute("modified", DATE_FORMAT.print(algorithm.getModified().getTime()));
    }

    @Override
    public void addName() {
        algorithmElem.addContent(new Element("Name").setText(algorithm.getName()));
    }

    @Override
    public void addContent() {
        algorithmElem.addContent(new Element("Content").setText(algorithm.getContent()));
    }

    @Override
    public void addItemDefinition(ItemDefinition itemDefinition) {
        Element e = new Element("ItemDefinition");
        algorithmElem.addContent(e);
        e.setAttribute("uid", itemDefinition.getUid());
        e.addContent(new Element("Name").setText(itemDefinition.getName()));
    }

    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }
}
