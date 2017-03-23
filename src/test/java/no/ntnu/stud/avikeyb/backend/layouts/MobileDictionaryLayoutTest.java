package no.ntnu.stud.avikeyb.backend.layouts;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.dictionary.DictionaryEntry;
import no.ntnu.stud.avikeyb.backend.dictionary.DictionaryFileLoader;
import org.junit.Test;

import no.ntnu.stud.avikeyb.backend.Layout;
import no.ntnu.stud.avikeyb.backend.dictionary.LinearEliminationDictionaryHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tor-Martin Holen on 01-Mar-17.
 */

public class MobileDictionaryLayoutTest extends LayoutTestBase {

    @Override
    protected Layout createLayout() {
        List<DictionaryEntry> entries = new DictionaryFileLoader(getClass().getClassLoader().getResource("word.list").getPath()).loadDictionary();
        MobileDictionaryLayout layout = new MobileDictionaryLayout(keyboard, new LinearEliminationDictionaryHandler(), entries);
        return layout;
    }

    @Test
    public void writeWordTest() {
        select(4);
        move();
        select(4);
        move(3);
        select(3);
        assertOutputBufferEquals("Test ");
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
