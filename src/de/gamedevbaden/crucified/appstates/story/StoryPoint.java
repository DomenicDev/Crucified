package de.gamedevbaden.crucified.appstates.story;

import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;

public abstract class StoryPoint {

    private String description;

    public StoryPoint(String description) {
        this.description = description;
    }

    /**
     * @return a short description of this story point (task)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Don't call manually. Is called by the the StoryManager.
     * @param entityData
     * @param stateManager
     */
    protected abstract void initStoryPoint(EntityData entityData, AppStateManager stateManager);

    protected abstract boolean isFinished();

    protected void update(){}

    protected void cleanup(){}

}
