package com.amee.domain.algorithm;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.AMEEStatus;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.path.Pathable;
import com.amee.platform.science.AlgorithmException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;

@Entity
@DiscriminatorValue("AL")
public class Algorithm extends AbstractAlgorithm implements com.amee.platform.science.Algorithm, Pathable {

    // Default Algorithm name to use in calculations
    public final static String DEFAULT = "default";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ITEM_DEFINITION_ID", nullable = true)
    private ItemDefinition itemDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ALGORITHM_CONTEXT_ID")
    private AlgorithmContext algorithmContext;

    public Algorithm() {
        super();
    }

    public Algorithm(ItemDefinition itemDefinition, String content) {
        super(content);
        setItemDefinition(itemDefinition);
        itemDefinition.add(this);
    }

    @Override
    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = super.getJSONObject(detailed);
        if (detailed) {
            obj.put("itemDefinition", getItemDefinition().getIdentityJSONObject());
        }
        return obj;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, this);
    }

    @Override
    public String getElementName() {
        return "Algorithm";
    }

    @Override
    public Element getElement(Document document, boolean detailed) {
        Element element = super.getElement(document, detailed);
        if (detailed) {
            element.appendChild(getItemDefinition().getIdentityElement(document));
            if (getAlgorithmContext() != null) {
                element.appendChild(getAlgorithmContext().getIdentityElement(document));
            }
        }
        return element;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        if (itemDefinition == null) return;
        this.itemDefinition = itemDefinition;
    }

    public AlgorithmContext getAlgorithmContext() {
        return algorithmContext;
    }

    public void setAlgorithmContext(AlgorithmContext algorithmContext) {
        this.algorithmContext = algorithmContext;
    }

    /**
     * Get the Algorithm content with any associated AlgorithmContext content.
     *
     * @return a string constructed from the Algorithm content and context
     */
    public String getFullContent() {
        StringBuffer outContent = new StringBuffer(super.getContent());
        if (getAlgorithmContext() != null) {
            outContent.insert(0, "\n"); // add spacer line
            outContent.insert(0, getAlgorithmContext().getContent());
        }
        return outContent.toString();
    }

    @Override
    public String getPath() {
        return getUid();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDisplayPath() {
        return getPath();
    }

    @Override
    public String getFullPath() {
        return getItemDefinition().getFullPath() + "/" + getPath();
    }

    public ObjectType getObjectType() {
        return ObjectType.AL;
    }

    public CompiledScript getCompiledScript(ScriptEngine engine) throws ScriptException {
        if (StringUtils.isBlank(getContent())) {
            throw new AlgorithmException(
                    "Algorithm content is null (" + getLabel() + ").");
        }
        CompiledScript compiledScript = ((Compilable) engine).compile(getContent());
        if (compiledScript == null) {
            throw new AlgorithmException(
                    "CompiledScript is null (" + getLabel() + ").");
        }
        return compiledScript;
    }

    public String getLabel() {
        return getItemDefinition() + "/" + getName();
    }

    @Override
    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = getItemDefinition().getHierarchy();
        entities.add(this);
        return entities;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || itemDefinition.isTrash();
    }
}