package com.reconova.face_reco_demo.utils;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;

/**
 * @author guluxixi
 * 用来获取要调用要的打开的cameraId，获取用于预览的分辨率
 * 获取预览界面需要旋转的角度，获取预览数据需要旋转的角度。
 *
 */
public class CameraHelper {
	
	/**
	 * 一般来说，为了能够有较好的速度保证，我们只支持下列几种分辨率。
	 */
	private static final int[][] sAcceptList = new int[][] {
			new int[] { 320, 240 },
			new int[] { 352, 288 },
			new int[] { 640, 480 },
			new int[] { 640, 360 },
			new int[] { 1280, 720 }};

	/**
	 * 获取要打开的摄像头id
	 * 
	 * @param context
	 * @return 返回要打开的摄像头id
	 */
	public static int getFutureCameraId(Context context) {
		// 从配置文件中获取要打开的cameraId
		int cameraId = CameraSetting.getCameraId(context);
		// 判断配置文件的cameraId是否合法，不合法设为0
		int num = Camera.getNumberOfCameras();
		if (cameraId > num) {
			cameraId = 0;
			// 保存到配置文件中。
			CameraSetting.setCameraId(context, 0);
		}
		return cameraId;
	}

	/**
	 * 获取要设置摄像头的预览分辨率
	 * 
	 * @param context
	 * @param camera
	 */
	public static int[] getFuturePreviewSize(Context context, Camera camera) {
		// 从配置文件中获取分辨率
		int width = CameraSetting.getPreWidth(context);
		int height = CameraSetting.getPreHeight(context);
		Log.e("", "before width:" + width + " height:" + height);
		// 判断该摄像头是不是支持这个分辨率
		List<Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
		boolean isSupportedSize = isIn(width, height, sizeList);
		// 如果摄像头不支持分辨率，取该摄像头支持的分辨率，更改配置文件中的内容。
		if (!isSupportedSize) {
			Size size = sizeList.get(sizeList.size() - 1);
			width = size.width;
			height = size.height;
			CameraSetting.setPreWidth(context, width);
			CameraSetting.setPreHeight(context, height);
		}
		int[] previewSize = new int[2];
		previewSize[0] = width;
		previewSize[1] = height;
		return previewSize;
	}

	/**
	 * 获取图片数据的需要的旋转角度。
	 * @param activity
	 * @param cameraId
	 * @return
	 */
	public static int getImageRotation(Activity activity, int cameraId) {
		
		int angle = getDisplayRotation(activity, cameraId);
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			angle = (360 - angle) % 360;
		}
		return angle;
	}

	/**
	 * 获取预览时显示需要旋转的角度
	 * 
	 * @param activity
	 * @param cameraId
	 * @return
	 */
	public static int getDisplayRotation(Activity activity, int cameraId) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	public static boolean isAcceptable(Size size) {
		int width = size.width;
		int height = size.height;
		for (int i = 0; i < sAcceptList.length; ++i) {
			if (width == sAcceptList[i][0] && height == sAcceptList[i][1]) {
				return true;
			}
		}
		return false;
	}

	private static boolean isIn(int width, int height, List<Size> sizes) {
		for (Size size : sizes) {
			Log.e("", "SizeList:" + size.width + " height:" + size.height);
			if (width == size.width && height == size.height) {
				return true;
			}
		}
		return false;
	}

}
