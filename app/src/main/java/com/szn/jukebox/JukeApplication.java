package com.szn.jukebox;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.szn.jukebox.model.Constants;
import com.szn.jukebox.utils.ComplexPreferences;

/**
 * Created by Julien Sezn on 23/09/2015.
 *
 */
public class JukeApplication extends Application {

    private static final String TAG = JukeApplication.class.getSimpleName();
    private static JukeApplication mInstance;
    protected SharedPreferences sharedPrefs = null;
    protected ComplexPreferences complexPreferences = null;
    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    private User user;


    public static synchronized JukeApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPrefs = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        complexPreferences = ComplexPreferences.getComplexPreferences(this, "complexPrefs", MODE_PRIVATE);

        initImageLoader();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public SharedPreferences getSharedPref(){
        return  sharedPrefs;
    }

    public User getUserFromPrefs(){
        return complexPreferences.getObject(Constants.USER, User.class);
    }

    public void setUser(User usr){
        sharedPrefs.edit().putInt("user_id", usr.getId()).commit();
        complexPreferences.putObject(Constants.USER, usr);
        user = usr;
    }

    public User getUser(){
        if(user == null)
            user = complexPreferences.getObject(Constants.USER, User.class);

        return user;
    }

    public void reinitUser(){
        sharedPrefs.edit().remove("user_id").apply();
    }

    public boolean isUserExists(){
        if(sharedPrefs.getInt("user_id", 0) > 0)
            return true;

        return false;
    }


    public void putObjectInPrefs(String key, Object obj){
        complexPreferences.putObject(key, obj);
    }

    public Object getObjectFromPrefs(String key, Object obj){
        return complexPreferences.getObject(key, obj.getClass());
    }

    public String getStringFromPrefs(String id) {
        return sharedPrefs.getString(id, null);
    }

    public void putStringInPrefs(String key, String value){
        sharedPrefs.edit().putString(key, value).commit();
    }
    public void removeFromPrefs(String key) {
    }

    public int getIntFromPrefs(String id) {
        return sharedPrefs.getInt(id, 0);
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 4096); // 4MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }

        return mRequestQueue;
    }


    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        req.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().add(req);;
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private void initImageLoader() {
//        Log.w(TAG, "Setting Cache TO: " + (Runtime.getRuntime().maxMemory() / 1024) / 8);
        imageLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024) / 8);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
