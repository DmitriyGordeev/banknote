package refacto.com.banknote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

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

    private Spinner _spinnerGroups;
    private Spinner _spinnerAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        _selectedGroupId = -1;
        _selectedAccountId = -1;

        _selectedAccountName = "";
        _selectedGroupName = "";

        dbHandler = new DataBaseHandler(this, "FundachDB");
        editText = findViewById(R.id.editText_Value);
        textViewSelectenGroupName = findViewById(R.id.textSelectedGroupName);
        textViewSelectenAccountName = findViewById(R.id.textSelectedAccountName);

        /* handle spinner element: */
        _spinnerGroups = findViewById(R.id.spinnerGroups);
        _spinnerAccounts = findViewById(R.id.spinnerAccounts);

        ArrayList<Group> groups = dbHandler.pullGroups();
        ArrayList<Account> accounts = dbHandler.pullAccounts();

        final List<String> groupNames = new ArrayList<>();
        final List<String> accountNames = new ArrayList<>();

        for(Group g : groups) {
            groupNames.add(g.name());
        }

        for(Account a : accounts) {
            accountNames.add(a.name());
        }

        // Put values inside groups spinner:
        ArrayAdapter<String> groupsDataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, groupNames);
        groupsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerGroups.setAdapter(groupsDataAdapter);



        /* handle spinner group selection selection */
        _spinnerGroups.setSelection(-1, false);
        _spinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textViewSelectenGroupName.setText(groupNames.get(position));
                _selectedGroupId = position + 1;
                _selectedGroupName = groupNames.get(position);
                Log.i("onItemSelected", "onItemSelected: ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // Put values inside accounts spinner:
        ArrayAdapter<String> accountsDataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, accountNames);
        accountsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerAccounts.setAdapter(accountsDataAdapter);

        _spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textViewSelectenAccountName.setText(accountNames.get(position));
                _selectedAccountId = position + 1;
                _selectedAccountName = accountNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



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
