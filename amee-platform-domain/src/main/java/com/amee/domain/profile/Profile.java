package com.amee.domain.profile;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.AMEEEntity;
import com.amee.domain.AMEEStatus;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import com.amee.domain.auth.AuthorizationContext;
import com.amee.domain.auth.Permission;
import com.amee.domain.auth.PermissionEntry;
import com.amee.domain.auth.User;
import com.amee.domain.environment.Environment;
import com.amee.domain.path.Pathable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "profile")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Profile extends AMEEEntity implements Pathable {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    public Profile() {
        super();
    }

    public Profile(User user) {
        super();
        setUser(user);
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", getUid());
        obj.put("path", getDisplayPath());
        obj.put("name", getDisplayName());
        if (detailed) {
            obj.put("created", getCreated().toString());
            obj.put("modified", getModified().toString());
            obj.put("user", getUser().getIdentityJSONObject());
            obj.put("environment", Environment.ENVIRONMENT.getIdentityJSONObject());
        }
        return obj;
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return XMLUtils.getIdentityJSONObject(this);
    }

    public Element getElement(Document document) {
        return getElement(document, true);
    }

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("Profile");
        element.setAttribute("uid", getUid());
        element.appendChild(XMLUtils.getElement(document, "Path", getPath()));
        element.appendChild(XMLUtils.getElement(document, "Name", getName()));
        if (detailed) {
            element.setAttribute("created", getCreated().toString());
            element.setAttribute("modified", getModified().toString());
            element.appendChild(getUser().getIdentityElement(document));
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, this);
    }

    /**
     * Add 'built-in' Permission to this Profile, such that the associated User owns the Profile. Will also clear
     * the current PermissionEntries in the AuthorizationContext so they are are not inherited for Profiles.
     *
     * @param authorizationContext to consider
     * @return permissions list
     */
    public List<Permission> handleAuthorizationContext(AuthorizationContext authorizationContext) {
        List<Permission> permissions = new ArrayList<Permission>();
        // Ensure PermissionEntries are not inherited for Profiles.
        // Access to a Profile is for owner only or other principals that have been granted direct access.
        authorizationContext.getEntries().clear();
        // Create Permission stating that this Profile is owned by the associated User, if the User is active.
        if (authorizationContext.getPrincipals().contains(getUser())) {
            permissions.add(new Permission(getUser(), this, PermissionEntry.OWN));
        }
        return permissions;
    }

    @Override
    public List<IAMEEEntityReference> getHierarchy() {
        // Profile does not support hierarchy.
        return Arrays.<IAMEEEntityReference>asList(this);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        this.user = user;
    }

    public String getName() {
        return "";
    }

    public String getPath() {
        return "";
    }

    public String getDisplayName() {
        return getDisplayPath();
    }

    public String getDisplayPath() {
        return getUid();
    }

    public String getFullPath() {
        return getDisplayPath();
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || user.isTrash();
    }

    public ObjectType getObjectType() {
        return ObjectType.PR;
    }
}