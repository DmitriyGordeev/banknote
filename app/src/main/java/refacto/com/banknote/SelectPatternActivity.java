package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectPatternActivity extends Activity {

    private static final String LOGTAG = "MyLog-PatternActivity";

    ListView _patternList;
    PatternItemAdapter _adapter;
    DataBaseHandler _dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pattern_activity);

        _dbHandler = new DataBaseHandler(this, "FundachDB");
        _patternList = (ListView)findViewById(R.id.listView_selectPatternList);

        /* on list item click: */
        _patternList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PatternInfo patternInfo = (PatternInfo)_patternList.getItemAtPosition(position);
                Log.i(LOGTAG, patternInfo._smsExample);

                /* start EditPatternActivity: */
                Intent intent = new Intent(getApplicationContext(), EditPatternActivity.class);
                intent.putExtra("sender", patternInfo._smsSender);
                intent.putExtra("example", patternInfo._smsExample);
                intent.putExtra("json", patternInfo._jsonContent);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        refreshPatternList();
        super.onResume();
    }

    public void refreshPatternList() {
        ArrayList<PatternInfo> patterns = _dbHandler.pullPatterns();
        _adapter = new PatternItemAdapter(getApplicationContext(), R.layout.pattern_list_item, patterns);
        _patternList.setAdapter(_adapter);
    }

}
