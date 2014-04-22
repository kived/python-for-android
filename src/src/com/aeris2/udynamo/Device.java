
package com.aeris2.udynamo;

import java.util.Random;

import com.magtek.mobile.android.scra.ConfigParam;
import com.magtek.mobile.android.scra.MagTekSCRA;
import com.magtek.mobile.android.scra.MTSCRAException;
import com.magtek.mobile.android.scra.ProcessMessageResponse;
import com.magtek.mobile.android.scra.SCRAConfiguration;
import com.magtek.mobile.android.scra.SCRAConfigurationDeviceInfo;
import com.magtek.mobile.android.scra.SCRAConfigurationReaderType;
import com.magtek.mobile.android.scra.StatusCode;

//import android.os.Bundle;
//import android.content.Context;
//import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
//import android.app.Activity;
import android.os.Looper;
import android.util.Log;

class Device {

    class LooperThread extends Thread {

        public void run() {
            Looper.prepare();
            mSCRADataHandler = new Handler(new SCRAHandlerCallback());
            mMTSCRA = new MagTekSCRA(mSCRADataHandler);
            //mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //mIntCurrentVolume = mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            InitializeData();
            Looper.loop();
        }
    }

    private MagTekSCRA mMTSCRA;
    //private Handler mSCRADataHandler = new Handler(new SCRAHandlerCallback());
    //private Handler mSCRADataHandler = new Handler(Looper.getMainLooper());
    private Handler mSCRADataHandler;

    private Message message;

    private int mIntCurrentDeviceStatus;
    private int mIntCurrentStatus;
    private int mIntCurrentVolume;
    private String mStringDebugData;

    //private AudioManager mAudioMgr;

    public static final int STATUS_IDLE = 1;

    public static final String CONFIGWS_URL = "https://deviceconfig.magensa.net/service.asmx";//Production URL
    private static final int CONFIGWS_READERTYPE = 1;
    private static final String CONFIGWS_USERNAME = "magtek";
    private static final String CONFIGWS_PASSWORD = "p@ssword";
    String mStringLocalConfig;

    private void InitializeData()
    {
        mMTSCRA.clearBuffers();
        //mLongTimerInterval = 0;
//		miReadCount=0;
        //mbAudioConnected=false;
        mIntCurrentVolume=0;
        mIntCurrentStatus = STATUS_IDLE;
        mIntCurrentDeviceStatus = MagTekSCRA.DEVICE_STATE_DISCONNECTED;

        //mStringDebugData ="";
        //mStringAudioConfigResult="";

    }
    private void debugMsg(String lpstrMessage)
    {
        Log.i("udynamo:",lpstrMessage);

    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mSCRADataHandler = new Handler(Looper.getMainLooper());
//        mMTSCRA = new MagTekSCRA(mSCRADataHandler);
//    }

    public Device() {
        new LooperThread().start();
    }

    public void setConfig() {
        try {
            Random randomGenerator = new Random();
            int ranint = randomGenerator.nextInt(5);
            if (ranint == 0) {
                debugMsg("INPUT_SAMPLE_RATE_IN_HZ=48000,");
                mMTSCRA.setConfigurationParams("INPUT_SAMPLE_RATE_IN_HZ=48000,");
            } else if (ranint == 1) {
                debugMsg("INPUT_SAMPLE_RATE_IN_HZ=32000,");
                mMTSCRA.setConfigurationParams("INPUT_SAMPLE_RATE_IN_HZ=32000,");
            } else if (ranint == 2) {
                debugMsg("INPUT_SAMPLE_RATE_IN_HZ=44100,");
                mMTSCRA.setConfigurationParams("INPUT_SAMPLE_RATE_IN_HZ=44100,");
            } else if (ranint == 3) {
                debugMsg("INPUT_AUDIO_SOURCE=VRECOG,");
                mMTSCRA.setConfigurationParams("INPUT_AUDIO_SOURCE=VRECOG,");
            } else if (ranint == 4) {
                debugMsg("-- Droidx");
                mMTSCRA.setConfigurationParams("INPUT_SAMPLE_RATE_IN_HZ = 32000,LOWER_SYNC_SAMPLE_LIMIT = 18,LOWER_3BIT_LIMIT = 2,UPPER_3BIT_LIMIT = 15,SAMPLES_PER_BIT = 4,HALF_BIT_LIMIT = 2,");
            } else if (ranint == 5) {
                debugMsg("INPUT_WAVE_FORM=0,");
                mMTSCRA.setConfigurationParams("INPUT_WAVE_FORM=0,");
            } else {
                debugMsg("No Change");
            }
        } catch(MTSCRAException ex) {
            debugMsg("Exception:" + ex.getMessage());
        }
    }

    public void openDevice() {
        if (mMTSCRA != null) {
            setConfig();
//            try {
//                setupAudioParameters();
//            } catch(MTSCRAException ex) {
//                debugMsg("setup audio Exception:" + ex.getMessage());
//            }
            mMTSCRA.openDevice();
        } else {
            debugMsg("mMTSCRA is null, can't open");
        }
    }

    public void closeDevice() {
        if (mMTSCRA != null) {
            mMTSCRA.closeDevice();
        } else {
            debugMsg("mMTSCRA is null, can't close");
        }
    }

    public String testDevice() {

        String test_data;
        test_data = "";

        if (mMTSCRA != null) {
            test_data = mMTSCRA.toString();
            test_data += "     ";
            test_data += mMTSCRA.getMagTekDeviceSerial();
        }

        if (message != null) {
            test_data += "     ";
            test_data += message.toString();
        } else {
            test_data += "no message";
        }
        return test_data;
    }

//    private void maxVolume()
//    {
//        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,mAudioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.FLAG_SHOW_UI);
//    }
//
//    private void minVolume()
//    {
//        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC,mIntCurrentVolume, AudioManager.FLAG_SHOW_UI);
//    }

    void dumpWebConfigResponse(ProcessMessageResponse lpMessageResponse)
    {
        String strDisplay="";
        try
        {

            if(lpMessageResponse!=null)
            {
                if(lpMessageResponse.Payload!=null)
                {
                    if(lpMessageResponse.Payload.StatusCode!= null)
                    {
                        if(lpMessageResponse.Payload.StatusCode.Number==0)
                        {
                            if(lpMessageResponse.Payload.SCRAConfigurations.size() > 0)
                            {
                                for (int i=0; i < lpMessageResponse.Payload.SCRAConfigurations.size();i++)
                                {
                                    SCRAConfiguration tConfig = (SCRAConfiguration) lpMessageResponse.Payload.SCRAConfigurations.elementAt(i);
                                    strDisplay="********* Config:" + Integer.toString(i+1) + "***********\n";

                                    strDisplay+="DeviceInfo:Model:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_MODEL) + "\n";
                                    strDisplay+="DeviceInfo:Device:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_DEVICE) + "\n";
                                    strDisplay+="DeviceInfo:Firmware:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_FIRMWARE) + "\n";
                                    strDisplay+="DeviceInfo.Platform:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_PLATFORM) + "\n";
                                    strDisplay+="DeviceInfo:Product:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_PRODUCT) + "\n";
                                    strDisplay+="DeviceInfo:Release:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_RELEASE) + "\n";
                                    strDisplay+="DeviceInfo:SDK:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_SDK) + "\n";
                                    strDisplay+="DeviceInfo:Status:" + tConfig.DeviceInfo.getProperty(SCRAConfigurationDeviceInfo.PROP_STATUS)+ "\n";
                                    //Status = 0 Unknown
                                    //Status = 1 Tested and Passed
                                    //Status = 2 Tested and Failed
                                    strDisplay+="ReaderType.Name:" + tConfig.ReaderType.getProperty(SCRAConfigurationReaderType.PROP_NAME) + "\n";
                                    strDisplay+="ReaderType.Type:" + tConfig.ReaderType.getProperty(SCRAConfigurationReaderType.PROP_TYPE) + "\n";
                                    strDisplay+="ReaderType.Version:" + tConfig.ReaderType.getProperty(SCRAConfigurationReaderType.PROP_VERSION) + "\n";
                                    strDisplay+="ReaderType.SDK:" + tConfig.ReaderType.getProperty(SCRAConfigurationReaderType.PROP_SDK) + "\n";
                                    strDisplay+="StatusCode.Description:" + tConfig.StatusCode.Description + "\n";
                                    strDisplay+="StatusCode.Number:" + tConfig.StatusCode.Number + "\n";
                                    strDisplay+="StatusCode.Version:" + tConfig.StatusCode.Version + "\n";
                                    for (int j=0; j < tConfig.ConfigParams.size();j++)
                                    {
                                        strDisplay+="ConfigParam.Name:" + ((ConfigParam)tConfig.ConfigParams.elementAt(j)).Name + "\n";
                                        strDisplay+="ConfigParam.Type:" + ((ConfigParam)tConfig.ConfigParams.elementAt(j)).Type + "\n";
                                        strDisplay+="ConfigParam.Value:" + ((ConfigParam)tConfig.ConfigParams.elementAt(j)).Value + "\n";
                                    }//for (int j=0; j < tConfig.ConfigParams.size();j++)
                                    strDisplay+="*********  Config:" + Integer.toString(i+1) + "***********\n";
                                    debugMsg(strDisplay);
                                }//for (int i=0; i < lpMessageResponse.Payload.SCRAConfigurations.size();i++)
                                //debugMsg(strDisplay);
                            }//if(lpMessageResponse.Payload.SCRAConfigurations.size() > 0)

                        }//if(lpMessageResponse.Payload.StatusCode.Number==0)
                        strDisplay= "Payload.StatusCode.Version:" + lpMessageResponse.Payload.StatusCode.getProperty(StatusCode.PROP_VERSION) + "\n";
                        strDisplay+="Payload.StatusCode.Number:" + lpMessageResponse.Payload.StatusCode.getProperty(StatusCode.PROP_NUMBER) + "\n";
                        strDisplay+="Payload.StatusCode.Description:" + lpMessageResponse.Payload.StatusCode.getProperty(StatusCode.PROP_DESCRIPTION) + "\n";
                        debugMsg(strDisplay);
                    }//if(lpMessageResponse.Payload.StatusCode!= null)

                }//if(lpMessageResponse.Payload!=null)
            }//if(lpMessageResponse!=null)
            else
            {
                debugMsg("Configuration Not Found");
            }

        }
        catch(Exception ex)
        {
            debugMsg("Exception:" + ex.getMessage());
        }

    }

    String setupAudioParameters()throws MTSCRAException
    {
        mStringLocalConfig="";
        String strResult="OK";

        try
        {

            debugMsg("Setting up Audio");

            //Option 3

            String strXMLConfig="";
//            if (!mGetConfigFromWeb.isChecked())
//            {
//                strXMLConfig = getConfigurationLocal();//retrieve saved configuration. This is optional but useful if the web service connection
//                //is not available or sluggish for some reason. It is important to provide a way to
//                //sync the local configuration to server configuration to keep the local phone config updated
//            }


            if (strXMLConfig.length() <= 0)
            {
                debugMsg("Retrieve Configuration From Web....");
                //setStatusMessage("Retrieve Configuration From Web");
                SCRAConfigurationDeviceInfo pDeviceInfo = new SCRAConfigurationDeviceInfo();
                pDeviceInfo.setProperty(SCRAConfigurationDeviceInfo.PROP_PLATFORM,"Android");
                pDeviceInfo.setProperty(SCRAConfigurationDeviceInfo.PROP_MODEL,android.os.Build.MODEL.toUpperCase());
                //pDeviceInfo.setProperty(SCRAConfigurationDeviceInfo.PROP_MODEL,"SPH-L720");
                strXMLConfig = mMTSCRA.getConfigurationXML(CONFIGWS_USERNAME,CONFIGWS_PASSWORD,CONFIGWS_READERTYPE,pDeviceInfo,CONFIGWS_URL,10000);//Call Web Service to retrieve XML
                if (strXMLConfig.length() > 0)
                {
                    debugMsg("Configuration Received From Server\n******************************\n" + strXMLConfig + "\n******************************\n");
                    ProcessMessageResponse pResponse = mMTSCRA.getConfigurationResponse(strXMLConfig);
                    if(pResponse!=null)
                    {
                        dumpWebConfigResponse(pResponse);
                        debugMsg("Setting Configuration From Response....");
                        mMTSCRA.setConfigurationResponse(pResponse);
                    }
                    mStringLocalConfig=strXMLConfig;
                    debugMsg("SDK Configuration Was Set Successful.\nPlease Swipe A Card....\n");
                    return strResult;
                }//if (strXMLConfig.length() > 0)
                else
                {
                    debugMsg("No Configuration Received, Using Default");
                    strResult="Error:" + "No Configuration Received, Using Default";
                    return strResult;

                }

            }
            else
            {
                debugMsg("Setting Configuration Locally From XML....");
                debugMsg("Configuration Saved Locally\n******************************\n" + strXMLConfig + "\n******************************\n");
                //dumpWebConfigResponse(strXMLConfig);
                mMTSCRA.setConfigurationXML(strXMLConfig);//Convert XML to Response Object
                mStringLocalConfig=strXMLConfig;
                return strResult;
            }

        }
        catch(MTSCRAException ex)
        {
            debugMsg("Exception:" + ex.getMessage());
            strResult = "Error:" +  ex.getMessage();
            //setStatusMessage("Failed Retrieving Configuration From Server:" + strResult);
            //throw new MTSCRAException(ex.getMessage());
        }
        return strResult;
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
                                //maxVolume();
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
                                //minVolume();
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
                            debugMsg("Transfer ended");
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
}

