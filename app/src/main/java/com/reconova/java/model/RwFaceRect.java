//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.reconova.java.model;

import java.util.ArrayList;
import java.util.Iterator;

public class RwFaceRect {
    public int track_no;
    public int person_no;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public int lefteye_x;
    public int lefteye_y;
    public int righteye_x;
    public int righteye_y;
    public int nose_x;
    public int nose_y;
    public int centermouth_x;
    public int centermouth_y;
    public float facial_score;
    public float brightness;
    public float clearness;
    public float pitch;
    public float yaw;
    public float roll;
    public float glassness;
    public int type;
    public String name;
    public float similarity;
    public ArrayList<String> recoNameList;
    public ArrayList<Float> recoSimList;

    public RwFaceRect() {
    }

    public String toString() {
        String info = "";
        if (this.recoNameList != null) {
            String candidate;
            for(Iterator var2 = this.recoNameList.iterator(); var2.hasNext(); info = info + "_" + candidate) {
                candidate = (String)var2.next();
            }

            if (this.similarity != 0.0F) {
                info = info + "_" + this.similarity;
            }
        }

        return info;
    }
}
