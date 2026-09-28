package com.hayagriva.engineer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Hosts the entire game as a local WebView page (assets/index.html, Three.js
 * bundled locally in assets/js -- no network access needed at all). This
 * activity's only job is to translate real D-pad KeyEvents from the TV
 * remote into calls on the page's window.JCB.* interface, which is the
 * exact same interface the page's own on-screen D-pad (used for browser
 * preview/testing) already calls.
 *
 * Mapping, matching the game's own on-screen control legend:
 *   DPAD_LEFT / DPAD_RIGHT -> turn left / right (held)
 *   DPAD_UP                -> drive forward (held)
 *   DPAD_DOWN / DPAD_CENTER / ENTER -> dig (single press)
 */
public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false); // a D-pad press isn't a "touch gesture"; let audio play anyway
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // The WebView itself is the only view, and it always keeps focus --
        // there's no on-screen focus chain to navigate, since every control
        // is intercepted here as a raw KeyEvent instead.
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
        webView.requestFocus();
    }

    private void runJs(String script) {
        webView.evaluateJavascript(script, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (event.getRepeatCount() == 0) runJs("window.JCB && window.JCB.setTurn(-1)");
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (event.getRepeatCount() == 0) runJs("window.JCB && window.JCB.setTurn(1)");
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (event.getRepeatCount() == 0) runJs("window.JCB && window.JCB.setForward(true)");
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (event.getRepeatCount() == 0) runJs("window.JCB && window.JCB.dig()");
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                runJs("window.JCB && window.JCB.setTurn(0)");
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                runJs("window.JCB && window.JCB.setForward(false)");
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
