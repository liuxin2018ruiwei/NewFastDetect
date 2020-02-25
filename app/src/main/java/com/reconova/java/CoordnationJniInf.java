package com.reconova.java;

import com.reconova.java.model.DynamicLandmarkInfo;

public class CoordnationJniInf {

    private static long mhandle = 0L;
    private static CoordnationJniInf sProcessor;

    public CoordnationJniInf() {
    }

    public static synchronized CoordnationJniInf getInstance() {
        if (sProcessor == null) {
            sProcessor = new CoordnationJniInf();
        }
        return sProcessor;
    }

    public int rwNDynamicInit(String fileDir)
    {
        rwfinalize ();
        String fn_dir = fileDir + "/assets/";
        String fn_modelpath = fn_dir + "rw_models/";
        mhandle = CoordnationJni.rwNDynamicInit(fn_modelpath, fn_modelpath, 0);
        if(mhandle == 0)
        {
            return -1;
        }
        return  0;
    }
    public  int rwChipsetConfigInit( String dev_name, int baud_rate)
    {
        return  CoordnationJni.rwChipsetConfigInit(dev_name,baud_rate);
    }
    public  int rwfinalize()
    {
        if(mhandle != 0){
            CoordnationJni.rwfinalize(mhandle);
            mhandle = 0;
        }
        return 0;
    }
    public  DynamicLandmarkInfo rwNDynamicLiveDetectorDynamic(byte[] img, int img_w, int img_h, int widthstep,
                                                                           int face_x, int face_y, int face_w, int face_h)
    {
        return CoordnationJni.rwNDynamicLiveDetectorDynamic(mhandle, img, img_w, img_h, widthstep, face_x,  face_y,  face_w,  face_h);
    }
    public  String getVersion()
    {
        return  CoordnationJni.getVersion(mhandle);
    }
}
