package refacto.com.banknote;


import android.app.Activity;
import android.os.Bundle;

public class EditTransactionActivity extends Activity {

    private static final String LOGTAG = "MyLog-EditTrActivity";
    DataBaseHandler dbHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_transaction_activity);
        dbHandler = new DataBaseHandler(this, "FundachDB");
    }

}
