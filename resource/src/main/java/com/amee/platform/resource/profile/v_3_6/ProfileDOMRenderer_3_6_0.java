package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.domain.data.DataCategory;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.profile.ProfileResource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileDOMRenderer_3_6_0 implements ProfileResource.Renderer {

    protected Profile profile;
    protected Element rootElem;
    protected Element profileElem;
    protected Element categoriesElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public void newProfile(Profile profile) {
        this.profile = profile;
        profileElem = new Element("Profile");
        if (rootElem != null) {
            rootElem.addContent(profileElem);
        }
    }

    @Override
    public void addBasic() {
        profileElem.setAttribute("uid", profile.getUid());
    }

    @Override
    public void addAudit() {
        profileElem.setAttribute("status", profile.getStatus().getName());
        profileElem.setAttribute("created", DATE_FORMAT.print(profile.getCreated().getTime()));
        profileElem.setAttribute("modified", DATE_FORMAT.print(profile.getModified().getTime()));
    }

    @Override
    public void startCategories() {
        if (profileElem != null) {
            categoriesElem = new Element("Categories");
            profileElem.addContent(categoriesElem);
        }
    }

    @Override
    public void newCategory(DataCategory category) {
        if (categoriesElem != null) {
            Element categoryElem = new Element("Category");
            categoriesElem.addContent(categoryElem);
            categoryElem.setAttribute("uid", category.getUid());
            categoryElem.addContent(new Element("Name").setText(category.getName()));
            categoryElem.addContent(new Element("WikiName").setText(category.getWikiName()));
        }
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
