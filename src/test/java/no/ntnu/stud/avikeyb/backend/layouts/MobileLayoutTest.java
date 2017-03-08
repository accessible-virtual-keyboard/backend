package no.ntnu.stud.avikeyb.backend.layouts;

import org.junit.Test;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.Layout;
import no.ntnu.stud.avikeyb.backend.Symbol;

import static org.junit.Assert.*;

/**
 * Created by Tor-Martin Holen on 15-Feb-17.
 */
public class MobileLayoutTest extends LayoutTestBase {

    @Override
    protected Layout createLayout() {
        return new MobileLayout(keyboard);
    }

    @Test
    public void testTypeLetter() throws Exception {
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        assertOutputBufferEquals("n");
    }

    @Test
    public void testNavigationReturnToStartRow() throws Exception {
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT2);
        assertOutputBufferEquals(" ");
    }

    @Test
    public void testNavigationReturnToStartColumn() throws Exception {
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT2);
        assertOutputBufferEquals(" ");
    }

    @Test
    public void testNavigationReturnToStartLetter() throws Exception {
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        assertOutputBufferEquals(" ");
    }

    @Test
    public void testSend() throws Exception {
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        assertOutputBufferEquals("u");

        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        assertOutputBufferEquals("uy");

        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT1);
        stepInput(InputType.INPUT2);

        assertLastOutputEquals("uy");
    }
}