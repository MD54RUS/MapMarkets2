package com.privateproperty.mapmarkets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Vector;


/**
 * Created by Pro-rock on 30.03.2016.
 */
public class ImageManager {

    public String md5(String s) {
    try {
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
    return "";
}
    public static void fileSave(InputStream is, FileOutputStream outputStream) {
        int i;
        try {
            while ((i = is.read()) != -1) {
                outputStream.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Vector<ImageView> downloaded = new Vector<ImageView>();
    public boolean findObject(ImageView object) {
        for (int i = 0; i < downloaded.size(); i++) {
            if (downloaded.elementAt(i).equals(object)) {
                return true;
            }
        }
        return false;
    }

    private Bitmap downloadImage(Context context, int cacheTime, String iUrl, ImageView iView) {
        Bitmap bitmap = null;
        if (cacheTime != 0) {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Log.d("MM", "SD-карта не доступна: " + Environment.getExternalStorageState());

            }
            File file = new File(context.getExternalCacheDir(), md5(iUrl)
                    + ".png");
            long time = new Date().getTime() / 1000;
            long timeLastModifed = file.lastModified() / 1000;
            try {
                if (file.exists()) {
                    if (timeLastModifed + cacheTime < time) {
                        file.delete();
                        file.createNewFile();
                        fileSave(new URL(iUrl).openStream(),
                                new FileOutputStream(file));
                    }
                } else {
                    file.createNewFile();
                    fileSave(new URL(iUrl).openStream(), new FileOutputStream(
                            file));
                }
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap == null) {
                file.delete();
            }
        } else {
            try {
                bitmap = BitmapFactory.decodeStream(new URL(iUrl).openStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (iView != null) {
            downloaded.remove(iView);
        }
        return bitmap;
    }
//    private void downloadImage (Context context, int cacheTime, String iUrl) {
//        Bitmap bitmap = null;
//        if (cacheTime != 0) {
//            File file = new File(context.getExternalCacheDir(), md5(iUrl)
//                    + ".cache");
//            long time = new Date().getTime() / 1000;
//            long timeLastModifed = file.lastModified() / 1000;
//            try {
//                if (file.exists()) {
//                    if (timeLastModifed + cacheTime < time) {
//                        file.delete();
//                        file.createNewFile();
//                        fileSave(new URL(iUrl).openStream(),
//                                new FileOutputStream(file));
//                    }
//                } else {
//                    file.createNewFile();
//                    fileSave(new URL(iUrl).openStream(), new FileOutputStream(
//                            file));
//                }
//                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (bitmap == null) {
//                file.delete();
//            }
//        } else {
//            try {
//                bitmap = BitmapFactory.decodeStream(new URL(iUrl).openStream());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }
    public void fetchImage(final Context context, final int cacheTime, final String url, final ImageView iView) {
        if (iView != null) {
            if (findObject(iView)) {
                return;
            }
            downloaded.add(iView);
        }
        new AsyncTask<String, Void, Bitmap>() {
            protected Bitmap doInBackground(String... iUrl) {
                return downloadImage(context, cacheTime, iUrl[0], iView);
            }
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (iView != null) {
                    iView.setImageBitmap(result);
                }
            }
        }.execute(new String[] { url });
    }

}

