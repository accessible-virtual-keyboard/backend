package no.ntnu.stud.avikeyb.backend.dictionary;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;


/**
 * Unit tests for the resource handler.
 * <p>
 * Created by Kristian Honningsvag.
 */

public class ResourceHandlerTest {

    private InMemoryDictionary dictionaryHandler;

    /**
     * Working directory for tests are: /AccessibleVirtualKeyboard/AViKEYB/app
     */
    @Before
    public void setUp() {
        dictionaryHandler = new DictionaryHandler(new DictionaryFileLoader("./src/main/res/raw/test_dictionary.txt").loadDictionary());
    }

    /**
     * Test storing a dictionary that has been loaded into memory.
     */
    @Test
    public void testStoreDictionaryEntries() {
        boolean isStored = false;
        try {
            ResourceHandler.storeDictionaryToFile(dictionaryHandler.getDictionary(), "./src/main/res/raw/test_dictionary.txt");
            isStored = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(true, isStored);
    }

}
