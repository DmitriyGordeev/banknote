package refacto.com.banknote;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class FService extends Service {

    IncomingSms incomingSms = new IncomingSms();
    public static boolean isActive;

    public FService() {
        isActive = false;
    }

    public void onCreate() {
        super.onCreate();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("Sms parser");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.build();

        startForeground(1, builder.build());
        isActive = true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        // BroadCast receiver:
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        isActive = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}