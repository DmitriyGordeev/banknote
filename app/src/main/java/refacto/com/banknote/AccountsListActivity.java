package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class AccountsListActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener {

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


        Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_accounts) {

            Intent intent = new Intent(this, AccountsListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_patterns) {

            Intent intent = new Intent(this, SelectPatternActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_groups) {

            Intent intent = new Intent(this, GroupListAcitvity.class);
            startActivity(intent);
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
