package de.gamedevbaden.crucified.enums;

import com.jme3.input.KeyInput;

/**
 * A collection of all player inputs.
 * <p>
 * Created by Domenic on 01.05.2017.
 */
public enum InputCommand {

    Forward(KeyInput.KEY_W),
    Backward(KeyInput.KEY_S),
    Left(KeyInput.KEY_A),
    Right(KeyInput.KEY_D),
    Shift(KeyInput.KEY_LSHIFT),
    Interaction(KeyInput.KEY_F),
    Scream(KeyInput.KEY_V);

    private int keyCode;
    private boolean pressed;

    InputCommand(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}