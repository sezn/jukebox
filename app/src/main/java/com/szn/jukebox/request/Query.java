package com.szn.jukebox.request;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.szn.jukebox.JukeApplication;
import com.szn.jukebox.model.Constants;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by seznju on 20/03/2015.
 */
public class Query {

    private static final String TAG = "Query";
    private Context context;
    private JukeApplication app;
    int mStatusCode;
    QueryListener listener;


    /**
     * Build  and launch the RequestQueue
     * @param context
     * @return mRequestQueue
     */
    public RequestQueue initRequestQueue(Context context) {
        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        return mRequestQueue;
    }

    public Query(Context context, QueryListener listener) {
        this.context  = context;
        this.listener = listener;
        this.app      = app.getInstance();
    }

    public void query(final String uri, int method, final Map<String,String> queriesParams) {

        StringRequest stringRequest = new StringRequest(method, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onSuccess(response, mStatusCode);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                if(queriesParams != null)
                    for (Map.Entry<String, String> entry : queriesParams.entrySet())
                        params.put(entry.getKey(), entry.getValue());
                return params;
            }
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                mStatusCode = response.statusCode;
//                return super.parseNetworkResponse(response);
//            }

            /**
             * @Overrider si besoin de Header spécifique
             * @return
             * @throws AuthFailureError
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                 String userAgent = Constants.USER_AGENT;
                    try {
                        String packageName = context.getPackageName();
                        PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
                        userAgent = packageName + "/" + info.versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                    headers.put("User-Agent", userAgent);
                    headers.put("Cache-Control", "no-cache");

                return headers;
            }
        };

        app.addToRequestQueue(stringRequest);
    }

    public interface QueryListener{
        void onSuccess(String response, int statusCode);
        void onError(VolleyError error);
    }


}
