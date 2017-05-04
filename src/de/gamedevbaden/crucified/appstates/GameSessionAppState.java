package de.gamedevbaden.crucified.appstates;

import com.jme3.app.state.AbstractAppState;
import de.gamedevbaden.crucified.game.GameSession;

/**
 * This class just holds the GameSession, so other AppState can easily call the methods of {@link GameSession}.
 * <p>
 * Created by Domenic on 01.05.2017.
 */
public class GameSessionAppState extends AbstractAppState {

    private GameSession gameSession;

    public GameSessionAppState(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}
