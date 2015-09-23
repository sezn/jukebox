package com.szn.jukebox.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.szn.jukebox.model.EyeCorrection;
import com.szn.jukebox.model.Product;

/**
 * Created by Julien Sezn on 10/09/2015.
 *
 */
public class Utils {

    private static final String TAG = "Utils";

    /**
     * @param context
     * @param fileName
     * @return jsonString
     */
    public static String readFromAssets(Context context, String fileName){

        String jsonString = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public static List<Product> getProductsFromJSON(Context con){
        List<Product> events = new ArrayList<>();

        Gson gson = new Gson();
        String jsonStr = readFromAssets(con, "products.json");
        JSONArray jsArray = null;
        try {
            JSONObject json = new JSONObject(jsonStr);
            jsArray = json.getJSONArray("products");

            if(jsArray != null){
                for(int i= 0; i < jsArray.length(); i++){
                    events.add(gson.fromJson(jsArray.getJSONObject(i).toString(), Product.class));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    public static List<EyeCorrection> getCorrectionsFromJSON(Context con){
        List<EyeCorrection> corrections = new ArrayList<>();

        Gson gson = new Gson();
        String jsonStr = readFromAssets(con, "corrections.json");
        JSONArray jsArray = null;
        try {
            JSONObject json = new JSONObject(jsonStr);
            jsArray = json.getJSONArray("eyes");

            if(jsArray != null){
                for(int i= 0; i < jsArray.length(); i++){
                    corrections.add(gson.fromJson(jsArray.getJSONObject(i).toString(), EyeCorrection.class));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return corrections;
    }


    public static Product getMesLentillesFromJSON(Context con){

        String jsonStr = readFromAssets(con, "meslentilles.json");
        Product lens = new Gson().fromJson(jsonStr, Product.class);
        return lens;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }





    public static void hideKeyBoard(Context con, View v) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
