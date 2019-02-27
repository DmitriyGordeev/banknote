package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class EditAccountActivity extends Activity {

    private static final String LOGTAG = "MyLog-EditAccActivity";
    Account _account;

    EditText editText_name;
    EditText editText_bank;
    EditText editText_value;
    Spinner  spinner_type;
    EditText editText_smsNumber;

    DataBaseHandler dbHandler;
    boolean is_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_activity);

        dbHandler = new DataBaseHandler(this, "FundachDB");

        /* setup layout objects: */
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_bank = (EditText) findViewById(R.id.editText_bank);
        editText_value = (EditText) findViewById(R.id.editText_value);
        spinner_type = (Spinner) findViewById(R.id.spinner_accountType);
        editText_smsNumber = (EditText) findViewById(R.id.editText_smsNumber);

        _account = null;
        is_edit = getIntent().getBooleanExtra("is_edit", false);

        if (is_edit) {
            receiveAccountObject();
            ((Button)findViewById(R.id.button_addAccount)).setText("Сохранить");
        }

        assignSpinnerValues();

    }

    public void onAccountConfirm(View v) {
        /* overwrite or add value to database: */

        createAccount();
        if(dbHandler == null) { return; }
        if(is_edit)
        {
            int overwritten = dbHandler.set(_account.id(), _account);
            Log.i(LOGTAG, "Overwritten: " + overwritten);
        }
        else {
            dbHandler.put(_account);
        }

        this.finish();
    }

    public void onAccountCancel(View v) {
        Intent intent = new Intent(this, AccountsListActivity.class);
        startActivity(intent);
    }


    /* create account from layout objects: */
    public void createAccount() {
        String name = editText_name.getText().toString();
        String bank = editText_bank.getText().toString();
        float value = 0;
        try {
            value = Float.parseFloat(editText_value.getText().toString());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        String typeString = (String)spinner_type.getSelectedItem();
        AccountType type = Account.accountTypeEnum(typeString);
        String smsNumber = editText_smsNumber.getText().toString();

        if(_account == null) {
            _account = new Account(name, bank, type, smsNumber, value);
        }
        else {
            _account = new Account(_account.id(), name, bank, type, smsNumber, value);
        }
    }


    /* create _account object by received from intent data: */
    private void receiveAccountObject() {

        int id = getIntent().getIntExtra("id", -1);
        Log.i(LOGTAG, "RECEIVED ID = " + id);
        String name = getIntent().getStringExtra("name");
        String bank = getIntent().getStringExtra("bank");
        AccountType type = Account.accountTypeEnum(getIntent().getStringExtra("type"));
        float value = getIntent().getFloatExtra("value", 0);
        String smsNumber = getIntent().getStringExtra("smsNumber");

        _account = new Account(id, name, bank, type, smsNumber, value);

        /* assing values for layout objects: */
        editText_name.setText(_account.name());
        editText_bank.setText(_account.bank());
        editText_value.setText(String.valueOf(_account.value()));
        editText_smsNumber.setText(_account.smsNumber());
    }


    /* assign Spinner values of possible account types: */
    private void assignSpinnerValues() {

        ArrayList<String> values = new ArrayList<>();
        values.add(Account.accountTypeStr(AccountType.CASH));
        values.add(Account.accountTypeStr(AccountType.VISA));
        values.add(Account.accountTypeStr(AccountType.MASTERCARD));
        values.add(Account.accountTypeStr(AccountType.MAESTRO));
        values.add(Account.accountTypeStr(AccountType.DEPOSIT));
        values.add(Account.accountTypeStr(AccountType.PIGGY));


        ArrayAdapter<String> adp= new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, values);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adp);
    }

}
