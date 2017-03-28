package org.hotwheel.weixin;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


public class DownLoadQrCodeThread extends Thread {
    private static String pathname = null;
    private String imageUrl = "";
    private boolean write;
    private OnloadQrCodeFinnishListener listener;

    public static void setPathname(String filepath) {
        pathname = filepath;
    }

    public void setListener(OnloadQrCodeFinnishListener listener) {
        this.listener = listener;
    }

    interface OnloadQrCodeFinnishListener {
        void onLoadSuccess(byte[] imageBytes);
    }


    public DownLoadQrCodeThread(String url, boolean writeToFile) {
        imageUrl = url;
        this.write = writeToFile;
    }

    @Override
    public void run() {
        URL url;
        DataInputStream bfin = null;
        try {
            url = new URL(imageUrl);
            bfin = new DataInputStream(url.openStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String pathname = "resources/qrcode.jpg";
        try {
            FileOutputStream fos = new FileOutputStream(new File(pathname));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = bfin.read(buffer)) > 0) {
                if (write) {
                    fos.write(buffer, 0, length);
                }
                bos.write(buffer, 0, length);
            }
            bfin.close();
            fos.close();
            byte[] imageBytes = bos.toByteArray();
            if (listener != null) {
                listener.onLoadSuccess(imageBytes);
            }
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("二维码生成");

    }
}
