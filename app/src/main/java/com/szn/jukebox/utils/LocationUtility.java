package com.szn.jukebox.utils;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julien Sezn on 18/09/2015.
 *
 */
public class LocationUtility {

    static final String TAG = LocationUtility.class.getSimpleName();



    public static Location getLocation(JSONObject jsonObject) {
        Location loc = new Location("Optic2000");

        double lat = 0, lon = 0;
        try {
            if (jsonObject != null
                    && ((JSONArray) jsonObject.get("results")).getJSONObject(0) != null
                    && ((JSONArray) jsonObject.get("results")).length() > 0) {
                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");
                lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");
                loc.setLatitude(lat);
                loc.setLongitude(lon);
                return loc;
            } else
                return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
