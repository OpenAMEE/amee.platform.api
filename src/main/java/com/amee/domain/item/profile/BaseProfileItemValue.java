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
    private NuProfileItem profileItem;

    public BaseProfileItemValue() {
        super();
    }

    public BaseProfileItemValue(ItemValueDefinition itemValueDefinition, NuProfileItem profileItem) {
        super(itemValueDefinition);
        setProfileItem(profileItem);
    }

    protected void copyTo(BaseProfileItemValue o) {
        super.copyTo(o);
        o.profileItem = profileItem;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getProfileItem().isTrash() || getItemValueDefinition().isTrash();
    }

    public NuProfileItem getProfileItem() {
        return profileItem;
    }

    @Override
    public NuProfileItem getItem() {
        return getProfileItem();
    }

    public void setItem(BaseItem item) {
        if (NuProfileItem.class.isAssignableFrom(item.getClass())) {
            this.profileItem = (NuProfileItem) item;
        } else {
            throw new IllegalStateException("A NuProfileItem instance was expected.");
        }
    }

    public void setProfileItem(NuProfileItem profileItem) {
        this.profileItem = profileItem;
    }
}
