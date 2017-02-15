package com.example.valchyshyn.photogallery;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adhamenaya.androidmosaiclayout.listeners.OnItemClickListener;
import com.adhamenaya.androidmosaiclayout.listeners.OnItemLongClickListener;
import com.adhamenaya.androidmosaiclayout.views.MosaicLayout;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.valchyshyn.photogallery.adapter.MosaicAdapter;
import com.example.valchyshyn.photogallery.utils.FileUtils;
import com.example.valchyshyn.photogallery.utils.ImageFileStorageUtil;
import com.example.valchyshyn.photogallery.utils.MosaicPatterns;
import com.example.valchyshyn.photogallery.utils.PermissionUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ImagePickerCallback, OnItemLongClickListener, OnItemClickListener {

  @BindView(R.id.mosaic_layout)
  MosaicLayout mosaicLayout;

  @BindView(R.id.fab)
  FloatingActionButton fab;

  List<Uri> imageList = new ArrayList<>();

  ImagePicker galleryImagePicker;
  CameraImagePicker cameraImagePicker;
  MosaicAdapter adapter;
  String cameraOutputPath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    Fresco.initialize(this, getImagePipelineConfig());

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        return (3 - position % 2);
      }
    });
    mosaicLayout.addPattern(MosaicPatterns.pattern1);
    mosaicLayout.addPattern(MosaicPatterns.pattern2);
    mosaicLayout.addPattern(MosaicPatterns.pattern3);
    mosaicLayout.addPattern(MosaicPatterns.pattern4);
    mosaicLayout.chooseRandomPattern(true);
    mosaicLayout.setOnItemClickListener(this);
    mosaicLayout.setmOnItemLongClickListener(this);

    if (PermissionUtil.checkWriteStoragePermission(this, this.findViewById(R.id.content_main), PermissionUtil.REQUEST_PERMISSION_WRITE_STORAGE)) {
      initializeMosaicAdapter();
    }


    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (PermissionUtil.checkWriteStoragePermission(MainActivity.this, MainActivity.this.findViewById(R.id.content_main), PermissionUtil.REQUEST_PERMISSION_WRITE_STORAGE)) {
          getMaterialDialog().show();
        }
      }
    });
  }

  private void initializeMosaicAdapter() {
    getImagesPaths();
    if (!imageList.isEmpty()) {
      adapter = new MosaicAdapter(this, imageList);
      mosaicLayout.setAdapter(adapter);
    }
  }

  private ImagePipelineConfig getImagePipelineConfig() {
    return ImagePipelineConfig.newBuilder(this)
        .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
        .setResizeAndRotateEnabledForNetwork(true)
        .setDownsampleEnabled(true)
        .build();
  }

  @Override
  protected void onDestroy() {
    Glide.get(this).clearMemory();
    super.onDestroy();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case PermissionUtil.REQUEST_PERMISSION_WRITE_STORAGE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          initializeMosaicAdapter();
        } else {
          PermissionUtil.showSnackbarForWriteStoragePermission(this, findViewById(R.id.content_main));
          ;
        }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      if (requestCode == Picker.PICK_IMAGE_DEVICE) {
        if (galleryImagePicker == null) {
          galleryImagePicker = new ImagePicker(this);
          galleryImagePicker.setImagePickerCallback(this);
        }
        galleryImagePicker.submit(data);
      } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
        if (cameraImagePicker == null) {
          cameraImagePicker = new CameraImagePicker(this, cameraOutputPath);
          cameraImagePicker.setImagePickerCallback(this);
        }
        cameraImagePicker.submit(data);
      }
    }
  }

  private MaterialDialog.Builder getMaterialDialog() {
    return new MaterialDialog.Builder(this)
        .title(R.string.title)
        .positiveText(R.string.agree)
        .negativeText(R.string.disagree)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            galleryImagePicker = new ImagePicker(MainActivity.this);
            galleryImagePicker.setImagePickerCallback(MainActivity.this);
            galleryImagePicker.allowMultiple();
            galleryImagePicker.shouldGenerateMetadata(false);
            galleryImagePicker.shouldGenerateThumbnails(false);
            galleryImagePicker.pickImage();
          }
        })
        .onNegative(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            cameraImagePicker = new CameraImagePicker(MainActivity.this);
            cameraImagePicker.setImagePickerCallback(MainActivity.this);
            cameraImagePicker.shouldGenerateMetadata(false);
            cameraImagePicker.shouldGenerateThumbnails(false);
            cameraOutputPath = cameraImagePicker.pickImage();
          }
        });
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putString("picker_path", cameraOutputPath);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey("picker_path")) {
        cameraOutputPath = savedInstanceState.getString("picker_path");
      }
    }
    super.onRestoreInstanceState(savedInstanceState);
  }

  public void getImagesPaths() {
    File directory = ImageFileStorageUtil.getExternalImageCacheDir(true);
    File[] images = new File[0];
    if (directory != null) {
      images = directory.listFiles();
    }

    for (File image : images) {
      imageList.add(Uri.fromFile(image));
    }
  }

  @Override
  public void onImagesChosen(List<ChosenImage> list) {
    getImagesPaths();
    if (adapter != null) {
      adapter.setImageList(imageList);
    } else {
      initializeMosaicAdapter();
    }
    recreateActivity();
  }

  private void recreateActivity() {
    this.recreate();
  }

  @Override
  public void onLongClick(final int position) {
    if (!imageList.isEmpty()) {
      new MaterialDialog.Builder(this)
          .positiveText(R.string.delete_image)
          .negativeText(R.string.cancel)
          .buttonsGravity(GravityEnum.CENTER)
          .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              FileUtils.deleteFile(imageList.get(position));
              recreateActivity();
            }
          })
          .onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              dialog.dismiss();
            }
          })
          .show();
    }
  }

  @Override
  public void onError(String s) {
    Log.d(MainActivity.class.getSimpleName(), "Error while picking image: " + s);
  }

  @Override
  public void onClick(int position) {
    List<String> uris = new ArrayList<>();
    for (Uri uri : imageList) {
      uris.add(uri.toString());
    }
    new ImageViewer.Builder(this, uris)
        .setStartPosition(position)
        .show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Toast.makeText(this, "To be done", Toast.LENGTH_LONG).show();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
