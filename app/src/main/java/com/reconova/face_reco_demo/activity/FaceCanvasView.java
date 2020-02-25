package com.reconova.face_reco_demo.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.reconova.java.model.RwFaceRect;

import java.util.ArrayList;

public class FaceCanvasView extends ImageView {
	private ArrayList<RwFaceRect> mFaceList;
	private int mCameraWidth = 0;
	private int mCameraHeight = 0;
	private boolean mFacingFront = false;
	private int mCanvasWidth;
	private int mCanvasHeight;
	private float mXRatio;
	private float mYRatio;

	private Paint mRectPaint;
	private Paint mNamePaint;
	private RectF mDrawFaceRect = new RectF();

	FaceCanvasView(Context context) {
		super(context);
		reset();
	}

	public FaceCanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		reset();
	}

	public FaceCanvasView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		reset();
	}

	public void reset() {
		if (mFaceList == null) {
			mFaceList = new ArrayList<RwFaceRect>();
		}
		mFaceList.clear();
		mCameraWidth = 1;
		mCameraHeight = 1;
		// 矩形框
		mRectPaint = new Paint();
		mRectPaint.setColor(Color.BLUE);
		mRectPaint.setStyle(Paint.Style.STROKE);
		mRectPaint.setStrokeWidth(8);
		// 识别名
		mNamePaint = new Paint();
		mNamePaint.setColor(Color.BLUE);
		mNamePaint.setTextSize(40);
		mNamePaint.setStyle(Paint.Style.FILL);
	}

	public void setCameraSize(int cameraWidth, int cameraHeight) {
		mCameraWidth = cameraWidth;
		mCameraHeight = cameraHeight;
	}

	public void setFaceList(ArrayList<RwFaceRect> faceList) {
		mFaceList.clear();
		if (faceList == null) {
			return;
		}
		mFaceList.addAll(faceList);

	}

	public void setFacingFront(boolean facingFront) {
		mFacingFront = facingFront;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCanvasWidth = canvas.getWidth();
		mCanvasHeight = canvas.getHeight();
		mXRatio = (float) mCanvasWidth / (float) mCameraWidth;
		mYRatio = (float) mCanvasHeight / (float) mCameraHeight;
		drawFaceResult(canvas);
	}

	/**
	 * 画人脸框：与人脸检测、注册、识别相关
	 * */
	private void drawFaceResult(Canvas canvas) {
		// 清空画布
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		// 获取画布长宽
		for (RwFaceRect faceRect : mFaceList) {
			/*
			 * 根据人脸矩形框与摄像头的面向，调整画布矩形的位置信息。
			 */

			if (mFacingFront) {
				mDrawFaceRect.left = mCanvasWidth
						- (float) faceRect.right * mXRatio;
				mDrawFaceRect.right = mCanvasWidth
						- (float) faceRect.left * mXRatio;
				mDrawFaceRect.top = (float) faceRect.top * mYRatio;
				mDrawFaceRect.bottom = (float) faceRect.bottom * mYRatio;
			} else {
				mDrawFaceRect.left = (float) faceRect.left * mXRatio;
				mDrawFaceRect.right = (float) faceRect.right * mXRatio;
				mDrawFaceRect.top = (float) faceRect.top * mYRatio;
				mDrawFaceRect.bottom = (float) faceRect.bottom * mYRatio;
			}
			canvas.drawRect(mDrawFaceRect, mRectPaint);

			// 画识别名
			String name = "";
			if (faceRect.recoNameList != null) {
				for (String candidate : faceRect.recoNameList) {
					name += "_" + candidate;
				}
				if(faceRect.similarity != 0){
					name += "_" + faceRect.similarity;
				}

				canvas.drawText(name, mDrawFaceRect.left, mDrawFaceRect.top - 10,
						mNamePaint);
			}

		}


	}

}
