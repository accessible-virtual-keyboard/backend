package no.ntnu.stud.avikeyb.backend.core;

import no.ntnu.stud.avikeyb.backend.Keyboard;
import no.ntnu.stud.avikeyb.backend.Suggestions;
import no.ntnu.stud.avikeyb.backend.dictionary.Dictionary;

/**
 * Simple single threaded implementation of the suggestion engine that runs the
 * query in the main thread.
 */
public class SingleThreadSuggestions extends Suggestions {

    private Dictionary dictionary;

    public SingleThreadSuggestions(Keyboard keyboard, Dictionary dictionary) {
        super(keyboard);
        this.dictionary = dictionary;
    }

    @Override
    protected void executeQuery(String word) {
        notifyListeners(dictionary.getSuggestionsStartingWith(word));
    }
}
