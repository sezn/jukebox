package com.szn.jukebox.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by Julien Sezn on 10/09/2015.
 *
 */
public interface LoginListener {

    void onLogged();

    void onLogError(VolleyError error);
}
