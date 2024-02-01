package com.sehmusacar.AcarSMSDetector;
//BroadcastReceiver'dır ve sistem veya uygulama olaylarını, özellikle SMS mesajlarını işlemek için kullanılır.
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.sehmusacar.AcarSMSDetector.Utils.LinkData;
import com.sehmusacar.AcarSMSDetector.Utils.MsgData;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "SmsBroadExp";
    private SmsBroadcastReceiver.OnSmsGotListener onSmsGotListener;

    DatabaseManager databaseManager;

    // SMS alındığında dinleyici için bir arayüz tanımla
    public interface OnSmsGotListener {
        void OnSmsGot(MsgData msgData);
    }

    public SmsBroadcastReceiver(SmsBroadcastReceiver.OnSmsGotListener onSmsGotListener) {
        this.onSmsGotListener = onSmsGotListener;
    }

    public SmsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = null;
            StringBuilder smsBody = new StringBuilder();

            long time_received = 0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsBody.append(smsMessage.getMessageBody());
                    if (smsSender == null) {
                        smsSender = (smsMessage.getDisplayOriginatingAddress());

                        time_received = smsMessage.getTimestampMillis();
                    }

                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");

                    if (pdus == null) {
                        return;
                    }

                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {

                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        Log.d(TAG, "onReceive: msg = " + messages[i].getServiceCenterAddress());
                        smsBody.append(messages[i].getMessageBody());

                    }
                    smsSender = messages[0].getDisplayOriginatingAddress();
                    time_received = messages[0].getTimestampMillis();
                }
            }

            databaseManager = new DatabaseManager(context);

            // Alınan SMS'i veritabanına kaydet
            MsgData msgData = new MsgData();
            msgData.setMsgTitle(smsSender);
            msgData.setMsgBody(smsBody.toString());
            msgData.setMsgDateLong(time_received);
            databaseManager.InsertMessages(msgData, 0, 0);

            Log.d(TAG, "onReceive: sender = " + smsSender + " body = " + smsBody);

            // SMS dinleyicisi kayıt edilmişse, dinleyiciyi çağır
            if (onSmsGotListener != null)
                onSmsGotListener.OnSmsGot(msgData);
        }

    }

    // Metindeki tüm bağlantıları çıkaran bir yardımcı işlev
    private ArrayList<LinkData> getAllLinks(String text) {

        ArrayList<LinkData> links = new ArrayList<>();

        final String LINK_REGEX = "\\b((?:https?|ftp|file):"
                + "//[-a-zA-Z0-9+&@#/%?="
                + "~_|!:, .;]*[-a-zA-Z0-9+"
                + "&@#/%=~_|])";

        Pattern p = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);

        while (m.find()) {
            LinkData temp = new LinkData();
            temp.setLinks(m.group());
            temp.setStart(m.start());
            temp.setEnd(m.end());
            links.add(temp);
        }

        return links;
    }
}
