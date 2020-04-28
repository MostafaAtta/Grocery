package thegroceryshop.com.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import thegroceryshop.com.custom.ValidationUtil;

public class IncomingSms extends BroadcastReceiver {
 
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
 
    public void onReceive(Context context, Intent intent) {
 
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
 
        try {
 
            if (bundle != null) {
 
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
 
                for (int i = 0; i < pdusObj.length; i++) {
 
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
 
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if(!ValidationUtil.isNullOrBlank(senderNum) && message != null && message.contains("The Grocery Shop, save time and energy,")){
                        if(message.contains(".")){
                            String arr[] = message.split("\\.");
                            if(arr.length > 0){
                                String otpSentence = arr[0];
                                if(otpSentence.contains(" ")){
                                    String arr1[] = otpSentence.split(" ");
                                    if(arr1.length > 0){
                                        String otp = arr1[arr1.length-1];

                                        Intent myIntent = new Intent("otp");
                                        myIntent.putExtra("otp",otp);
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                                    }
                                }
                            }
                        }
                    }
 
                }
            }
 
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver " +e);
 
        }
    }
}