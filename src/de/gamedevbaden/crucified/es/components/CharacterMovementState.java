package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * This component contains the movement information for characters such as players or monsters.
 * This component can be used to play a animation for each state.
 * Created by Domenic on 29.04.2017.
 */
@Serializable
public class CharacterMovementState implements EntityComponent {

    /********* CONSTANTS START ******************/

    public static final int IDLE = 0;

    public static final int MOVING_FORWARD = 1;
    public static final int MOVING_FORWARD_LEFT = 2;
    public static final int MOVING_FORWARD_RIGHT = 3;

    public static final int MOVING_BACK = 4;
    public static final int MOVING_BACK_LEFT = 5;
    public static final int MOVING_BACK_RIGHT = 6;

    public static final int MOVING_LEFT = 7;
    public static final int MOVING_RIGHT = 8;

    public static final int RUNNING_FORWARD = 9;
    public static final int RUNNING_FORWARD_LEFT = 10;
    public static final int RUNNING_FORWARD_RIGHT = 11;

    public static final int RUNNING_BACK = 12;
    public static final int RUNNING_BACK_LEFT = 13;
    public static final int RUNNING_BACK_RIGHT = 14;


    /********* CONSTANTS END *****************/

    private int movementState;

    public CharacterMovementState() {

    }

    public CharacterMovementState(int movementState) {
        this.movementState = movementState;
    }

    public int getMovementState() {
        return movementState;
    }
}
