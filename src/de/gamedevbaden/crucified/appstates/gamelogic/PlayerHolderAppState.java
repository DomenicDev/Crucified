package de.gamedevbaden.crucified.appstates.gamelogic;

import com.jme3.app.state.AbstractAppState;
import com.simsilica.es.EntityId;

public class PlayerHolderAppState extends AbstractAppState {

    private EntityId playerOne;
    private EntityId playerTwo;

    public PlayerHolderAppState() {
    }

    public PlayerHolderAppState(EntityId playerOne, EntityId playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public EntityId getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(EntityId playerOne) {
        this.playerOne = playerOne;
    }

    public EntityId getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(EntityId playerTwo) {
        this.playerTwo = playerTwo;
    }
}
