package com.szn.jukebox.interfaces;

import android.view.View;

import fr.infogene.contacto.model.Lens;
import fr.infogene.contacto.model.Store;
import fr.infogene.contacto.model.Subscription;

/**
 * Created by Julien Sezn on 02/09/2015.
 *
 */
public interface AdapterListener {

    void onSelected(View v, int pos);
    void onSelected(View v, Lens lens);
    void onSelected(View v, Subscription subs);
    void onSelected(View v, Store store);

    void onPayBtn(View v, float price);

    void onFavoriteSelected(Store store);
}
