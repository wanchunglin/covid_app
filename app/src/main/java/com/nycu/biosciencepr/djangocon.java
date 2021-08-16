package com.nycu.biosciencepr;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class djangocon {
    String end = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    HttpURLConnection djangoconnect = null;

    public String connection(String urlpath,
                                    String method,
                                    Map<String, String> property,
                                    String content,
                                    String filepath) throws IOException {
        URL url = new URL(urlpath);
        djangoconnect = (HttpURLConnection) url.openConnection();
        djangoconnect.setRequestMethod(method);
        for (Map.Entry<String, String> entry : property.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            djangoconnect.setRequestProperty(key, val);
        }

        djangoconnect.setDoInput(true);
        djangoconnect.setDoOutput(true);

        OutputStream os = djangoconnect.getOutputStream();
        DataOutputStream writer = new DataOutputStream(os);
        writer.write(content.getBytes(StandardCharsets.UTF_8));
        writer.flush();

        if(filepath !=null){
            FileInputStream fStream = new FileInputStream(filepath);
            /* 設定每次寫入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length;
            /* 從檔案讀取資料至緩衝區 */
            while ((length = fStream.read(buffer)) != -1) {
                /* 將資料寫入DataOutputStream中 */
                writer.write(buffer, 0, length);
            }
            writer.writeBytes(end);
            writer.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            fStream.close();
            writer.flush();
        }
        writer.close();
        os.close();
        InputStream inputStream = djangoconnect.getInputStream();
        int status = djangoconnect.getResponseCode();
        Log.d("djangoresponse", String.valueOf(status));
        String response = null;
        if (status == 200) {
            response = "";
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);

            String line;
            while ((line = in.readLine()) != null) {
                response = response + line + '\n';
            }
            in.close();
        }
        Log.d("djangoresponse", response);
        return response;
    }
}
