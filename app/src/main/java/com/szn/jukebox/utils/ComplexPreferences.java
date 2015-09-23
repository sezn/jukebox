package com.szn.jukebox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ComplexPreferences {

	private static ComplexPreferences complexPreferences;
	private Context context;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private static Gson GSON = new Gson();
	Type typeOfObject = new TypeToken<Object>() {}.getType();

	private ComplexPreferences(Context context, String namePreferences, int mode) {
		this.context = context;
		if (namePreferences == null || namePreferences.equals("")) {
			namePreferences = "complex_preferences";
		}
		preferences = context.getSharedPreferences(namePreferences, mode);
		editor = preferences.edit();
	}

	public static ComplexPreferences getComplexPreferences(Context context,	String namePreferences, int mode) {
		if (complexPreferences == null) {
			complexPreferences = new ComplexPreferences(context, namePreferences, mode);
		}

		return complexPreferences;
	}

	public void putObject(String key, Object object) {
		if(object == null){
            editor.putString(key, null).commit();
//			throw new IllegalArgumentException("object is null");
            return;
		}

		if(key.equals("") || key == null){
			throw new IllegalArgumentException("key is empty or null");
		}

		editor.putString(key, GSON.toJson(object));
        commit();
	}

	public void commit() {
		editor.commit();
	}

	public <T> T getObject(String key, Class<T> a) {
	
		String gson = preferences.getString(key, null);
		if (gson == null) {
			return null;
		} else {
            Log.w("Complex", gson);

			try{
				return GSON.fromJson(gson, a);
			} catch (Exception e) {
				throw new IllegalArgumentException("Object storaged with key " + key + " is instanceof other class");				
			}
		}
	}
		
	
}
