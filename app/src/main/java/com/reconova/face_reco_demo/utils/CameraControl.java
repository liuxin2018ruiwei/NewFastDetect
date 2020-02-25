package com.reconova.face_reco_demo.utils;

import java.io.IOException;


import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class CameraControl {
	public static int sPreviewWidth = 320;	// 预览图片宽度
	public static int sPreViewHeight = 240; // 预览图片高度
	
	private Activity mActivity;
	private View mAdjustView;
	
	private SurfaceTexture mSurfaceTexture;
	private Camera mOpenCamera;	// 已打开的摄像头
	private PreviewCallback mPreviewCallback;
	
	public int mOpenCameraId = -1; // 已打开的摄像头id
	
	
	public CameraControl() {
		mSurfaceTexture = new SurfaceTexture(0);
	}

	
	/**
	 * 打开默认摄像头
	 */
	public void openCamera() {
		int cameraId = CameraHelper.getFutureCameraId(mActivity);
		openCamera(cameraId);
	}

	/**
	 * 打开指定的摄像头
	 * 
	 * @param id
	 */
	public void openCamera(int cameraId) {
		// 指定的摄像头跟已打开的摄像头一致,不在重复打开
		if (mOpenCamera != null && mOpenCameraId == cameraId) {
			return;
		}
		// 其他摄像头 正在用，关闭其他摄像头
		else if (mOpenCamera != null) {
			closeCamera();
		}
		openRealCamera(cameraId);
	}

	private void openRealCamera(int cameraId) {
		mOpenCameraId = cameraId;
		mOpenCamera = Camera.open(mOpenCameraId);
	}

	/**
	 * 开始预览
	 */
	public void startPreView() {

		if (mOpenCamera == null)
			return;

		try {
			mOpenCamera.setPreviewTexture(mSurfaceTexture);
		} catch (IOException e) {
			closeCamera();
			return;
		}
		int[] futurePreviewSize = CameraHelper.getFuturePreviewSize(mActivity, mOpenCamera); // 设置预览分辨率
		Camera.Parameters parameters = mOpenCamera.getParameters();
		sPreviewWidth = futurePreviewSize[0];
		sPreViewHeight = futurePreviewSize[1];
		parameters.setPreviewSize(sPreviewWidth, sPreViewHeight);
		mOpenCamera.setParameters(parameters);
		mOpenCamera.setPreviewCallback(mPreviewCallback);
		mOpenCamera.setDisplayOrientation(CameraHelper.getDisplayRotation(
				mActivity, mOpenCameraId));
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
