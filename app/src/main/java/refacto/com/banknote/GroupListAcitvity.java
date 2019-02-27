package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class GroupListAcitvity extends Activity {

    private static final String LOGTAG = "MyLog-GroupActivity";

    ListView _listView;
    DialogGroupAdapter _adapter;
    DataBaseHandler dbHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_activity);
        _listView = (ListView)findViewById(R.id.listView_groupsList);
        dbHandler = new DataBaseHandler(this, "FundachDB");

        handleListInteractions();
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshGroupList();
    }


    public void onAddGroup(View v) {
        Intent intent = new Intent(this, EditGroupActivity.class);
        intent.putExtra("is_edit", false);
        startActivity(intent);
    }


    private void refreshGroupList() {
        ArrayList<Group> groups = dbHandler.pullGroups();
        _adapter = new DialogGroupAdapter(getApplicationContext(), R.layout.dialog_grouplist, groups);
        _listView.setAdapter(_adapter);
    }


    private void handleListInteractions() {

        // onItem click:
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group)(parent.getAdapter().getItem(position));
                Intent intent = new Intent(getApplicationContext(), EditGroupActivity.class);

                intent.putExtra("is_edit", true);
                intent.putExtra("id", group.id());
                intent.putExtra("name", group.name());
                intent.putExtra("is_profit", group.isProfit());

                Log.i(LOGTAG, "GROUP LIST: startActivity()");
                startActivity(intent);
            }
        });



        // long click:
        _listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group)(parent.getAdapter().getItem(position));
                dbHandler.removeGroup(group.id());
                refreshGroupList();
                return true;
            }
        });

    }


}
