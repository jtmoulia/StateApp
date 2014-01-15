package com.pocketknife.state;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by jtmoulia on 1/14/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StateDetailFragment extends Fragment {

    private static final Uri CONTENT_URI = StateContract.Notes.CONTENT_URI;
    private static final String TAG = "com.pocketknife.state.StateDetailFragment";

    private long _id;
    private EditText titleView;
    private EditText bodyView;
    private TextView updatedView;

    /**
     * Listens for changes in editable, saving as necessary
     */
    private class SaveTextWatcher implements TextWatcher {

        private ContentResolver mContentResolver;
        private Uri noteUri;


        public SaveTextWatcher() {
            mContentResolver = getActivity().getContentResolver();
            noteUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(_id));
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // pass
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            Log.d(TAG, "Text changed (updating): " + charSequence);
            int updates = mContentResolver.update(noteUri, getContentValues(), null, new String[]{});
            if (updates == 0) {
                Log.w(TAG, "Did not successfully update note");
            } else {
                Log.d(TAG, String.format("Updated %d rows", updates));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // pass
        }

        private ContentValues getContentValues() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StateContract.Notes.TITLE, getTitle());
            contentValues.put(StateContract.Notes.BODY, getBody());

            return contentValues;
        }
    }

    /**
     * Factory method for creating these fragments
     *
     * @param arguments
     * @return
     */
    public static StateDetailFragment newInstance(Bundle arguments) {
        StateDetailFragment detailFragment = new StateDetailFragment();
        detailFragment.setArguments(arguments);
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _id = getArguments().getLong(StateContract.Notes._ID);
        } else {
            throw new IllegalArgumentException("requires _id argument onCreate");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_detail, container, false);


        Bundle arguments = getArguments();
        if (arguments != null) {
            SaveTextWatcher saveTextTitleWatcher = new SaveTextWatcher();
            titleView = (EditText) view.findViewById(R.id.title);
            titleView.setText(arguments.getString(StateContract.Notes.TITLE));
            titleView.addTextChangedListener(saveTextTitleWatcher);

            SaveTextWatcher saveTextBodyWatcher = new SaveTextWatcher();
            bodyView = (EditText) view.findViewById(R.id.body);
            bodyView.setText(arguments.getString(StateContract.Notes.BODY));
            bodyView.addTextChangedListener(saveTextBodyWatcher);

            updatedView = (TextView) view.findViewById(R.id.updated);
            updatedView.setText(arguments.getString(StateContract.Notes.UPDATED));
        } else {
            throw new IllegalArgumentException("arguments required");
        }

        return view;
    }

    public String getTitle() {
        return titleView.getText().toString();
    }

    public String getBody() {
        return bodyView.getText().toString();
    }
}
