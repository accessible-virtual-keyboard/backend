package no.ntnu.stud.avikeyb.backend.layouts;


import org.junit.Test;

import java.util.Collections;
import java.util.List;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.Layout;
import no.ntnu.stud.avikeyb.backend.core.SingleThreadSuggestions;
import no.ntnu.stud.avikeyb.backend.dictionary.Dictionary;

/**
 * Created by pitmairen on 17/02/2017.
 */
public class AdaptiveLayoutTest extends LayoutTestBase {

    @Override
    protected Layout createLayout() {
        return new AdaptiveLayout(keyboard,new SingleThreadSuggestions(keyboard, new Dictionary() {
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
    public void testFirstLetter() throws Exception {

        assertOutputBufferEquals("");


        // When selecting the first letter it should end up just selecting
        // "t" and space (" ") repeatedly.
        String[] expectedOutputs = {"t", "t ", "t t", "t t "};

        for (String output : expectedOutputs) {
            // Select the fist letter in the first row
            selectRow();
            selectColumn();

            assertOutputBufferEquals(output);
        }
    }


    @Test
    public void testSecondLetterInFirstRow() throws Exception {

        assertOutputBufferEquals("");

        String[] expectedOutputs = {"a", "ar", "ar ", "ar a"};

        for (String output : expectedOutputs) {
            // Select the fist letter in the first row
            selectRow();
            nextColumn();
            selectColumn();

            assertOutputBufferEquals(output);
        }
    }

    @Test
    public void testFirstLetterInSecondRow() throws Exception {

        assertOutputBufferEquals("");

        String[] expectedOutputs = {"c", "ci", "cil", "cily", "cilyl"};

        for (String output : expectedOutputs) {
            // Select the fist letter in the first row
            nextRow();
            selectRow();
            selectColumn();

            assertOutputBufferEquals(output);
        }
    }


    @Test
    public void testTypeName() throws Exception {

        assertOutputBufferEquals("");

        nextRow();
        selectRow();
        nextColumn();
        nextColumn();
        selectColumn();

        assertOutputBufferEquals("p");

        selectRow();
        selectColumn();

        assertOutputBufferEquals("pe");

        selectRow();
        nextColumn();
        selectColumn();

        assertOutputBufferEquals("per");

    }


    private void nextRow() {
        stepInput(InputType.INPUT1);
    }

    private void selectRow() {
        stepInput(InputType.INPUT2);
    }

    private void nextColumn() {
        stepInput(InputType.INPUT1);
    }

    private void selectColumn() {
        stepInput(InputType.INPUT2);
    }
}