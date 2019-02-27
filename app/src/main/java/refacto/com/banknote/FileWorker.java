package refacto.com.banknote;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class FileWorker {

    public static String read(String filename, Context context) {
        String data = new String();
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(filename);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = "";
            while((line = bufferedReader.readLine()) != null)
                data += line + "\n";

            data = data.substring(0, data.length() - 1);

        } catch(Exception e) { e.printStackTrace(); }

        return data;
    }

    public static void write(String filename, String data, Context context) {
        File file = new File(context.getFilesDir(), filename);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
