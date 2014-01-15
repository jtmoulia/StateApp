package com.pocketknife.state;

import android.net.Uri;

/**
 * Created by jtmoulia on 1/12/14.
 */
public class StateContract {

    public static final String AUTHORITY =
            "com.pocketknife.state.provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class Notes {
        public static String CONTENT_URI_SUFFIX = "notes";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(StateContract.CONTENT_URI, CONTENT_URI_SUFFIX);

        public static final String ITEM_MIME_TYPE =
            "vnd.android.cursor.item/vnd.pocketknife.provider.note";
        public static final String MIME_TYPE =
            "vnd.android.cursor.dir/vnd.pocketknife.provider.note";

        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String BODY = "body";
        public static final String CREATED = "created";
        public static final String UPDATED = "updated";
    }
}
