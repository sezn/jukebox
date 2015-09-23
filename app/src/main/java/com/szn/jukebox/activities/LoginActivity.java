package com.szn.jukebox.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.szn.jukebox.JukeApplication;
import com.szn.jukebox.R;
import com.szn.jukebox.fragments.LoginFragment;
import com.szn.jukebox.interfaces.LoginListener;



public class LoginActivity extends AppCompatActivity implements LoginListener {


    private static final String TAG = LoginActivity.class.getSimpleName();
    private Toolbar toolbar;
    private Fragment fragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (toolbar != null)
            toolbar.setTitle(getTitle());

        JukeApplication app = JukeApplication.getInstance();
        if(app.isUserExists())
            goToMain();

        showView(0);
    }


    private void showView(int position, Object... obj) {

        Bundle data = new Bundle();
        fragment = LoginFragment.newInstance(data);
        // update the main content by replacing fragments
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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


    void goToMain(){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLogged() {
        goToMain();
    }

    @Override
    public void onLogError(VolleyError error) {

    }
}
