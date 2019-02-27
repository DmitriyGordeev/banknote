package refacto.com.banknote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaCodec;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditPatternActivity extends Activity {

    private static final String LOGTAG = "MyLog-EditPatternAct";

    TransactionPattern _pattern;
    String messageExample;
    TextView textView_messageExample;

    /* EditTexts: */
    EditText editText_cardName_regexOuter;
    EditText editText_cardName_regexInner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_pattern_activity);

        /* init layout objects: */
        textView_messageExample = (TextView)findViewById(R.id.textView_smsExample_label);
        editText_cardName_regexOuter = (EditText)findViewById(R.id.editText_cardName_regexOuter);
        editText_cardName_regexInner = (EditText)findViewById(R.id.editText_cardName_regexInner);

        messageExample = "empty";

        /* check if activity was created from SmsMessageList
        then retreive sms message and set to TextView
        */
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            if(extras.containsKey("message")) {
                messageExample = (String)extras.get("message");
            }
        }

        textView_messageExample.setText(messageExample);



        /* -------------------------------------------------------------- */
        /* test span coloring: */

        messageExample = "Бла бла бла Покупка: 65890 руб. Спасибо!";
        textView_messageExample.setText(messageExample);

        testRegexColoring();
    }

    public void onSelectSms(View v) {
        Intent intent = new Intent(this, SmsReader.class);
        startActivity(intent);
    }

    private void testRegexColoring() {

        editText_cardName_regexInner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                final SpannableStringBuilder sb = new SpannableStringBuilder(messageExample);
//
//                // Span to set text color to some RGB value
//                final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(100, 238, 120));
//
//                // Span to make text bold
//                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
//
//
//                try {
//                    Pattern pattern = Pattern.compile(s.toString());
//                    Matcher matcher = pattern.matcher(messageExample);
//
//                    // Set the text color for regex matches:
//                    while (matcher.find()) {
//                        sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                        sb.setSpan(bss, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                    }
//                }
//                catch(Exception e) {
//                    e.printStackTrace();
//                }
//
//                textView_messageExample.setText(sb)

                regexColoringBackground(textView_messageExample, s.toString(), new BackgroundColorSpan(Color.rgb(100, 0, 100)));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });






    }


    private void regexColoringBackground(TextView textView, String regex, BackgroundColorSpan bcs) {
        final SpannableStringBuilder sb = new SpannableStringBuilder(textView.getText());
        sb.removeSpan(textView);

        // Span to make text bold
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(textView.getText());

            // Set the text color for regex matches:
            while (matcher.find()) {
                sb.setSpan(bcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sb.setSpan(bss, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        textView.setText(sb);
    }

}
