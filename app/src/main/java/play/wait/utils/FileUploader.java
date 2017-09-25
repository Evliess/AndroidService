package play.wait.utils;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

/**
 * Created by guijj on 9/5/2017.
 */

public class FileUploader {
    private static final String SERVER_IP = "10.0.2.2";
    private static final int SERVER_PORT = 9887;
    public static void uploadFile(final File uploadFile) {
        try {
            String souceid = "The Song Of Ice And Fire";
            String head = "Content-Length=" + uploadFile.length() + ";filename=" + uploadFile.getName() + ";sourceid=" +
                    (souceid == null ? "" : souceid) + "\r\n";
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            OutputStream outStream = socket.getOutputStream();
            outStream.write(head.getBytes());
            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());
            String response = StreamTool.readLine(inStream);
            String[] items = response.split(";");
            String position = items[1].substring(items[1].indexOf("=") + 1);
            RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");
            fileOutStream.seek(Integer.valueOf(position));
            byte[] buffer = new byte[1024];
            int len = -1;
            int length = Integer.valueOf(position);
            while ((len = fileOutStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
                length += len;
            }
            fileOutStream.close();
            outStream.close();
            inStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
