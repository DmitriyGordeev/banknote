package refacto.com.banknote;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PatternItemAdapter extends BaseAdapter {

    private final static String LOGTAG = "MyLog-PatternAdapter";

    Context _context;
    LayoutInflater _inflater;
    ArrayList<PatternInfo> _data;
    int _layoutResourceId;

    PatternItemAdapter(Context context, int layoutResourceId, ArrayList<PatternInfo> data) {
        _context = context;
        _data = data;
        _layoutResourceId = layoutResourceId;

        _inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return _data.size();
    }


    @Override
    public PatternInfo getItem(int pos) {
        return _data.get(pos);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.i(LOGTAG, "getView() started");

        View view = convertView;
        if(view == null)
            view = _inflater.inflate(_layoutResourceId, parent, false);

        Log.i(LOGTAG, "getView() match 0");

        PatternInfo pi = _data.get(position);

        Log.i(LOGTAG, "getView() match 1");

        // assigning layout:
        ((TextView)view.findViewById(R.id.textView_sender)).setText(pi._smsSender);

        Log.i(LOGTAG, "getView() finished");
        return view;
    }
}
