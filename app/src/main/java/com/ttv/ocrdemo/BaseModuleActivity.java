package com.ttv.ocrdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseModuleActivity extends AppCompatActivity {
  private static final int UNSET = 0;

  protected HandlerThread mBackgroundThread;
  protected Handler mBackgroundHandler;
  protected Handler mUIHandler;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUIHandler = new Handler(getMainLooper());
  }

  protected void startBackgroundThread() {
    mBackgroundThread = new HandlerThread("ModuleActivity");
    mBackgroundThread.start();
    mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
  }

  @Override
  protected void onDestroy() {
    stopBackgroundThread();
    super.onDestroy();
  }

  protected void stopBackgroundThread() {
    mBackgroundThread.quitSafely();
    try {
      mBackgroundThread.join();
      mBackgroundThread = null;
      mBackgroundHandler = null;
    } catch (InterruptedException e) {
      Log.e("BaseModuleActivity", "Error on stopping background thread", e);
    }
  }

  protected int getInfoViewCode() {
    return UNSET;
  }

  protected String getInfoViewAdditionalText() {
    return null;
  }

}
