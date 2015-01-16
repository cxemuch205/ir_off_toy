package ua.iroff.Tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import ua.iroff.Constants.App;
import ua.iroff.Models.CodeData;

/**
 * Created by daniil on 11/19/14.
 */
public class Tools {

    public static final String TAG = "Tools";
    public static final String TAG_CODE = "CODES DATA";
    public static final String FILES_IR_CODES_DIR_ROOT = "/sdcard/codesIR/";
    public static final String TXT_FILE_DATA = ".txt";

    public static void saveDataCodesFromAssetsToExternal(Context context) {
        AssetManager assetManager = context.getAssets();
        String filePath = "codes/";
        filePath += App.FileName.CODES_POWER_ON_OFF;
        filePath += TXT_FILE_DATA;
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/codes");
            dir.mkdirs();
            InputStream codeData = assetManager.open(filePath);
            File file = new File(dir, App.FileName.CODES_POWER_ON_OFF + TXT_FILE_DATA);
            FileOutputStream f = new FileOutputStream(file);
            f.write(readInputStreamFile(codeData).getBytes());
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<CodeData> getCodesList(Context context, int typeCodes) {
        ArrayList<CodeData> result = new ArrayList<CodeData>();

        String filePath = "";
        switch (typeCodes) {
            case App.CommandType.POWER_ON_OFF:
                filePath += App.FileName.CODES_POWER_ON_OFF;
                break;
        }

        filePath += TXT_FILE_DATA;
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/codes");
            dir.mkdirs();
            File file = new File(dir, filePath);
            InputStream codeData = new FileInputStream(file);
            if (codeData != null) {
                result.addAll(parseFileData(codeData));
            } else {
                Log.e(TAG_CODE, "CAN`t get file inputStream");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static ArrayList<CodeData> parseFileData(InputStream codeData) {
        if (codeData == null) {
            return null;
        }
        ArrayList<CodeData> result = new ArrayList<CodeData>();
        String dataFile = null;
        dataFile = readInputStreamFile(codeData);

        if (dataFile != null && checkFileDataCorrect(dataFile)) {
            CodeData codeDataItem = null;
            for (int i = 0; i < dataFile.length(); i++) {
                if (codeDataItem == null)
                    codeDataItem = new CodeData();

                // GET CODE
                if (dataFile.substring(i, (i + 1)).contentEquals("!")) {
                    for (int j = (i + 1); j < dataFile.length(); j++) {
                        if (dataFile.substring(j, (j + 1)).contentEquals("!")) {
                            codeDataItem.code = dataFile.substring(i + 1, j);
                            i = j + 1;
                            break;
                        }
                    }
                }

                //GET BRAND
                if (dataFile.substring(i, (i + 1)).contentEquals("#")) {
                    for (int j = (i + 1); j < dataFile.length(); j++) {
                        if (dataFile.substring(j, (j + 1)).contentEquals("#")) {
                            codeDataItem.brand = dataFile.substring(i + 1, j);
                            i = j + 1;
                            result.add(codeDataItem);
                            codeDataItem = null;
                            break;
                        }
                    }
                }
            }
        } else {
            Log.e(TAG_CODE, "CAN`T parse data");
        }
        return result;
    }

    private static String readInputStreamFile(InputStream codeData) {
        String dataFile = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(codeData);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                receiveString += "\n";
                stringBuilder.append(receiveString);
            }

            codeData.close();
            dataFile = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFile;
    }

    private static boolean checkFileDataCorrect(String dataFile) {
        if (dataFile != null && dataFile.length() > 0) {
            int countV = 0, countSharp = 0;
            for (int i = 0; i < dataFile.length(); i++) {
                if(dataFile.substring(i, (i+1)).contentEquals("!"))
                    countV++;
                if(dataFile.substring(i, (i+1)).contentEquals("#"))
                    countSharp++;
            }
            if(countSharp%2 == 0 && countV%2 == 0)
                return true;
        }
        Log.e(TAG_CODE, "#### NO CORRECT CODES FILE");
        return false;
    }
}
