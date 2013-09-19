package com.amee.domain.item.profile;

import com.amee.domain.AMEEStatus;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.path.Pathable;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseProfileItemValue extends BaseItemValue implements Pathable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROFILE_ITEM_ID")
    private ProfileItem profileItem;

    public BaseProfileItemValue() {
        super();
    }

    public BaseProfileItemValue(ItemValueDefinition itemValueDefinition, ProfileItem profileItem) {
        super(itemValueDefinition);
        setProfileItem(profileItem);
    }

    protected void copyTo(BaseItemValue o) {
        super.copyTo(o);
        BaseProfileItemValue v = (BaseProfileItemValue) o;
        v.profileItem = profileItem;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getProfileItem().isTrash() || getItemValueDefinition().isTrash();
    }

    public ProfileItem getProfileItem() {
        return profileItem;
    }

    @Override
    public ProfileItem getItem() {
        return getProfileItem();
    }

    public void setItem(BaseItem item) {
        if (ProfileItem.class.isAssignableFrom(item.getClass())) {
            this.profileItem = (ProfileItem) item;
        } else {
            throw new IllegalStateException("A ProfileItem instance was expected.");
        }
    }

    public void setProfileItem(ProfileItem profileItem) {
        this.profileItem = profileItem;
    }
}
