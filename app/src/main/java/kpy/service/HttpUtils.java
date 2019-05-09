package kpy.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    private static final String URL = "http://192.168.1.190:8888/request";

    public static String post(String string) {//string POST参数,get 请求的URL地址,context 联系上下文
        String html = null;
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);//超时时间
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(string);
            out.flush();
            out.close();

            InputStream inputStream = conn.getInputStream();
            byte[] data = StreamTool.read(inputStream);
            html = new String(data, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-----" + e);
            return "{\"success\":-1}";
        }
        return html;
    }


    public static class StreamTool {
        /**
         * @param inStream
         * @return
         */
        public static byte[] read(InputStream inStream) throws Exception {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            return outStream.toByteArray();
        }

        public FileInputStream output(String fileString) {
            File file = new File(fileString);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return fileInputStream;
        }


    }
}
