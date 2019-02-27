package refacto.com.banknote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AccountAdapter extends BaseAdapter {

    Context _context;
    LayoutInflater _inflater;
    ArrayList<Account> _data;
    int _layoutResourceId;

    AccountAdapter(Context context, int layoutResourceId, ArrayList<Account> data) {
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
    public Account getItem(int pos) {
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

        Account a = _data.get(position);

        // assigning layout:
        ((TextView)view.findViewById(R.id.textAccountName)).setText(a.name());
        ((TextView)view.findViewById(R.id.textView_bankName)).setText(a.bank());
        ((TextView)view.findViewById(R.id.textView_value)).setText(String.valueOf(a.value()));
        ((TextView)view.findViewById(R.id.textView_accountType)).setText(Account.accountTypeStr(a.accountType()));

        return view;
    }
}
