package no.ntnu.stud.avikeyb.backend.layouts;


import java.util.ArrayList;
import java.util.List;

import no.ntnu.stud.avikeyb.backend.InputType;
import no.ntnu.stud.avikeyb.backend.Keyboard;
import no.ntnu.stud.avikeyb.backend.Symbol;

/**
 * Created by Tor-Martin Holen on 15-Feb-17.
 */

public class MobileLayout extends StepLayout {

    protected int[] stepIndices;
    protected Symbol[] symbols;
    protected Keyboard keyboard;
    protected State state = State.SELECT_ROW;
    protected ArrayList<Symbol> markedSymbols = new ArrayList<>();
    protected int[] location = new int[]{-1, -1, -1 , -1};
    protected List<String> suggestions;
    protected int nSuggestions = 10;

    public MobileLayout(Keyboard keyboard) {
        suggestions = new ArrayList<>();
        this.keyboard = keyboard;

        symbols = new Symbol[]{
                Symbol.SPACE, Symbol.E, Symbol.O, Symbol.T, Symbol.I, Symbol.L, Symbol.S, Symbol.C, Symbol.W,
                Symbol.A, Symbol.N, Symbol.D, Symbol.R, Symbol.U, Symbol.Y, Symbol.F, Symbol.V, Symbol.Z,
                Symbol.H, Symbol.M, Symbol.B, Symbol.P, Symbol.K, Symbol.PERIOD, Symbol.J, Symbol.QUESTION_MARK, Symbol.SEND,
                Symbol.G, Symbol.X, Symbol.COMMA, Symbol.Q, Symbol.EXCLAMATION_MARK};
        stepIndices = new int[]{0, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 32, 32};
        nextRow();
    }

    protected MobileLayout(){
    }


    public enum State {
        SELECT_ROW,
        SELECT_COLUMN,
        SELECT_LETTER,
        SELECT_DICTIONARY
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
                        state = State.SELECT_COLUMN;
                        nextColumn();
                        break;
                }
                break;
            case SELECT_COLUMN:
                switch (input) {
                    case INPUT1:
                        nextColumn();
                        break;
                    case INPUT2:
                        state = State.SELECT_LETTER;
                        nextLetter();
                        break;
                }
                break;
            case SELECT_LETTER:
                switch (input) {
                    case INPUT1:
                        nextLetter();
                        break;
                    case INPUT2:
                        state = State.SELECT_ROW;
                        selectCurrentSymbols(keyboard);
                        reset();
                        break;
                }
                break;
            case SELECT_DICTIONARY:
                switch (input){
                    case INPUT1:

                        break;
                    case INPUT2:
                        state = State.SELECT_ROW;
                        //selectCurrentSymbols(keyboard);
                        reset();
                        break;
                }
                break;
        }
        notifyLayoutListeners();
    }

    /**
     * Handles positional logic for rows
     */
    protected void nextRow() {
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
    protected void nextColumn() {
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

    protected void nextDictionaryRow(){
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
    protected void nextLetter() {
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
    protected void selectCurrentSymbols(Keyboard keyboard) {
        Symbol symbol = markedSymbols.get(0); // will only contain one symbol in SELECT_LETTER state.

        if (symbol == Symbol.SEND) {
            keyboard.sendCurrentBuffer();
        } else {
            keyboard.addToCurrentBuffer(symbol.getContent());
        }
    }

    private void addMarkedSymbol(int index) {
        try {
            markedSymbols.add(symbols[index]);
        } catch (Exception e) {

        }
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


    public List<String> getSuggestions(){
        return suggestions;
    }

    public State getState() {
        return state;
    }

    public int getMaxPossibleSuggestions() {
        return nSuggestions;
    }

}