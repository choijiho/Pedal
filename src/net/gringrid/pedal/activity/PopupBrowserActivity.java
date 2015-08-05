package net.gringrid.pedal.activity;

import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.SharedData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PopupBrowserActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_popup_browser);
	    String url = getIntent().getExtras().getString("URL");
	    initView(url);
	}

	private void initView(String url) {
		WebView id_wv = (WebView)findViewById(R.id.id_wv);
		id_wv.getSettings().setJavaScriptEnabled(true);
		id_wv.addJavascriptInterface(this, "AndroidFunction");
		id_wv.setWebViewClient(new WebViewClient());
		id_wv.loadUrl(url);
	}
	
    public void setOAuthResult(String userId, String email, String aceessToken){
    	SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_STRAVA_USER_ID, userId);
    	SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_STRAVA_EMAIL, email);
    	SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_STRAVA_ACCESS_TOKEN, aceessToken);
    	Toast.makeText(this, "Strava Authentication Complete.", Toast.LENGTH_SHORT).show();
    	finish();
    }
    	
}
