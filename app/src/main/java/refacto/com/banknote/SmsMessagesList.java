package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsMessagesList extends Activity {

    ListView messagesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sms_message_activity);

        String sender = getIntent().getStringExtra("sender");
        String joinedMessages = getIntent().getStringExtra("messages");

        TextView senderLabel = (TextView)findViewById(R.id.textView_senderLabel);
        senderLabel.setText(sender);


        messagesList = (ListView)findViewById(R.id.listView_messagesList);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, joinedMessages.split("_StringJoiner_"));

        messagesList.setAdapter(adapter);
        handleListInteractions();
    }

    private void handleListInteractions() {
        messagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String message = (String)parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), EditPatternActivity.class);

                intent.putExtra("message", message);

                if(SmsReader.cursor != null) {
                    if(!SmsReader.cursor.isClosed())
                        SmsReader.cursor.close();
                }

                startActivity(intent);
            }
        });
    }

}
