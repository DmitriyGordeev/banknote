package refacto.com.banknote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Filter;

public class TransactionWideAdapter extends BaseAdapter {

    Context _context;
    LayoutInflater _inflater;
    ArrayList<TransactionWide> _data;
    int _layoutResourceId;

    TransactionWideAdapter(Context context, int layoutResourceId, ArrayList<TransactionWide> data) {
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
    public TransactionWide getItem(int pos) {
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

        TransactionWide tr = _data.get(position);

        // assigning layout:
        if(tr.group == null)
            ((TextView)view.findViewById(R.id.textGroupName)).setText("Нет категории");
        else
            ((TextView)view.findViewById(R.id.textGroupName)).setText(tr.group.name());


        if(tr.account == null)
            ((TextView)view.findViewById(R.id.textAccountName)).setText("Счет не назначен");
        else
            ((TextView)view.findViewById(R.id.textAccountName)).setText(tr.account.name());


        if(tr.tr == null)
            ((TextView)view.findViewById(R.id.textValue)).setText("0.0");
        else
            ((TextView)view.findViewById(R.id.textValue)).setText(String.valueOf(tr.tr.value()) + " \u20BD");


        return view;
    }


    public void filter(String constraint) {
    }
}
