package no.ntnu.stud.avikeyb.backend.layouts;

import org.junit.Test;

/**
 * Created by Tor-Martin Holen on 01-Mar-17.
 */

public class MobileLayoutTest extends MobileLayoutTestBase {

    @Test
    public void writeWordTests() {
        writeWord_Test();
        assertOutputBufferEquals("Test ");
    }

    @Test
    public void sendTest(){
        writeWord_Test();
        selectSend();
        assertOutputBufferEquals("");
    }

    /**
     * Writes "Test, test! "
     */
    @Test
    public void writeSentences() {
        writeWord_Test();
        selectComma();
        writeWord_Test();
        selectExclamationMark();
        assertOutputBufferEquals("Test, test! ");
    }

    @Test
    public void deleteSingleWord(){
        writeWord_Test();
        fixWord(3);
        assertOutputBufferEquals("");
        selectDictionary();
        chooseItemAt(0);
        assertOutputBufferEquals("At ");
        deleteWord(1);
        assertOutputBufferEquals("");
        writeWord_Test();
        assertOutputBufferEquals("Test ");
    }

    @Test
    public void deleteCommaSeparatedWords(){
        writeWord_Test();
        selectComma();
        writeWord_Test();
        fixWord(5);
        assertOutputBufferEquals("Test, ");
        fixWord(1);
        assertOutputBufferEquals("Test ");
        fixWord(1);
        selectDictionary();
        chooseItemAt(0);
        assertOutputBufferEquals("Test ");
    }

    @Test
    public void modeChange(){
        selectToggleMode();
        selectETA_ETI_();
        chooseItemAt(1);
        selectETA_ETI_();
        chooseItemAt(0);
        selectSRH_OSD();
        chooseItemAt(1);
        selectETA_ETI_();
        chooseItemAt(1);
        assertOutputBufferEquals("Test");
        selectDictionary();
        chooseItemAt(0);
        assertOutputBufferEquals("Test ");
    }

    /**
     * Writes "Test "
     */
    private void writeWord_Test(){
        selectETA_ETI_();
        selectETA_ETI_();
        selectSRH_OSD();
        selectETA_ETI_();
        selectDictionary();
        chooseItemAt(0);
    }

}
