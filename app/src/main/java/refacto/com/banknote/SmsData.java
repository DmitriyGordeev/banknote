package refacto.com.banknote;

public class SmsData {

    // constructors:
    public SmsData () {}
    public SmsData(String sender, String message)
    {
        _sender = sender;
        _message = message;
    }

    // methods:
    public String getSender() { return _sender; }
    public String getMessage() { return _message; }

    public void setMessage(String msg) {
        _message = msg;
    }
    public void setSender(String sender) {
        _sender = sender;
    }

    // fields:
    private String _sender;
    private String _message;
}
