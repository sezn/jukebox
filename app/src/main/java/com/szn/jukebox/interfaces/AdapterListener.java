package com.szn.jukebox.interfaces;

import android.view.View;

import com.szn.jukebox.model.Product;
import com.szn.jukebox.model.Store;
import com.szn.jukebox.model.Subscription;

/**
 * Created by Julien Sezn on 02/09/2015.
 *
 */
public interface AdapterListener {

    void onSelected(View v, int pos);
    void onSelected(View v, Product lens);
    void onSelected(View v, Subscription subs);
    void onSelected(View v, Store store);

    void onPayBtn(View v, float price);

    void onFavoriteSelected(Store store);
}
