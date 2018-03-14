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

    /**
     * this is the host
     * @return
     */
    public EntityId getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(EntityId playerOne) {
        this.playerOne = playerOne;
    }

    /**
     * this is the client
     * @return
     */
    public EntityId getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(EntityId playerTwo) {
        this.playerTwo = playerTwo;
    }
}
