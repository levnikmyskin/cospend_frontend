package it.edoardo.cospend_frontend;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import it.edoardo.cospend_frontend.http.nextcloud.NextcloudLoginCallback;
import it.edoardo.cospend_frontend.http.nextcloud.NextcloudLoginFinalResponse;
import it.edoardo.cospend_frontend.http.nextcloud.NextcloudLoginHelper;
import it.edoardo.cospend_frontend.http.nextcloud.NextcloudLoginInitResponse;
import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity implements NextcloudLoginCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextInputLayout nextcloudServerInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        nextcloudServerInputLayout = findViewById(R.id.nextcloudInputLayout);

    }

    public void onLoginButtonClicked(View view) {
        EditText editText = nextcloudServerInputLayout.getEditText();
        if (editText == null) {
            Log.println(Log.ERROR, TAG, "Login requested, but edit text is null");
            return;
        }
        String serverStr = editText.getText().toString().trim();
        Log.println(Log.DEBUG, TAG, "Login request, redirecting user to " + serverStr);
        CospendApp app = (CospendApp) getApplication();
        NextcloudLoginHelper ncHelper = new NextcloudLoginHelper(this);
        HttpUrl serverUrl = NextcloudLoginHelper.buildLoginFlowUrl(serverStr);
        if (serverUrl == null) {
            nextcloudServerInputLayout.setError("Invalid URL");
            return;
        }
        app.executorService.execute(() -> ncHelper.loginFlow(serverUrl));
    }

    @Override
    public void onLoginInitSucces(NextcloudLoginInitResponse response) {
        // TODO DELETE THIS
        Log.d(TAG, String.format("Login init success. We got endpoint=%s and token=%s", response.poll.endpoint, response.poll.token));
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
        intent.launchUrl(MainActivity.this, Uri.parse(response.login));
    }

    @Override
    public void onLoginInitFail(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            nextcloudServerInputLayout.setError(msg);
        });
    }

    @Override
    public void onLoginFinalSuccess(NextcloudLoginFinalResponse response) {
        // TODO DELETE THIS
        Log.d(TAG, String.format("Login success. We got login=%s, passw=%s", response.loginName, response.appPassword));

        // TODO: how do we close the browser tab?
        // I guess we can simply redirect the user to the "real" main activity?
        runOnUiThread(() -> Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onLoginFinalFail(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            nextcloudServerInputLayout.setError(msg);
        });
    }
}
