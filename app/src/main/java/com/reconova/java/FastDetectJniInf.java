package com.reconova.java;

import android.content.Context;

import com.reconova.java.model.FastDetectParam;
import com.reconova.java.model.FastFaceInfo;
import com.reconova.java.model.RecoImage;
import com.reconova.java.model.RwFaceRect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastDetectJniInf {

    private static long mhandle = 0L;
    private static FastDetectJniInf sProcessor;

    public FastDetectJniInf() {
    }

    public static synchronized FastDetectJniInf getInstance() {
        if (sProcessor == null) {
            sProcessor = new FastDetectJniInf();
        }
        return sProcessor;
    }

    public  int  rwFastDetectInit(String fileDir, String licPath ,Context mContext)
    {
        this.rwfinalize();
        String fn_dir = fileDir + "/assets/";
        String fn_modelpath = fn_dir + "rw_models/";
        mhandle = FastDetectJni.initFace(fn_modelpath,licPath,mContext);
        if(mhandle == 0){
            return -1;
        }
        FastDetectJni.SetFaceSize(mhandle,80,400);
        return 0;
    }

    public  int rwfinalize()
    {
        if (mhandle != 0L) {

            FastDetectJni.destroyFace(mhandle);
            mhandle = 0L;
            return 0;
        }
        return -1;
    }
    public  String getVersion()
    {
        return FastDetectJni.getVersion(mhandle);
    }
    public  int SetFaceSize(int min, int max)
    {
        return FastDetectJni.SetFaceSize(mhandle,min, max);
    }

    public ArrayList<RwFaceRect>  DetectFaceFast( RecoImage faceImage, int method)
    {
        RwFaceRect[] mRwFaceRect =  FastDetectJni.detectFace(mhandle,faceImage,method);
        if(mRwFaceRect == null){
            return  null;
        }
        return  new ArrayList(Arrays.asList(mRwFaceRect));
    }

}
