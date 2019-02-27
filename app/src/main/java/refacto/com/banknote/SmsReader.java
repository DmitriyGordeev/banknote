package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SmsReader extends Activity {

    private static final String LOGTAG = "MyLog-SmsReader";

    ListView smsReaderData;
    SmsStoredAdapter adapter;
    HashMap<String, ArrayList<String>> map;

    public static Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_reader_activity);

        smsReaderData = (ListView)findViewById(R.id.listView_smsReaderData);

        handleListInteractions();
        refreshSmsData();
    }

    @Override
    public void onBackPressed() {
        if(cursor != null && !cursor.isClosed())
            cursor.close();
        super.onBackPressed();
    }

    /* reads sms messages from inbox: */
    private void refreshSmsData() {

        Uri uri = Uri.parse("content://sms/inbox");
        cursor = getContentResolver().query(uri, null, null ,null,null);
        startManagingCursor(cursor);

        ArrayList<SmsStored> smsListData = new ArrayList<>();

        // Read the sms data and store it in the list
        if(cursor.moveToFirst()) {
            for(int i=0; i < cursor.getCount(); i++) {
                SmsStored sms = new SmsStored();
                sms.setBody(cursor.getString(cursor.getColumnIndexOrThrow("body")).toString());
                sms.setNumber(cursor.getString(cursor.getColumnIndexOrThrow("address")).toString());
                smsListData.add(sms);

                cursor.moveToNext();
            }
        }

        map = convertData(smsListData);

        /* get list of senders from map: */
        ArrayList<String> senders = new ArrayList<>();
        for(HashMap.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            senders.add(entry.getKey());
        }

        // adapter = new SmsStoredAdapter(getApplicationContext(), R.layout.sms_stored_list_element, smsListData);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, senders);
        smsReaderData.setAdapter(adapter);
    }


    /* convert ArrayList<SmsStored> into Map<String, ArrayList<String>>: */
    private HashMap<String, ArrayList<String>> convertData(ArrayList<SmsStored> data) {

        HashMap<String, ArrayList<String>> output = new HashMap<>();
        for(SmsStored smsStored : data) {
            if(output.containsKey(smsStored.getNumber())) {
                output.get(smsStored.getNumber()).add(smsStored.getBody());
            }
            else {
                ArrayList<String> messages = new ArrayList<>();
                messages.add(smsStored.getBody());
                output.put(smsStored.getNumber(), messages);
            }
        }

        return output;
    }


    private void handleListInteractions() {
        smsReaderData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SmsMessagesList.class);
                String sender = (String)parent.getAdapter().getItem(position);

                /* serialize arrayList: */
                String joined = "";
                ArrayList<String> messages = map.get(sender);
                for(String s : messages) {
                    joined += s + "_StringJoiner_";
                }

                intent.putExtra("sender", sender);
                intent.putExtra("messages", joined);
                startActivity(intent);
            }
        });
    }


}
