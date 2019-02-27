package refacto.com.banknote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOGTAG = "MyLog-MainActivity";
    public static final String BROADCAST = "com.example.d_gordeev.smsonly.android.action.broadcast";

    ListView lv;
    TransactionWideAdapter adapter;
    DataBaseHandler dbHandler;
    private SwitchCompat _smsServiceSwitcher;
    TextView textView_dayConsumeValue;
    TextView textView_restValue;
    HorizontalScrollView hscrollView_filterStroke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /* initialize layout objects: */
        lv = (ListView)findViewById(R.id.listView);
        dbHandler = new DataBaseHandler(this, "FundachDB");
        dbHandler.defaultPatterns(dbHandler.getWritableDatabase());
        _smsServiceSwitcher = (SwitchCompat) findViewById(R.id.drawer_sms_switch);
        textView_dayConsumeValue = (TextView) findViewById(R.id.textView_dayConsumeValue);
        textView_restValue = (TextView) findViewById(R.id.textView_restValue);
        hscrollView_filterStroke = (HorizontalScrollView)findViewById(R.id.hscrollView_filterStroke);

        handleListInteractions();

        /* handle sms switcher action: */
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);
        _smsServiceSwitcher = (SwitchCompat)actionView.findViewById(R.id.drawer_sms_switch);
        _smsServiceSwitcher.setChecked(FService.isActive);
        _smsServiceSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_smsServiceSwitcher.isChecked()) {
                    onStart(v);
                    Snackbar.make(v, "Разбор смс включен", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                }
                else {
                    onStop(v);
                    Snackbar.make(v, "Разбор смс выключен", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        processSmsQueue();
        refreshList();
        super.onResume();

        float totalValue = dbHandler.totalAccountsValue();
        textView_restValue.setText(String.valueOf(totalValue) + " \u20BD");

        float consumeValue = dbHandler.calcTotalConsume();
        textView_dayConsumeValue.setText(String.valueOf(consumeValue) + " \u20BD");
    }

    public void onStart(View v) {
        Intent serviceIntent = new Intent(this, FService.class);
        startService(serviceIntent);
    }

    public void onStop(View v) {
        stopService(new Intent(this, FService.class));
    }

    public void onAddTransactionClick(View v) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }

    /* refresh main list of transactions from db: */
    void refreshList() {
        ArrayList<TransactionWide> trs = dbHandler.pullTrWide();
        adapter = new TransactionWideAdapter(getApplicationContext(), R.layout.transaction_list_item, trs);
        lv.setAdapter(adapter);
    }

    /* pull queue of incame sms from broadcast receiver
     and send to TransactionParser: */
    void processSmsQueue() {

        ArrayList<SmsData> smsQueue = IncomingSms.consumeQueue();
        Log.i(LOGTAG, "smsQueue.size() = " + smsQueue.size());

        if(smsQueue == null || smsQueue.isEmpty()) {
            return;
        }

        // consume entire queue and process each message:
        for(SmsData smsData : smsQueue)
        {
            String message = smsData.getMessage();
            Transaction tr = selectRightPattern(message);

            if(tr != null) {
                dbHandler.put(tr);
                Log.i(LOGTAG, "parsed sucessfully");
            }
        }
    }

    private void addTr_test() {
        Date date = new Date(2017, 12, 1);
        Time time = new Time(12,12,12);

        Transaction tr = new Transaction(date, time, 0, 1, 200, "Comment");
        dbHandler.put(tr);
    }

    /* parse patterns from database: */
    private ArrayList<TransactionPattern> loadTransactionPatterns()
    {
        Log.i(LOGTAG, "ERROR TRACE: entry loadTransactionPatterns()");

        ArrayList<TransactionPattern> patterns = new ArrayList<>();
        ArrayList<PatternInfo> infos = dbHandler.pullPatterns();

        Log.i(LOGTAG, "ERROR TRACE: loadTransactionPatterns() pullPatterns()");

        for(PatternInfo pi : infos) {
            Log.i(LOGTAG, "PATTERN_INFO: " + pi._smsSender + " ; " + pi._smsExample);

            TransactionPattern p = new TransactionPattern();
            if(p.parseJson(pi._jsonContent))
                patterns.add(p);
        }
        return patterns;
    }

    private void handleListInteractions() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddTransactionActivity.class);

                Object object = parent.getAdapter().getItem(position);
                TransactionWide trWide = (TransactionWide)object;

                Group group = trWide.group;
                Account account = trWide.account;

                intent.putExtra("is_edit", true);
                intent.putExtra("tr_id", trWide.tr.id());

                intent.putExtra("group_id", group.id());
                intent.putExtra("groupname", group.name());

                intent.putExtra("account_id", account.id());
                intent.putExtra("accountname", account.name());

                intent.putExtra("value", trWide.tr.value());

                startActivity(intent);
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });
    }

    /*
     select right pattern for incame sms message
     and form Transaction Object
     if no suitable pattern, returns null reference
    TODO: filtering of successfully parsed sms
    TODO: and select most valueable
     */
    private Transaction selectRightPattern(String smsMessage) {
        ArrayList<TransactionPattern> patterns = loadTransactionPatterns();
        for(TransactionPattern p : patterns)
        {
            p.parseSms(smsMessage);
            Transaction tr = p.formTransaction();
            if(tr != null) {
                Log.i(LOGTAG, "pattern selected!");
                return tr;
            }

        }
        return null;
    }

}
