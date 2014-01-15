import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;

import com.pocketknife.state.StateContract;
import com.pocketknife.state.StateProvider;

/**
 * Created by jtmoulia on 1/12/14.
 */
public class StateProviderTest extends ProviderTestCase2<StateProvider> {

    private ContentProvider contentProvider;
    private ContentValues testContValues;

    private final String TEST_TITLE = "Test Title";
    private final String TEST_BODY = "Test Body";

    public StateProviderTest() {
        super(StateProvider.class, StateContract.AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        contentProvider = getProvider();

        testContValues = new ContentValues();
        testContValues.put(StateContract.Notes.TITLE, TEST_TITLE);
        testContValues.put(StateContract.Notes.BODY, TEST_BODY);
    }

    /**
     * Test that insertion can occur without an error being thrown
     */
    public void testInsert() {
        //assertEquals(3, 3);
        contentProvider.insert(
                StateContract.Notes.CONTENT_URI,
                testContValues);

        // TODO - test invalid uri
    }

    /**
     * Test that a value can be inserted and then queried for
     */
    public void testInsertQuery() {
        contentProvider.insert(
                StateContract.Notes.CONTENT_URI,
                testContValues);
        Cursor result = contentProvider.query(
                StateContract.Notes.CONTENT_URI,
                new String[]{}, "",
                new String[]{}, "" );

        assertTrue(result.moveToNext());

        int titleCol = result.getColumnIndexOrThrow(
                StateContract.Notes.TITLE);
        int bodyCol = result.getColumnIndexOrThrow(
                StateContract.Notes.BODY);

        assertEquals(result.getString(titleCol), TEST_TITLE);
        assertEquals(result.getString(bodyCol), TEST_BODY);

        assertFalse(result.moveToNext());
    }
}
