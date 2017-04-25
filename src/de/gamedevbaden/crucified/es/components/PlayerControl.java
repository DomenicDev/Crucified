package de.gamedevbaden.crucified.es.components;


import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 12.04.2017.
 */
public class PlayerControl implements EntityComponent{

    private boolean forward;
    private boolean backward;
    private boolean left;
    private boolean right;
    private boolean running;

//    private boolean crouch;

    public PlayerControl() {
    }

    public PlayerControl(boolean forward, boolean backward, boolean left, boolean right, boolean running) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.running = running;
    }

    public boolean isForward() {
        return forward;
    }

    public boolean isBackward() {
        return backward;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isRunning() {
        return running;
    }
}
