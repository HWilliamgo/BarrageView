package com.william.barrageview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by William on 2018/8/17.
 */
public class BarrageView extends View {
    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDefaultPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    class Entry {
        long startTime;
        long currentTime;
        String t;
        int x;
        int y;
        TextPaint paint;

        Entry(long startTime, String t, int x, int y, TextPaint paint) {
            this.startTime = startTime;
            this.t = t;
            this.x = x;
            this.y = y;
            this.paint = paint;
        }
    }

    enum Mode {
        ALL, TOP, BOTTOM
    }

    private static final int DEFAULT_TEXT_SIZE = 80;
    private static final int DEFAULT_TEXT_COLOR = Color.RED;
    private static final int DEFAULT_CENTER_SHOW_TIME = 3;
    private static final int DEFAULT_SPEED = 4;

    private LinkedList<Entry> mEntries = new LinkedList<>();
    private LinkedList<Entry> mCenterEntries = new LinkedList<>();

    private int mWidth;//弹幕控件宽度
    private int mHeight;//弹幕控件高度

    private int mSpeed = DEFAULT_SPEED;
    private int mTextSize = DEFAULT_TEXT_SIZE;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private Mode mMode = Mode.ALL;
    private Random random = new Random();

    private int mCenterShowingTime = DEFAULT_CENTER_SHOW_TIME;

    private TextPaint mDefaultPaint;

    private void initDefaultPaint() {
        mDefaultPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mDefaultPaint.setTextSize(mTextSize);
        mDefaultPaint.setColor(mTextColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Entry p;
        for (int i = 0; i < mEntries.size(); i++) {
            p = mEntries.get(i);
            canvas.drawText(p.t, p.x, p.y, p.paint);
        }
        for (int i = 0; i < mCenterEntries.size(); i++) {
            p = mCenterEntries.get(i);
            canvas.drawText(p.t, p.x, p.y, p.paint);
        }

        handleMovement();
        invalidate();
    }

    private void handleMovement() {
        Entry p;
        for (int i = 0; i < mEntries.size(); i++) {
            p = mEntries.get(i);
            p.x -= mSpeed;
            if (p.x < -p.paint.measureText(p.t)) {
                mEntries.remove(i);
            }
        }
        for (int i = 0; i < mCenterEntries.size(); i++) {
            p = mCenterEntries.get(i);
            p.currentTime = System.currentTimeMillis();
            if (p.currentTime - p.startTime >= 1000 * mCenterShowingTime) {
                mCenterEntries.remove(i);
            }
        }
    }

    private int getYByMode(Mode mode) {
        int y = 0;
        switch (mode) {
            case ALL:
                y = random.nextInt(mHeight - mTextSize) + mTextSize;
                break;
            case TOP:
                y = random.nextInt(mHeight / 2 - mTextSize) + mTextSize;
                break;
            case BOTTOM:
                y = random.nextInt(mHeight / 2 - mTextSize) + mTextSize + mHeight / 2;
                break;
        }
        return y;
    }

    public void send(String content) {
        Entry entry = new Entry(0, content, mWidth,
                getYByMode(mMode), mDefaultPaint);
        mEntries.add(entry);
    }

    public void send(String content, int color, int textSize) {
        TextPaint paint = new TextPaint(mDefaultPaint);
        paint.setColor(color);
        paint.setTextSize(textSize);

        Entry entry = new Entry(0, content, mWidth,
                getYByMode(mMode), paint);
        mEntries.add(entry);
    }

    public void sendCenter(String content) {
        Entry entry = new Entry(System.currentTimeMillis(), content,
                (int) ((mWidth - mDefaultPaint.measureText(content)) / 2)
                , getYByMode(mMode), new TextPaint(mDefaultPaint));
        mCenterEntries.add(entry);
    }

    public void sendCenter(String content, int color, int textSize) {
        TextPaint paint = new TextPaint(mDefaultPaint);
        paint.setColor(color);
        paint.setTextSize(textSize);

        Entry entry = new Entry(System.currentTimeMillis(), content,
                (int) ((mWidth - mDefaultPaint.measureText(content)) / 2)
                , getYByMode(mMode), paint);
        mCenterEntries.add(entry);
    }

    public void clearScreen() {
        mEntries.clear();
        mCenterEntries.clear();
    }

    public void setSpeed(@IntRange(from = 3, to = 10) int speed) {
        this.mSpeed = speed;
    }

    public void setDefaultCenterShowTime(@IntRange(from = 1, to = 10) int time) {
        this.mCenterShowingTime = time;
    }

    public void setDefaultTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setDefaultTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }
}
