package com.example.P2PSQ;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo; 
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.content.Context;


/*
 *   
 * 
 * 
 * 
 * 
 */
public class CameraUtil 
{


	public static void takePicture()
	{ 
		Camera camera;
		int cameraId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) 
		{
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) 
			{
				Log.d("P2PSQ", "Camera found");
				cameraId = i;

				break;
			}
		}
		if ( cameraId  >= 0 )
		{
			Log.d("P2PSQ" , " Reached here !"); 
			camera = Camera.open(cameraId);
			Log.d("P2PSQ" , " opened camera  !");
			/*
			  camera.setPreviewCallback((PreviewCallback) new CameraPreview(null,camera));
		      camera.takePicture(null, null,
				        new PhotoHandler());
			 */
			camera.release();
		}

	}		 
}


