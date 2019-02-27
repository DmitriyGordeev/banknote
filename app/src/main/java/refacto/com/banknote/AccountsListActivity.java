package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class AccountsListActivity extends Activity {

    ListView _listView;
    AccountAdapter _adapter;
    DataBaseHandler dbHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_list_activity);
        _listView = findViewById(R.id.listView_accountsList);
        dbHandler = new DataBaseHandler(this, "FundachDB");

        handleListInteractions();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAccountList();
    }

    public void onAddAccount(View v) {
        Intent intent = new Intent(this, EditAccountActivity.class);
        intent.putExtra("is_edit", false);
        startActivity(intent);
    }

    private void refreshAccountList() {
        ArrayList<Account> accounts = dbHandler.pullAccounts();
        _adapter = new AccountAdapter(getApplicationContext(), R.layout.dialog_accountlist, accounts);
        _listView.setAdapter(_adapter);
    }

    private void handleListInteractions() {


        // onItem click:
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Account account = (Account)(parent.getAdapter().getItem(position));
                Intent intent = new Intent(getApplicationContext(), EditAccountActivity.class);

                intent.putExtra("is_edit", true);
                intent.putExtra("id", account.id());
                intent.putExtra("name", account.name());
                intent.putExtra("bank", account.bank());
                intent.putExtra("type", Account.accountTypeStr(account.accountType()));
                intent.putExtra("value", account.value());
                intent.putExtra("smsNumber", account.smsNumber());

                startActivity(intent);
            }
        });



        // long click:
        _listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Account account = (Account)(parent.getAdapter().getItem(position));
                dbHandler.removeAccount(account.id());

                refreshAccountList();
                return true;
            }
        });

    }

}
