package com.szn.jukebox.interfaces;

import android.view.View;

/**
 * Created by Julien Sezn on 13/07/2015.
 */
public interface FragmentListener {

//    void onBtnClick(View v);
    void onClick(View v);

    void onClickOnView(View v, int state);

//    void onAddressChanged(int what, int subscriptionId, SubscriberAddress address);
}
