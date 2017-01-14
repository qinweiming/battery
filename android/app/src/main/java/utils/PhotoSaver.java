package utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoSaver {

    public static String createPhotoName() {
        String fileName = "";
        //获得系统时间
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        fileName = dateFormat.format(date) + ".jpg";
        return fileName;
    }

    public static void savePhoto2SDCard(String path, String photoName, Bitmap photoBitmap){
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File dir = new File(path);
            if (!dir.exists()){
                dir.mkdirs();
            }
            File photoFile = new File(path, photoName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null){
                    if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)){
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Log.i("Tag", "照片拍摄完毕并存储成功");
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                Log.i("Tag", "照片拍摄完毕 但存储失败");
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                Log.i("Tag", "照片拍摄完毕 但存储失败");
                e.printStackTrace();
            }
        }
    }

}
