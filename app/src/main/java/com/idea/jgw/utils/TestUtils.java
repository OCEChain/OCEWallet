package com.idea.jgw.utils;

import android.os.Environment;

import com.idea.jgw.utils.common.CommonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestUtils {

    public static void writeWithName(String fileName, String content) {
        String path = "/";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            path = Environment.getRootDirectory().getPath();
        }
        writeWithPath(path + File.separator + "ttemp_test" + fileName, content);
    }

    public static void writeWithPath(String filePath, String content) {
        File file = new File(filePath);
        if(!file.getParentFile().exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
            fos = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }
    }
}
