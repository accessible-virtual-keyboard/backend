package no.ntnu.stud.avikeyb.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the interface of the suggestion engine.
 * <p>
 * To find suggestions the findSuggestionsStartingWith method must be called. When the suggestions are found
 * any Suggestions.Listener listening for suggestions will be to be notified about the found suggestions.
 * <p>
 * This class does not implement the actual search as the search may need to talk to an
 * database or do other long running tasks that could block when used with a gui library.
 * To prevent blocking this class must have its own implementation for each platform that
 * needs this feature. Implementation must implement the executeQuery method. Depending on the
 * platform, this method should do the query in a separate thread if the platform can not tolerate
 * the main thread to be blocked.
 */
public abstract class Suggestions {

    /**
     * Listener that is notified when new suggestions are found
     */
    public interface Listener {
        /**
         * Called when new suggestions are found
         *
         * @param suggestions a list of suggestions
         */
        void onSuggestions(List<String> suggestions);
    }

    private List<Suggestions.Listener> listeners;

    public Suggestions() {
        listeners = new ArrayList<>();
    }

    /**
     * Add a listener that will be notified when new suggestions are found
     *
     * @param listener the listener to add
     */
    public void addListener(Suggestions.Listener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener
     *
     * @param listener the listener to remove
     */
    public void removeListener(Suggestions.Listener listener) {
        listeners.remove(listener);
    }


    /**
     * Find suggestions for words starting with the given prefix
     *
     * @param prefix the prefix to search for
     */
    public void findSuggestionsStartingWith(String prefix){
        executeQuery(prefix);
    }


    /**
     * Find suggestions for the given word
     * <p>
     * Once the suggestions are found the implementation of this method must call
     * notifyListeners with the found suggestions to notify the listeners.
     * <p>
     * If needed the query can be run in a different thread, but the notifyListeners call
     * must be done on the main thread.
     *
     * @param word the partial word to find suggestions for
     */
    protected abstract void executeQuery(String word);


    /**
     * Notify the listeners about new suggestions
     *
     * @param suggestions a list of suggestions
     */
    protected void notifyListeners(List<String> suggestions) {
        for (Suggestions.Listener listener : listeners) {
            listener.onSuggestions(suggestions);
        }
    }

}
