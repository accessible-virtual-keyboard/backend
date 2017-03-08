package no.ntnu.stud.avikeyb.backend.layouts;

import org.junit.Test;

import no.ntnu.stud.avikeyb.backend.Layout;
import no.ntnu.stud.avikeyb.backend.dictionary.LinearEliminationDictionaryHandler;

/**
 * Created by Tor-Martin Holen on 01-Mar-17.
 */

public class MobileDictionaryLayoutTest extends LayoutTestBase {

    @Override
    protected Layout createLayout() {
        return new MobileDictionaryLayout(keyboard, new LinearEliminationDictionaryHandler());
    }

/*    @Test
    private void testDictionary(){

    }*/
}
