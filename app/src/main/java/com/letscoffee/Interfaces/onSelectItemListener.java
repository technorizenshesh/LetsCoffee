package com.letscoffee.Interfaces;

import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelItem;

public interface onSelectItemListener {
    void onAddToCart(ModelItem item);
    void onRemoverToCart(ModelItem item);
    void onItemDetails(ModelItem item);
}
