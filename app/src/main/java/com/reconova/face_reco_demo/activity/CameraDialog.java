package com.reconova.face_reco_demo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.reconova.face_reco_demo.R;
import com.reconova.face_reco_demo.utils.CameraHelper;
import com.reconova.face_reco_demo.utils.CameraSetting;
import com.reconova.face_reco_demo.utils.SFHCameraCallback;

public class CameraDialog extends Dialog {

	Spinner mCameraSpinner; // 选择相机下拉框
	Spinner mSolutionSpinner; // 选择分辨率下拉框

	ArrayList<String> mCameraEntries = new ArrayList<String>();
	ArrayList<Integer> mCameraEntryValues = new ArrayList<Integer>();
	ArrayAdapter<String> mCameraListAdapter;

	ArrayList<String> mCameraSolutions = new ArrayList<String>();
	List<Size> mCameraSolutionValues = new ArrayList<Size>();
	ArrayAdapter<String> mCameraSolutionAdapter;

	SFHCameraCallback mSFHCameraCallback = null;

	public void setSFHCameraCallback(SFHCameraCallback callback) {
		mSFHCameraCallback = callback;
	}

	public CameraDialog(Context context) {
		super(context);
	}

	public CameraDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.camera_dialog);
		this.setTitle("相机设置");
		findViews();
		setUpViews();
	}

	private void findViews() {
		mCameraSpinner = (Spinner) this.findViewById(R.id.spinner_camera);
		mSolutionSpinner = (Spinner) this
				.findViewById(R.id.spinner_camera_solution);
	}

	private void setUpViews() {
		loadCameraInfos();
		mCameraListAdapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				android.R.id.text1, mCameraEntries);

		mCameraSpinner.setAdapter(mCameraListAdapter);
		mCameraSpinner.setSelection(mSFHCameraCallback.mOpenCameraId);
		mCameraSpinner.setOnItemSelectedListener(mCameraSelectedListener);
		mCameraListAdapter.notifyDataSetChanged();

		loadCameraSolutions();
		mCameraSolutionAdapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				android.R.id.text1, mCameraSolutions);

		mSolutionSpinner.setAdapter(mCameraSolutionAdapter);
		verifyAndSelectSolution();

		this.setOnShowListener(mOnShowListener);
		this.setOnDismissListener(mOnDismissListener);
	}

	private void loadCameraInfos() {
		mCameraEntries.clear();
		mCameraEntryValues.clear();

		CameraInfo cameraInfo = new CameraInfo();
		int count = Camera.getNumberOfCameras();
		String facingFrontStr = ":前置";
		String facingBackStr = ":后置";
		for (int i = 0; i < count; ++i) {
			Camera.getCameraInfo(i, cameraInfo);
			String info = "摄像头"
					+ i
					+ (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT ? facingFrontStr
							: facingBackStr);
			mCameraEntries.add(info);
			mCameraEntryValues.add(i);
		}
	}

	private void loadCameraSolutions() {
		mCameraSolutions.clear();
		mCameraSolutionValues.clear();
		Camera camera = mSFHCameraCallback.getOpenCamera();
		if (camera != null) {
			List<Size> sizeList = camera.getParameters()
					.getSupportedPreviewSizes();
			for (Size size : sizeList) {
				fitlerSize(size);
			}
			if (mCameraSolutions.size() == 0) {
				Size size = sizeList.get(sizeList.size() - 1);
				mCameraSolutions.add(size.width + "X" + size.height);
				mCameraSolutionValues.add(size);
			}
		}
	}

	private void fitlerSize(Size size) {
		if (CameraHelper.isAcceptable(size)) {
			mCameraSolutions.add(size.width + "X" + size.height);
			mCameraSolutionValues.add(size);
		}
	}

	private void verifyAndSelectSolution() {

		int width = SFHCameraCallback.sPreviewWidth;
		int height = SFHCameraCallback.sPreViewHeight;

		for (int i = 0; i < mCameraSolutionValues.size(); ++i) {
			Size size = mCameraSolutionValues.get(i);
			if (size.width == width && size.height == height) {
				mSolutionSpinner.setSelection(i);
				return;
			}
		}

		if (mCameraSolutionValues.size() > 0) {
			mSolutionSpinner.setSelection(0);
		}

	}

	OnShowListener mOnShowListener = new OnShowListener() {
		@Override
		public void onShow(DialogInterface dialog) {
			mSFHCameraCallback.stopPreview();
		}
	};

	OnDismissListener mOnDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {

			int solutionIndex = mSolutionSpinner.getSelectedItemPosition();
			SFHCameraCallback.sPreviewWidth = mCameraSolutionValues
					.get(solutionIndex).width;
			SFHCameraCallback.sPreViewHeight = mCameraSolutionValues
					.get(solutionIndex).height;
			mSFHCameraCallback.adjustSize();
			int cameraId = mCameraSpinner.getSelectedItemPosition();
			
			saveInfo(cameraId, SFHCameraCallback.sPreviewWidth, SFHCameraCallback.sPreViewHeight);
			mSFHCameraCallback.openCamera(cameraId);
			mSFHCameraCallback.startPreView();
		}

		private void saveInfo(int cameraId, int width, int height) {
			Context context = CameraDialog.this.getContext();
			CameraSetting.setCameraId(context, cameraId);
			CameraSetting.setPreWidth(context, width);
			CameraSetting.setPreHeight(context, height);
		}

	};

	OnItemSelectedListener mCameraSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			loadCameraSolutions();
			mCameraSolutionAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	};
}
