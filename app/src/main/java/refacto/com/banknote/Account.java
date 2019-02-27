package refacto.com.banknote;

enum AccountType {
    VISA, MASTERCARD, CASH, MAESTRO, DEPOSIT, PIGGY
}

public class Account {

    private int _id;
    private String _name;
    private String _bank;
    private float _value;
    private AccountType _type;
    private String _smsNumber;
    private boolean is_active;

    public Account(String name, String bank, AccountType type, String smsNumber, float value) {
        _id = -1;
        _name = name;
        _bank = bank;
        _type = type;
        _smsNumber = smsNumber;
        _value = value;
        is_active = true;
    }

    public Account(int id, String name, String bank, AccountType type, String smsNumber, float value) {
        _id = id;
        _name = name;
        _bank = bank;
        _type = type;
        _smsNumber = smsNumber;
        _value = value;
        is_active = true;
    }


    public int id() { return _id; }
    public String name() { return _name; }
    public String bank() { return _bank; }
    public AccountType accountType() { return _type; }
    public String smsNumber() { return _smsNumber; }
    public float value() { return _value; }
    public boolean active() { return is_active; }


    public void name(String name) { _name = name; }
    public void bank(String bankName) { _bank = bankName; }
    public void accountType(AccountType type) { _type = type; }
    public void smsNumber(String smsNumber) { _smsNumber = smsNumber; }
    public void value(float value) { _value = value; }
    public void active(boolean IsActive) { is_active = IsActive; }

    public float plus(float value) {
        _value += value;
        return _value;
    }

    public float minus(float value) {
        _value -= value;
        return _value;
    }

    public String str() {
        String typeStr = accountTypeStr(_type);
        return String.valueOf(_id) + ", " + _name + ", " + _bank + ", " + typeStr + ", " + _smsNumber + ", " + _value;
    }

    public static String accountTypeStr(AccountType type) {
        switch(type) {
            case VISA:
                return "VISA";

            case MASTERCARD:
                return "MASTERCARD";

            case CASH:
                return "CASH";

            case MAESTRO:
                return "MAESTRO";

            case DEPOSIT:
                return "DEPOSIT";

            case PIGGY:
                return "PIGGY";

            default:
                return "";
        }
    }

    public static AccountType accountTypeEnum(String value) {
        switch(value) {
            case "VISA":
                return AccountType.VISA;

            case "MASTERCARD":
                return AccountType.MASTERCARD;

            case "CASH":
                return AccountType.CASH;

            case "MAESTRO":
                return AccountType.MAESTRO;

            case "DEPOSIT":
                return AccountType.DEPOSIT;

            case "PIGGY":
                return AccountType.PIGGY;

            default:
                return AccountType.CASH;
        }
    }

}
