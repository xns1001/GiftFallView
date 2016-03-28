package me.xns.giftfallview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by xns on 2016/3/11.
 */
public class GiftFallView extends View {
    private static final int NUM_SNOWFLAKES = 30; // 红包数量
    private static final int DELAY = 8; // 延迟
    private SnowFlake[] mSnowFlakes; // 红包
    private boolean mIsLast =false;
    private boolean mIsDraw=false;
    private boolean mCanFinished =true;
    private Bitmap[] mBitmaps;
    public GiftFallView(Context context) {
        super(context);
    }
    public GiftFallView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GiftFallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init(Bitmap[] bmps){
        this.mBitmaps=bmps;
    }
    public void stopFall(){
        mIsLast =true;
    }
    public void startFall(){
        mIsDraw=true;
        invalidate();
    }

    @TargetApi(21)
    public GiftFallView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            initSnow(w>0?w:1, h>0?h:1);
        }
    }

    private void initSnow(int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿
        paint.setStyle(Paint.Style.FILL); // 填充;
        mSnowFlakes = new SnowFlake[NUM_SNOWFLAKES];
        SnowFlake.initCanvasDimens(width, height);
        for (int i = 0; i < NUM_SNOWFLAKES; ++i) {
            mSnowFlakes[i] = SnowFlake.create(width, height, paint, mBitmaps[i%mBitmaps.length]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!mIsDraw)return;
        if(mIsLast){
            for(SnowFlake s:mSnowFlakes){
                s.draw(canvas, true);
                mCanFinished &=s.mIsFinshed;
            }
            if(mCanFinished){
                reset();
                return;}
            mCanFinished =true;
        }else{
            for (SnowFlake s : mSnowFlakes) {
                s.draw(canvas,false);
            }
        }
        getHandler().postDelayed(runnable, DELAY);
    }

    private void reset() {
        mIsDraw=false;
        mIsLast=false;
        for(SnowFlake snowFlake: mSnowFlakes){
            snowFlake.reset(SnowFlake.CANVAS_WIDTH,SnowFlake.CANVAS_HEIGHT);
            snowFlake.mIsFinshed=false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (Bitmap bmp :mBitmaps){
            bmp.recycle();
        }
    }

    // 重绘线程
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
    public static class SnowFlake {

        // 红包的角度
        private static final float ANGE_RANGE = 0.1f; // 角度范围
        private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f; // 一般的角度
        private static final float HALF_PI = (float) Math.PI / 2f; // 半PI
        private static final float ANGLE_SEED =20f; // 角度随机种子
        private static final float ANGLE_DIVISOR = 10000f; // 角度的分母
        private static  int CANVAS_WIDTH;
        private static  int CANVAS_HEIGHT;
        // 红包的移动速度
        private static final float INCREMENT_LOWER = 20f;
        private static final float INCREMENT_UPPER = 30f;

        private final RandomGenerator mRandom; // 随机控制器
        private final Point mPosition; // 红包位置
        private float mAngle; // 角度
        private final float mIncrement; // 红包的速度
        private final float mFlakeWidth; // 图片的宽度
        private final float mFlakeHeight;
        private final Paint mPaint; // 画笔
        private final Bitmap mBitmap;
        private boolean mIsFinshed;
        private SnowFlake(RandomGenerator random, Point position, float angle, float increment, Paint paint,Bitmap bmp) {
            mRandom = random;
            mPosition = position;
            mIncrement = increment;
            mFlakeWidth = bmp.getWidth();
            mFlakeHeight=bmp.getHeight();
            mPaint = paint;
            mAngle = angle;
            mBitmap= bmp;
            mIsFinshed=false;
        }
        public static void initCanvasDimens(int width,int height){
            CANVAS_WIDTH=width;
            CANVAS_HEIGHT=height;
        }

        public static SnowFlake create(int width, int height, Paint paint,Bitmap bmp) {
            RandomGenerator random = new RandomGenerator();
            int x = random.getRandom(width);
            int y = -random.getRandom(height);
            Point position = new Point(x, y);
            float angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
            float increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER);
            return new SnowFlake(random, position, angle, increment, paint,bmp);
        }
        // 绘制红包
        public void draw(Canvas canvas,boolean isLast) {
            move(isLast);
            canvas.drawBitmap(mBitmap, mPosition.x, mPosition.y, mPaint);
        }
        // 移动红包
        private void move(boolean isLast) {
            double x = mPosition.x + (mIncrement * Math.cos(mAngle))*mRandom.getRandom(1.0f,5f);
            double y = mPosition.y + (mIncrement * Math.sin(mAngle));

            mAngle += mRandom.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR; // 随机晃动
            mPosition.set((int) x, (int) y);
            // 移除屏幕, 重新开始
            if (!isInside(CANVAS_WIDTH, CANVAS_HEIGHT)) {
                if(isLast){
                    mIsFinshed=true;
                }else {
                    reset(CANVAS_WIDTH);
                }
            }
        }
        // 判断是否在其中
        private boolean isInside(int width, int height) {
            int x = mPosition.x;
            int y = mPosition.y;
            return x >= -mFlakeWidth  && x  <= width+mFlakeWidth &&  y  <= height+mFlakeHeight;
        }
        // 重置红包
        private void reset(int width) {
            mPosition.x = mRandom.getRandom(width);
            mPosition.y = (int) (-mFlakeWidth - 1); // 最上面
        }

        /**
         * 一次播放完后初始化
         * @param width
         * @param height
         */
        private void reset(int width,int height){
            mPosition.x = mRandom.getRandom(width);
            mPosition.y = -mRandom.getRandom(height);
        }
    }
    public static class RandomGenerator {
        private static final Random RANDOM = new Random();

        // 区间随机
        public float getRandom(float lower, float upper) {
            float min = Math.min(lower, upper);
            float max = Math.max(lower, upper);
            return getRandom(max - min) + min;
        }

        // 上界随机
        public float getRandom(float upper) {
            return RANDOM.nextFloat() * upper;
        }

        // 上界随机
        public int getRandom(int upper) {
            return RANDOM.nextInt(upper);
        }
    }
}