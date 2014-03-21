package com.example.P2PSQ;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale; 
import java.util.Date;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;

public class PhotoHandler implements PictureCallback {

  

	  private final Context context;

	  public PhotoHandler(Context context) {
	    this.context = context;
	  }
  @Override
  public void onPictureTaken(byte[] data, Camera camera) {
   
	Log.d("Camerautil" ,"pict taken" )  ;
    File pictureFileDir = getDir();
    
    Log.d("Camerautil" ,pictureFileDir.toString() )  ;

    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

      Log.d("P2PSQ", "Can't create directory to save image.");
     
      return;

    }
   
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss",Locale.US);
    String date = dateFormat.format(new Date());
    String photoFile = "Picture_" + date + ".jpg";

    String filename = pictureFileDir.getPath() + File.separator + photoFile;
    Log.d("P2PSQ",filename ); 
    File pictureFile = new File(filename);

    try {
      FileOutputStream fos = new FileOutputStream(pictureFile);
      fos.write(data);
      fos.close();
      
    } catch (Exception error) {
      Log.d("P2PSQ", "File" + filename + "not saved: "
          + error.getMessage());
      
    }
    camera.release();
  }

  private File getDir() {
    File sdDir = Environment
      .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    return new File(sdDir, "CameraAPIDemo");
  }
} 