package refacto.com.banknote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogGroupAdapter extends BaseAdapter {

    Context _context;
    LayoutInflater _inflater;
    ArrayList<Group> _data;
    int _layoutResourceId;

    DialogGroupAdapter(Context context, int layoutResourceId, ArrayList<Group> data) {
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
    public Group getItem(int pos) {
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

        Group g = _data.get(position);

        // assigning layout:
        ((TextView)view.findViewById(R.id.textGroupName)).setText(g.name());
        ((TextView)view.findViewById(R.id.textIsProfit)).setText(String.valueOf(g.isProfit()));

        return view;
    }
}
