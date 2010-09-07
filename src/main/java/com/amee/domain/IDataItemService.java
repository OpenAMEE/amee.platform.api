package com.amee.domain;

import com.amee.domain.item.data.NuDataItem;

public interface IDataItemService extends IItemService {

    public NuDataItem getItemByUid(String uid);
}
