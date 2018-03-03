package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public abstract class AbstractGame extends AbstractAppState {

    protected AppStateManager stateManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        setupGame();
        super.initialize(stateManager, app);
    }

    public abstract void setupGame();

    public abstract void onGameStart();

}
