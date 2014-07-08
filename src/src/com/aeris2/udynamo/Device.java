
package com.aeris2.udynamo;

import java.util.HashMap;

import org.renpy.android.PythonActivity;

import com.magtek.mobile.android.scra.MagTekSCRA;
import com.magtek.mobile.android.libDynamag.*;

import android.media.AudioManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;

import android.util.Log;

class Device {

    private MagTekSCRA mMTSCRA;
    private Handler mSCRADataHandler;

    private MagTeklibDynamag MagTeklibDynamag;
    private Handler mReaderDataHandler;

    public static final int STATUS_PROCESSCARD = 2;
    public static final int DEVICE_MESSAGE_CARDDATA_CHANGE =3;
    public static final int DEVICE_STATUS_CONNECTED = 4;
    public static final int DEVICE_STATUS_DISCONNECTED = 5;

    public static final int DEVICE_STATUS_CONNECTED_SUCCESS = 0;
    public static final int DEVICE_STATUS_CONNECTED_FAIL = 1;
    public static final int DEVICE_STATUS_CONNECTED_PERMISSION_DENIED = 2;

    private HashMap data;

    private int mIntCurrentDeviceStatus;
    private int mIntCurrentStatus;
    private int mIntCurrentVolume;
    private String mStringDebugData;

    private AudioManager mAudioMgr;

    public static final int STATUS_IDLE = 1;

    private final Object lock = new Object();

    private Handler mUIProcessCardHandler;
    private String mStringProcessCardResult;
    private String mStringCardDataBuffer;

    private void InitializeData()
    {
        mMTSCRA.clearBuffers();
        //mLongTimerInterval = 0;
        //miReadCount=0;
        //mbAudioConnected=false;
        mIntCurrentVolume=0;
        mIntCurrentStatus = STATUS_IDLE;
        mIntCurrentDeviceStatus = MagTekSCRA.DEVICE_STATE_DISCONNECTED;
        //mStringDebugData ="";
        //mStringAudioConfigResult="";

    }

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

    private void debugMsg(String lpstrMessage)
    {
        Log.i("udynamo:",lpstrMessage);

    }

    public Device(String driver_type) {

        final String DriverType = driver_type;

        PythonActivity.mActivity.runOnUiThread(new Runnable () {
            public void run() {
                Context context = PythonActivity.mActivity;
                if (DriverType.equals("audio")) {
                    debugMsg("Starting Audio Driver");
                    mSCRADataHandler = new Handler(new SCRAHandlerCallback());
                    mMTSCRA = new MagTekSCRA(mSCRADataHandler);
                    //InitializeData();
                    mAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    mIntCurrentVolume = mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                    debugMsg("Created Objects");
                    debugMsg("Current Volume: " + mIntCurrentDeviceStatus);
                } else if (DriverType.equals("usb")) {
                    debugMsg("Starting USB Driver");
                    mUIProcessCardHandler = new Handler();
                    mReaderDataHandler = new Handler(new MtHandlerCallback());
                    MagTeklibDynamag = new MagTeklibDynamag(context, mReaderDataHandler);
                } else {
                    debugMsg("No Driver Type Set.");
                }

            }
        });
    }

    public void openDevice() {
        if (mMTSCRA != null) {
            data = null;
            if (mAudioMgr.isWiredHeadsetOn()) {
                mMTSCRA.openDevice();
            }
        } else if (MagTeklibDynamag != null) {
            data = null;
            debugMsg("Open USB Device");
            MagTeklibDynamag.openDevice();
        } else {
            debugMsg("Device is Not plugged in. Can't turn on");
        }
    }

    public void closeDevice() {
        if (mMTSCRA != null) {
            mMTSCRA.closeDevice();
        } else if (MagTeklibDynamag != null) {
            MagTeklibDynamag.closeDevice();
        } else {
            debugMsg("mMTSCRA is null, can't close");
        }
    }

//    public String testDevice() {
//
//        String test_data;
//        test_data = "";
//
//        if (mMTSCRA != null) {
//            test_data = mMTSCRA.toString();
//            test_data += "     ";
//            test_data += mMTSCRA.getMagTekDeviceSerial();
//        }
//
//        if (message != null) {
//            test_data += "     ";
//            test_data += message.toString();
//        } else {
//            test_data += "no message";
//        }
//        return test_data;
//    }

    private void maxVolume()
    {
        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,mAudioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    private void minVolume()
    {
        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,mIntCurrentVolume, 0);
    }

    private void displayResponseData()
    {

        String strDisplay="";


        String strResponse =  mMTSCRA.getResponseData();
        if(strResponse!=null)
        {
            strDisplay =  strDisplay + "Response.Length=" +strResponse.length()+ "\n";
        }

        strDisplay =  strDisplay + "EncryptionStatus=" + mMTSCRA.getEncryptionStatus() + "\n";
        strDisplay =  strDisplay + "SDK.Version=" + mMTSCRA.getSDKVersion() + "\n";
        strDisplay =  strDisplay + "Reader.Type=" + mMTSCRA.getDeviceType() + "\n";
        strDisplay =  strDisplay + "Track.Status=" + mMTSCRA.getTrackDecodeStatus() + "\n";
        strDisplay =  strDisplay + "KSN=" + mMTSCRA.getKSN()+ "\n";
        strDisplay =  strDisplay + "Track1.Masked=" + mMTSCRA.getTrack1Masked() + "\n";
        strDisplay =  strDisplay + "Track2.Masked=" + mMTSCRA.getTrack2Masked() + "\n";
        strDisplay =  strDisplay + "Track3.Masked=" + mMTSCRA.getTrack3Masked() + "\n";
        strDisplay =  strDisplay + "Track1.Encrypted=" + mMTSCRA.getTrack1() + "\n";
        strDisplay =  strDisplay + "Track2.Encrypted=" + mMTSCRA.getTrack2() + "\n";
        strDisplay =  strDisplay + "Track3.Encrypted=" + mMTSCRA.getTrack3() + "\n";
        strDisplay =  strDisplay + "MagnePrint.Encrypted=" + mMTSCRA.getMagnePrint() + "\n";
        strDisplay =  strDisplay + "MagnePrint.Status=" + mMTSCRA.getMagnePrintStatus() + "\n";
        strDisplay =  strDisplay + "Card.IIN=" + mMTSCRA.getCardIIN() + "\n";
        strDisplay =  strDisplay + "Card.Name=" + mMTSCRA.getCardName() + "\n";
        strDisplay =  strDisplay + "Card.Last4=" + mMTSCRA.getCardLast4() + "\n";
        strDisplay =  strDisplay + "Card.ExpDate=" + mMTSCRA.getCardExpDate() + "\n";
        strDisplay =  strDisplay + "Card.SvcCode=" + mMTSCRA.getCardServiceCode() + "\n";
        strDisplay =  strDisplay + "Card.PANLength=" + mMTSCRA.getCardPANLength() + "\n";
        strDisplay =  strDisplay + "Device.Serial=" + mMTSCRA.getDeviceSerial()+ "\n";
        strDisplay =  strDisplay  + "SessionID=" + mMTSCRA.getSessionID() + "\n";

        switch(mMTSCRA.getDeviceType())
        {
            case MagTekSCRA.DEVICE_TYPE_AUDIO:
                strDisplay =  strDisplay  + "Card.Status=" + mMTSCRA.getCardStatus() + "\n";
                strDisplay =  strDisplay  + "Firmware.Partnumber=" + mMTSCRA.getFirmware()+ "\n";
                strDisplay =  strDisplay  + "MagTek.SN=" + mMTSCRA.getMagTekDeviceSerial()+ "\n";
                strDisplay =  strDisplay  + "TLV.Version=" + mMTSCRA.getTLVVersion()+ "\n";
                strDisplay =  strDisplay  + "HashCode=" + mMTSCRA.getHashCode()+ "\n";
                String tstrTkStatus = mMTSCRA.getTrackDecodeStatus();
                String tstrTk1Status="01";
                String tstrTk2Status="01";
                String tstrTk3Status="01";

                if(tstrTkStatus.length() >=6)
                {
                    tstrTk1Status=tstrTkStatus.substring(0,2);
                    tstrTk2Status=tstrTkStatus.substring(2,4);
                    tstrTk3Status=tstrTkStatus.substring(4,6);
                    debugMsg("Track1.Status=" + tstrTk1Status );
                    debugMsg("Track2.Status=" + tstrTk2Status );
                    debugMsg("Track3.Status=" + tstrTk3Status );
                    if ((!tstrTk1Status.equalsIgnoreCase("01"))&&
                            (!tstrTk2Status.equalsIgnoreCase("01"))&&
                            (!tstrTk3Status.equalsIgnoreCase("01")))
                    {
                        closeDevice();
                    }
                }
                else
                {
                    closeDevice();
                }
                break;
            case MagTekSCRA.DEVICE_TYPE_BLUETOOTH:
                strDisplay =  strDisplay  + "CardDataCRC=" + mMTSCRA.getCardDataCRC() + "\n";

                break;
            default:
                break;

        };
        if(strResponse!=null)
        {
            strDisplay =  strDisplay + "Response.Raw=" + strResponse + "\n";

            HashMap hm = new HashMap();
            hm.put("track1_encrypted", mMTSCRA.getTrack1());
            hm.put("track2_encrypted", mMTSCRA.getTrack2());
            hm.put("track3_encrypted", mMTSCRA.getTrack3());
            hm.put("ksn", mMTSCRA.getKSN());
            hm.put("encyrption_status", mMTSCRA.getEncryptionStatus());
            hm.put("sdk_version", mMTSCRA.getSDKVersion());
            hm.put("reader_type", mMTSCRA.getDeviceType());
            hm.put("track1_masked", mMTSCRA.getTrack1Masked());
            hm.put("track2_masked", mMTSCRA.getTrack2Masked());
            hm.put("track3_masked", mMTSCRA.getTrack3Masked());
            hm.put("magneprint_encrypted", mMTSCRA.getMagnePrint());
            hm.put("magneprint_status", mMTSCRA.getMagnePrintStatus());
            hm.put("card_iin", mMTSCRA.getCardIIN());
            hm.put("card_name", mMTSCRA.getCardName());
            hm.put("card_last4", mMTSCRA.getCardLast4());
            hm.put("card_expdate", mMTSCRA.getCardExpDate());
            hm.put("card_panlength", mMTSCRA.getCardPANLength());
            hm.put("device_serial", mMTSCRA.getDeviceSerial());
            hm.put("session_id", mMTSCRA.getSessionID());

            if (mMTSCRA.getDeviceType() == MagTekSCRA.DEVICE_TYPE_AUDIO) {
                hm.put("card_status", mMTSCRA.getCardStatus());
                hm.put("firmware_partnumber", mMTSCRA.getFirmware());
                hm.put("magtek_sn", mMTSCRA.getMagTekDeviceSerial());
                hm.put("tlv_version", mMTSCRA.getTLVVersion());
                hm.put("hashcode", mMTSCRA.getHashCode());

                String tstrTkStatus = mMTSCRA.getTrackDecodeStatus();
                String tstrTk1Status="01";
                String tstrTk2Status="01";
                String tstrTk3Status="01";

                if(tstrTkStatus.length() >=6)
                {
                    tstrTk1Status=tstrTkStatus.substring(0,2);
                    tstrTk2Status=tstrTkStatus.substring(2,4);
                    tstrTk3Status=tstrTkStatus.substring(4,6);

                    hm.put("track1_status", tstrTk1Status);
                    hm.put("track2_status", tstrTk2Status);
                    hm.put("track3_status", tstrTk3Status);
                }
            }
            setData(hm);
        }

        mStringDebugData = strDisplay;
        debugMsg(mStringDebugData);

    }

    private class SCRAHandlerCallback implements Callback {
        public boolean handleMessage(Message msg)
        {
            debugMsg("Got Message: " + msg.toString());
//            message = msg;

            try
            {
                switch (msg.what)
                {
                    case MagTekSCRA.DEVICE_MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case MagTekSCRA.DEVICE_STATE_CONNECTED:
                                mIntCurrentStatus = STATUS_IDLE;
                                mIntCurrentDeviceStatus = MagTekSCRA.DEVICE_STATE_CONNECTED;
                                debugMsg("Connected");
                                maxVolume();
                                //setStatus(R.string.title_connected, Color.GREEN);
                                break;
                            case MagTekSCRA.DEVICE_STATE_CONNECTING:
                                mIntCurrentDeviceStatus = MagTekSCRA.DEVICE_STATE_CONNECTING;
                                debugMsg("Connecting");
                                //setStatus(R.string.title_connecting, Color.YELLOW);
                                break;
                            case MagTekSCRA.DEVICE_STATE_DISCONNECTED:
                                mIntCurrentDeviceStatus = MagTekSCRA.DEVICE_STATE_DISCONNECTED;
                                debugMsg("Disconnected");
                                //setStatus(R.string.title_not_connected, Color.RED);
                                minVolume();
                                break;
                        }
                        break;
                    case MagTekSCRA.DEVICE_MESSAGE_DATA_START:
                        if (msg.obj != null)
                        {
                            debugMsg("Transfer started: Card Swiped...");
                            //mCardDataEditText.setText("Card Swiped...");
                            return true;
                        }
                        break;
                    case MagTekSCRA.DEVICE_MESSAGE_DATA_CHANGE:
                        if (msg.obj != null)
                        {
                            debugMsg("Transfer ended. Getting Data.");
                            //displayInfo();
                            displayResponseData();
                            msg.obj=null;
                            //if(mStringLocalConfig.length() > 0)
                            //{
                            //     setConfigurationLocal(mStringLocalConfig);//optional but can be useful to retrieve from locally and get it from server only certain times
                            //     mStringLocalConfig="";
                            //}

                            return true;
                        }
                        break;
                    case MagTekSCRA.DEVICE_MESSAGE_DATA_ERROR:
                        debugMsg("Card Swipe Error... Please Swipe Again.");
                        //mCardDataEditText.setText("Card Swipe Error... Please Swipe Again.\n");
                        return true;
                    case MagTekSCRA.DEVICE_INACTIVITY_TIMEOUT:
                        debugMsg("Inactivity Timeout...");
                        return true;
                    default:
                        if (msg.obj != null)
                        {
                            return true;
                        }
                        break;
                };

            }
            catch(Exception ex)
            {

            }
            return false;
        }
    }

    private void ClearData()
    {
        mStringProcessCardResult = "";
    }

    private void ClearMessageBuffer() {
        mStringCardDataBuffer = "";

    }

    private String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff ) + 0x100, 16).substring(1).toUpperCase() + " ";
        }

        return result;
    }

    private void DisplayCommandResult(byte[] result)
    {
        String strDisplay="";
        strDisplay+="Result.Code: (Hex)\n" + String.format("%2s", Integer.toHexString(result[0])).replace(' ', '0').substring(0, 2).toUpperCase() + "\n\n";

        if (result.length > 1 && result[1] > 0) {
            strDisplay+="Data.Length: (Hex)\n" + String.format("%2s", Integer.toHexString(result[1])).replace(' ', '0') + "\n\n";

            byte[] data = new byte[result[1]];
            System.arraycopy(result, 2, data, 0, data.length);
            strDisplay+="Data: (Hex)\n" + getHexString(data) + "\n\n";
        }

        debugMsg(strDisplay);
    }

    private void DisplayCardData()
    {

        HashMap hm = new HashMap();

        String magneprint = MagTeklibDynamag.getMagnePrint();
        String magnestatus = MagTeklibDynamag.getMagnePrintStatus();

//        // Fix MagnePrint
//        if (magneprint.length() >= magnestatus.length()) {
//            magneprint = magneprint.substring(magnestatus.length(), magneprint.length()-1);
//        }

        hm.put("track1_encrypted", MagTeklibDynamag.getTrack1());
        hm.put("track2_encrypted", MagTeklibDynamag.getTrack2());
        hm.put("track3_encrypted", MagTeklibDynamag.getTrack3());
        hm.put("ksn", MagTeklibDynamag.getKSN());
        hm.put("encyrption_status", MagTeklibDynamag.getEncryptionStatus());
        hm.put("sdk_version", MagTeklibDynamag.getSDKVersion());
        hm.put("track1_masked", MagTeklibDynamag.getTrack1Masked());
        hm.put("track2_masked", MagTeklibDynamag.getTrack2Masked());
        hm.put("track3_masked", MagTeklibDynamag.getTrack3Masked());
        hm.put("magneprint_encrypted", magneprint);
        hm.put("magneprint_status", magnestatus);
        hm.put("device_serial", MagTeklibDynamag.getDeviceSerial());
        hm.put("session_id", MagTeklibDynamag.getSessionID());

        String tstrTkStatus = MagTeklibDynamag.getTrackDecodeStatus();
        String tstrTk1Status="01";
        String tstrTk2Status="01";
        String tstrTk3Status="01";

        if(tstrTkStatus.length() >=6)
        {
            tstrTk1Status=tstrTkStatus.substring(0,2);
            tstrTk2Status=tstrTkStatus.substring(2,4);
            tstrTk3Status=tstrTkStatus.substring(4,6);

            hm.put("track1_status", tstrTk1Status);
            hm.put("track2_status", tstrTk2Status);
            hm.put("track3_status", tstrTk3Status);
        }
        setData(hm);

        String strDisplay="";
        //strDisplay+="Data.Count:\n" + mIntDataCount + "\n\n";
        strDisplay+="Track.Decode.Status\n" + tstrTkStatus + "\n\n";
        strDisplay+="SDK.Version:\n" + hm.get("sdk_version") + "\n\n";
        strDisplay+="Encrypt.Status:\n" + hm.get("encyrption_status") + "\n\n";
        strDisplay+="Encrypted.Track1:\n" + hm.get("track1_encrypted") + "\n\n";
        strDisplay+="Encrypted.Track2:\n" + hm.get("track2_encrypted") + "\n\n";
        strDisplay+="Encrypted.Track3:\n" + hm.get("track3_encrypted") + "\n\n";
        strDisplay+="Masked.Track1:\n" + hm.get("track1_masked") + "\n\n";
        strDisplay+="Masked.Track2:\n" + hm.get("track2_masked") + "\n\n";
        strDisplay+="Masked.Track3:\n" + hm.get("track3_masked") + "\n\n";
        strDisplay+="Device.Serial:\n" + hm.get("device_serial") + "\n\n";
        strDisplay+="Key.Serial (KSN):\n" + hm.get("ksn") + "\n\n";
        strDisplay+="MagnePrint.Status:\n" + hm.get("magneprint_status") + "\n\n";
        strDisplay+="MagnePrint.Data:\n" + hm.get("magneprint_encrypted") + "\n\n";

        debugMsg("Got Data:");
        debugMsg(strDisplay);

    }

    private void ProcessMessage() {
        String strResult = "OK";
        try {
            MagTeklibDynamag.setCardData(mStringCardDataBuffer);
            ClearMessageBuffer();
            evCardDataReceived();
        } catch (Exception ex) {
            strResult = "Error: " + ex.getMessage();
        }
        debugMsg(strResult);
    }

    private String ProcessCardData() {
        String strResult = "OK";
        try {
			/*if ((MagTeklibDynamag.getTrack2Masked().length() > 0)&& (!MagTeklibDynamag.getTrack2Masked().equalsIgnoreCase(";E?")))
			{
				strResult = "OK";
			}
			else
			{
				strResult = "Error: Card Read Failed, masked track2 starts with ;E? ";
			}
			*/

        }
        catch (Exception ex)
        {
            strResult = "Error: " + ex.getMessage();
        }

        return strResult;

    }

    private void evCardDataReceived() {
        //SetStatus(R.string.status_processing_card, Color.YELLOW);
        ClearData();
        //ClearScreen();
        Thread tProcessCardData = new Thread() {
            public void run() {
                //mIntCurrentStatus = STATUS_PROCESSCARD;
                mStringProcessCardResult = ProcessCardData();
                mUIProcessCardHandler.post(mUIUpdateProcessCardResults);
            }
        };
        tProcessCardData.start();

    }

    final Runnable mUIUpdateProcessCardResults = new Runnable() {
        public void run() {
            try {
                if (!mStringProcessCardResult.equalsIgnoreCase("OK"))
                {
//                    SetStatus(R.string.status_card_read_failed, Color.RED);
//                    DisplayMessage("Card Read Error", mStringProcessCardResult);
//                    ClearCardDataBuffer();
//                    ClearScreen();
                    debugMsg("Card Read Error: " + mStringProcessCardResult);
                    ClearData();
                }
                else
                {
                    DisplayCardData();
//                    SetStatus(R.string.status_default, Color.GREEN);
                }
            } catch (Exception ex) {

            }
        }
    };

    private class MtHandlerCallback implements Callback {
        public boolean handleMessage(Message msg) {

            boolean ret = false;

            switch (msg.what) {
                case DEVICE_MESSAGE_CARDDATA_CHANGE:
//                    mIntDataCount++;
                    mStringCardDataBuffer = (String)msg.obj;
                    debugMsg("Card Data Buffer: " + msg.obj);
                    ProcessMessage();
                    ret = true;
                    break;

                case DEVICE_STATUS_CONNECTED:
                    if (((Number)msg.obj).intValue() == DEVICE_STATUS_CONNECTED_SUCCESS) {
//                        SetStatus(R.string.status_device_connected, Color.GREEN);
                        debugMsg("Device Connected");
                        byte [] response = MagTeklibDynamag.sendCommandWithLength("14");
                        debugMsg("Get Status Response: ");
                        DisplayCommandResult(response);
                    } else if (((Number)msg.obj).intValue() == DEVICE_STATUS_CONNECTED_FAIL){
//                        SetStatus(R.string.status_device_connected_fail, Color.RED);
                        debugMsg("Device Connect Failed");
                    } else if (((Number)msg.obj).intValue() == DEVICE_STATUS_CONNECTED_PERMISSION_DENIED){
//                        SetStatus(R.string.status_device_connected_permission_denied, Color.RED);
                        debugMsg("Device Connect Permission Denied");
                    }

                    break;

                case DEVICE_STATUS_DISCONNECTED:
//                    SetStatus(R.string.status_device_disconnected, Color.RED);
//                    mIntDataCount=0;
                    debugMsg("Device Disconnected");
                    break;

                default:
                    debugMsg("Default Message");
                    ret = false;
                    break;

            }



            return ret;
        }
    }
}

