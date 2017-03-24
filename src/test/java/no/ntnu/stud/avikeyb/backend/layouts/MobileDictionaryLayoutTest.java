package no.ntnu.stud.avikeyb.backend.layouts;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.dictionary.DictionaryEntry;
import no.ntnu.stud.avikeyb.backend.dictionary.DictionaryFileLoader;
import org.junit.Test;

import no.ntnu.stud.avikeyb.backend.Layout;
import no.ntnu.stud.avikeyb.backend.dictionary.LinearEliminationDictionaryHandler;

import java.util.List;

/**
 * Created by Tor-Martin Holen on 01-Mar-17.
 */

public class MobileDictionaryLayoutTest extends LayoutTestBase {

    @Override
    protected Layout createLayout() {
        List<DictionaryEntry> entries = new DictionaryFileLoader(getClass().getClassLoader().getResource("dictionary.txt").getPath()).loadDictionary();
        MobileDictionaryLayout layout = new MobileDictionaryLayout(keyboard, new LinearEliminationDictionaryHandler(), entries);
        return layout;
    }

    @Test
    public void writeWordTests() {
        writeWord_Test();
        assertOutputBufferEquals("Test ");
    }

    /**
     * Writes "Test "
     */
    private void writeWord_Test(){
        selectETA();
        selectETA();
        selectSRH();
        selectETA();
        selectDictionary();
        chooseItemAt(0);
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

    private void selectETA(){
        select(2);
    }

    private void selectOIN(){
        select();
        move();
        select();
    }

    private void selectSRH(){
        move();
        select(2);
    }

    private void selectLDCU(){
        move();
        select();
        move();
        select();
    }

    private void selectWYBV(){
        move();
        select();
        move(2);
        select();
    }

    private void selectMFPG(){
        move(2);
        select(2);
    }

    private void selectKXJQZ(){
        move(2);
        select(1);
        move();
        select();
    }

    private void selectToggleMode(){
        move(2);
        select();
        move(2);
        select();
    }

    private void selectDictionary(){
        move(3);
        select(2);
    }

    /**
     * Should be used for choosing elements within tiles (toogle mode, dictionary and punctuation menu)
     * @param index selects index, starts at 0.
     */
    private void chooseItemAt(int index){
        move(index);
        select();
    }

    private void selectDeleteMenu(){
        select();
        move(2);
        select();
    }


    private void fixWord(int times){
        selectDeleteMenu();
        select(times);
        move(2);
        select();
    }

    private void deleteWord(int times){
        selectDeleteMenu();
        move();
        select(times);
        move();
        select();
    }

    private void selectPunctuationSymbols(){
        move(3);
        select();
        move(1);
        select();
    }

    private void selectPeriod(){
        selectPunctuationSymbols();
        chooseItemAt(0);
    }

    private void selectComma(){
        selectPunctuationSymbols();
        chooseItemAt(1);
    }

    private void selectQuestionMark(){
        selectPunctuationSymbols();
        chooseItemAt(2);
    }

    private void selectExclamationMark(){
        selectPunctuationSymbols();
        chooseItemAt(3);
    }

    private void selectSend(){
        move(3);
        select();
        move(2);
        select();
    }

    private void move(){
        stepInput(InputType.INPUT1);
    }

    private void move(int times){
        for (int i = 0; i < times; i++) {
            move();
        }
    }

    private void select(){
        stepInput(InputType.INPUT2);
    }

    private void select(int times){
        for (int i = 0; i < times; i++) {
            select();
        }
    }
}
