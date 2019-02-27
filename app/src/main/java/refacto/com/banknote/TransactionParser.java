package refacto.com.banknote;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

class TransactionElement {

    TransactionElement() {
        _regexOuter = ".*";
        _regexInner = ".*";
        _outerFound = "";
        _innerFound = "";
        _regexRemovals = new ArrayList<>();
    }
    TransactionElement(String regexOuter, String regexInner) {
        _regexOuter = regexOuter;
        _regexInner = regexInner;
        _outerFound = "";
        _innerFound = "";
        _regexRemovals = new ArrayList<>();
    }

    public String _regexOuter;
    public String _regexInner;

    /* regexes for remove: */
    public ArrayList<String> _regexRemovals;

    public String _outerFound;
    public String _innerFound;

    /* main parse method: */
    public String parse(String input) {

        // search and remove outer substring:
        _outerFound = RegexHelper.singleMatch(input, _regexOuter);
        if(!_outerFound.isEmpty())
            input = input.replace(_outerFound, "");

        // search inner substring in found outer:
        _innerFound = RegexHelper.singleMatch(_outerFound, _regexInner);
        for(String r : _regexRemovals)
        {
            if(!r.isEmpty()) {
                String trash = RegexHelper.singleMatch(_innerFound, r);
                _innerFound = _innerFound.replace(trash, "");
            }
        }

        _innerFound = _innerFound.trim();
        return input;
    }

    /* json serialization */
    public JSONObject json() {
        JSONObject object = new JSONObject();
        try {
            object.put("regexInner", _regexInner);
            object.put("regexOuter", _regexOuter);

            JSONArray jsonArray = new JSONArray();
            for(String s : _regexRemovals)
                jsonArray.put(s);
            object.put("regexRemovals", jsonArray);
        }
        catch (JSONException e) { e.printStackTrace(); }
        return object;
    }

    /* json parse: */
    public boolean parseJson(JSONObject object) {
        try {
            _regexInner = object.getString("regexInner");
            _regexOuter = object.getString("regexOuter");

            JSONArray arr = object.getJSONArray("regexRemovals");
            for(int i = 0; i < arr.length(); i++)
                _regexRemovals.add(arr.getString(i));
        }
        catch(JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

class TransactionPattern {

    public final static String LOGTAG = "MyLog-TrPattern";

    public TransactionPattern() {
        _cardName = new TransactionElement();
        _costElement = new TransactionElement();
        _balanceElement = new TransactionElement();
        _commentElement = new TransactionElement();
    }

    public TransactionElement _cardName;
    public TransactionElement _costElement;
    public TransactionElement _balanceElement;
    public TransactionElement _commentElement;

    public String parseSms(String input) {
        input = _costElement.parse(input);
        input = _balanceElement.parse(input);
        input = _cardName.parse(input);
        input = _commentElement.parse(input);
        return input;
    }

    /* return null object if failed: */
    public Transaction formTransaction() {

        Date date = new Date(2017, 12, 1);
        Time time = new Time(12, 12, 12);

        float cost = 0;
        try {
            _costElement._innerFound = _costElement._innerFound.replace(',','.');
            cost = Float.parseFloat(_costElement._innerFound);
        } catch(Exception e) {
            Log.i(LOGTAG, "formTransaction() : unable to parse cost from : " + _costElement._innerFound);
            return null;
        }

        float balance = 0;
        try {
            _balanceElement._innerFound = _balanceElement._innerFound.replace(',','.');
            balance = Float.parseFloat(_balanceElement._innerFound);
        } catch(Exception e) {
            Log.i(LOGTAG, "formTransaction() : unable to parse balance from : " + _balanceElement._innerFound);
            return null;
        }

        Transaction tr = new Transaction(date, time, 1, 1, cost, _commentElement._innerFound);
        return tr;
    }

    /* json serialization: */
    public String json() {
        JSONObject object = new JSONObject();
        try {
            object.put("cardElement", _cardName.json());
            object.put("costElement", _costElement.json());
            object.put("balanceElement", _balanceElement.json());
            object.put("commentElement", _commentElement.json());
        }
        catch(JSONException e) {
            e.printStackTrace();
            return "";
        }

        Log.i(LOGTAG, object.toString());
        return object.toString();
    }

    /* parse json method: */
    public boolean parseJson(String json) {
        try {
            JSONObject object = new JSONObject(json);

            if(!_cardName.parseJson(object.getJSONObject("cardElement")))
                return false;

            if(!_costElement.parseJson(object.getJSONObject("costElement")))
                return false;

            if(!_balanceElement.parseJson(object.getJSONObject("balanceElement")))
                return false;

            if(!_commentElement.parseJson(object.getJSONObject("commentElement")))
                return false;
        }
        catch(JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}