package no.ntnu.stud.avikeyb.backend.layouts;


import java.util.HashMap;

import no.ntnu.stud.avikeyb.backend.InputType;

/**
 * A step layout is a layout that registers an input step as a single on -> off toggle of an input signal.
 * <p>
 * This layout class is intended as a base for Layouts that only needs simple input detection, where a single
 * on/off toggle of a signal is detected as a single step in the layout.
 * <p>
 * Other classes that need move advanced input handling, e.g. long press, should extend the BaseLayout or implement the
 * Layout interface
 */
public abstract class StepLayout extends BaseLayout {

    private HashMap<InputType, Boolean> currentState;

    public StepLayout() {

        currentState = new HashMap<>();

        for (InputType input : InputType.values()) {
            currentState.put(input, false);
        }

    }

    public void setInputState(InputType input, boolean value) {
        if (currentState.get(input) && !value) {
            onStep(input);
        }
        currentState.put(input, value);
    }


    /**
     * Called when a input signal of the specified type is detected
     *
     * @param input the input signal type that was registered
     */
    protected abstract void onStep(InputType input);
}
