package com.example.mlapplication.helper;

import android.graphics.Rect;
import android.graphics.RectF;

public class BoxWithLabel {
    public String text;
    public Rect rect;

    public BoxWithLabel(String text, Rect rect) {
        this.text = text;
        this.rect = rect;
    }

    public BoxWithLabel(String displayName, RectF boundingBox) {
        this.text = displayName;
        this.rect = new Rect();
        boundingBox.round(rect);
    }
}