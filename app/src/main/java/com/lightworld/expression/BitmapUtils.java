package com.lightworld.expression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class BitmapUtils {


    public static ByteArrayOutputStream compressImage(Bitmap src, int height, int width) {
        if (src == null) {
            return null;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(src, width, height, false);

        int compress = 90;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compress, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length > 200 * 1024) {
            baos.reset();//重置baos即清空baos
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compress, baos);//这里压缩options%，把压缩后的数据存放到baos中
            compress -= 10;//每次都减少10
            if (compress <= 0) {
                break;
            }
        }
        return baos;
    }

    public static String BitmapToFile(Bitmap bitmap, File file) {
        if (bitmap != null && file != null) {
            try {
                FileOutputStream stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
                return file.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static BitmapFactory.Options getSampleSizeOption(String path, int reqWidth,
                                                            int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = BitmapUtils.calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return options;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static String getThumbnailUrl(String url, boolean scaleToFit, int width, int height, int quality) {
        if (width >= 0 && height >= 0) {
            if (quality >= 1 && quality <= 100) {
                int mode = scaleToFit ? 2 : 1;
                return url + String.format("?imageView/%d/w/%d/h/%d/q/%d/format/%s", mode,
                        width, height, quality, "png");
            } else {
                throw new IllegalArgumentException("Invalid quality,valid range is 0-100.");
            }
        } else {
            throw new IllegalArgumentException("Invalid width or height.");
        }
    }

    /**
     * 七牛云的图片通过拼接返回不同大小的缩略图
     */
    public static String[] getOriginUrl(String[] urls) {
        String url;
        for (int i = 0, s = urls.length; i < s; i++) {
            url = urls[i];
            int index = url.indexOf("?imageView");
            if (index > -1) {
                urls[i] = getThumbnailUrl(url.substring(0, index), true, 480, 800, 90);
            }
        }
        return urls;
    }
}
