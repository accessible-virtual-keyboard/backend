package no.ntnu.stud.avikeyb.backend.layouts;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.Keyboard;
import no.ntnu.stud.avikeyb.backend.Symbol;
import no.ntnu.stud.avikeyb.backend.dictionary.LinearEliminationDictionaryHandler;


/**
 * Handles mobile layout logic.
 * <p>
 * Created by Tor-Martin Holen on 21-Feb-17.
 */

public class MobileDictionaryLayout extends StepLayout {

/*    public interface MobileLayoutSwap {
        public void onStart();

        public void onDictionaryOn();

        public void onDictionaryOff();
    }*/

    private int[] stepIndices;
    private Symbol[] symbols;
    private Keyboard keyboard;

    private State state = State.SELECT_ROW;
    private DictionaryState dictionaryState = DictionaryState.DICTIONARY_ON;

    private ArrayList<Symbol> markedSymbols = new ArrayList<>();
    private int[] location = new int[]{-1, -1, -1, -1};
    private List<String> suggestions;
    private int nSuggestions = 10;

    private LinearEliminationDictionaryHandler dictionary;


    public enum State {
        SELECT_ROW,
        SELECT_COLUMN,
        SELECT_LETTER,
        SELECT_DICTIONARY
    }

    public enum DictionaryState {
        DICTIONARY_ON,
        DICTIONARY_OFF
    }


    public MobileDictionaryLayout(Keyboard keyboard, LinearEliminationDictionaryHandler dictionary) {
        suggestions = new ArrayList<>();
        this.keyboard = keyboard;
        this.dictionary = dictionary;

        updateLayoutStructure();
        /*MobileLayoutSwap.onStart();*/
        setBaseSuggestions();
        nextRow();
    }

    private void updateLayoutStructure() {
        switch (dictionaryState) {
            case DICTIONARY_ON:
                symbols = new Symbol[]{
                        Symbol.E, Symbol.T, Symbol.A,
                        Symbol.O, Symbol.I, Symbol.N,
                        Symbol.CORRECT_WORD, Symbol.DELETE_WORD, Symbol.DELETION_DONE,

                        Symbol.S, Symbol.R, Symbol.H,
                        Symbol.L, Symbol.D, Symbol.C, Symbol.U,
                        Symbol.W, Symbol.Y, Symbol.B, Symbol.V,

                        Symbol.M, Symbol.F, Symbol.P, Symbol.G,
                        Symbol.K, Symbol.X, Symbol.J, Symbol.Q, Symbol.Z,
                        Symbol.DICTIONARY_ADD_WORD, Symbol.DICTIONARY_TOGGLE,

                        Symbol.DICTIONARY,
                        Symbol.PERIOD, Symbol.COMMA, Symbol.QUESTION_MARK, Symbol.EXCLAMATION_MARK,
                        Symbol.SEND};

                stepIndices = new int[]{
                        0, 3, 6, 9,
                        12, 16, 20,
                        24, 29, 31,
                        32, 36, 37};

                break;
            case DICTIONARY_OFF:
                symbols = new Symbol[]{
                        Symbol.E, Symbol.T, Symbol.I, Symbol.SPACE,
                        Symbol.A, Symbol.N, Symbol.L,
                        Symbol.CORRECT_WORD, Symbol.DELETE_WORD, Symbol.DELETION_DONE,

                        Symbol.O, Symbol.S, Symbol.D,
                        Symbol.R, Symbol.C, Symbol.P, Symbol.B,
                        Symbol.M, Symbol.W, Symbol.K, Symbol.J,

                        Symbol.H, Symbol.U, Symbol.G, Symbol.V,
                        Symbol.F, Symbol.Y, Symbol.X, Symbol.Q, Symbol.Z,
                        Symbol.DICTIONARY_ADD_WORD, Symbol.DICTIONARY_TOGGLE,

                        Symbol.DICTIONARY,
                        Symbol.PERIOD, Symbol.COMMA, Symbol.QUESTION_MARK, Symbol.EXCLAMATION_MARK,
                        Symbol.SEND};

                stepIndices = new int[]{
                        0, 4, 7, 10,
                        13, 17, 21,
                        25, 30, 32,
                        33, 37, 38};
                break;
        }

    }

    @Override
    protected void onStep(InputType input) {
        switch (state) {
            case SELECT_ROW:
                switch (input) {
                    case INPUT1: //Move
                        nextRow();
                        break;
                    case INPUT2: //Select
                        changeStateColumnSelection();
                        break;
                }
                break;
            case SELECT_COLUMN:
                switch (input) {
                    case INPUT1:
                        nextColumn();
                        break;
                    case INPUT2:
                        state = State.SELECT_ROW;
                        if (markedSymbols.contains(Symbol.SEND)) {
                            keyboard.sendCurrentBuffer();
                            dictionary.reset();
                            setBaseSuggestions();
                            reset();
                            break;
                        } else if (markedSymbols.contains(Symbol.DICTIONARY)) {
                            changeStateDictionarySelection();
                            break;
                        } else if (markedSymbols.contains(Symbol.DELETE_WORD)) {
                            if (dictionary.hasWordHistory() || !keyboard.getCurrentBuffer().isEmpty()) { // We don't want the user to input punctuation symbols when no words has been entered
                                changeStateLetterSelection();
                                break;
                            }
                        } else if (markedSymbols.contains(Symbol.PERIOD)) {
                            if (!keyboard.getCurrentBuffer().isEmpty()) { // We don't want the user to input punctuation symbols when no words has been entered
                                changeStateLetterSelection();
                                break;
                            }
                        } else if (markedSymbols.contains(Symbol.DICTIONARY_TOGGLE)) {
                            changeStateLetterSelection();
                            break;
                        } else if (dictionaryState == DictionaryState.DICTIONARY_ON) {
                            dictionary.findValidSuggestions(getStringsFromMarkedSymbols());
                            setSuggestions(dictionary.getSuggestions(nSuggestions));
                            //TODO add marked symbols to the list at the left in the layout as a history feature
                            reset();
                            break;
                        } else if (dictionaryState == DictionaryState.DICTIONARY_OFF) {
                            changeStateLetterSelection();
                            //TODO suggestion logic when dictionary is off
                            break;
                        }

                        //logMarked();
                }
                break;

            case SELECT_LETTER:
                switch (input) {
                    case INPUT1:
                        nextLetter();
                        break;
                    case INPUT2:

                        if (markedSymbols.contains(Symbol.DELETE_WORD)) {
                            if (!keyboard.getCurrentBuffer().isEmpty()) {
                                deleteLastWord();
                                dictionary.previousWord();
                                setBaseSuggestions();
                            }
                        } else if (markedSymbols.contains(Symbol.CORRECT_WORD)) {
                            //TODO add logic to handle proper word correction regardless of the dictionary state
                            //Log.d(TAG, "onStep: -----------------------------------------");
                            if (dictionary.hasWordHistory()) {
                                //Log.d(TAG, "onStep: has history");
                                dictionary.removeLastWordHistoryElement();
                                if (!dictionary.hasWordHistory()) {
                                    setBaseSuggestions();
                                } else {
                                    setCurrentSuggestions();
                                }
                            } else if (!(dictionary.hasWordHistory() && keyboard.getCurrentBuffer().isEmpty())) {
                                //Log.d(TAG, "onStep: has no history");
                                if (dictionaryState == DictionaryState.DICTIONARY_ON) {
                                    deleteLastWord();
                                } else if (dictionaryState == DictionaryState.DICTIONARY_OFF) {
                                    removeLastLetter();
                                }


                                dictionary.previousWord();
                                if (!dictionary.hasWordHistory() && keyboard.getCurrentBuffer().isEmpty()) {
                                    setBaseSuggestions();
                                } else {
                                    setCurrentSuggestions();
                                }

                            }
                        } else if (markedSymbols.contains(Symbol.DELETION_DONE)) {
                            state = State.SELECT_ROW;
                            reset();
                            break;
                        } else if (markedSymbols.contains(Symbol.DICTIONARY_TOGGLE)) {
                            if (dictionaryState == DictionaryState.DICTIONARY_ON) {
                                dictionaryState = DictionaryState.DICTIONARY_OFF;
                            } else if (dictionaryState == DictionaryState.DICTIONARY_OFF) {
                                dictionaryState = DictionaryState.DICTIONARY_ON;
                            }
                            dictionary.clearWordHistory();
                            setBaseSuggestions();
                            updateLayoutStructure();
                            changeStateRowSelection();
                            break;
                        } else if (markedSymbols.contains(Symbol.DICTIONARY_ADD_WORD)) {
                            //TODO implement add word to dictionary functionality
                            changeStateRowSelection();
                            break;

                        } else if (markedSymbols.contains(Symbol.SPACE) || markedSymbols.contains(Symbol.PERIOD) || markedSymbols.contains(Symbol.COMMA) || markedSymbols.contains(Symbol.EXCLAMATION_MARK) || markedSymbols.contains(Symbol.QUESTION_MARK)) {
                            //TODO make space start new word history
                            dictionary.nextWord();
                            setBaseSuggestions();
                            writeSymbol();
                            changeStateRowSelection();
                            break;
                        } else {
                            if (dictionaryState == DictionaryState.DICTIONARY_OFF) {
                                //TODO add possible suggestions to word history
                                String marked = getMarkedSymbols().get(0).getContent();
                                dictionary.findValidSuggestions(Collections.singletonList(marked));
                                setCurrentSuggestions();
                                selectCurrentSymbols();
                            } else if (dictionaryState == DictionaryState.DICTIONARY_ON) {
                                writeSymbol(); //Will never trigger
                            }

                            changeStateRowSelection();
                            break;
                        }

                }
                break;

            case SELECT_DICTIONARY:
                switch (input) {
                    case INPUT1:
                        nextDictionaryRow();
                        break;
                    case INPUT2:
                        //TODO check if dictionary state is on or off
                        if (dictionaryState == DictionaryState.DICTIONARY_ON) {
                            addWord();
                        } else if (dictionaryState == DictionaryState.DICTIONARY_OFF) {
                            addWordWithNoDictionary();
                        }

                        setSuggestions(dictionary.getBaseSuggestion(nSuggestions));
                        state = State.SELECT_ROW;
                        reset();
                        break;
                }
                break;
        }

        notifyLayoutListeners();

    }

    private void changeStateRowSelection() {
        state = State.SELECT_ROW;
        reset();
    }

    private void changeStateColumnSelection() {
        state = State.SELECT_COLUMN;
        nextColumn();
    }

    private void changeStateLetterSelection() {
        state = State.SELECT_LETTER;
        nextLetter();
    }

    private void changeStateDictionarySelection() {
        state = State.SELECT_DICTIONARY;
        softReset();
        nextDictionaryRow();
    }

    public DictionaryState getDictionaryState() {
        return dictionaryState;
    }

    /**
     * @return list of strings
     */
    private List<String> getStringsFromMarkedSymbols() {
        List<String> stringList = new ArrayList<>();
        for (Symbol sym : markedSymbols) {
            stringList.add(sym.getContent());
        }
        return stringList;
    }

    public void logMarked() {
        String result = "";
        for (Symbol sym : markedSymbols) {
            result += sym.getContent() + " ";
        }
        //Log.d("MobLayout", "Marked symbols: " + result);
    }

    public void softReset() {
        location = new int[]{-1, -1, -1, -1};
        markedSymbols = new ArrayList<>();
    }

    /**
     * Capitalizes a word if it starts with an lowercase letter [a-z], returns original word if not.
     *
     * @param currentWord The word that should be capitalized;
     * @return string with word capitalized, if it starts with a letter.
     */
    private String capitalizeWord(String currentWord) {
        String capitalizedLetter = currentWord.substring(0, 1).toUpperCase();
        if (currentWord.substring(0, 1).matches("[a-z]")) {
            return currentWord.replaceFirst("[a-z]", capitalizedLetter);
        } else {
            return currentWord;
        }

    }

    /**
     * Capitalizes a word that should be added to a text, if deemed appropriate.
     * Should be called every time a word is added to current text, if not all appropriate cases won't be found.
     *
     * @param currentText the text to check, for occurrences where a capital letter should be present.
     * @param nextWord    the word to check, if it should be modified to have a capital letter
     * @return
     */
    private String capitalizationCheck(String currentText, String nextWord) {
        if (currentText.isEmpty()) {
            nextWord = capitalizeWord(nextWord);
        }

        //RegExp if end matches ". ", "! " or "? " nextWord should be capitalized.
        if (currentText.matches("^[A-Za-z,.!?'\"\\s]+[.!?][ ]$")) {
            nextWord = capitalizeWord(nextWord);
        }
        return nextWord;
    }


    private void removeLastLetter() {
        String currentBuffer = keyboard.getCurrentBuffer();
        int endIndex = currentBuffer.length() - 1;
        if (endIndex >= 0) {
            currentBuffer = currentBuffer.substring(0, endIndex);
            keyboard.clearCurrentBuffer();
            keyboard.addToCurrentBuffer(currentBuffer);
        }
    }

    private void deleteLastWord() {
        String currentBuffer = keyboard.getCurrentBuffer();
        int secondLastSpace = currentBuffer.trim().lastIndexOf(" ");
        if (secondLastSpace == -1) {
            currentBuffer = "";
        } else {
            currentBuffer = currentBuffer.substring(0, secondLastSpace) + " ";
        }

        keyboard.clearCurrentBuffer();
        keyboard.addToCurrentBuffer(currentBuffer);
    }


    private void addWord() {
        String currentText = keyboard.getCurrentBuffer();
        String currentWord = getSuggestions().get(location[3]);

        currentWord = capitalizationCheck(currentText, currentWord);

        keyboard.addToCurrentBuffer(currentWord + " ");
        dictionary.nextWord();

    }

    private void addWordWithNoDictionary() {
        deleteLastWord();
        addWord();
    }

    private void setCurrentSuggestions() {
        setSuggestions(dictionary.getSuggestions(nSuggestions));
    }

    private void writeSymbol() {
        String keyboardInput = keyboard.getCurrentBuffer().trim();
        keyboard.clearCurrentBuffer();
        keyboard.addToCurrentBuffer(keyboardInput);
        selectCurrentSymbols();
        if (!markedSymbols.get(0).getContent().equals(" ")) {
            keyboard.addToCurrentBuffer(" ");
        }
    }

    private void setBaseSuggestions() {
        setSuggestions(dictionary.getBaseSuggestion(nSuggestions));
    }

    /**
     * Handles positional logic for rows
     */
    private void nextRow() {
        location[0]++;

        if (location[0] >= 4) {
            location[0] = 0;
        }


        markedSymbols = new ArrayList<>();
        int lowerBound = stepIndices[location[0] * 3];
        int upperBound = stepIndices[location[0] * 3 + 3];
        //Log.d("MobLayout", "Bounds, Lower: " + lowerBound + " Upper: " + upperBound);
        for (int i = lowerBound; i < upperBound; i++) {
            addMarkedSymbol(i);
        }
        //logLocation();
    }

    /**
     * Handles positional logic for columns
     */
    private void nextColumn() {
        location[1]++;
        markedSymbols = new ArrayList<>();

        int[] bounds = new int[]{0, 0};
        if (stepIndices.length == location[0] * 3 + location[1] + 1) {
            location[1] = 0;
            bounds = updateColumnBounds(bounds);
        } else {
            bounds = updateColumnBounds(bounds);
        }

        if (bounds[0] == bounds[1] || location[1] >= 3) {
            location[1] = 0;
            bounds = updateColumnBounds(bounds);
        }
        for (int i = bounds[0]; i < bounds[1]; i++) {
            addMarkedSymbol(i);
        }
        //logLocation();
    }

    private void nextDictionaryRow() {
        location[3]++;
        if (location[3] >= suggestions.size() || location[3] >= nSuggestions) {
            location[3] = 0;
        }
    }

    private int[] updateColumnBounds(int[] bounds) {
        bounds[0] = stepIndices[location[0] * 3 + location[1]];
        bounds[1] = stepIndices[location[0] * 3 + location[1] + 1];
        return bounds;
    }


    /**
     * Handles positional logic for selecting letters
     */
    private void nextLetter() {
        location[2]++;

        int lowerBound = stepIndices[location[0] * 3 + location[1]];
        int upperBound = stepIndices[location[0] * 3 + location[1] + 1];
        int symbolsInGroup = upperBound - lowerBound;

        //Log.d("MobLayout", "Symbols in group: " + symbolsInGroup);
        //Log.d("MobLayout", "Bounds, Lower: " + lowerBound + " Upper: " + upperBound);
        if (location[2] >= symbolsInGroup) {
            location[2] = 0;
        }

        markedSymbols = new ArrayList<>();
        int index = stepIndices[location[0] * 3 + location[1]] + location[2];
        //Log.d("MobLayout", "Index: " + index);
        addMarkedSymbol(index);
        //logLocation();
    }

    public ArrayList<Symbol> getMarkedSymbols() {
        return markedSymbols;
    }

    /**
     * Sends the current symbol, only usable in state SELECT_LETTER
     */
    private void selectCurrentSymbols() {
        Symbol symbol = markedSymbols.get(0); // will only contain one symbol in SELECT_LETTER state.

        if (symbol == Symbol.SEND) {
            keyboard.sendCurrentBuffer();
        } else {
            String text = symbol.getContent();
            text = capitalizationCheck(keyboard.getCurrentBuffer(), text);
            keyboard.addToCurrentBuffer(text);
        }
    }

    private void addMarkedSymbol(int index) {
        try {
            markedSymbols.add(symbols[index]);
        } catch (Exception e) {

        }
    }

    public List<String> getHistory(){
        return dictionary.getHistory();
    }

    public void reset() {
        location = new int[]{-1, -1, -1, -1};
        markedSymbols = new ArrayList<>();
        nextRow();
    }

    public Symbol[] getSymbols() {
        return symbols;
    }

    private void logLocation() {
        //Log.d("MobLayout", "Position: " + location[0] + ", " + location[1] + ", " + location[2]);
        String markedSymbolsText = "";
        for (Symbol s : markedSymbols) {
            markedSymbolsText += s.getContent() + " ";
        }
        //Log.d("MobLayout", markedSymbolsText);
    }


    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public int getMarkedWord() {
        return location[3];
    }


    public List<String> getSuggestions() {
        return suggestions;
    }

    public State getState() {
        return state;
    }

    public int getMaxPossibleSuggestions() {
        return nSuggestions;
    }

}
