package com.sk.collapse.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sk.collapse.activity.R;

import tyrantgit.explosionfield.ExplosionField;

/**
 * Created by sk on 16-9-9.
 */
public class BazierSpringView extends FrameLayout {

    private final static int EXPLOR_DISTANCE = 100;
    private final static int DEFAULT_RADIUS = 100;

    private Paint mPaint;
    private Path mPath;

    private float mAuthorX=100, mAuthorY=0;
    private float startX=100, startY=0;

    private float endX=100, endY=0;

    private float mStartRadius, mEndRadius;

    private boolean isTouch;
    private boolean isExploed;

    private TextView mTxtTextView;
    private BazierSpringView mSelf;

    private ExplosionField mExploField;

    public BazierSpringView(Context context) {
        this(context, null);
    }

    public BazierSpringView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BazierSpringView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(2f);

        mPath = new Path();

        mStartRadius = mEndRadius = DEFAULT_RADIUS;

        mTxtTextView = (TextView)new TextView(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTxtTextView.setLayoutParams(lp);
        mTxtTextView.setGravity(Gravity.CENTER);
        mTxtTextView.setTextSize(13);
        mTxtTextView.setTextColor(Color.WHITE);
        mTxtTextView.setText("12");
        mTxtTextView.setBackgroundResource(R.drawable.bazierspring_bg_selector);

        mSelf = this;

        //this.removeAllViews();
        this.addView(mTxtTextView);




        mExploField = ExplosionField.attach2Window((Activity) context);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

//        if(startX == 0 && startY == 0) {
//            startX = mTxtTextView.getX();
//            startY = mTxtTextView.getY();
//            mStartRadius = mTxtTextView.getWidth();
//        }

        mSelf.setX(startX);
        mSelf.setY(startY);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect rect = new Rect();
            int[] location = new int[2];
            mSelf.getDrawingRect(rect);
            mSelf.getLocationOnScreen(location);
            rect.left = location[0];
            rect.top = location[1];
            rect.right = rect.right + location[0];
            rect.bottom = rect.bottom + location[1];
            if (rect.contains((int)event.getRawX(), (int)event.getRawY())){
                isTouch = true;
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            isTouch = false;
            mSelf.setX(startX);
            mSelf.setY(startY);
        }

        invalidate();
        if(isExploed){
            return super.onTouchEvent(event);
        }
        mAuthorX =  (event.getX() + startX)/2;
        mAuthorY =  (event.getY() + startY)/2;
        endX =  event.getX();
        endY =  event.getY();

        return true;
    }

    private void calculatePosition() {
        final float distance = (float) Math.sqrt(Math.pow(endY - startY, 2) + Math.pow(endX - startX, 2));
        mStartRadius = mEndRadius = -distance/15+DEFAULT_RADIUS;
        if(mStartRadius < 9) {
            //to explore
            isExploed = true;
            //mExploField.explode(mTxtTextView);
            //return;
        }


        float startOffsetX = (float) (mStartRadius * Math.sin(Math.atan((endY - startY) / (endX - startX))));
        float startOffsetY = (float) (mStartRadius * Math.cos(Math.atan((endY - startY) / (endX - startX))));

        float endOffsetX = (float) (mEndRadius * Math.sin(Math.atan((endY - startY) / (endX - startX))));
        float endOffsetY = (float) (mEndRadius * Math.cos(Math.atan((endY - startY) / (endX - startX))));

        float x1 = startX - startOffsetX;
        float y1 = startY + startOffsetY;

        float x2 = endX  - endOffsetX;
        float y2 = endY + endOffsetY;

        float x3 = endX + endOffsetX;
        float y3 = endY - endOffsetY;

        float x4 = startX + startOffsetX;
        float y4 = startY - startOffsetY;

        mPath.reset();
        mPath.moveTo(x1, y1);
        mPath.quadTo(mAuthorX, mAuthorY, x2, y2);
        mPath.lineTo(x3, y3);
        mPath.quadTo(mAuthorX, mAuthorY, x4, y4);
        mPath.lineTo(x1, y1);

        mSelf.setX(endX);
        mSelf.setY(endY);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if(isExploed) {

        } else {
            calculatePosition();
            canvas.drawPath(mPath, mPaint);

            canvas.save();
            canvas.translate(startX, startY);
            canvas.drawCircle(startX, startY, mStartRadius, mPaint);
            canvas.restore();
            canvas.drawCircle(endX, endY, mEndRadius, mPaint);
        }

        super.onDraw(canvas);

    }

}
