package com.reconova.face_reco_demo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.reconova.face_reco_demo.R;
import com.reconova.face_reco_demo.processor.ProcessorManager;
import com.reconova.face_reco_demo.utils.ImageHelper;
import com.reconova.face_reco_demo.utils.SFHCameraCallback;
import com.reconova.face_reco_demo.utils.SettingsHelper;
import com.reconova.java.CoordnationJniInf;
import com.reconova.java.FastDetectJniInf;
import com.reconova.java.model.DynamicLandmarkInfo;
import com.reconova.java.model.FastFaceInfo;
import com.reconova.java.model.RecoImage;
import com.reconova.java.model.RwFaceRect;
import com.reconova.processor.ImageHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class RecoFaceActivity extends BaseActivity implements
		Camera.PreviewCallback {
	// 帮助控制button的状态。
	class ButtonStateController {
		ArrayList<Button> mButtons = new ArrayList<Button>();

		public void addButton(Button button) {
			mButtons.add(button);
		}

		public void onlyDisable(Button buttonToDisable) {
			for (Button btn : mButtons) {
				btn.setEnabled(!(btn == buttonToDisable));
			}
		}
	}

	final  String TAG = "nwgsyps_FaceActivity";
	private ButtonStateController mButtonStateController;
	private Button mSettingButton;
	private Button mCameraButton;
	private Button mDetectButton;
	private Button mRegisterButton;
	private Button mRecoButton;
	private Button mManagerButton;

	private TextView mStateTextView;

	private FrameLayout mAdjustSizeView;
	private SurfaceView mCameraSurfaceView;
	private FaceCanvasView mFaceCanvasView;

	private CameraDialog mCameraSettingDialog;

	private ProcessorManager mProcessorManager;
	private Hashtable<String, FaceProcessor> mProcessorSet;
	private FaceProcessor mCurrentProcessor;

	private ImageHolder mImageHolder;

	private SFHCameraCallback mSFHCameraCallback;

	private SettingsHelper mSettingsHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reco_face);

		findViews();
		setupViews();
		initCameraAndProcessor();
		gotoDetectState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 设置参数
		setUpParameters();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 关闭摄像机
		mSFHCameraCallback.closeCamera();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// 设置参数
	private void setUpParameters() {
		if (mSettingsHelper == null) {
			mSettingsHelper = new SettingsHelper(getApplicationContext());
		}
		// 设置是否界面翻转
		mFaceCanvasView.setFacingFront(mSettingsHelper.isDrawingReverse());
	}

	private void initCameraAndProcessor() {
		// camera相关
		mSFHCameraCallback = new SFHCameraCallback(
				mCameraSurfaceView.getHolder(), this, this);
		mSFHCameraCallback.setAdjustView(mAdjustSizeView);
		// 处理器相关
		mProcessorManager = new ProcessorManager();

		mProcessorSet = new Hashtable<String, FaceProcessor>();
		mProcessorSet.put(FaceDetectProcessor.class.getName(),
				new FaceDetectProcessor());

		mCurrentProcessor = mProcessorSet.get(FaceDetectProcessor.class
				.getName());

		mImageHolder = new ImageHolder(null, 0, 0, ImageHolder.YUVSP420_TYPE);
	}

	private void findViews() {
		mSettingButton = (Button) this.findViewById(R.id.setting__btn);
		mCameraButton = (Button) this.findViewById(R.id.camera_btn);
		mRecoButton = (Button) this.findViewById(R.id.reco_btn);
		mDetectButton = (Button) this.findViewById(R.id.detect_btn);
		mRegisterButton = (Button) this.findViewById(R.id.register_btn);
		mManagerButton = (Button) this.findViewById(R.id.manager_btn);

		mStateTextView = (TextView) this.findViewById(R.id.state_text);

		mAdjustSizeView = (FrameLayout) this
				.findViewById(R.id.frame_camera_draw);
		mCameraSurfaceView = (SurfaceView) this
				.findViewById(R.id.surfaceview_camera);
		mFaceCanvasView = (FaceCanvasView) this
				.findViewById(R.id.canvasview_draw);

		//隐藏按钮
        mRecoButton.setVisibility(View.GONE);
        mRegisterButton.setVisibility(View.GONE);
        mManagerButton.setVisibility(View.GONE);
	}

	private void setupViews() {
		mFaceCanvasView.setCameraSize(SFHCameraCallback.sPreviewWidth,
				SFHCameraCallback.sPreViewHeight);

		mStateTextView.setTextColor(Color.BLACK);
		// 按钮事件设置
		mDetectButton.setOnClickListener(mBtnOnClickListner);
		mRegisterButton.setOnClickListener(mBtnOnClickListner);
		mRecoButton.setOnClickListener(mBtnOnClickListner);
		mSettingButton.setOnClickListener(mBtnOnClickListner);
		mManagerButton.setOnClickListener(mBtnOnClickListner);
		mCameraButton.setOnClickListener(mBtnOnClickListner);

		mButtonStateController = new ButtonStateController();
		mButtonStateController.addButton(mDetectButton);
		mButtonStateController.addButton(mRegisterButton);
		mButtonStateController.addButton(mRecoButton);
	}

	private OnClickListener mBtnOnClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mDetectButton == v) {
				gotoDetectState();
			}
			if (mSettingButton == v) {
				startActivity(new Intent(RecoFaceActivity.this,
						SettingsActivity.class));
			}
			if (mCameraButton == v) {
				if (mCameraSettingDialog == null) {
					mCameraSettingDialog = new CameraDialog(
							RecoFaceActivity.this);
				}
				mCameraSettingDialog.setSFHCameraCallback(mSFHCameraCallback);
				mCameraSettingDialog.show();
			}
		}
	};

	/**
	 * 检测状态
	 */
	private void gotoDetectState() {
		mButtonStateController.onlyDisable(mDetectButton);
		mStateTextView.setText("人脸检测中...");
		mCurrentProcessor.reset();
		mCurrentProcessor = mProcessorSet.get(FaceDetectProcessor.class.getName());
	}


	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (mProcessorManager == null) {
			mProcessorManager = new ProcessorManager();
		}
		Camera.Size size = camera.getParameters().getPreviewSize();
		int ratio = (size.width > 640 ? 2 : 1);
//		int rotateAngle = mSFHCameraCallback.getImageRotation();
        int rotateAngle = 0;
		data = ImageHelper.rotateYUV420sp(data, size.width, size.height, rotateAngle);
		if (rotateAngle != 0 && rotateAngle != 180) {
			mImageHolder.setSize(size.height, size.width);
			mFaceCanvasView.setCameraSize(size.height/ratio, size.width/ratio);
		} else {
			mImageHolder.setSize(size.width, size.height);
			mFaceCanvasView.setCameraSize(size.width/ratio, size.height/ratio);
		}
		mImageHolder.setImageData(data);
		mImageHolder.setRatio(ratio);
		mCurrentProcessor.setImageHolder(mImageHolder);
		mCurrentProcessor.setCurrentTime(System.currentTimeMillis());
		if(!isFinishing())
			mProcessorManager.startProcessor(mCurrentProcessor);
	}

	// 人脸检测处理器
	private class FaceDetectProcessor extends FaceProcessor {

		void testCoor(ArrayList<RwFaceRect> faceList){
			if(faceList ==null){
				return;
			}
			if(faceList.size() > 0){
					RecoImage image = RecoImage.convertToRecoImage(imageHolder);
					int faceW  = faceList.get(0).right - faceList.get(0).left;
					int faceH = faceList.get(0).bottom - faceList.get(0).top;

					long start = System.currentTimeMillis();
					DynamicLandmarkInfo mDynamicLandmarkInfo = CoordnationJniInf.getInstance().rwNDynamicLiveDetectorDynamic(image.imagedata,image.width, image.height,image.width,
							faceList.get(0).left, faceList.get(0).top,faceW,faceH);
					Log.i("TestTime","time " + (System.currentTimeMillis() - start));
			}
		}

		public synchronized ArrayList<RwFaceRect> faceDetect(ImageHolder imageHolder)
		{
			RecoImage image = RecoImage.convertToRecoImage(imageHolder);
			long startFastTime = System.currentTimeMillis();
			ArrayList<RwFaceRect>  DetectFaceFast = FastDetectJniInf.getInstance().DetectFaceFast(image,0);
			Log.i("TestTime","time " + (System.currentTimeMillis() - startFastTime));
			return DetectFaceFast;
		}

		@Override
		public Object onProcess() {
			long startTime = System.currentTimeMillis();
			faceList =  faceDetect(imageHolder);
			mFaceCanvasView.setFaceList(faceList);
			mFaceCanvasView.postInvalidate();


//			testCoor(faceList);

			return null;
		}

		@Override
		public void onPostExcute(Object result) {
		}
	}

	private abstract class FaceProcessor implements ProcessorManager.IProcessor {
		protected ImageHolder imageHolder;
		protected ArrayList<RwFaceRect> faceList;
		protected long lastTime;
		protected float time_interval;

		public FaceProcessor() {
			lastTime = -1;
			time_interval = 0;
		}

		public void reset() {
			lastTime = -1;
			time_interval = 0;
		}

		public void setImageHolder(ImageHolder imageHolder) {
			this.imageHolder = imageHolder;
		}

		// 设置当前时间，计算两帧之间的时间间隔
		public void setCurrentTime(long currentTime) {
			if (lastTime == -1) {
				time_interval = 0;
			} else {
				time_interval = (currentTime - lastTime) / 1000.0f;
			}
			lastTime = currentTime;
		}
	}

}
