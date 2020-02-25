package com.reconova.face_reco_demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CameraSetting {
	
	public static final String PREF_CAMERA_SETTING = "cameraSetting";
	public static final String KEY_CAMERA_ID = "camera_id";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_HEIGHT = "height";
	
	public static final int DEFAULT_ID = 0;

	public static void setCameraId(Context context, int cameraId) {
		SharedPreferences sp = context.getSharedPreferences(PREF_CAMERA_SETTING,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(KEY_CAMERA_ID, cameraId);
		editor.commit();
	}

	public static int getCameraId(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREF_CAMERA_SETTING,
				Context.MODE_PRIVATE);
		int cameraId = sp.getInt(KEY_CAMERA_ID, DEFAULT_ID);
		return cameraId;
	}

	public static void setPreWidth(Context context, int width) {
		SharedPreferences sp = context.getSharedPreferences(PREF_CAMERA_SETTING,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(KEY_WIDTH, width);
		editor.commit();
	}

	public static int getPreWidth(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREF_CAMERA_SETTING,
				Context.MODE_PRIVATE);
		int width = sp.getInt(KEY_WIDTH, 1280);
		return width;
	}

	public static void setPreHeight(Context context, int height) {
		SharedPreferences sp = context.getSharedPreferences(PREF_CAMERA_SETTING,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(KEY_HEIGHT, height);
		editor.commit();
	}

	public static int getPreHeight(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREF_CAMERA_SETTING,
				Context.MODE_PRIVATE);
		int height = sp.getInt(KEY_HEIGHT, 720);
		return height;
	}
}
