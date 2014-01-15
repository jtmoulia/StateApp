package com.pocketknife.state;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by jtmoulia on 1/14/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StateListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = "com.pocketknife.state.StateListFragment";

    private SimpleCursorAdapter mCursorAdapter;

    public StateListFragment() {
        // pass
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // String[] values = new String[]{"First", "Second", "Third"};
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.note_list_item,
                null,
                new String[]{
                        StateContract.Notes.TITLE,
                        StateContract.Notes.BODY,
                        StateContract.Notes.UPDATED},
                new int[]{R.id.title, R.id.body, R.id.updated},
                0);
        setListAdapter(mCursorAdapter);

        // Prepare the loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "List item clicked! " + String.valueOf(id));

        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        int titleCol = cursor.getColumnIndex(StateContract.Notes.TITLE);
        int bodyCol = cursor.getColumnIndex(StateContract.Notes.BODY);
        int updatedCol = cursor.getColumnIndex(StateContract.Notes.UPDATED);

        Intent intent = new Intent(getActivity(), StateDetailActivity.class);
        intent.putExtra(StateContract.Notes._ID, id);
        intent.putExtra(StateContract.Notes.TITLE, cursor.getString(titleCol));
        intent.putExtra(StateContract.Notes.BODY, cursor.getString(bodyCol));
        intent.putExtra(StateContract.Notes.UPDATED, cursor.getString(updatedCol));

        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                StateContract.Notes.CONTENT_URI,
                new String[]{
                        StateContract.Notes._ID,
                        StateContract.Notes.TITLE,
                        StateContract.Notes.BODY,
                        StateContract.Notes.UPDATED},
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
