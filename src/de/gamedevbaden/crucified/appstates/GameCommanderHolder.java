package de.gamedevbaden.crucified.appstates;

import com.jme3.app.state.AbstractAppState;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.game.GameCommander;

import java.util.HashMap;

/**
 * Created by Domenic on 08.06.2017.
 */
public class GameCommanderHolder extends AbstractAppState {

    private HashMap<EntityId, GameCommander> map = new HashMap<>();

    public void add(EntityId player, GameCommander commander) {
        map.put(player, commander);
    }

    public GameCommander get(EntityId player) {
        return map.get(player);
    }

}
