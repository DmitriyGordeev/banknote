package refacto.com.banknote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {

    Context _context;
    LayoutInflater _inflater;
    ArrayList<Transaction> _data;
    int _layoutResourceId;


    TransactionAdapter(Context context, int layoutResourceId, ArrayList<Transaction> data) {
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
    public Transaction getItem(int pos) {
        return _data.get(pos);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null)
            view = _inflater.inflate(_layoutResourceId, parent, false);

        Transaction tr = _data.get(position);

        // assigning layout values:
        ((TextView)view.findViewById(R.id.textValue)).setText(String.valueOf(tr.value()));
        ((TextView)view.findViewById(R.id.textAccountName)).setText(String.valueOf(tr.accountId()));

        return view;
    }
}



