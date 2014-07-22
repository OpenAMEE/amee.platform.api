package com.amee.domain.auth;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.AMEEEntity;
import com.amee.domain.AMEEEntityReference;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import com.amee.domain.environment.Environment;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;

/**
 * A GroupPrincipal joins a Group to a principal via an EntityReference.
 *
 * @author Diggory Briercliffe
 */
@Entity
@Table(name = "group_principal")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GroupPrincipal extends AMEEEntity implements Comparable {

    /**
     * The Group that the principal is a member of.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    /**
     * The principal that is a member of the Group.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "entityId", column = @Column(name = "principal_id")),
            @AttributeOverride(name = "entityUid", column = @Column(name = "principal_uid")),
            @AttributeOverride(name = "entityType", column = @Column(name = "principal_type"))})
    private AMEEEntityReference principalReference = new AMEEEntityReference();

    public GroupPrincipal() {
        super();
    }

    public GroupPrincipal(Group group, IAMEEEntityReference principal) {
        this();
        setGroup(group);
        setPrincipalReference(new AMEEEntityReference(principal));
    }

    public int compareTo(Object o) throws ClassCastException {
        if (this == o) return 0;
        if (equals(o)) return 0;
        GroupPrincipal groupPrincipal = (GroupPrincipal) o;
        return getUid().compareTo(groupPrincipal.getUid());
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", getUid());
        obj.put("group", getGroup().getIdentityJSONObject());
        obj.put("principal", getPrincipalReference().getJSONObject());
        if (detailed) {
            obj.put("environment", Environment.ENVIRONMENT.getIdentityJSONObject());
            obj.put("created", getCreated());
            obj.put("modified", getModified());
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
        Element element = document.createElement("GroupPrincipal");
        element.setAttribute("uid", getUid());
        element.appendChild(getGroup().getIdentityElement(document));
        element.appendChild(getPrincipalReference().getElement(document, "Principal"));
        if (detailed) {
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
            element.setAttribute("created", getCreated().toString());
            element.setAttribute("modified", getModified().toString());
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, this);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        if (group != null) {
            this.group = group;
        }
    }

    public AMEEEntityReference getPrincipalReference() {
        return principalReference;
    }

    public void setPrincipalReference(AMEEEntityReference principalReference) {
        if (principalReference != null) {
            this.principalReference = principalReference;
        }
    }

    public ObjectType getObjectType() {
        return ObjectType.GP;
    }
}