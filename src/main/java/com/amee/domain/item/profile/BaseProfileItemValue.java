package com.amee.domain.item.profile;

import com.amee.domain.AMEEStatus;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.path.Pathable;

import javax.persistence.*;

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

    public void setProfileItem(NuProfileItem profileItem) {
        this.profileItem = profileItem;
    }
}
