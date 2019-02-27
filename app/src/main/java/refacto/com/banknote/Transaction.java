package refacto.com.banknote;

import java.sql.Date;
import java.sql.Time;

public class Transaction {

    private int _id;
    private Date _date;
    private Time _time;
    private int _accountId;
    private int _groupId;
    private float _value;
    private String _comment;

    public Transaction(Date date, Time time, int account, int group, float value, String comment) {
        _id = -1;
        _date = date;
        _time = time;
        _accountId = account;
        _groupId = group;
        _value = value;
        _comment = comment;
    }

    public Transaction(int id, Date date, Time time, int account, int group, float value, String comment) {
        _id = id;
        _date = date;
        _time = time;
        _accountId = account;
        _groupId = group;
        _value = value;
        _comment = comment;
    }

    public int id() { return _id; }
    public Date date() { return _date; }
    public Time time() { return _time; }
    public int accountId() { return _accountId; }
    public int groupId() { return _groupId; }
    public float value() { return _value; }
    public String comment() { return _comment; }

    public void date(Date date) { _date = date; }
    public void time(Time time) { _time = time; }
    public void accountId(int accountId) { _accountId = accountId; }
    public void groupId(int groupId) { _groupId = groupId; }
    public void value(float value) { _value = value; }
    public void comment(String value) { _comment = value; }

    public String str() {
        return String.valueOf(_id) + ", " +
                _date.toString() + ", " +
                _time.toString() + ", " +
                String.valueOf(_accountId) + ", " +
                String.valueOf(_groupId) + ", " +
                String.valueOf(_value) + ", " +
                _comment;
    }

}
