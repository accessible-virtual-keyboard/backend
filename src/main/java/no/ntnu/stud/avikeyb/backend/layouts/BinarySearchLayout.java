package no.ntnu.stud.avikeyb.backend.layouts;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.Keyboard;
import no.ntnu.stud.avikeyb.backend.Suggestions;
import no.ntnu.stud.avikeyb.backend.Symbol;
import no.ntnu.stud.avikeyb.backend.Symbols;

/**
 * Basic binary search layout
 */
public class BinarySearchLayout extends StepLayout {


    private static Symbol[] symbols = Symbols.merge(
            Symbols.alphabet(),
            Symbols.build(Symbol.SPACE, Symbol.SEND),
            Symbols.numbers(),
            Symbols.commonPunctuations());


    private Keyboard keyboard;
    private Suggestions suggestionsEngine;
    private List<String> suggestions = new ArrayList<>();

    private BinarySearchTreeDefinition.Node currentNode;

    public BinarySearchLayout(Keyboard keyboard, Suggestions suggestionsEngine) {
        this.keyboard = keyboard;
        this.suggestionsEngine = suggestionsEngine;
        reset();
        listenForSuggestions();
    }

    /**
     * Returns all symbols in the layout
     *
     * @return the symbols in the layout
     */
    public Symbol[] getSymbols() {
        return symbols;
    }

    /**
     * Checks if a symbol is active
     *
     * @param symbol the symbol to check
     * @return true if the symbol is active
     */
    public boolean symbolIsActive(Symbol symbol) {
        return currentNode.contains(symbol);
    }

    /**
     * Checks if a symbol is active
     *
     * @param symbol the symbol to check
     * @return true if the symbol is active
     */
    public boolean symbolIsActiveLeft(Symbol symbol) {
        return currentNode.getLeft().contains(symbol);
    }

    /**
     * Checks if a symbol is active
     *
     * @param symbol the symbol to check
     * @return true if the symbol is active
     */
    public boolean symbolIsActiveRight(Symbol symbol) {
        return currentNode.getRight().contains(symbol);
    }


    /**
     * Check if a suggestion is active in the left bucket
     *
     * @param suggestion the suggestion to check
     * @return true if the suggestion is in the left bucket
     */
    public boolean suggestionIsLeft(String suggestion) {
        return currentNode.getLeft().contains(suggestion);
    }

    /**
     * Check if a suggestion is active in the right bucket
     *
     * @param suggestion the suggestion to check
     * @return true if the suggestion is in the right bucket
     */
    public boolean suggestionIsRight(String suggestion) {
        return currentNode.getRight().contains(suggestion);
    }

    /**
     * Check if a suggestion is active
     *
     * @param suggestion the suggestion to check
     * @return true if the suggestion is currently selectable
     */
    public boolean suggestionIsActive(String suggestion) {
        return currentNode.contains(suggestion);
    }


    /**
     * Returns the list of current suggested words
     *
     * @return a list of words
     */
    public List<String> getSuggestions() {
        return suggestions;
    }


    @Override
    protected void onStep(InputType input) {

        // Select the correct side and split it into the new left and right side
        if (input == InputType.INPUT1) {
            selectLeft();
        } else if (input == InputType.INPUT2) {
            selectRight();
        }

        // Check if we have reached a final selection
        checkCompleted();

        notifyLayoutListeners();
    }

    private void checkCompleted() {
        // If the current node is a single node we have reached the end of the selection process
        // so we select the current item
        if (currentNode.isSingle()) {
            selectCurrent();
            reset();
        }
    }

    // Get the current item and send it to the keyboard
    private void selectCurrent() {
        Object item = currentNode.getItem();
        if (item instanceof Symbol) {
            selectSymbol((Symbol)item);
        } else if (item instanceof String) {
            selectSuggestion((String) item);
        }
    }


    private void selectSymbol(Symbol symbol) {
        if (symbol == Symbol.SEND) {
            keyboard.sendCurrentBuffer();
        } else {
            keyboard.addToCurrentBuffer(symbol.getContent());
        }
    }

    private void selectSuggestion(String suggestion) {
        // Remove the characters that has already been written and add a space to the word before
        // adding it to the keyboard output buffer
        String sug = suggestion.substring(keyboard.getCurrentWord().length());
        keyboard.addToCurrentBuffer(sug + Symbol.SPACE.getContent());
    }

    private void selectLeft() {
        currentNode = currentNode.getLeft();
    }

    private void selectRight() {
        currentNode = currentNode.getRight();
    }

    // Rebuild the selection tree so that the user can start typing something new
    private void reset() {
        currentNode = BinarySearchTreeDefinition.buildBinarySearchLayoutTree(suggestions);
    }

    // Listen for suggestions and add new suggestions to the layout
    private void listenForSuggestions() {

        suggestionsEngine.addListener(new Suggestions.Listener() {
            @Override
            public void onSuggestions(List<String> suggestions1) {
                // Take only the 7 first suggestions. A new list is needed to copy the items because
                // the sub list will keep a reference to the underlying list an keep it from
                // getting garbage collected.
                suggestions = new ArrayList<>(suggestions1.subList(0, Math.min(7, suggestions1.size())));
                BinarySearchLayout.this.reset();
                BinarySearchLayout.this.notifyLayoutListeners();
            }
        });

    }

}
