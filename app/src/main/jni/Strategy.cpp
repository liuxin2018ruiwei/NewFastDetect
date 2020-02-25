#include <stdio.h>
#include <android/log.h>
#include <time.h>

#include "Strategy.h"


#define DE_BUG (1)

#define LOG_TAG "C_NATIVE_IMGE_PRO"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


void *   init(const char *model_path , const char *fn_fdir_database, S_JniContext *ctx )
{
    //void *p_handle;
    //int ret = rwNDynamicLiveDetector_Init(&p_handle, (const char *)model_path, (const char *)fn_fdir_database, ctx);

    void *p_handle;
    int ret = rwNRwFaceDetector_Init(&p_handle, (const char *)model_path, (const char *)fn_fdir_database, ctx);
#if DE_BUG
    LOGD("##init ret = %d  , p_handle = %p， ctx->jAppCtx= %p \n", ret, p_handle, ctx->jAppCtx );
#endif

    return p_handle;
}

int InitChipset(const char *dev_name, int baud_rate )
{
    return  0;//0rwNDynamicLiveDetector_ChipsetConfig_LicenseParamInit(dev_name, baud_rate);
}

int  finalize( void * handle_java )
{
    //return rwNDynamicLiveDetector_Finalize((void *)handle_java);
    return rwNRwFaceDetector_Finalize((void *)handle_java);
}
const char * getVersion (void * handle_java)
{
#if DE_BUG
    LOGD(" rwNDynamicLiveDetector_GetVersion = %s \n", rwNRwFaceDetector_GetVersion(handle_java));
#endif
    //const char *version = rwNDynamicLiveDetector_GetVersion(handle_java);
    const char * version = rwNRwFaceDetector_GetVersion((void *)handle_java );
#if DE_BUG
    LOGD(" Verion = %s \n", version);
#endif
    return version ;
}

int setFaceSize(void * handle, int minFace, int maxFace)
{
       rwftface::DetectParam mParam;

        mParam.Min_face = minFace;
        mParam.Max_face = maxFace;

        mParam.net1_TH = 0.8;
        mParam.net2_TH = 0.8;
        mParam.net3_TH = 0.8;

        mParam.roi_Min_face = 80;
        mParam.roi_Max_face = 400;

        mParam.roi_net1_TH = 0.5;
        mParam.roi_net2_TH = 0.5;
        mParam.roi_net3_TH = 0.5;

        mParam.scale = 2;
        mParam.factor = 0.709;

        mParam.net1_BOXES = 20;
        mParam.net2_BOXES = 5;

        mParam.track_score = 0.5;
        mParam.update_num = 15;
/*
        //测试

      mParam.factor = 0.809f;
      mParam.scale = 1.0f;
      mParam.Min_face = 20;
      mParam.Max_face = 900;
      mParam.net1_BOXES = 20;
      mParam.net2_BOXES = 10;
      mParam.net1_TH = 0.4;
      mParam.net2_TH = 0.3;
      mParam.net3_TH = 0.3;
      mParam.roi_Min_face = 150;
      mParam.roi_Max_face = 450;
      mParam.roi_net1_TH = 0.5;
      mParam.roi_net2_TH = 0.5;
      mParam.roi_net3_TH = 0.5;
      mParam.track_score = 0.5;
      */

         bool retBool = rwNRwFaceDetector_InitParam((void *)handle, (const rwftface::DetectParam) *(&mParam));
#if DE_BUG
         LOGD("====== ======retBool = %d; mParam.factor = %f, mParam.scale = %f \n", retBool, mParam.factor, mParam.scale);
         LOGD("====== ======mParam.Min_face = %d,mParam.Max_face = %d \n", mParam.Min_face, mParam.Max_face);
         LOGD("====== ======mParam.net1_BOXES = %d,mParam.net2_BOXES = %d \n", mParam.net1_BOXES, mParam.net2_BOXES);
         LOGD("====== ======mParam.net1_TH = %f,mParam.net2_BOXES = %f, net3_TH = %f \n", mParam.net1_TH, mParam.net2_TH,mParam.net3_TH);
         LOGD("====== ======mParam.roi_Min_face = %d,mParam.roi_Max_face = %d \n", mParam.roi_Min_face, mParam.roi_Max_face);
         LOGD("====== ======mParam.roi_net1_TH = %f,mParam.roi_net2_TH = %f, roi_net3_TH = %f \n", mParam.roi_net1_TH, mParam.roi_net2_TH,mParam.roi_net3_TH);
         LOGD("====== ======mParam.track_score = %f, \n", mParam.track_score);
#endif

         if(retBool == false)
         {
            return -1;
         }
         return 0;
}

int  fastDetectFace(void *handle, const unsigned char* img, int img_w, int img_h, int widthstep, rwftface::FaceList* mFacelist, int  * size )
{
    std::vector<rwftface::FaceList> faces;
    int ret =  rwNRwFaceDetector_DetectFaceFast(handle, img,  img_w,  img_h,  widthstep, faces);

    int len  = faces.size();
    *size = len;
    if(len <= 0)
    {
        return -1;
    }
    if(len > 10 )
    {
        len = 10;
    }
    //mFacelist = new rwftface::FaceList[len];
    memset(mFacelist, sizeof(rwftface::FaceList) * len , 0);

    LOGD("len = %d \n", len);
    for(int i = 0; i < len; i++ )
    {
        rwftface::FaceList mFaceTemp = faces.at(i);
        memcpy( &mFacelist[i], &mFaceTemp, sizeof(rwftface::FaceList));

        LOGD("mFacelist[%d]->facebox.xmin = %f \n", i, mFacelist[i].facebox.xmin);
        LOGD("mFacelist[%d]->facebox.xmax = %f \n", i, mFacelist[i].facebox.xmax);
        LOGD("mFacelist[%d]->facebox.ymin = %f\n", i, mFacelist[i].facebox.ymin);
        LOGD("mFacelist[%d]->facebox.ymax  = %f\n", i, mFacelist[i].facebox.ymax);
        LOGD("mFacelist[%d]->facebox.score = %f\n", i, mFacelist[i].facebox.score);
    }
    faces.clear();
    return ret;
}

