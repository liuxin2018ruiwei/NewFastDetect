package com.reconova.face_reco_demo.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.reconova.face_reco_demo.R;
import com.reconova.java.CoordnationJniInf;
import com.reconova.java.FastDetectJni;
import com.reconova.java.FastDetectJniInf;
import com.reconova.java.model.FastDetectParam;
import com.reconova.java.model.FastFaceInfo;
import com.reconova.java.model.RecoImage;
import com.reconova.java.model.RwFaceRect;
import com.reconova.processor.ImageHolder;
import com.reconova.utils.FileTool;

/**
 * 欢迎界面进行初始化。
 */
public class SplashActivity extends BaseActivity {

	protected static final String TAG = "SplashActvity";


	void JNI()
    {
        Log.i("TEST", "start");
        FastDetectJni jni = new FastDetectJni();
        String fileDir = getApplication().getFilesDir().getAbsolutePath();
        String fn_dir = fileDir + "/assets/";
        String fn_modelpath = fn_dir + "rw_models";
        FileTool.copyAssetFiles(getApplication(), fileDir);

        long handle = jni.initFace(fn_modelpath,fn_modelpath, getApplication());

        Log.i("TEST", "handle = "+ handle);

        jni.SetFaceSize(handle,40,800);

        String Version = jni.getVersion(handle);
        Log.i("Version", "Version = "+ Version);


        String imgPathTest = getApplication().getFilesDir().getAbsolutePath() + "/assets/t1.jpg";
        Log.i(TAG,"imgPathTest = " + imgPathTest);
        ImageHolder imgTestHolder = ImageHolder.createFromImagePath(imgPathTest);
        RecoImage image = RecoImage.convertToRecoImage(imgTestHolder);
        byte[] grayImageData = ImageHolder.allocateGray8(imgTestHolder);
        ImageHolder.convertToGray8(imgTestHolder, grayImageData);

        Log.i(TAG,"convertToGray8 end");

        RwFaceRect[] mRwFaceRect =  jni.detectFace(handle,image,0);
        if(mRwFaceRect == null)
        {
            return ;
        }
        if(mRwFaceRect.length > 0)
        {
            RwFaceRect mTempRect = mRwFaceRect[0];
            Log.i("TEST", "jni.DetectFace top = " + mTempRect.top);
            Log.i("TEST", "jni.DetectFace bottom = " + mTempRect.bottom);
            Log.i("TEST", "jni.DetectFace left = " + mTempRect.left);
            Log.i("TEST", "jni.DetectFace right = " + mTempRect.right);
        }
        Log.i("TEST", "jni.DetectFace");
        jni.destroyFace(handle);
        Log.i("TEST", "jni.DetectFace Test ok");
    }
    InitNativeLibTask mInitNativeLibTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);


        JNI();



        mInitNativeLibTask = new InitNativeLibTask();
        mInitNativeLibTask.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


    private class InitNativeLibTask extends AsyncTask<Object, Object, Boolean> {

        public static final int STATE_NOT_START = 0;
        public static final int STATE_RUNNING = 1;
        public static final int STATE_DONE = 2;
        public static final int STATE_CANCEL = 3;

        public int mState = STATE_NOT_START;

        @Override
        protected Boolean doInBackground(Object... arg0) {
            mState = STATE_RUNNING;
            String fileDir = getApplicationContext().getFilesDir()
                    .getAbsolutePath();
            // 将assets路径下的模型文件，拷贝到指定路径。
            FileTool.copyAssetFiles(getApplicationContext(), fileDir);

            Log.i(TAG, "faceInit start, fileDir = " + fileDir);
            boolean ret = true;
            if (ret == true) {
                int retFaset = FastDetectJniInf.getInstance().rwFastDetectInit(fileDir,fileDir, getApplicationContext());
                Log.i("retFaset", "Fast Face Init  retFaset = " + retFaset);
                if (retFaset == 0) {
                    int retCoor = CoordnationJniInf.getInstance().rwNDynamicInit(fileDir);
                    Log.i("retFaset", "retCoor  = " + retCoor);
                    if (retCoor == 0) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
            Log.i(TAG, "faceInit end");

            return ret;
        }

        protected void onPostExecute(Boolean result) {
            mState = STATE_DONE;

            Log.i(TAG, "onPostExecute start");
            if (result) {
                FastDetectJniInf.getInstance().SetFaceSize(40,800);
                Intent intent = new Intent(SplashActivity.this,
                        RecoFaceActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "初始化失败",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}
