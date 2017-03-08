package no.ntnu.stud.avikeyb.backend.dictionary;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Unit tests for the binary dictionary.
 * <p>
 * Created by Kristian Honningsvag.
 */

public class DictionaryHandlerTest {

    private DictionaryHandler dictionaryHandlerNoFrequency;
    private DictionaryHandler dictionaryHandlerWithOneFrequency;
    private DictionaryHandler dictionaryHandlerWithTwoFrequency;
    List<String> expectedOutputs;

    /**
     * Working directory for tests are: /AccessibleVirtualKeyboard/AViKEYB/app
     */
    @Before
    public void setUp() {

//        System.out.println(getClass().getClassLoader().getResourceAsStream("raw/word.list"));
        dictionaryHandlerNoFrequency = new DictionaryHandler(new DictionaryFileLoader(getClass().getClassLoader().getResource("word.list").getPath()).loadDictionary());
        dictionaryHandlerWithOneFrequency = new DictionaryHandler(new DictionaryFileLoader(getClass().getClassLoader().getResource("dictionary.txt").getPath()).loadDictionary());
        dictionaryHandlerWithTwoFrequency = new DictionaryHandler(new DictionaryFileLoader(getClass().getClassLoader().getResource("test_dictionary.txt").getPath()).loadDictionary());
    }

    /**
     * Test sending in invalid input.
     */
    @Test
    public void testInvalidInput() {
        expectedOutputs = Arrays.asList();
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("1239461598247264023421552"));
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("./!@#$%^&*("));
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith(null));

        assertNotEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("")); // Return most used words
    }

    /**
     * Test when expecting a single result in return.
     */
    @Test
    public void testSingleResults() {
        expectedOutputs = Arrays.asList("enormousnesses");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("enormousness"));

        expectedOutputs = Arrays.asList("brattishnesses");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("brattishness"));

        expectedOutputs = Arrays.asList("patrializations");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("patrialization"));
    }


    /**
     * Test when expecting multiple results in return.
     */
    @Test
    public void testMultipleResults() {
        expectedOutputs = Arrays.asList("isoclinal", "isoclinally", "isoclinals", "isocline", "isoclines", "isoclinic", "isoclinics");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("isocl"));

        expectedOutputs = Arrays.asList("teniacide", "teniacides", "teniae", "teniafuge", "teniafuges", "tenias", "teniases", "teniasis");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("tenia"));

        expectedOutputs = Arrays.asList("sarment", "sarmenta", "sarmentaceous", "sarmentose", "sarmentous", "sarments", "sarmentum");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("sarmen"));
    }


    /**
     * Test with a word not in dictionary.
     */
    @Test
    public void testNotInDictionary() {
        expectedOutputs = Arrays.asList();
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("kkxkkxkfkkdfksdfkd"));
    }


    /**
     * Test with the first entry in dictionary.
     */
    @Test
    public void testFirstEntry() {
        expectedOutputs = Arrays.asList("aah", "aahed", "aahing", "aahs", "aal", "aalii", "aaliis", "aals", "aardvark", "aardvarks", "aardwolf", "aardwolves", "aargh", "aarrgh", "aarrghh", "aas", "aasvogel", "aasvogels");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("aa"));
    }

    /**
     * Test cases where end of dictionary is reached.
     */
    @Test
    public void testEndReached() {
        expectedOutputs = Arrays.asList("zyzzyva", "zyzzyvas");
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("zyz"));

        expectedOutputs = Arrays.asList();
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("zyzzyvas"));
    }

    /**
     * Test with a dictionary that has one set of frequencies in addition to the words.
     */
    @Test
    public void testWithOneFrequencyDictionary() {
        expectedOutputs = Arrays.asList("you", "your", "you're", "yeah", "yes", "you've");
        assertEquals(expectedOutputs, dictionaryHandlerWithOneFrequency.getSuggestionsStartingWith("y").subList(0, 6));
    }

    /**
     * Test with a dictionary that has both standard and user frequencies in addition to the words.
     */
    @Test
    public void testWithTwoFrequencyDictionary() {
        expectedOutputs = Arrays.asList("you're", "yeah", "you", "your", "yes");
        assertEquals(expectedOutputs, dictionaryHandlerWithTwoFrequency.getSuggestionsStartingWith("y").subList(0, 5));
    }

    /**
     * Test adding a word.
     */
    @Test
    public void testAddingWord() {
        expectedOutputs = Arrays.asList("zzzbkldiutgudzxkeudz");
        dictionaryHandlerNoFrequency.addWordToDictionary("zzzbkldiutgudzxkeudz", 0, 1);
        assertEquals(expectedOutputs, dictionaryHandlerNoFrequency.getSuggestionsStartingWith("zzzbkldiutgudzxkeud").subList(0, 1));
    }

//    @Test
//    public void standardizeDictionary() {
//        DictionaryHandler dictionaryHandler = new DictionaryHandler();
//        try {
//            dictionaryHandler.setDictionary(ResourceHandler.loadDictionaryFromFile("./src/main/res/dictionary/dictionary.txt"));
//            dictionaryHandler.mergeDuplicateEntries();
//            ResourceHandler.storeDictionaryToFile(dictionaryHandler.getDictionary(), "./src/main/res/dictionary/dictionary.txt");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

}
