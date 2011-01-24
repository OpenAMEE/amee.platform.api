package com.amee.domain.data;

import com.amee.domain.item.BaseItemValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ItemValueMap {

    private NuItemValueMap nuItemValueMap;

    @Transient
    private transient ItemValueMap adapter;

    public ItemValueMap(NuItemValueMap nuItemValueMap) {
        super();
        setNuItemValueMap(nuItemValueMap);
    }

    private void setNuItemValueMap(NuItemValueMap itemValueMap) {
        itemValueMap.setAdapter(this);
        this.nuItemValueMap = itemValueMap;
    }

    public static ItemValueMap getItemValueMap(NuItemValueMap itemValueMap) {
        if (itemValueMap != null) {
            if (itemValueMap.getAdapter() != null) {
                return itemValueMap.getAdapter();
            } else {
                return new ItemValueMap(itemValueMap);
            }
        } else {
            return null;
        }
    }

    public boolean isLegacy() {
        return false;
    }

    public ItemValue get(String path) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            BaseItemValue biv = getNuItemValueMap().get(path);
            if (biv != null) {
                return ItemValue.getItemValue(biv);
            } else {
                return null;
            }
        }
    }

    public List<ItemValue> getAll(Date startDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return nuToAdapter(getNuItemValueMap().getAll(startDate));
        }
    }

    public List<ItemValue> getAll(String path) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return nuToAdapter(getNuItemValueMap().getAll(path));
        }
    }

    public ItemValue get(String path, Date startDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            BaseItemValue biv = getNuItemValueMap().get(path, startDate);
            if (biv != null) {
                return ItemValue.getItemValue(biv);
            } else {
                return null;
            }
        }
    }

    public ItemValue put(String path, ItemValue itemValue) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuItemValueMap().put(path, itemValue.getNuEntity());
            return itemValue;
        }
    }

    private List<ItemValue> nuToAdapter(List<BaseItemValue> baseItemValues) {
        return (List<ItemValue>) CollectionUtils.collect(
                baseItemValues, new Transformer() {
                    public Object transform(Object baseItemValue) {
                        return ((BaseItemValue) baseItemValue).getAdapter();
                    }
                });
    }

    public NuItemValueMap getNuItemValueMap() {
        return nuItemValueMap;
    }

    public Set keySet() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuItemValueMap().keySet();
        }
    }

    public ItemValueMap getAdapter() {
        return adapter;
    }
}
