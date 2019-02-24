package io.github.aktera.rakugakiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ナビゲーションバーを非表示にする
        setImmersiveSticky();
    }

    // ウィンドウがアクティブにされた
    @Override
    protected void onResume() {
        super.onResume();
        // 他のウィンドウから戻った場合にナビゲーションバーの状態がリセットされているため再実行
        setImmersiveSticky();
    }

    // ナビゲーションバーを非表示にする
    protected void setImmersiveSticky() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void onClickButtonBlack(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonBlack(v);
    }

    public void onClickButtonWhite(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonWhite(v);
    }

    public void onClickButtonRed(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonRed(v);
    }

    public void onClickButtonGreen(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonGreen(v);
    }

    public void onClickButtonBlue(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonBlue(v);
    }

    public void onClickButtonSmall(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonSmall(v);
    }

    public void onClickButtonMedium(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonMedium(v);
    }

    public void onClickButtonLarge(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonLarge(v);
    }

    public void onClickButtonSave(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonSave(v);
    }

    public void onClickButtonLoad(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonLoad(v);
    }

    public void onClickButtonClear(View v) {
        RakugakiView view = findViewById(R.id.view_rakugaki);
        view.onClickButtonClear(v);
    }
}
