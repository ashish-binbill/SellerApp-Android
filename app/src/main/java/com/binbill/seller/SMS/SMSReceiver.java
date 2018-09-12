package com.binbill.seller.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.binbill.seller.BinBillSeller;

/**
 * Created by shruti.vig on 9/10/18.
 */

public class SMSReceiver extends BroadcastReceiver {
    private static SMSListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (BinBillSeller.isActivityVisible()) {
            Bundle bundle = intent.getExtras();

            SmsMessage smsMessage;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                smsMessage = msgs[0];
            } else {
                Object pdus[] = (Object[]) bundle.get("pdus");
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            }

            if (smsMessage.getOriginatingAddress().toLowerCase().contains("bin")) {

                Log.d("SMSReceiver", "Message --- " + smsMessage.getMessageBody());

                if (mListener != null)
                    mListener.messageReceived(smsMessage.getMessageBody());
            }
        }
    }

    public static void bindListener(SMSListener listener) {
        mListener = listener;
    }

}

