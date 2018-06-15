package br.ufop.ildeir.ubspaces.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ildeir on 23/05/2018.
 */

public class Utils {

    public static class StreamToByteArray{

        public static byte[] extractByteArray(InputStream inputStream) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return baos.toByteArray();
        }

    }

}
