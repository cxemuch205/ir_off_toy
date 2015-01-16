package ua.iroff.Tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ua.iroff.BaseActivity;
import ua.iroff.Constants.App;
import ua.iroff.Interfaces.OnProgressSendingListener;
import ua.iroff.Models.CodeData;

/**
 * Created by daniil on 11/19/14.
 */
public class SendCodeManager {

    private static Object irdaService;
    private static Method irWrite;
    private static ConsumerIrManager irManager;

    public static void sendCodes(final BaseActivity activity, final int typeCodes, final OnProgressSendingListener sendingListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            irInit4KitKat(activity);
        }else{
            irInit4JellyBean(activity);
        }

        Thread sendThread = null;
        switch (typeCodes) {
            case App.CommandType.POWER_ON_OFF:
                sendThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (sendingListener != null) {
                            sendingListener.onProgress(typeCodes, "CONNECT TO IR...", null);
                        }
                        ArrayList<CodeData> codesList = Tools.getCodesList(activity, typeCodes);
                        if (codesList != null && codesList.size() > 0) {
                            for (int i = 0; i < codesList.size(); i++) {
                                CodeData code = codesList.get(i);
                                if (sendingListener != null) {
                                    sendingListener.onProgress(typeCodes, "SEND " + code.brand + " CODE: " + code.code, code);
                                }
                                sendCode(code);
                            }
                        }
                        if (sendingListener != null) {
                            sendingListener.onEnd(typeCodes);
                        }
                    }
                });
                break;
        }

        if (sendThread != null) {
            sendThread.setDaemon(true);
            sendThread.start();
            if (sendingListener != null) {
                sendingListener.onStart(typeCodes);
            }
        }
    }

    private static void irInit4JellyBean(Context context) {
        irdaService = context.getSystemService("irda");
        Class c = irdaService.getClass();
        Class p[] = { String.class };
        try {
            irWrite = c.getMethod("write_irsend", p);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void irInit4KitKat(Context context) {
        irManager = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
    }

    private static void sendCode(CodeData code) {
        if (code != null && code.code != null && code.code.length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String codeD = code.code;
                int pX = codeD.indexOf("x");
                String patternS[] = codeD.split(" ");
                int pattern[] = new int[patternS.length];
                for (int i = 0; i < pattern.length; i++) {
                    String afterX = patternS[i].substring(patternS[i].indexOf("x")+1, patternS[i].length());
                    boolean isInteger = false;
                    try {
                        Integer.parseInt(afterX);
                        isInteger = true;
                    } catch (Exception e) {}
                    if (isInteger) {
                        pattern[i] = Integer.parseInt(afterX);
                    } else {
                        pattern[i] = Integer.parseInt(afterX, 16);
                    }
                }
                int frequency = 38028; // IT IS for SAMSUNG
                try {
                    irManager.transmit(frequency, pattern);
                } catch (Exception e) {}
            } else {
                String data = code.code;
                if (data != null) {
                    try {
                        irWrite.invoke(irdaService, data);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
