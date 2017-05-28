package de.gamedevbaden.crucified.appstates;

import com.jme3.app.state.AbstractAppState;
import de.gamedevbaden.crucified.game.GameCommander;

import java.util.ArrayList;

/**
 * Created by Domenic on 27.05.2017.
 */
public class GameCommanderCollector extends AbstractAppState {

    private ArrayList<GameCommander> commanders = new ArrayList<>();

    public void addGameCommander(GameCommander gameCommander) {
        commanders.add(gameCommander);
    }

    public void removeGameCommander(GameCommander gameCommander) {
        commanders.remove(gameCommander);
    }

    public ArrayList<GameCommander> getCommanders() {
        return commanders;
    }

    @Override
    public void cleanup() {
        this.commanders.clear();
        this.commanders = null;
        super.cleanup();
    }
}
