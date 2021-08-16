package com.example.paint_diary.Paint;

import android.graphics.MaskFilter;
import android.graphics.Path;

public class FingerPath {
    private int color;
    private int strokeWidth;
    private Path path;
    private MaskFilter filter;
    public FingerPath(int color, int strokeWidth, Path path, MaskFilter filter) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
        this.filter = filter;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public MaskFilter getFilter() {
        return filter;
    }

    public void setFilter(MaskFilter filter) {
        this.filter = filter;
    }

}
