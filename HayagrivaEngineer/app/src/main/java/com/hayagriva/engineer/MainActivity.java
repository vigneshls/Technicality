package com.hayagriva.engineer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Hosts the entire game as a local WebView page (assets/index.html, Three.js
 * bundled locally in assets/js -- no network access needed at all).
 *
 * D-pad LEFT/RIGHT/UP/DOWN are intentionally NOT intercepted here. A focused
 * WebView already receives hardware KeyEvents as real, trusted DOM
 * keydown/keyup events -- Android maps those four to the same
 * ArrowLeft/Right/Up/Down keys a browser would report for a keyboard, and
 * the page already has working listeners for exactly those (the same ones
 * used for browser preview/testing). An earlier version of this activity
 * intercepted every key press and replayed it into the page via
 * evaluateJavascript(); that broke the game's opening sound, because a
 * script call injected from native code is never treated as a real user
 * gesture, and Chrome's autoplay policy (which this WebView's engine
 * follows) silently refuses to let a newly-created AudioContext produce any
 * sound outside of one. Letting the real KeyEvent reach the page directly
 * for those four keys fixes that by construction.
 *
 * DPAD_CENTER/ENTER (the loader-arm action) IS still intercepted here,
 * deliberately: which DOM key a WebView reports for the physical "OK/Select"
 * button varies across devices, and on some it can collide with Space
 * (already bound to "dig"). Explicitly forwarding just this one button to
 * window.JCB.loaderAction() avoids that ambiguity. This doesn't reintroduce
 * the audio-gesture problem for the common case, since a curious player
 * almost always presses Up/Left/Right/Down (real, unintercepted gestures)
 * before ever finding the center button.
 */
public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null); // make sure the 3D scene is GPU-rendered, not software

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
        webView.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (event.getRepeatCount() == 0) {
                webView.evaluateJavascript("window.JCB && window.JCB.loaderAction()", null);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
