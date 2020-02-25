//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.reconova.java;

public class InfoHolder {
    public InfoHolder() {
    }

    public static class MatchResult {
        static final double NO_FACE_IN_FIRST_IMG = -1.0D;
        static final double NO_FACE_IN_SECOND_IMG = -2.0D;
        public boolean result;
        public double similarity;

        public MatchResult() {
        }
    }

    public static class RecoParams {
        static InfoHolder.RecoParams sRecoParams = new InfoHolder.RecoParams();
        public boolean bHandFast = true;
        public float face_detect_threshold = 0.9F;
        public int search_topK = 3;
        public float search_threshold = 0.0F;
        public float moving_vel_x = 1.0F;
        public float moving_vel_y = 1.0F;
        public float moving_acc = 0.1F;
        public float hand_threshold = 0.5F;
        public float mouse_click_time_interval = 1.5F;
        public boolean bFilterFHandWaving = false;
        public float filterHandWavingTime = 0.0F;

        public RecoParams() {
        }
    }
}
