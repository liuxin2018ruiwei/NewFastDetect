package com.reconova.java;

import android.graphics.Point;

import com.reconova.java.model.DynamicLandmarkInfo;
import com.reconova.java.model.EyeLandmarkInfo;
import com.reconova.java.model.FaceLandmarkInfo;

//import android.content.Context;
public class CoordnationJni {
    static {
        System.loadLibrary("CrwDLiveDetect");
        System.loadLibrary("Coordination");
    }
    public static native long rwNDynamicInit(String model_path,String database_path, int context);
    public static native int rwChipsetConfigInit( String dev_name, int baud_rate);
    public static native int rwfinalize(long handle);
    public static native DynamicLandmarkInfo rwNDynamicLiveDetectorDynamic(long handle, byte[] img, int img_w, int img_h, int widthstep,
                                                                           int face_x, int face_y, int face_w, int face_h);
    public static native String getVersion(long handle);


}
