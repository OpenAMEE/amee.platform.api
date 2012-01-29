package com.amee.platform.resource.profileitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.domain.ProfileItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.profile.BaseProfileItemValue;
import com.amee.platform.resource.profileitemvalue.ProfileItemValueResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValueDOMRenderer_3_6_0 implements ProfileItemValueResource.Renderer {

    @Autowired
    protected ProfileItemService profileItemService;
    
    protected BaseProfileItemValue profileItemValue;
    protected Element rootElem;
    protected Element profileItemValueElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }
    
    @Override
    public void newProfileItemValue(BaseProfileItemValue profileItemValue) {
        this.profileItemValue = profileItemValue;
        profileItemValueElem = new Element("Value");
        if (rootElem != null) {
            rootElem.addContent(profileItemValueElem);
        }
    }

    @Override
    public void addBasic() {
        profileItemValueElem.setAttribute("uid", profileItemValue.getUid());
        profileItemValueElem.addContent(new Element("Value").setText(profileItemValue.getValueAsString()));
        if (NumberValue.class.isAssignableFrom(profileItemValue.getClass())) {
            NumberValue nv = (NumberValue) profileItemValue;
            if (nv.hasUnit()) {
                profileItemValueElem.addContent(new Element("Unit").setText(nv.getUnit().toString()));
                if (nv.hasPerUnit()) {
                    profileItemValueElem.addContent(new Element("PerUnit").setText(nv.getPerUnit().toString()));
                    profileItemValueElem.addContent(new Element("CompoundUnit").setText(nv.getCompoundUnit().toString()));
                }
            }
        }
    }

    @Override
    public void addAudit() {
        profileItemValueElem.setAttribute("status", profileItemValue.getStatus().getName());
        profileItemValueElem.setAttribute("created", DATE_FORMAT.print(profileItemValue.getCreated().getTime()));
        profileItemValueElem.setAttribute("modified", DATE_FORMAT.print(profileItemValue.getModified().getTime()));
    }

    @Override
    public void addItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        Element e = new Element("ItemValueDefinition");
        profileItemValueElem.addContent(e);
        e.setAttribute("uid", itemValueDefinition.getUid());
        e.addContent(new Element("Name").setText(itemValueDefinition.getName()));
        e.addContent(new Element("Path").setText(itemValueDefinition.getPath()));
    }

    @Override
    public void addProfileItem() {
        Element e = new Element("Item");
        profileItemValueElem.addContent(e);
        e.setAttribute("uid", profileItemValue.getProfileItem().getUid());
    }

    @Override
    public void addDataCategory() {
        Element e = new Element("Category");
        profileItemValueElem.addContent(e);
        e.setAttribute("uid", profileItemValue.getProfileItem().getDataCategory().getUid());
        e.addContent(new Element("WikiName").setText(profileItemValue.getProfileItem().getDataCategory().getWikiName()));
    }

    @Override
    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }
}
