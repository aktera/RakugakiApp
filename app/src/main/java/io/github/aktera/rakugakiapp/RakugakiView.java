package io.github.aktera.rakugakiapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Bitmap.Config.ARGB_8888;

public class RakugakiView extends View {

    private Paint paint = new Paint();

    private Bitmap bitmapMemory;
    private Canvas canvasMemory;

    private Bitmap bitmapLine;
    private Canvas canvasLine;

    private float preX = 0.0f;
    private float preY = 0.0f;

    private float curStrokeWidth = 5.0f;


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
        bitmapMemory = Bitmap.createBitmap(getWidth(),getHeight(), ARGB_8888);
        canvasMemory = new Canvas(bitmapMemory);

        // 線描画キャンバスを作成する
        bitmapLine = Bitmap.createBitmap(getWidth(),getHeight(), ARGB_8888);
        canvasLine = new Canvas(bitmapLine);

        // ペイント属性を初期化しておく
        paint.setColor(0xff000000);
        paint.setStrokeWidth(convertDp2Px(curStrokeWidth, getContext()));
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
        float pressure = motionEvent.getPressure();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // メモリキャンバスを透過色でリセットする
                //canvasLine.drawColor(0xffffffff);
                canvasLine.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                // 筆圧対応
                paint.setStrokeWidth(convertDp2Px(convPressure(pressure), getContext()));

                // 点を描画し、メモリキャンバスにコピーし、再描画を指示する
                canvasLine.drawPoint(x, y, paint);
                canvasMemory.drawBitmap(bitmapLine, 0, 0, null);
                invalidate();

                // 座標を更新する
                preX = x;
                preY = y;
                break;

            case MotionEvent.ACTION_UP:
                // 筆圧対応
                paint.setStrokeWidth(convertDp2Px(convPressure(pressure), getContext()));

                // 線を描画し、メモリキャンバスにコピーし、再描画を指示する
                canvasLine.drawLine(preX, preY, x, y, paint);
                canvasMemory.drawBitmap(bitmapLine, 0, 0, null);
                invalidate();

                // 座標を更新する
                preX = x;
                preY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                // 筆圧対応
                paint.setStrokeWidth(convertDp2Px(convPressure(pressure), getContext()));

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
        curStrokeWidth = 5.0f;
        invalidate();
    }

    public void onClickButtonMedium(View v) {
        curStrokeWidth = 10.0f;
        invalidate();
    }

    public void onClickButtonLarge(View v) {
        curStrokeWidth = 15.0f;
        invalidate();
    }

    private float convPressure(float src) {
        return curStrokeWidth * src;
    }

    public void onClickButtonSave(View v) {
        // キャンバスを保存する
        saveCanvas(getContext());
        invalidate();
    }

    public void onClickButtonLoad(View v) {
        // 保存されたキャンバスファイルが存在するなら
        String path = getContext().getFilesDir() + "/" + getContext().getString(R.string.path_canvas);
        if (isPathExists(path)) {
            // メモリキャンバスを透過色でリセットする
            canvasMemory.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            // 保存されたキャンバスを読み込む
            loadCanvas(getContext());
            invalidate();
        }
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

    // キャンバスを保存する
    public void saveCanvas(Context context) {
        try {
            // 出力ストリームを開く
            String path = context.getString(R.string.path_canvas);
            FileOutputStream outStream = context.openFileOutput(path, MODE_PRIVATE);

            // メモリキャンバスのビットマップをエンコードしてストリームに出力する
            bitmapMemory.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.d("RakugakiView", e.toString());
        } catch (IOException e) {
            Log.d("RakugakiView", e.toString());
        }
    }

    // 保存されたキャンバスを読み込む
    public void loadCanvas(Context context) {
        InputStream inputStream = null;
        try {
            // 入力ストリームを開く
            String path = context.getString(R.string.path_canvas);
            inputStream = context.openFileInput(path);

            // 読み込んだビットマップをデコードし、メモリキャンバスに書き出す
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            canvasMemory.drawBitmap(image, 0, 0, null);
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.d("RakugakiView", e.toString());
        } catch (IOException e) {
            Log.d("RakugakiView", e.toString());
        }
    }

    // パスが存在するか調べる
    public static boolean isPathExists(String filepathpath) {
        File file = new File(filepathpath);
        return file.exists();
    }
}
