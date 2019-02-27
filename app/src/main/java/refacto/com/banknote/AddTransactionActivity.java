package refacto.com.banknote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class AddTransactionActivity extends Activity {

    private static final String LOGTAG = "MyLog-AddTrActv";

    DialogGroupAdapter dialogGroupAdapter;
    AccountAdapter accountGroupAdapter;

    private int _selectedGroupId;
    private int _selectedAccountId;

    private String _selectedAccountName;
    private String _selectedGroupName;

    Transaction transaction;
    DataBaseHandler dbHandler;

    EditText editText;
    TextView textViewSelectenGroupName;
    TextView textViewSelectenAccountName;

    boolean is_edit;
    int transaction_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        _selectedGroupId = -1;
        _selectedAccountId = -1;

        _selectedAccountName = "";
        _selectedGroupName = "";

        dbHandler = new DataBaseHandler(this, "FundachDB");
        editText = (EditText)findViewById(R.id.editText_Value);
        textViewSelectenGroupName = (TextView) findViewById(R.id.textSelectedGroupName);
        textViewSelectenAccountName = (TextView) findViewById(R.id.textSelectedAccountName);


        // assigning values if something received via intent:
        is_edit = getIntent().getBooleanExtra("is_edit", false);
        transaction_id = getIntent().getIntExtra("tr_id", -1);
        if(is_edit) {

            _selectedGroupId = getIntent().getIntExtra("group_id", -1);
            _selectedAccountId = getIntent().getIntExtra("account_id", -1);

            String groupname = getIntent().getStringExtra("groupname");
            String accountname = getIntent().getStringExtra("accountname");
            float value = getIntent().getFloatExtra("value", 0);

            textViewSelectenGroupName.setText(groupname);
            textViewSelectenAccountName.setText(accountname);
            editText.setText(String.valueOf(value));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onSelectGroup(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите категорию");
        final ListView modeList = new ListView(this);


        /* get groups from database: */
        ArrayList<Group> groups = new ArrayList<>();
        if(dbHandler != null)
            groups = dbHandler.pullGroups();


        dialogGroupAdapter = new DialogGroupAdapter(getApplicationContext(), R.layout.dialog_grouplist, groups);
        modeList.setAdapter(dialogGroupAdapter);
        builder.setView(modeList);

        final Dialog dialog = builder.create();
        dialog.show();


        /* on item click event: */
        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Group g = (Group)(parent.getAdapter().getItem(position));
                _selectedGroupId = g.id();
                _selectedGroupName = g.name();
                textViewSelectenGroupName.setText(_selectedGroupName);

                Log.i(LOGTAG, "selected group index: " + _selectedGroupId);
                dialog.dismiss();
            }
        });
    }

    public void onSelectAccount(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Account");
        ListView modeList = new ListView(this);


        /* get accounts from database: */
        ArrayList<Account> accounts = new ArrayList<>();
        if(dbHandler != null)
            accounts = dbHandler.pullAccounts();


        accountGroupAdapter = new AccountAdapter(getApplicationContext(), R.layout.dialog_accountlist, accounts);
        modeList.setAdapter(accountGroupAdapter);
        builder.setView(modeList);

        final Dialog dialog = builder.create();
        dialog.show();


        /* on item click event: */
        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Account a = (Account) (parent.getAdapter().getItem(position));
                _selectedAccountId = a.id();
                _selectedAccountName = a.name();
                textViewSelectenAccountName.setText(_selectedAccountName);

                Log.i(LOGTAG, "selected account index: " + _selectedAccountId);
                dialog.dismiss();
            }
        });
    }

    public void onAddTransaction(View v) {

        String valueString = editText.getText().toString();
        float value = 0;
        try {
            value = Float.parseFloat(valueString);
        }
        catch(Exception e) {
            Log.d(LOGTAG, "unable to parse float value from editText");
            Toast.makeText(getApplicationContext(), "Значение нечисловое", Toast.LENGTH_SHORT).show();
            return;
        }

        Date date = new Date(2017, 12, 1);
        Time time = new Time(12, 12, 12);

        if(_selectedAccountId != -1 && _selectedGroupId != -1) {

            if(is_edit) {
                transaction = new Transaction(transaction_id, date, time, _selectedAccountId, _selectedGroupId, value, "");
                dbHandler.set(transaction_id, transaction);

            } else {
                transaction = new Transaction(date, time, _selectedAccountId, _selectedGroupId, value, "");
                dbHandler.put(transaction);
            }
        }
        else if(_selectedAccountId != -1) {
            Toast.makeText(getApplicationContext(), "Не выбран счет", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Toast.makeText(getApplicationContext(), "Не выбрана категория", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onCancelAddTransaction(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
