package no.ntnu.stud.avikeyb.backend.layouts;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.Layout;
import no.ntnu.stud.avikeyb.backend.core.SingleThreadSuggestions;
import no.ntnu.stud.avikeyb.backend.dictionary.Dictionary;

/**
 * Created by pitmairen on 08/02/2017.
 */
public class BinarySearchLayoutTest extends LayoutTestBase {

    @Override
    protected Layout createLayout() {
        return new BinarySearchLayout(keyboard, new SingleThreadSuggestions(keyboard, new Dictionary() {
            @Override
            public List<String> getSuggestionsStartingWith(String match) {
                return Collections.emptyList();
            }

            @Override
            public void updateWordUsage(String string) {

            }
        }));
    }

    @Test
    public void testSelectLetters() throws Exception {

        assertOutputBufferEquals("");
        assertLastOutputEquals("");

        // a
        goLeft();
        goLeft();
        goLeft();
        goLeft();
        goLeft();
        goLeft();

        assertOutputBufferEquals("a");
        assertLastOutputEquals("");

        // h
        goLeft();
        goLeft();
        goRight();
        goLeft();
        goRight();
        goRight();

        assertOutputBufferEquals("ah");
        assertLastOutputEquals("");

        // a
        goLeft();
        goLeft();
        goLeft();
        goLeft();
        goLeft();
        goLeft();

        assertOutputBufferEquals("aha");
        assertLastOutputEquals("");

        // Send
        goLeft();
        goRight();
        goRight();
        goRight();
        goRight();

        assertOutputBufferEquals("");
        assertLastOutputEquals("aha");
    }


    private void goLeft() {
        stepInput(InputType.INPUT1);
    }

    private void goRight() {
        stepInput(InputType.INPUT2);
    }


}