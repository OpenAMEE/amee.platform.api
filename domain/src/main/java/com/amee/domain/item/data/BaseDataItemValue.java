package com.amee.domain.item.data;

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
public abstract class BaseDataItemValue extends BaseItemValue implements Pathable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DATA_ITEM_ID")
    private DataItem dataItem;

    public BaseDataItemValue() {
        super();
    }

    public BaseDataItemValue(ItemValueDefinition itemValueDefinition, DataItem dataItem) {
        super(itemValueDefinition);
        setDataItem(dataItem);
    }

    protected void copyTo(BaseItemValue o) {
        super.copyTo(o);
        BaseDataItemValue v = (BaseDataItemValue) o;
        v.dataItem = dataItem;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getDataItem().isTrash() || getItemValueDefinition().isTrash();
    }

    public DataItem getDataItem() {
        return dataItem;
    }

    @Override
    public DataItem getItem() {
        return getDataItem();
    }

    public void setItem(BaseItem item) {
        if (DataItem.class.isAssignableFrom(item.getClass())) {
            this.dataItem = (DataItem) item;
        } else {
            throw new IllegalStateException("A DataItem instance was expected.");
        }
    }

    public void setDataItem(DataItem dataItem) {
        this.dataItem = dataItem;
    }
}
