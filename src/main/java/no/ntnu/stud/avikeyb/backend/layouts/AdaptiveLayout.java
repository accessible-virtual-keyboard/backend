package no.ntnu.stud.avikeyb.backend.layouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.Keyboard;
import no.ntnu.stud.avikeyb.backend.Suggestions;
import no.ntnu.stud.avikeyb.backend.Symbol;

/**
 * The adaptive layout changes the order of the letters as the user types. After each typed letter,
 * the letters are placed in such a way that the letters with the highest probability
 * to follow the last typed letter are placed at the positions that requires the least amount
 * of steps to be selected in the layout.
 */
public class AdaptiveLayout extends StepLayout {

    /**
     * The states that the layout can be in
     */
    public enum State {
        ROW_SELECTION,
        COLUMN_SELECTION,
        SUGGESTION_SELECTION,
    }

    // Currently a hard coded row length
    private static final int ROW_LENGTH = 6;

    private Keyboard keyboard;

    // Map from letter to optimal alphabet layout
    private HashMap<String, Symbol[]> optimalLayoutMap;

    private int currentRow;
    private int currentColumn;
    private int currentSuggestion;
    private Symbol[] currentAdaptiveLayout;
    private State currentState = State.ROW_SELECTION;
    private List<String> suggestions = new ArrayList<>();


    public AdaptiveLayout(Keyboard keyboard, Suggestions suggestions) {
        this.keyboard = keyboard;
        // Create the optimal layout map and reset to set the current layout based on the
        // keyboard's current output buffer.
        optimalLayoutMap = createSymbolLayoutMap();
        reset();

        suggestions.addListener(new Suggestions.Listener() {
            @Override
            public void onSuggestions(List<String> suggestions1) {
                AdaptiveLayout.this.suggestions = new ArrayList<>(suggestions1.subList(0, Math.min(6, suggestions1.size())));
                AdaptiveLayout.this.currentSuggestion = 0;
                if(currentState == State.SUGGESTION_SELECTION){
                    reset(); // If the suggestions change while we are in suggestions mode we have to reset to prevent crashes.
                }
                AdaptiveLayout.this.notifyLayoutListeners();
            }
        });

    }

    /**
     * Returns the current internal state of the layout
     *
     * @return the state of the layout
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Returns the symbols in the layout.
     * <p>
     * The order of the symbols will change after each typed letter. The order of the symbols
     * is the order that the implementation thinks is the optimal order based on the last typed
     * character.
     *
     * @return an array of symbols
     */
    public Symbol[] getSymbols() {
        return currentAdaptiveLayout;
    }

    /**
     * Returns the current active row in the layot
     *
     * @return the current active row index
     */
    public int getCurrentRow() {
        return currentRow;
    }

    /**
     * Retuens the current active column in the layout
     *
     * @return the current active column index
     */
    public int getCurrentColumn() {
        return currentColumn;
    }


    /**
     * Returns the row length used in the layout
     *
     * @return the length of the rows
     */
    public int getRowSize() {
        return ROW_LENGTH;
    }

    public int getCurrentSuggestion(){
        return currentSuggestion;
    }
    public List<String> getSuggestions(){
        return suggestions;
    }

    @Override
    protected void onStep(InputType input) {

        switch (currentState) {
            case ROW_SELECTION:
                stepInRowSelectionMode(input);
                break;
            case COLUMN_SELECTION:
                stepInColumnSelectionMode(input);
                break;
            case SUGGESTION_SELECTION:
                stepInSuggestionsMode(input);
                break;
        }

        notifyLayoutListeners();
    }

    // Do a step when in row selection mode
    private void stepInRowSelectionMode(InputType input) {

        // Currently input 1 is used for moving to the next row and then input 2
        // is used to select the current row and switch to column selection mode.

        switch (input) {
            case INPUT1:
                // wrap around to the first row when the end is reached
                currentRow = (currentRow + 1) % getTotalRowCount();
                break;
            case INPUT2:
                currentState = State.COLUMN_SELECTION;
                break;
        }
    }

    private void stepInColumnSelectionMode(InputType input) {

        switch (input) {
            case INPUT1:
                // wrap around to the first symbol in the row when the end is reached
                currentColumn = (currentColumn + 1) % getCurrentRowLength();
                break;
            case INPUT2:
                selectCurrentSymbol();
                break;
        }
    }

    private void stepInSuggestionsMode(InputType input){
        switch (input) {
            case INPUT1:
                currentSuggestion = (currentSuggestion + 1) % suggestions.size() ;
                break;
            case INPUT2:
                selectCurrentSuggestion();
                reset();
                break;
        }
    }

    // Select the current active symbol
    private void selectCurrentSymbol() {
        Symbol current = currentAdaptiveLayout[currentRow * ROW_LENGTH + currentColumn];
        if (current == Symbol.SEND) {
            keyboard.sendCurrentBuffer();
            reset();
        } else if (current == Symbol.DICTIONARY) {
            if(!suggestions.isEmpty()) {
                currentState = State.SUGGESTION_SELECTION;
            }
        } else {
            keyboard.addToCurrentBuffer(current.getContent());
            reset();
        }
    }

    private void selectCurrentSuggestion(){
        String suggestion = suggestions.get(currentSuggestion);
        suggestion = suggestion.substring(keyboard.getCurrentWord().length());
        keyboard.addToCurrentBuffer(suggestion + Symbol.SPACE.getContent());
        reset();
    }

    // Reset the internal state
    private void reset() {
        currentColumn = 0;
        currentRow = 0;
        currentSuggestion = 0;
        currentState = State.ROW_SELECTION;
        currentAdaptiveLayout = getNextOptimalLayout(getLastTypedLetter());
    }


    // Returns the total number of rows in the layout
    private int getTotalRowCount() {
        // It has to be the ceiling because a partially filled row will count as a complete row.
        return (int) Math.ceil(currentAdaptiveLayout.length / (double) ROW_LENGTH);
    }

    // The number of columns in the current active row
    private int getCurrentRowLength() {
        // The row length will be either the ROW_LENGTH or if we are at the last row in the layout
        // and this row is not completely filled, the length will be the total number of symbols in
        // in the layout minus the number of symbols in the preceding rows.
        return Math.min(ROW_LENGTH, currentAdaptiveLayout.length - currentRow * ROW_LENGTH);
    }


    // returns the last letter in the keyboard output buffer
    private String getLastTypedLetter() {
        String buffer = keyboard.getCurrentBuffer();
        if (buffer.length() > 0) {
            return Character.toString(buffer.charAt(buffer.length() - 1));
        }
        return "";
    }


    // Returns the optimal layout that should be used after the last letter typed
    private Symbol[] getNextOptimalLayout(String lastLetter) {
        Symbol[] layout = optimalLayoutMap.get(lastLetter);
        if (layout != null) {
            return layout;
        }
        // Default to the layout coming after a space
        return optimalLayoutMap.get(" ");
    }

    // Creates a map that maps from a letter to the layout that should be used after the corresponding
    // letter has been typed. The layouts are represented as an array of symbols.
    private HashMap<String, Symbol[]> createSymbolLayoutMap() {

        HashMap<String, Symbol> stringToSymbol = createStringToSymbolMap(); // e.g: "a" -> Symbol.A
        HashMap<String, String> letterToLayout = createComputedLayoutMap(); // e.g: "a" -> "nrtlsc ..."

        HashMap<String, Symbol[]> symbolLayoutMap = new HashMap<>(); // e.g: "a" -> {Symbol.N, Symbol.R, Symbol.T, ...}

        for (String letter : letterToLayout.keySet()) {

            String stringLayout = letterToLayout.get(letter);
            Symbol[] symbolLayout = new Symbol[stringLayout.length() + 2];
            for (int i = 0; i < stringLayout.length(); i++) {
                symbolLayout[i] = stringToSymbol.get(Character.toString(stringLayout.charAt(i)));
            }
            symbolLayout[stringLayout.length()] = Symbol.SEND;
            symbolLayout[stringLayout.length() + 1] = Symbol.DICTIONARY;

            symbolLayoutMap.put(letter, symbolLayout);
        }

        return symbolLayoutMap;
    }


    // Creates a map that maps the string representation of a symbol to the corresponding
    // Symbol enum instance. This is used to convert the string representation of a layout
    // to a Symbol array representation of the same layout.
    private HashMap<String, Symbol> createStringToSymbolMap() {
        HashMap<String, Symbol> map = new HashMap<>();
        for (Symbol symbol : Symbol.values()) {
            map.put(symbol.getContent(), symbol);
        }
        return map;
    }


    private HashMap<String, String> createComputedLayoutMap() {

        // Map from each letter to the layout that should be used after the corresponding
        // letter has been typed. Each chunk of 6 characters represents a row in the layout.
        HashMap<String, String> map = new HashMap<>();
        map.put(" ", "taoiswcbphfmdrelnguvyjkqxz ");
        map.put("a", "nrtlsc imdpgbyukvwfhzxaejoq");
        map.put("b", "ealorui sbytjvcwpnmdzxqkhgf");
        map.put("c", "oheaktirul cysqmpngdzxwvjfb");
        map.put("d", " eiaosruydlgnmwvjfctphbqkzx");
        map.put("e", " rsdnaltcempvxygfwiobhqkzuj");
        map.put("f", "ieoarful tyswcbzxvqpnmkjhgd");
        map.put("g", " ehriaouslgnytmfwdbzxvqpkjc");
        map.put("h", "eao ituryhnlsmbwfdqczxvpkjg");
        map.put("i", "nstcoeladrgvmpf bzkxqujiywh");
        map.put("j", "eoaui trzyxwvsqpnmlkjhgfdcb");
        map.put("k", "e isyanlourtfmdwpgkhzxvqjcb");
        map.put("l", "ei laoysudtfvkmcpbrnwgzxqjh");
        map.put("m", "aeio pmbusyncfrtlkzxwvqjhgd");
        map.put("n", "g tedsiaconkyvuflbjmhzrqpxw");
        map.put("o", "nrulmotsw pcdvbkaigfyehxzjq");
        map.put("p", "eraoil puhstymkdfcnbzxwvqjg");
        map.put("q", "u zyxwvtsrqponmlkjihgfedcba");
        map.put("r", "e aiostrydunmglkcvbpfwhjzxq");
        map.put("s", " teishouapclymwknfqdgbrvjzx");
        map.put("t", " eiahrostuylcmwnzfbdvgpxqkj");
        map.put("u", "rnsltmcegiadpbf hzyxvokuqjw");
        map.put("v", "eiao yvusrlzxwtqpnmkjhgfdcb");
        map.put("w", "aei honsrlwdbykftmuzxvqpjgc");
        map.put("x", "pi teacyuoshwqzxvrnmlkjgfdb");
        map.put("y", " seiaolcmdntprwbugfzkhvyxqj");
        map.put("z", "ea iozyvuslgxwtrqpnmkjhfdcb");

        return map;
    }

}
