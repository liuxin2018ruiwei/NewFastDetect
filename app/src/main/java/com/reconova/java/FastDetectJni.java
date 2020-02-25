package com.reconova.java;


import android.content.Context;

import com.reconova.java.model.FastDetectParam;
import com.reconova.java.model.FastFaceInfo;
import com.reconova.java.model.RecoImage;
import com.reconova.java.model.RwFaceRect;

public class FastDetectJni {
    static {
        System.loadLibrary("CrwFastFaceDetect");
        System.loadLibrary("FastDetect");
    }
    //public static native long rwFastDetectInit(String model_path,String database_path, int context);
//    public static native int rwFastDetectChipsetConfigInit( String dev_name, int baud_rate);
//    public static native int rwfinalize(long handle);
//    public static native String getVersion(long handle);
//    public static native int initParam(long handle, FastDetectParam mParam);
//    public static native FastFaceInfo[] DetectFace(long handle, byte [] img, int img_w, int img_h, int widthstep);
//    public static native FastFaceInfo[] DetectFaceTwo(long handle, byte [] img, int img_w, int img_h, int widthstep);
//    public static native FastFaceInfo[] DetectFaceFast(long handle, byte [] img, int img_w, int img_h, int widthstep);
//    public static native FastFaceInfo[] DetectFaceByRoi(long handle, byte [] img, int img_w, int img_h, int widthstep,
//                                                                        int roi_x, int roi_y, int roi_w, int roi_h);
//    public static native FastFaceInfo[] TrackFace(long handle, byte [] img, int img_w, int img_h, int widthstep, FastFaceInfo[] mFastInfo, int mFastInfolen);

    public static native long initFace(String pathAsses,String licpath, Object context);
    public static native int InitChipset(String devName, int rate);
    public static native void destroyFace(long handle);
    public static native String getVersion(long handle);

    public static native int SetFaceSize(long mHandle, int minSize, int maxSize);

    public static native RwFaceRect[] detectFace(long mhandle, RecoImage faceImage, int method);
}
