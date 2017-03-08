package no.ntnu.stud.avikeyb.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the interface of the suggestion engine.
 * <p>
 * Objects of this class will listen to the keyboard and find suggested words as new characters
 * are added to the keyboard output buffer. Someone interested in the suggestion can register
 * an WordHistory.Listener to be notified when new suggestions are found.
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

    private Keyboard keyboard;
    private List<Suggestions.Listener> listeners;

    public Suggestions(Keyboard keyboard) {
        this.keyboard = keyboard;
        listeners = new ArrayList<>();
        setupKeyboardListener();
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


    private void setupKeyboardListener() {
        keyboard.addStateListener(new Keyboard.KeyboardListener() {
            @Override
            public void onOutputBufferChange(String oldBuffer, String newBuffer) {
                executeQuery(keyboard.getCurrentWord());
            }
        });
    }

}
