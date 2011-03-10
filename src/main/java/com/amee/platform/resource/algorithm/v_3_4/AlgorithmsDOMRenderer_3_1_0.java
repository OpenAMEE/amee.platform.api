package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import com.amee.platform.resource.algorithm.AlgorithmsResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class AlgorithmsDOMRenderer_3_1_0 implements AlgorithmsResource.Renderer {

    private Element rootElem;
    private Element algorithmsElem;

    public void start() {
        rootElem = new Element("Representation");
        algorithmsElem = new Element("Algorithms");
        rootElem.addContent(algorithmsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newAlgorithm(AlgorithmResource.Renderer renderer) {
        algorithmsElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Algorithm").detach());
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
