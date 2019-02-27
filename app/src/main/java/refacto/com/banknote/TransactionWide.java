package refacto.com.banknote;

public class TransactionWide {

    public Transaction tr;
    public Group group;
    public Account account;

    public String str() {
        if(group != null && account != null)
            return "value: " + tr.value() + " , group: " + group.name() + ", account name: " + account.name();
        return "";
    }
}
