package com.aeris2.rambler;

import org.renpy.android.PythonActivity;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import net.homeatm.reader.ReaderController;
import net.homeatm.reader.ReaderController.ReaderControllerState;
import net.homeatm.reader.ReaderController.ReaderStateChangedListener;

class Device {

    private ReaderController readerController;
    private ReaderStateChangedListener stateChangedListener;
    private String currentResults = "";

    private HashMap data;
    private final Object lock = new Object();

    public boolean hasData() {
        if (data == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setData(HashMap local_data) {
        synchronized (lock) {
            data = local_data;
        }
    }
    public HashMap getData() {
        synchronized (lock) {
            HashMap temp = data;
            data = null;
            return temp;
        }
    }

    public Device() {

        PythonActivity.mActivity.runOnUiThread(new Runnable () {
            public void run() {
                stateChangedListener = new StateChangedListener();
                Context context = PythonActivity.mActivity;
                readerController = new ReaderController(context, stateChangedListener);
                readerController.setDetectDeviceChange(true);
                debugMsg("Created Objects");
            }
        });

    }

    public void openDevice() {
        if (readerController != null) {
            data = null;
            readerController.startReader();
        } else {
            debugMsg("readerController is null, can't open");
        }
    }

    public void closeDevice() {
        if (readerController != null) {
            try {
                if (readerController.getReaderState() != ReaderControllerState.STATE_IDLE) {
                    readerController.stopReader();
                }
            }
            catch(Exception ex)
            {
                debugMsg("Couldn't stop Reader: " + ex.toString());
            }
        } else {
            debugMsg("readerController is null, can't close");
        }
    }

    private void debugMsg(String lpstrMessage)
    {
        Log.i("rambler:", lpstrMessage);

    }

    // -----------------------------------------------------------------------
    // CSwiper API Callbacks
    // -----------------------------------------------------------------------

    private void getKsnCompleted(String ksn) {
        debugMsg("Got KSN: " + ksn);
    }

    private void cardSwipeDetected() {
        debugMsg("SwipeDetected");
    }

    private void decodeCompleted(HashMap<String, String> decodeData) {

//        String formatID = decodeData.get("formatID");
//        String ksn = decodeData.get("ksn");
//        String encTracks = decodeData.get("encTracks");
//        String encTrack1 = decodeData.get("encTrack1");
//        String encTrack2 = decodeData.get("encTrack2");
//        String track1Length = decodeData.get("track1Length");
//        String track2Length = decodeData.get("track2Length");
//        String maskedPAN = decodeData.get("maskedPAN");
//        String expiryDate = decodeData.get("expiryDate");
//        String serviceCode = decodeData.get("serviceCode");
//        String cardHolderName = decodeData.get("cardHolderName");

        String separator = "; ";
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, String> entry : decodeData.entrySet()) {
            sb.append(entry.getKey() + ": " + entry.getValue() + separator);
        }

        debugMsg("Decode Complete");
        currentResults = sb.toString();
        debugMsg(currentResults);
        setData(decodeData);
    }

    private void setError(String error) {
        HashMap error_map = new HashMap();
        error_map.put("error", error);
        setData(error_map);
    }
    private void decodeError(String error) {
        setError(error);
        debugMsg("Decode Error: " + error);
    }

    private void error(int errorCode, String err) {
        setError(err);
        debugMsg("Error: " + err);
    }

    private void interrupted() {
        debugMsg("Interrupted");
    }

    private void noDeviceDetected() {
        debugMsg("No Device Detected");
    }

    private void timeout() {
        debugMsg("Timeout");
    }

    private void waitingForCardSwipe() {
        debugMsg("Waiting for Swipe");
    }

    private void waitingForDevice() {
        debugMsg("Waiting for Device");
    }

    private void devicePlugged() {
        debugMsg("Device Plugged");
    }

    private void deviceUnplugged() {
        debugMsg("Device Unplugged");
    }

    private class StateChangedListener implements ReaderStateChangedListener {

        @Override
        public void onGetKsnCompleted(String ksn) {
            getKsnCompleted(ksn);
        }

        @Override
        public void onCardSwipeDetected() {
            cardSwipeDetected();
        }

        @Override
        public void onDecodeCompleted(HashMap<String, String> decodeData) {
            decodeCompleted(decodeData);
        }

        @Override
        public void onDecodeError(net.homeatm.reader.ReaderController.DecodeResult decodeResult) {
            decodeError(decodeResult.name());
        }

        @Override
        public void onError(String message) {
            error(0, message);
        }

        @Override
        public void onInterrupted() {
            interrupted();
        }

        @Override
        public void onNoDeviceDetected() {
            noDeviceDetected();
        }

        @Override
        public void onTimeout() {
            timeout();
        }

        @Override
        public void onWaitingForCardSwipe() {
            waitingForCardSwipe();
        }

        @Override
        public void onWaitingForDevice() {
            waitingForDevice();
        }

        @Override
        public void onDevicePlugged() {
            devicePlugged();
        }

        @Override
        public void onDeviceUnplugged() {
            deviceUnplugged();
        }

    }



}