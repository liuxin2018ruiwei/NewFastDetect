/**
 * 
 */
package com.reconova.face_reco_demo.utils;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

public class SFHCameraCallback implements SurfaceHolder.Callback {
	public static final String TAG = "SFHCAMERACALLBACK";

	public static int sPreviewWidth = 640;	// 预览图片宽度
	public static int sPreViewHeight = 480; // 预览图片高度


	private SurfaceHolder mSurfaceHolder;	// 用于显示预览界面的surfaceholder
	private Camera.PreviewCallback mPreviewCallback; // 用于获取预览数据回调函数
	private Activity mActivity;
	private View mAdjustView;
	
	private Camera mOpenCamera;	// 已打开的摄像头
	public int mOpenCameraId = -1; // 已打开的摄像头id

	@SuppressWarnings("deprecation")
	public SFHCameraCallback(SurfaceHolder holder,
			Camera.PreviewCallback previewCallback, Activity activity) {
		
		this.mSurfaceHolder = holder;
		this.mSurfaceHolder.addCallback(this);
		this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.mPreviewCallback = previewCallback;
		this.mActivity = activity;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		startPreView();
		Log.e(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		adjustSize();
		openCamera();
		Log.e(TAG, "surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		closeCamera();
		Log.e(TAG, "surfaceDestroyed");
	}

	/**
	 * 获取已经打开的摄像头
	 * 
	 * @return
	 */
	public Camera getOpenCamera() {
		return mOpenCamera;
	}

	/**
	 * 打开默认摄像头
	 */
	public void openCamera() {
		int cameraId = CameraHelper.getFutureCameraId(mActivity);
//		openCamera(1);
		boolean openCamera = openCamera(0);
		if(!openCamera){
			openCamera = openCamera(1);
			if(openCamera){
				Log.i("TAG", "openCamera(0)");
			}
		}else{
			Log.i("TAG", "openCamera(1)");
		}
//		return openCamera;
	}

//	/**
//	 * 打开指定的摄像头
//	 * 
//	 * @param id
//	 */
//	public void openCamera(int cameraId) {
//		// 指定的摄像头跟已打开的摄像头一致,不在重复打开
//		if (mOpenCamera != null && mOpenCameraId == cameraId) {
//			return;
//		}
//		// 其他摄像头 正在用，关闭其他摄像头
//		else if (mOpenCamera != null) {
//			closeCamera();
//		}
//		openRealCamera(cameraId);
//	}
	/**
	 * 打开指定的摄像头
	 * 
	 * @param cameraId
	 */
	public boolean openCamera(int cameraId) {
		// 指定的摄像头跟已打开的摄像头一致,不在重复打开
		if (mOpenCamera != null && mOpenCameraId == cameraId) {
			return false;
		}
		// 其他摄像头 正在用，关闭其他摄像头
		else if (mOpenCamera != null) {
			Log.e(TAG, "openCamera closeCamera");
			closeCamera();
		}
		boolean openRealCamera = openRealCamera(cameraId);
		// openRealCamera(1);
		Log.e(TAG, "openRealCamera");
		return openRealCamera;
	}

	private boolean openRealCamera(int cameraId) {
		try {
			mOpenCameraId = cameraId;
			mOpenCamera = Camera.open(mOpenCameraId);

			return true;
		} catch (Exception e) {
			// Toast.makeText(mActivity.getApplicationContext(), "摄像头不可用",
			// Toast.LENGTH_LONG).show();
			Log.e("SFHCameraCallback", "摄像头不可用");
			Log.e("SFHCameraCallback", e.getCause() + "");
			// closeCamera();
			return false;
		}

	}

	/**
	 * 开始预览
	 */
	public void startPreView() {

		if (mOpenCamera == null)
			return;

		try {
			mOpenCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			closeCamera();
			return;
		}
		CameraSetting.setPreHeight(mActivity, sPreViewHeight);
		CameraSetting.setPreWidth(mActivity, sPreviewWidth);
		int[] futurePreviewSize = CameraHelper.getFuturePreviewSize(mActivity, mOpenCamera); // 设置预览分辨率
		Camera.Parameters parameters = mOpenCamera.getParameters();
//		parameters.setPreviewFormat(ImageFormat.YUY2);
		Log.e("", "parameters PreviewFormat:" + parameters.getPreviewFormat());
		sPreviewWidth = futurePreviewSize[0];
		sPreViewHeight = futurePreviewSize[1];
//		parameters.setFocusMode("fixed");
		parameters.setPreviewSize(sPreviewWidth, sPreViewHeight);
		mOpenCamera.setParameters(parameters);
		mOpenCamera.setPreviewCallback(mPreviewCallback);
//		mOpenCamera.setDisplayOrientation(CameraHelper.getDisplayRotation(
//				mActivity, mOpenCameraId));
//		mOpenCamera.setDisplayOrientation(90);//test 0305
		mOpenCamera.startPreview();

	}

	/**
	 * 结束预览
	 */
	public void stopPreview() {
		if (mOpenCamera != null) {
			mOpenCamera.stopPreview();
		}
	}

	/**
	 * 关闭摄像头
	 */
	public void closeCamera() {
		if (mOpenCamera == null) {
			return;
		}
		mOpenCamera.setPreviewCallback(null);
		mOpenCamera.stopPreview();
		mOpenCamera.release();
		mOpenCameraId = -1;
		mOpenCamera = null;
		Log.i("SFHCamera", "the camera is close");
	}

	/**
	 * 是否是前置摄像头
	 * @return true 前置摄像头， false 后置摄像头。
	 */
	public boolean facingFront() {
		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(mOpenCameraId, cameraInfo);
		return cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
	}
	
	/**
	 * 获取当前camera的图片需要旋转的角度
	 * @return 图片旋转角度
	 */
	public int getImageRotation() {
		return CameraHelper.getImageRotation(mActivity, mOpenCameraId);
	}

	/**
	 * 设置需要调整与摄像长宽比例的view（比如用来显示摄像头内容的view，保证显示的时候画面不失真）
	 * 
	 * @param view
	 */
	public void setAdjustView(View view) {
		this.mAdjustView = view;
	}

	public void adjustSize() {
		if (mAdjustView == null) {
			return;
		}
		ViewGroup.LayoutParams params = mAdjustView.getLayoutParams();
		float width = mAdjustView.getWidth();
		float height = mAdjustView.getHeight();
		float ratio = width / height;
		float cameraRatio = (float) sPreviewWidth / (float) sPreViewHeight;
		// 调整width
		if (ratio > cameraRatio) {
			width = height * cameraRatio;
		} else {
			height = width / cameraRatio;
		}
		params.width = (int) width;
		params.height = (int) height;
	}

}
