/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package or.kr.ajouhosp.stroke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import or.kr.ajouhosp.stroke.CameraSource;
import or.kr.ajouhosp.stroke.CameraSourcePreview;
import or.kr.ajouhosp.stroke.GraphicOverlay;
import or.kr.ajouhosp.stroke.R;
import or.kr.ajouhosp.stroke.posedetector.PoseDetectorProcessor;
import or.kr.ajouhosp.stroke.posedetector.PoseGraphic;
import or.kr.ajouhosp.stroke.preference.PreferenceUtils;
import or.kr.ajouhosp.stroke.preference.SettingsActivity;
import or.kr.ajouhosp.stroke.preference.SettingsActivity.LaunchSource;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback, SurfaceHolder.Callback {
  //private static final String POSE_DETECTION = "Pose Detection";

  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;
  private static final String CAPTURE_PATH = "/CAPTURE_TEST";
  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;

  private TextToSpeech tts;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_vision_live_preview);
    getHashKey();
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
    actionBar.setDisplayHomeAsUpEnabled(true);

    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }
    initTts();

    ToggleButton captureImage = findViewById(R.id.captureImage);
    captureImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          //captureActivity(or.kr.ajouhosp.stroke.activity.LivePreviewActivity.this);
        initCount();
      }
    });
  }

  /*해시키 확인*/
  private void getHashKey(){
    PackageInfo packageInfo = null;
    try {
      packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    if (packageInfo == null)
      Log.e("KeyHash", "KeyHash:null");

    for (Signature signature : packageInfo.signatures) {
      try {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
      } catch (NoSuchAlgorithmException e) {
        Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
      }
    }
  }

  public static void captureActivity(Activity context) {
    if(context == null) return;
    View root = context.getWindow().getDecorView().getRootView();
    root.setDrawingCacheEnabled(true);
    root.buildDrawingCache();

  // 루트뷰의 캐시를 가져옴
    Bitmap screenshot = root.getDrawingCache();

  // get view coordinates
    int[] location = new int[2];
    root.getLocationInWindow(location);

  // 이미지를 자를 수 있으나 전체 화면을 캡쳐 하도록 함
    Bitmap bmp = Bitmap.createBitmap(screenshot, location[0], location[1], root.getWidth(), root.getHeight(), null, false);
    String strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + CAPTURE_PATH;
    File folder = new File(strFolderPath);
    if(!folder.exists()) {
      folder.mkdirs();
    }

    String strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png";
    File fileCacheItem = new File(strFilePath);
    OutputStream out = null;
    try {
      fileCacheItem.createNewFile();
      out = new FileOutputStream(fileCacheItem);
      bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void initCount(){
    PoseGraphic.checkpoint=0;
    PoseGraphic.count=0;
  }

  public void initTts(){
    //tts생성 및 초기화
    tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int status) {
        if(status != ERROR){tts.setLanguage(Locale.KOREA);}
      }
    });

    //ToggleButton open_graph = findViewById(R.id.open_graph);
    String state = Environment.getExternalStorageState();
    Log.d("dir", "directtt:"+state);

      writeFile("file1","이미지 가이드와 동일하게 환자의 자세를 조정해주세요.");
      writeFile("file2","변경된 이미지 가이드라인을 따라 자세를 취해주세요.");

/*    open_graph.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(),ChartActivity.class);
        startActivity(intent);
      }
    });*/
  }

  public void writeFile(String filename, String txt) {
    File file = new File(Environment.getExternalStorageDirectory(), filename);

    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter writer = new FileWriter(file, false);
      String str = txt;
      writer.write(str);
      writer.close();
    } catch (IOException e) {}
  }

  public String readFile(String filename) {
    String fileTitle = filename + ".txt";
    File file = new File(Environment.getExternalStorageDirectory(), fileTitle);
    String result = "";

    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      while ((line = reader.readLine()) != null) {
        result += line;
      }
      reader.close();
    } catch (FileNotFoundException e1) {} catch (IOException e2) {}
    return result;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.live_preview_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      intent.putExtra(SettingsActivity.EXTRA_LAUNCH_SOURCE, LaunchSource.LIVE_PREVIEW);
      startActivity(intent);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void createCameraSource() {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try{
      PoseDetectorOptionsBase poseDetectorOptions =
              PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
      boolean shouldShowInFrameLikelihood =
              PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
      Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);
      cameraSource.setMachineLearningFrameProcessor(
              new PoseDetectorProcessor(this, poseDetectorOptions, shouldShowInFrameLikelihood));
    }catch (Exception e){
      Log.e(TAG, "Can not create image processor: POSE_DETECTION", e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
              .show();
    }

  }

  public void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource();
    startCameraSource();
  }
  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }



  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {

  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

  }
}