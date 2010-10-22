package com.amee.domain.item.data;

import com.amee.domain.AMEEStatus;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.path.Pathable;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;

@MappedSuperclass
public abstract class BaseDataItemValue extends BaseItemValue implements Pathable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DATA_ITEM_ID")
    private NuDataItem dataItem;

    @Transient
    private transient String fullPath;

    public BaseDataItemValue() {
        super();
    }

    public BaseDataItemValue(ItemValueDefinition itemValueDefinition, NuDataItem dataItem) {
        super(itemValueDefinition);
        setDataItem(dataItem);
    }

    protected void copyTo(BaseDataItemValue o) {
        super.copyTo(o);
        o.dataItem = dataItem;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getDataItem().isTrash() || getItemValueDefinition().isTrash();
    }

    public NuDataItem getDataItem() {
        return dataItem;
    }

    @Override
    public NuDataItem getItem() {
        return getDataItem();
    }

    public void setItem(BaseItem item) {
        if (NuDataItem.class.isAssignableFrom(item.getClass())) {
            this.dataItem = (NuDataItem) item;
        } else {
            throw new IllegalStateException("A NuDataItem instance was expected.");
        }
    }

    public void setDataItem(NuDataItem dataItem) {
        this.dataItem = dataItem;
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        return getDataItem().getAdapter().getItemService().getJSONObject(dataItem, detailed);
    }
}
