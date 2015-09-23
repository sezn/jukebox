package com.szn.jukebox.request;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.szn.jukebox.BuildConfig;
import com.szn.jukebox.JukeApplication;
import com.szn.jukebox.model.Constants;


/**
 * Classe Utilitaire de Requete
 */
public class JSONRequest extends JsonObjectRequest {

    private static final String TAG = "JSONRequest";
    private final Response.Listener<JSONObject> listener;
    private  JSONObject jsonRequest;
    JukeApplication app;

    public JSONRequest(int method, String url,  JSONObject jsonObject, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonObject, listener, errorListener);
        this.jsonRequest = jsonObject;
        this.listener = listener;
        this.app = JukeApplication.getInstance();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        String userAgent = Constants.USER_AGENT;
        try {
            String packageName = app.getPackageName();
            PackageInfo info = app.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        headers.put("Accept", "application/json");
        headers.put("User-Agent", userAgent);
        headers.put("Cache-Control", "no-cache");
        return headers;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(json), HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            if(BuildConfig.DEBUG) Log.w(TAG, "UnsupportedEncodingException");
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            if(BuildConfig.DEBUG) Log.w(TAG, "JsonSyntaxException");
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            if(BuildConfig.DEBUG) Log.w(TAG, "JSONException");
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError){
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }

        return volleyError;
    }

}