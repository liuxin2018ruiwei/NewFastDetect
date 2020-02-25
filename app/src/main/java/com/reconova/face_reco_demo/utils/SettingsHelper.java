package com.reconova.face_reco_demo.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsHelper {
	public static String TAG = "SettingsHelper";
	private Context mContext;
	
	
	public SettingsHelper(Context context) {
		mContext = context;
	}
	
	/**
	 * 判断设置中是否设置了界面翻转
	 * @return true设置了界面翻转 ， 否则false
	 */
	public boolean isDrawingReverse() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		return sp.getBoolean("pref_draw_reverse_checkbox", true);
	}
	
	/**
	 * 获取设置界面中指定的特征提取的最大次数
	 * @return 特征提取的最大次数
	 */
	public int getMaxExtractNum() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		int minValidNum = 1;
		int maxValidNum = 10;
		int defaultNum  = 5;
		
		String numValue = sp.getString("pref_key_register_num", String.valueOf(defaultNum));
		
		int  num = maxValidNum;
		try {
			num = Integer.parseInt(numValue);
			if (num < minValidNum || num > maxValidNum) {
				num = maxValidNum;
			}
		} catch (Exception e) {
			num = maxValidNum;
		}
		return num;
	}
	
	
	/**
	 * 获取设置界面中指定的人脸识别时的搜索阈值。
	 * @return
	 */
	public float getSearchThreshold() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		String threshold  = sp.getString("pref_key_face_similarity", "50");
		float minValidThreshold = 1.0f;
		float maxValidThreshold = 100.0f;
		float defaultNValue = 50.0f;
		float similarity;
		try {
			similarity = Float.parseFloat(threshold);
			if (similarity < minValidThreshold || similarity > maxValidThreshold) {
				similarity = defaultNValue;
			}
		} catch (Exception e) {
			similarity = defaultNValue;
		}
		return similarity;
		
		// 由于设置界面中的搜索相似度阈值采用百分制，
		// 由于库中的人脸相似度阈值采用的特征差距（0 ~ 50）
		// 下面代码将将百分制阈值转成特征差距。
		
		/*
		 * 公式如下：
		 * search_threshold = 100-similarity (80<similarity<=100)
		 * search_threshold = (140-s)/3      (50<=similarity<=80) 
		 * search_threshold = 50-0.4y        (0<=similarity<50)
		 
		
		float search_threshold = 0.0f;
		if (similarity > 80 && similarity <= 100) {
			search_threshold = 100.0f - similarity;
		} else if (similarity >= 50 && similarity <= 80) {
			search_threshold = (140.0f - similarity) / 3.0f;
		} else if (similarity >= 0 && similarity < 50) {
			search_threshold = 50 - 0.4f * similarity;
		}
		return search_threshold;*/
	}
	
	/**
	 * 获取鼠标速度
	 * @return 鼠标速度
	 */
	public float getMouseSpeed() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		String stringValue = sp.getString("pref_key_mouse_speed", "6");
		int speed = 6;
		try {
			speed = Integer.parseInt(stringValue);
			if (speed < 1 || speed > 20) {
				speed = 6;
			}
		} catch (Exception e){
			speed = 6;
		}
		return ((float)speed) * 0.1f;
	}

	/**
	 * 获取设置界面中指定的图片保存数据
	 * @return
	 */
	public  int getPicNumber() {
		int getNum;
		final  int MIN_NUM = 0;
		final  int MAX_NUM = 10000;
		final  int DEFAULT_NUM = 50;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		String picNumber  = sp.getString("pref_genaral_save_pic_number", "50");
		try {
			getNum = DEFAULT_NUM;
			int temp = Integer.parseInt(picNumber);
			if (getNum < MAX_NUM || getNum > MIN_NUM) {
				getNum = temp;
			}
		} catch (Exception e) {
			getNum = DEFAULT_NUM;
		}
		return getNum;
	}

	/**
	 * 判断设置中是否设置了界面翻转
	 * @return true设置了界面翻转 ， 否则false
	 */
	public boolean isSavePic() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		return sp.getBoolean("pref_general_checkbox_save_pic", false);
	}



}
