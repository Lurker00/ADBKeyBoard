package com.android.adbkeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class EmptyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Rotate.Start(this);
        finish();
        Log.i("EmptyActivity", "onCreate");
    }
}
