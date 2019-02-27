package refacto.com.banknote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

class PatternInfo
{
    PatternInfo(String jsonContent, String sender, String example) {
        _jsonContent = jsonContent;
        _smsSender = sender;
        _smsExample = example;
    }

    String _jsonContent;
    String _smsSender;
    String _smsExample;
}

public class DataBaseHandler extends SQLiteOpenHelper {

    private String db_name;
    private final static String LOGTAG = "MyLog-DataBaseHandler";

    public DataBaseHandler(Context context, String name) {
        super(context, name, null, 1);
        db_name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table groups (" +
                "id integer primary key autoincrement," +
                "name text, " +
                "is_profit boolean," +
                "is_active boolean)");

        db.execSQL("create table accounts (" +
                "id integer primary key autoincrement," +
                "name text, " +
                "bank text," +
                "value float(2)," +
                "type text, " +
                "smskey text," +
                "is_active boolean)");

        db.execSQL("create table transactions (" +
                "id integer primary key autoincrement," +
                " accountId integer," +
                " groupId integer," +
                " dateval text," +
                " timeval text," +
                " value float(2)," +
                " comment text)");

        db.execSQL("create table patterns (" +
                "id integer primary key autoincrement," +
                "json text," +
                "sms_sender text," +
                "sms_example text)");

        /* fills groups and accounts tables with initial values
         * at database creation: */
//        defaultGroups(db);
//        defaultAccounts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /* calculates total value from each account: */
    public float totalAccountsValue() {
        float value = 0;
        ArrayList<Account> accounts = pullAccounts();
        for(Account a : accounts) {
            value += a.value();
        }
        return value;
    }
    public float calcTotalConsume() {
        float summ = 0;
        ArrayList<Transaction> transactions = pullTransactions();
        for(Transaction t : transactions) {
            summ += t.value();
        }
        return summ;
    }

    /* put methods: */
    public void put(Group group) {
        Log.i(LOGTAG, "started put(Group)");

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", group.name());
        cv.put("is_profit", group.isProfit());
        cv.put("is_active", group.active());

        db.insert("groups", null, cv);
        db.close();
    }
    public void put(SQLiteDatabase db, Group group) {
        ContentValues cv = new ContentValues();
        cv.put("name", group.name());
        cv.put("is_profit", group.isProfit());
        cv.put("is_active", group.active());
        db.insert("groups", null, cv);
    }
    public void put(Account account) {
        Log.i(LOGTAG, "started put(Account)");

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", account.name());
        cv.put("bank", account.bank());
        cv.put("type", Account.accountTypeStr(account.accountType()));
        cv.put("smskey", account.smsNumber());
        cv.put("value", account.value());
        cv.put("is_active", account.active());

        db.insert("accounts", null, cv);
        db.close();

        Log.i(LOGTAG, "finished put(Account)");
    }
    public void put(SQLiteDatabase db, Account account) {
        ContentValues cv = new ContentValues();
        cv.put("name", account.name());
        cv.put("bank", account.bank());
        cv.put("type", Account.accountTypeStr(account.accountType()));
        cv.put("smskey", account.smsNumber());
        cv.put("value", account.value());
        cv.put("is_active", account.active());

        db.insert("accounts", null, cv);
    }
    public void put(Transaction transaction) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("accountId", transaction.accountId());
        cv.put("groupId", transaction.groupId());
        cv.put("dateval", transaction.date().toString());
        cv.put("timeval", transaction.time().toString());
        cv.put("value", transaction.value());
        cv.put("comment", transaction.comment());


        Account account = getAccount(transaction.accountId());
        if(account != null) {
            account.value(account.value() - transaction.value());
            set(transaction.accountId(), account);
        } else {
            return;
        }


        db.insert("transactions", null, cv);
        db.close();
    }
    public void put(TransactionPattern pattern, String smsSender, String smsExample) {
        Log.i(LOGTAG, "started put(TransactionPattern)");

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("json", pattern.json());
        cv.put("sms_sender", smsSender);
        cv.put("sms_example", smsExample);

        db.insert("patterns", null, cv);
        db.close();

        Log.i(LOGTAG, "finished put(TransactionPattern)");
    }
    public void put(SQLiteDatabase db, TransactionPattern pattern, String smsSender, String smsExample) {
        Log.i(LOGTAG, "started put(SQLiteDatabase, TransactionPattern)");

        ContentValues cv = new ContentValues();
        cv.put("json", pattern.json());
        cv.put("sms_sender", smsSender);
        cv.put("sms_example", smsExample);
        db.insert("patterns", null, cv);

        Log.i(LOGTAG, "finished put(SQLiteDatabase, TransactionPattern)");
    }


    /* retreive methods: */
    public ArrayList<Group> pullGroups() {

        ArrayList<Group> output = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("groups", null, null, null, null, null, null);

        if(c.moveToFirst())
        {
            int idIndex = c.getColumnIndex("id");
            int nameIndex = c.getColumnIndex("name");
            int isProfitIndex = c.getColumnIndex("is_profit");

            do {
                Group g = new Group(c.getInt(idIndex), c.getString(nameIndex), c.getInt(isProfitIndex) > 0);
                output.add(g);
            }
            while (c.moveToNext());
        }

        return output;
    }
    public ArrayList<Account> pullAccounts() {

        ArrayList<Account> output = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("accounts", null, null, null, null, null, null);

        if(c.moveToFirst())
        {
            int idIndex = c.getColumnIndex("id");
            int nameIndex = c.getColumnIndex("name");
            int bankIndex = c.getColumnIndex("bank");
            int typeIndex = c.getColumnIndex("type");
            int smsIndex = c.getColumnIndex("smskey");
            int valueIndex = c.getColumnIndex("value");

            do {
                String typeStr = c.getString(typeIndex);
                AccountType type = Account.accountTypeEnum(typeStr);

                Account a = new Account(c.getInt(idIndex), c.getString(nameIndex), c.getString(bankIndex), type, c.getString(smsIndex), c.getFloat(valueIndex));
                output.add(a);
            }
            while (c.moveToNext());
        }

        return output;
    }
    public ArrayList<Transaction> pullTransactions() {

        ArrayList<Transaction> output = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("transactions", null, null, null, null, null, null);

        if(c.moveToFirst())
        {
            int idIndex = c.getColumnIndex("id");
            int accountIdIndex = c.getColumnIndex("accountId");
            int groupIdIndex = c.getColumnIndex("groupId");
            int dateIndex = c.getColumnIndex("dateval");
            int timeIndex = c.getColumnIndex("timeval");
            int valueIndex = c.getColumnIndex("value");
            int commentIndex = c.getColumnIndex("comment");

            do {
                Date date = new Date(2017, 5, 12);
                Time time = new Time(17, 23, 15);

                Transaction tr = new Transaction(c.getInt(idIndex),
                        date,
                        time,
                        c.getInt(accountIdIndex),
                        c.getInt(groupIdIndex),
                        c.getFloat(valueIndex),
                        c.getString(commentIndex));

                output.add(tr);
            }
            while (c.moveToNext());
        }

        return output;
    }
    public ArrayList<PatternInfo> pullPatterns() {

        ArrayList<PatternInfo> output = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("patterns", null, null, null, null, null, null);

        if(c.moveToFirst())
        {
            int jsonIndex = c.getColumnIndex("json");
            int sms_senderIndex = c.getColumnIndex("sms_sender");
            int sms_exampleIndex = c.getColumnIndex("sms_example");
            do {
                output.add(new PatternInfo(c.getString(jsonIndex), c.getString(sms_senderIndex), c.getString(sms_exampleIndex)));
            }
            while (c.moveToNext());
        }

        return output;
    }


    /* TransactionWide retreive methods: */
    public ArrayList<TransactionWide> pullTrWide() {

        ArrayList<TransactionWide> output = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("transactions", null, null, null, null, null, null);

        if(c.moveToFirst())
        {
            int idIndex = c.getColumnIndex("id");
            int accIndex = c.getColumnIndex("accountId");
            int grIndex = c.getColumnIndex("groupId");
            int dateIndex = c.getColumnIndex("dateval");
            int timeIndex = c.getColumnIndex("timeval");
            int valIndex = c.getColumnIndex("value");
            int commIndex = c.getColumnIndex("comment");

            do {

                // retreive related group:
                int groupId = c.getInt(grIndex);
                Group g = getGroup(groupId);


                // retreive related account:
                int accountId = c.getInt(accIndex);
                Account a = getAccount(accountId);


                /// fake Date and Time:
                Date date = new Date(2017, 12, 1);
                Time time = new Time(12, 12, 12);


                Transaction tr = new Transaction(c.getInt(idIndex),
                        date,
                        time,
                        accountId,
                        groupId,
                        c.getFloat(valIndex),
                        c.getString(commIndex));


                TransactionWide trWide = new TransactionWide();
                trWide.tr = tr;
                trWide.account = a;
                trWide.group = g;

                output.add(trWide);
            }
            while (c.moveToNext());
        }

        return output;
    }
    public TransactionWide pullTrWide(int index) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("transactions", null, null, null, null, null, null);

        if(c.moveToPosition(index)) {
            int idIndex = c.getColumnIndex("id");
            int accIndex = c.getColumnIndex("accountId");
            int grIndex = c.getColumnIndex("groupId");
            int dateIndex = c.getColumnIndex("dateval");
            int timeIndex = c.getColumnIndex("timeval");
            int valIndex = c.getColumnIndex("value");
            int commIndex = c.getColumnIndex("comment");


            // retreive related group:
            int groupId = c.getInt(grIndex);
            Group g = getGroup(groupId);


            // retreive related account:
            int accountId = c.getInt(accIndex);
            Account a = getAccount(accountId);


            // retreived transaction ( the rest ):
            /// fake Date and Time:
            Date date = new Date(2017, 12, 1);
            Time time = new Time(12, 12, 12);


            Transaction tr = new Transaction(c.getInt(idIndex),
                    date,
                    time,
                    accountId,
                    groupId,
                    c.getFloat(valIndex),
                    c.getString(commIndex));


            TransactionWide trWide = new TransactionWide();
            trWide.tr = tr;
            trWide.account = a;
            trWide.group = g;

            return trWide;
        }

        return null;
    }


    /* retreive by Id methods: */
    public Group getGroup(int index) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from groups where id=" + index, null);

        if(c.moveToFirst()) {

            int idIndex = c.getColumnIndex("id");
            int nameIndex = c.getColumnIndex("name");
            int isProfitIndex = c.getColumnIndex("is_profit");

            return new Group(c.getInt(idIndex), c.getString(nameIndex), c.getInt(isProfitIndex) > 0);
        }
        return null;
    }
    public Account getAccount(int index) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from accounts where id=" + index, null);

        if(c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int nameIndex = c.getColumnIndex("name");
            int bankIndex = c.getColumnIndex("bank");
            int accType = c.getColumnIndex("type");
            int smsKey = c.getColumnIndex("smskey");
            int valueKey = c.getColumnIndex("value");


            return new Account(c.getInt(idIndex),
                    c.getString(nameIndex),
                    c.getString(bankIndex),
                    Account.accountTypeEnum(c.getString(accType)),
                    c.getString(smsKey), c.getFloat(valueKey));
        }
        return null;
    }
    public String getPattern(int index) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from patterns where id=" + index, null);

        if(c.moveToFirst()) {
            int jsonIndex = c.getColumnIndex("json");
            return c.getString(jsonIndex);
        }
        return "";
    }


    // TODO: MUST BE TESTED !!!
    /* set specific row methods: */
    public int set(int index, Group group) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", group.name());
        cv.put("is_profit", group.isProfit());

        return db.update("groups", cv, "id=" + index, null);
    }
    public int set(int index, Account account) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", account.name());
        cv.put("bank", account.bank());
        cv.put("type", Account.accountTypeStr(account.accountType()));
        cv.put("smskey", account.smsNumber());
        cv.put("value", account.value());

        return db.update("accounts", cv, "id=" + index, null);
    }
    public int set(int index, Transaction transaction) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("accountId", transaction.accountId());
        cv.put("groupId", transaction.groupId());
        cv.put("dateval", transaction.date().toString());
        cv.put("timeval", transaction.time().toString());
        cv.put("value", transaction.value());
        cv.put("comment", transaction.comment());

        return db.update("transactions", cv, "id=" + index, null);
    }
    public int set(int index, PatternInfo patternInfo) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("json", patternInfo._jsonContent);
        cv.put("sms_sender", patternInfo._smsSender);
        cv.put("sms_example", patternInfo._smsExample);

        return db.update("patterns", cv, "id=" + index, null);
    }


    // TODO: MUST BE TESTED !!!
    /* remove specific row by index: */
    public boolean removeGroup(int index) {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete("groups", "id=" + index, null) > 0;
    }
    public boolean removeAccount(int index) {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete("accounts", "id=" + index, null) > 0;
    }
    public boolean removeTransaction(int index) {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete("transactions", "id=" + index, null) > 0;
    }
    public boolean removePatternInfo(int index) {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete("patterns", "id=" + index, null) > 0;
    }


    // --------------------------------------------------------------------------------
    /* fill Groups and Accounts tables with initial objects
     * when application starts first time:
     */
    public void defaultGroups(SQLiteDatabase db) {
        put(db, new Group("job", true));
        put(db, new Group("food", false));
        put(db, new Group("goods", false));
        put(db, new Group("fun", false));
        put(db, new Group("internet", false));
    }

    public void defaultAccounts(SQLiteDatabase db) {
        put(db, new Account("cash", "", AccountType.CASH, "", 0));
    }


    /// FOR TEST PURPOSES:
    public void defaultPatterns(SQLiteDatabase db) {

        // Mock TransactionPattern object for alphabank:
        TransactionElement costElement = new TransactionElement("Summa:\\s[0-9]+,[0-9]{0,2}\\sRUR", "[0-9]+,[0-9]{0,2}");
        TransactionElement balanceElement = new TransactionElement("Ostatok:\\s[0-9]+,[0-9]{0,2}\\sRUR", "[0-9]+,[0-9]{0,2}");
        TransactionElement cardElement = new TransactionElement("^[0-9A-Z\\*]{4,10};\\s", "[0-9A-Z\\*]{4,10}");
        TransactionElement sourceElement = new TransactionElement();
        sourceElement._regexRemovals.add("Uspeshno;");
        sourceElement._regexRemovals.add("Pokupka;");
        sourceElement._regexRemovals.add(";");
        sourceElement._regexRemovals.add("[0-9]{2}\\.[0-9]{2}\\.[0-9]{2,4}");
        sourceElement._regexRemovals.add("[0-9]{2}:[0-9]{2}:[0-9]{2}");

        TransactionPattern pattern = new TransactionPattern();
        pattern._costElement = costElement;
        pattern._balanceElement = balanceElement;
        pattern._cardName = cardElement;
        pattern._commentElement = sourceElement;

        put(db, pattern, "Sms Sender example, 900", "Sms message example");
    }

}
