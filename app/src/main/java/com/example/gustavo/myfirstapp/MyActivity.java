package com.example.gustavo.myfirstapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


public class MyActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    private static final String DEBUG_TAG = "HttpExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /** Called when the user clicks the Send button */
    public void loadURL2(View view) {
        try {
            /*URL url = new URL("http://www.oantagonista.com/pagina/1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            //is = conn.getInputStream();
*/

            /*Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse("http://www.oantagonista.com/pagina/1");

            XPathExpression xpath = XPathFactory.newInstance()
                    .newXPath().compile("//td[text()=\"Description\"]/following-sibling::td[2]");

            String result = (String) xpath.evaluate(doc, XPathConstants.STRING);*/
        }
        catch (Exception e)
        {
            Log.d(DEBUG_TAG, "ERRO DOCUMENT HTTP");
        }
    }

    public void loadURL(View view) {
        try {
            new DownloadPage().execute();
        }
        catch (Exception e)
        {
            Log.d(DEBUG_TAG, "ERRO DOCUMENT HTTP BOTAO "+e.toString());
        }
    }

    class DownloadPage extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                //URL url = new URL("http://www.oantagonista.com/pagina/1");
                URL url = new URL("http://www.oantagonista.com/pagina/1601");
                BufferedReader reader = null;
                StringBuilder builder = new StringBuilder();
                try {
                    reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                    for (String line; (line = reader.readLine()) != null; ) {
                        builder.append(line.trim());
                    }
                } finally {
                    if (reader != null) try {
                        reader.close();
                    } catch (IOException logOrIgnore) {
                        Log.d(DEBUG_TAG, "ERRO DOCUMENT HTTP");
                    }
                }

                return builder.toString();
            }
            catch (Exception e)
            {
                Log.d(DEBUG_TAG, "ERRO DOCUMENT HTTP2"+e.toString());
            }

            return "";
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try
            {
                Spanned res = Html.fromHtml(result, null, new MyHtmlTagHandler());

                int len = res.length();
                int next;
                for (int i = 0; i < res.length(); i = next) {
                    next = res.nextSpanTransition(i, len, Object.class);
                    Log.d(DEBUG_TAG, "SpanTransition: " + i);

                    //res, i, next
                    int end = next;
                    int start = i;
                    final int len2 = end - start;
                    char[] buffer = new char[len2];//"pompom".toCharArray();
                    TextUtils.getChars(res, start, end, buffer, 0);
                    String text = String.valueOf(buffer).trim();
                    text = text.replace("\n","-");
                    Log.d(DEBUG_TAG, "SpanTransitionBuffer: " + text);

                    URLSpan[] urls;
                    urls = res.getSpans(i, len, URLSpan.class);
                    if (urls.length > 0)
                    {
                        Log.d(DEBUG_TAG, "SpanTransitionBufferURL: " + urls[0].getURL());
                    }
                }

                /*
                http://stackoverflow.com/questions/19345539/android-breaking-down-a-spanned-object
                Use nextSpanTransition() to find the starting point of the next span.
                The characters between your initial position (first parameter to nextSpanTransition())
                and the next span represent an unspanned portion of text.
                */


                TextView my_text_view = (TextView) findViewById(R.id.my_text_view);
                my_text_view.setText(res);
                my_text_view.setMovementMethod(new ScrollingMovementMethod());
            }
            catch (Exception e)
            {
                Log.d(DEBUG_TAG, "ERRO DOCUMENT HTTP3 " +e.toString() + " " + e.getMessage());
            }
        }
    }

    public static class MyHtmlTagHandler implements Html.TagHandler {
        static int pos;
        static int pos_article;
        static int pos_desc;

        private static <T> Object getLast(Spanned text, Class<T> kind) {
    /*
     * This knows that the last returned object from getSpans()
     * will be the most recently added.
     */
            Object[] objs = text.getSpans(0, text.length(), kind);

            if (objs.length == 0) {
                return null;
            } else {
                return objs[objs.length - 1];
            }
        }

        private static void start(SpannableStringBuilder text, Object mark) {
            int len = text.length();
        }

        private static <T> void end(SpannableStringBuilder text, Class<T> kind,
                                    Object repl) {
            int len = text.length();
            Object obj = getLast(text, kind);
            int where = text.getSpanStart(obj);

            text.removeSpan(obj);

            if (where != len) {
                text.setSpan(repl, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

            /*if(tag.equalsIgnoreCase("article") || tag.equalsIgnoreCase("section")) {
                if (opening) {
                    start((SpannableStringBuilder) output, new Pom());
                } else {
                    end((SpannableStringBuilder) output, Pom.class, new StrikethroughSpan());
                }
            }*/
        }
    }

    public static class Pom{};
    //public native int pom();

}

