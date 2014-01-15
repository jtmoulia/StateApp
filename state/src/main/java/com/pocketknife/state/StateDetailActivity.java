package com.pocketknife.state;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by jtmoulia on 1/14/14.
 */
public class StateDetailActivity extends Activity {

    private final String TAG = "com.pocketknife.state.StateDetailActivity";

    private long _id;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_state_detail);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            _id = extras.getLong(StateContract.Notes._ID);

            StateDetailFragment detailFrag =
                    StateDetailFragment.newInstance(extras);
            getFragmentManager().beginTransaction().add(
                    android.R.id.content,
                    detailFrag).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                int dels = getContentResolver().delete(
                        Uri.withAppendedPath(StateContract.Notes.CONTENT_URI, String.valueOf(_id)),
                        null, null);
                if (dels != 1) {
                    Log.w(TAG, String.format("Unexpected dels val: %d", dels));
                }
                finish();
        }

        return true;
    }

}
