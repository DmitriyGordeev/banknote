package refacto.com.banknote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.ArrayList;


public class IncomingSms extends BroadcastReceiver {

    public final static String LOGTAG = "MyLog-IncomingSms";

    public static ArrayList<SmsData> smsDataContainer = new ArrayList<>();
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "onReceive()", Toast.LENGTH_SHORT).show();

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    smsDataContainer.add(new SmsData(phoneNumber, message));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<SmsData> consumeQueue() {
        ArrayList<SmsData> arr = new ArrayList<>(smsDataContainer);
        smsDataContainer.clear();
        return arr;
    }
}
