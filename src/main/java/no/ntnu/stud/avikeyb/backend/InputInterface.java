package no.ntnu.stud.avikeyb.backend;

/**
 * Represents the interface input devices can use to communicate and send input signals to the keyboard
 */
public interface InputInterface {

    /**
     * Sets the current input state of the signal of the specified type
     *
     * @param input the input signal type
     * @param value boolean indicating if the signal is on or off
     */
    void setInputState(InputType input, boolean value);

}
