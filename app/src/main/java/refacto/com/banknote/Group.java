package refacto.com.banknote;

public class Group {

    private int _id;
    private String _name;
    private boolean _is_profit;
    boolean is_active;

    public Group(String name, boolean is_profit) {
        _id = -1;
        _name = name;
        _is_profit = is_profit;
        is_active = true;
    }

    public Group(int id, String name, boolean is_profit) {
        _id = id;
        _name = name;
        _is_profit = is_profit;
        is_active = true;
    }

    public int id() { return _id; }
    public String name() { return _name; }
    public boolean isProfit() { return _is_profit; }
    public boolean active() { return is_active; }

    public void name(String text) { _name = text; }
    public void isProfit(boolean value) { _is_profit = value; }
    public void active(boolean IsActive) { is_active = IsActive; }
}
