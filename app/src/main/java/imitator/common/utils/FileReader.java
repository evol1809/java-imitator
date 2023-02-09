package imitator.common.utils;

import imitator.App;
import imitator.common.exception.CriticalRuntimeException;

import java.io.*;


public class FileReader {

    private FileReader() {}

    public static byte[] readAll(String filePath) {

        ClassLoader classLoader = App.class.getClassLoader();

        // Absolute path or resource path
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath);
             InputStream in = (inputStream != null) ? inputStream : new FileInputStream(filePath)) {

            byte[] bytes = new byte[in.available()];
            if(in.read(bytes) == 0)
                throw new CriticalRuntimeException("The file is empty: " + filePath);
            return bytes;

        } catch (FileNotFoundException e) {
            throw new CriticalRuntimeException("The file not found:" + filePath);
        } catch (SecurityException e) {
            throw new CriticalRuntimeException("The file does not have read access:" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
