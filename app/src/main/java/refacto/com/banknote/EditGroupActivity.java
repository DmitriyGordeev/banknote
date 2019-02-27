package refacto.com.banknote;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class EditGroupActivity extends Activity {

    private static final String LOGTAG = "MyLog-EditGroupAct";

    Group _group;
    DataBaseHandler dbHandler;
    boolean is_edit;

    /* layout objects: */
    EditText editText_groupName;
    CheckBox checkBox_isProfit;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_group_activity);
        dbHandler = new DataBaseHandler(this, "FundachDB");

        editText_groupName = (EditText)findViewById(R.id.editText_groupName);
        checkBox_isProfit = (CheckBox) findViewById(R.id.checkBox_groupIsProfit);

        _group = null;
        is_edit = getIntent().getBooleanExtra("is_edit", false);

        if(is_edit) {
            receiveGroupObject();
            ((Button)findViewById(R.id.button_groupConfirm)).setText("Сохранить");
        }
    }


    public void onGroupConfirm(View v) {

        createGroup();
        Log.i(LOGTAG, "CREATED GROUP OBJECT: name = " +  _group.name() + " ; is_profit = " + _group.isProfit());

        if(dbHandler == null) { return; }

        if(is_edit)
        {
            int overwritten = dbHandler.set(_group.id(), _group);
            Log.i(LOGTAG, "Overwritten: " + overwritten);
        }
        else {
            dbHandler.put(_group);
        }

        this.finish();
    }


    public void onGroupCancel(View v) {
        Intent intent = new Intent(this, GroupListAcitvity.class);
        startActivity(intent);
    }


    /* create group from layout objects: */
    public void createGroup() {

        String name = editText_groupName.getText().toString();
        boolean is_profit = checkBox_isProfit.isChecked();

        if(_group == null) {
            _group = new Group(name, is_profit);
        }
        else {
            _group = new Group(_group.id(), name, is_profit);
        }
    }


    /* create _account object by received from intent data: */
    private void receiveGroupObject() {

        Log.i(LOGTAG, "EDIT GROUP: receiveGroupObject()");

        int id = getIntent().getIntExtra("id", -1);
        String name = getIntent().getStringExtra("name");
        boolean is_profit = getIntent().getBooleanExtra("is_profit", false);

        Log.i(LOGTAG, "EDIT GROUP: receiveGroupObject() : extras received");

        _group = new Group(id, name, is_profit);

        Log.i(LOGTAG, "EDIT GROUP: receiveGroupObject() : _group created");


        /* assing values for layout objects: */
        editText_groupName.setText(_group.name());
        checkBox_isProfit.setChecked(_group.isProfit());

        Log.i(LOGTAG, "EDIT GROUP: receiveGroupObject() : exit point");

    }



}
