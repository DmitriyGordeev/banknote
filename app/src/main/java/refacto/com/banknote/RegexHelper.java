package refacto.com.banknote;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexHelper {
    public static ArrayList<String> findMatches(String data, String regex) {
        ArrayList<String> matches = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(data);

        while(m.find()) {
            matches.add(m.group());
        }

        return matches;
    }
    public static String singleMatch(String input, String regex) {
        ArrayList<String> matches = findMatches(input, regex);
        if(!matches.isEmpty())
            return matches.get(0);
        return "";
    }
}

