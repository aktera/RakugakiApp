package io.github.aktera.rakugakiapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class RakugakiView extends View {

    private Paint paint = new Paint();

    private Bitmap bitmapMemory;
    private Canvas canvasMemory;

    private Bitmap bitmapLine;
    private Canvas canvasLine;

    private float preX = 0.0f;
    private float preY = 0.0f;

/*
  ┏━━━━━━━━━━━━━━━━━━┓
  ┃            canvasView              ┃ 実際にウィンドウに表示されるキャンバス
  ┗━━━━━━━━━━━━━━━━━━┛ onDrawの引数で、呼ばれる度にリセットされる
                    ↑
  ┏━━━━━━━━━━━━━━━━━━┓
  ┃           canvasMemory             ┃ メモリに確保したダブルバッファの裏キャンバス
  ┗━━━━━━━━━━━━━━━━━━┛ 描画の内容は保存される
                    ↑
  ┏━━━━━━━━━━━━━━━━━━┓
  ┃            canvasLine              ┃ 線を描画するためのキャンバス
  ┗━━━━━━━━━━━━━━━━━━┛ 線ごとにリセットされる
 */

    public RakugakiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // メモリキャンバスを作成する
        bitmapMemory = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        canvasMemory = new Canvas(bitmapMemory);

        // 線描画キャンバスを作成する
        bitmapLine = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        canvasLine = new Canvas(bitmapLine);

        // ペイント属性を初期化しておく
        paint.setColor(0xff000000);
        paint.setStrokeWidth(convertDp2Px(4.0f,getContext()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvasView) {
        // メモリキャンバスに描画されたビットマップをビューに転送する
        canvasView.drawBitmap(bitmapMemory, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // メモリキャンバスを透過色でリセットする
                //canvasLine.drawColor(0xffffffff);
                canvasLine.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                // 点を描画し、メモリキャンバスにコピーし、再描画を指示する
                canvasLine.drawPoint(x, y, paint);
                canvasMemory.drawBitmap(bitmapLine, 0, 0, null);
                invalidate();

                // 座標を更新する
                preX = x;
                preY = y;
                break;

            case MotionEvent.ACTION_UP:
                // 線を描画し、メモリキャンバスにコピーし、再描画を指示する
                canvasLine.drawLine(preX, preY, x, y, paint);
                canvasMemory.drawBitmap(bitmapLine, 0, 0, null);
                invalidate();

                // 座標を更新する
                preX = x;
                preY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                // 線を描画し、メモリキャンバスにコピーし、再描画を指示する
                canvasLine.drawLine(preX, preY, x, y, paint);
                canvasMemory.drawBitmap(bitmapLine, 0, 0, null);
                invalidate();

                // 座標を更新する
                preX = x;
                preY = y;
                break;

            case MotionEvent.ACTION_CANCEL:
                // よく分からないイベント
                break;

            default:
                break;
        }
        return true;
    }

    public void onClickButtonBlack(View v) {
        paint.setColor(0xff000000);
        invalidate();
    }

    public void onClickButtonWhite(View v) {
        paint.setColor(0xffffffff);
        invalidate();
    }

    public void onClickButtonRed(View v) {
        paint.setColor(0xffff0000);
        invalidate();
    }

    public void onClickButtonGreen(View v) {
        paint.setColor(0xff00ff00);
        invalidate();
    }

    public void onClickButtonBlue(View v) {
        paint.setColor(0xff0000ff);
        invalidate();
    }

    public void onClickButtonSmall(View v) {
        paint.setStrokeWidth(convertDp2Px(4.0f, getContext()));
        invalidate();
    }

    public void onClickButtonMedium(View v) {
        paint.setStrokeWidth(convertDp2Px(8.0f, getContext()));
        invalidate();
    }

    public void onClickButtonLarge(View v) {
        paint.setStrokeWidth(convertDp2Px(12.0f, getContext()));
        invalidate();
    }

    public void onClickButtonClear(View v) {
        // メモリキャンバスを透過色でリセットする
        canvasMemory.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    // デバイス単位をピクセルに変換する
    public static float convertDp2Px(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }

    // ピクセルをデバイス単位に変換する
    public static float convertPx2Dp(int px, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / metrics.density;
    }

    // スナックバーにメッセージをポップアップする
    private void snackbarMessage(String message) {
        Activity activity = (Activity)getContext();
        Snackbar.make(activity.getWindow().getDecorView(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
