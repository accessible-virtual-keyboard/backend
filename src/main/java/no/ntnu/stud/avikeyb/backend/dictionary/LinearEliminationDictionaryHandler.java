package no.ntnu.stud.avikeyb.backend.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tor-Martin Holen on 21-Feb-17.
 */

public class LinearEliminationDictionaryHandler implements InMemoryDictionary {
    private List<DictionaryEntry> fullDictionary;
    private List<DictionaryEntry> fullDictionaryFrequencySorted;

    private List<List<SearchEntry>> sentenceHistory; //Stores all the word histories until the sentence is sent.
    private List<SearchEntry> wordHistory; //List of suggestions given at different word lengths.

    /**
     * Constructs a dictionary
     */
    public LinearEliminationDictionaryHandler()  {
        sentenceHistory = new ArrayList<>();
    }

    /**
     * Finds suggestions from the previous suggestion list. The words is added to the new
     * suggestion list if the specified index matches a letter in the list. Thus the new suggestion
     * list is much smaller than the previous one.
     */
    public void findValidSuggestions(List<String> lettersToFindAtIndex, boolean nextWordOnEmptySearch) {
        isWordHistoryInitialized();

        List<DictionaryEntry> reducedSuggestionList;
        reducedSuggestionList = reduceValidSuggestions(lettersToFindAtIndex, getLastSuggestions());

        if(reducedSuggestionList.isEmpty() && nextWordOnEmptySearch){
            nextWord();
        }else{
            SearchEntry entry = new SearchEntry(reducedSuggestionList, lettersToFindAtIndex);
            wordHistory.add(entry);
        }

    }

    private void isWordHistoryInitialized(){
        if(wordHistory == null){
            wordHistory = new ArrayList<>();
            SearchEntry entry = new SearchEntry(fullDictionary, new ArrayList<String>(0));
            wordHistory.add(entry);
        }
    }

    public void clearWordHistory(){
        wordHistory = new ArrayList<>();
        SearchEntry entry = new SearchEntry(fullDictionary, new ArrayList<String>(0));
        wordHistory.add(entry);
    }

    private List<DictionaryEntry> reduceValidSuggestions(List<String> lettersToFindAtIndex, List<DictionaryEntry> searchList){
        List<DictionaryEntry> reducedSuggestionList = new ArrayList<>();
        int searchIndex = findSearchIndex();
        //Log.d("LinearElimination", "Original suggestions: " + searchList.size());
        for (DictionaryEntry entry : searchList) {
            for (int i = 0; i < lettersToFindAtIndex.size(); i++) {
                String letter = lettersToFindAtIndex.get(i);
                boolean contained = entry.getWord().substring(searchIndex).startsWith(letter); //TODO fix index out of bounds exception
                if (contained) {
                    reducedSuggestionList.add(entry);
                    break;
                }
            }
        }
        //Log.d("LinearElimination", "Reduced suggestions: " + reducedSuggestionList.size());
        return reducedSuggestionList;
    }

    /**
     * Returns the last valid suggestions
     *
     * @return List<DictionaryEntry> containing valid suggestions
     */
    private List<DictionaryEntry> getLastSuggestions(){
        /*if(wordHistory.size() == 0){
            return fullDictionary;
        }else{*/
            return wordHistory.get(wordHistory.size()-1).getSearchResult();
        //}

    }

    /**
     * Adds word to sentence history and resets the word history to a empty list.
     * Should be called when the user selects word from dictionary
     */
    public void nextWord() {
        //Log.d(TAG, "nextWord: before: word history size: " + wordHistory.size() + ", sentence history size: " +sentenceHistory.size());

        if(wordHistory.size() > 1){
            sentenceHistory.add(new ArrayList<>(wordHistory.subList(1, wordHistory.size())));
        }else {
            sentenceHistory.add(new ArrayList<SearchEntry>());
        }

        wordHistory = new ArrayList<>(wordHistory.subList(0,1));
        //Log.d(TAG, "nextWord: after: word history size: " + wordHistory.size() + ", sentence history size: " +sentenceHistory.size());
    }
    /**
     * Adds the previous word to word history again and removes it from the sentence history.
     * Should be called when the user reverts to a state with no letters left in current word history.
     */
    public void previousWord() {
        int sentenceHistoryIndex = sentenceHistory.size()-1;
        if(sentenceHistoryIndex != -1){
            //Log.d(TAG, "previousWord: before: word history size: " + wordHistory.size() + ", sentence history size: " +sentenceHistory.size());
            wordHistory.clear();
            wordHistory.add(new SearchEntry(fullDictionary, new ArrayList<String>()));
            wordHistory.addAll(sentenceHistory.get(sentenceHistoryIndex));
            sentenceHistory.remove(sentenceHistoryIndex);
        }
        //Log.d(TAG, "previousWord: after: word history size: " + wordHistory.size() + ", sentence history size: " +sentenceHistory.size());
    }

    /**
     * Prints n suggestions from the suggestion list
     *
     * @param n the amount of suggestions needed
     */
    public void printListSuggestions(int n) {
        printDictionary(getSuggestionsWithFrequencies(n));
    }

    /**
     * Reverts the suggestion history n steps, so it deletes n entries from the end of the
     * suggestion history (used to implement backspace functionality).
     * @param steps number of steps to revert.
     * @return true if reversion succeeded
     */
    public boolean revertLastSearch(int steps){
        int index = wordHistory.size()-steps;
        if(index > 0){
            wordHistory = wordHistory.subList(0, index);
            return true;
        }else{
            return false;
        }
    }

    /**
     * Reverts the suggestion history one step, so it deletes a entry from the end of the
     * suggestion history (used to implement backspace functionality).
     * @return true if reversion succeeded
     */
    public boolean revertLastSearch(){
        return revertLastSearch(1);
    }

    /**
     * Returns the suggestion list containing n elements of the original list, sorted by frequency.
     *
     * @param n number of suggestions to include in sublist
     * @return
     */
    public List<DictionaryEntry> getSuggestionsWithFrequencies(int n) {
        List<DictionaryEntry> lastSuggestions = getLastSuggestions();
        ListSorter.sortList(lastSuggestions, SortingOrder.FREQUENCY_HIGH_TO_LOW);
        if (lastSuggestions.size() < n) {
            return lastSuggestions;
        } else {
            return lastSuggestions.subList(0, n);
        }
    }

    /**
     *
     * @param n number of suggestions wanted
     * @return
     */
    public List<String> getDefaultSuggestion(int n){
        isWordHistoryInitialized();
        List<DictionaryEntry> dictionaryEntryList;
        List<String> resultList;

        if(n > fullDictionaryFrequencySorted.size()){
            dictionaryEntryList = fullDictionaryFrequencySorted;
            resultList = new ArrayList<>(fullDictionaryFrequencySorted.size());
        }else {
            dictionaryEntryList = fullDictionaryFrequencySorted.subList(0,n);
            resultList = new ArrayList<>(n);
        }

        for (int i = 0; i < dictionaryEntryList.size(); i++) {
            DictionaryEntry de = dictionaryEntryList.get(i);
            resultList.add(de.getWord());
        }
        return resultList;
    }

    /**
     * Returns the suggestion list containing n elements of the original list, sorted by frequency.
     *
     * @param n number of suggestions to include in sublist
     * @return
     */
    public List<String> getSuggestions(int n) {
        List<DictionaryEntry> list = getSuggestionsWithFrequencies(n);
        List<String> resultList = new ArrayList<>();
        for (DictionaryEntry entry:list) {
            resultList.add(entry.getWord());
        }
        return resultList;
    }

    /**
     * Finds the index where last suggestion list should be searched to eliminate unfit words.
     * @return index where findValidSuggestion() should search last suggestion list.
     */
    private int findSearchIndex(){
        if(wordHistory.size() == 0){
            return 0;
        }else{
            return wordHistory.size()-1;
        }

    }

    /**
     * Prints a list with the word and frequency
     *
     * @param list
     */
    public void printDictionary(List<DictionaryEntry> list){
        for (DictionaryEntry entry : list) {
            System.out.println(entry.getWord() + " - " + entry.getUserFrequency());
        }
    }
    public void addSpecialCharacterHistoryEntry(String specialCharacter){
        clearWordHistory();
        wordHistory.add(new SearchEntry(specialCharacter));
        nextWord();
    }

    public void reset(){
        sentenceHistory.clear();
        wordHistory = wordHistory.subList(0,1);
    }

    public boolean hasWordHistory(){
        return wordHistory.size() > 1;
    }
    public void removeLastWordHistoryElement(){
        wordHistory.remove(wordHistory.size()-1);
    }

    @Override
    public void setDictionary(List<DictionaryEntry> dictionary) {
        fullDictionary = dictionary;
        fullDictionaryFrequencySorted = new ArrayList<>();
        fullDictionaryFrequencySorted.addAll(dictionary);
        ListSorter.sortList(fullDictionaryFrequencySorted, SortingOrder.FREQUENCY_HIGH_TO_LOW);

    }

    @Override
    public List<DictionaryEntry> getDictionary() {
        //  TODO: Is this returning the right object?
        return fullDictionary;
    }

    public List<String> getHistory() {
        List<String> result = new ArrayList<>();
        for (SearchEntry entry: wordHistory) {
            List<String> search = entry.getSearch();
            String innerResult = "";
            for (int i = 0; i < search.size(); i++) {
                String subRes = search.get(i);
                /*if(i != search.size()-1){
                    innerResult += subRes + " ";
                }else {*/
                    innerResult += subRes;
                //}
            }
            result.add(innerResult.toUpperCase());
        }
        result.remove(0);
        return result;
    }

    public boolean isCurrentHistoryEntrySpecial() {
        if(wordHistory.size() == 2){
            return wordHistory.get(1).isSpecial();
        }
        return false;
    }

    private class SearchEntry{
        List<DictionaryEntry> searchResult;
        List<String> search;
        Boolean special;

        public SearchEntry(List<DictionaryEntry> searchResult, List<String> search){
            this.searchResult = searchResult;
            this.search = search;
            special = false;
        }

        public SearchEntry(String specialSymbol){
            DictionaryEntry dEntry = new DictionaryEntry(specialSymbol,0,0);
            searchResult = Collections.singletonList(dEntry);
            search = Collections.singletonList(specialSymbol);
            special = true;
        }

        public List<DictionaryEntry> getSearchResult() {
            return searchResult;
        }

        public List<String> getSearch() {
            return search;
        }

        public Boolean isSpecial() {
            return special;
        }
    }

    /**
     * Gives the size of the word history list minus the default element at index zero
     * @return
     */
    public int getWordHistorySize(){
        return wordHistory.size()-1;
    }
}
