LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE := fastFace-prebuilt
LOCAL_SRC_FILES := libCrwFastFaceDetect.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := FastDetect
LOCAL_LDLIBS :=-llog
LOCAL_SRC_FILES := NativeDriverProcessor.cpp \
                    Strategy.h \
                    Strategy.cpp
LOCAL_SHARED_LIBRARIES := fastFace-prebuilt

include $(BUILD_SHARED_LIBRARY)


