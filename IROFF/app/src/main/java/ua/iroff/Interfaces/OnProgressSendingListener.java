package ua.iroff.Interfaces;

/**
 * Created by daniil on 11/19/14.
 */
public interface OnProgressSendingListener {
    void onStart(int typeCodes);

    void onProgress(int typeCodes, String msg, Object data);

    void onEnd(int typeCodes);
}
