package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import java.util.ArrayList;

/* Not used yet */

public class StoryEventAppState extends AbstractAppState {

    private ArrayList<String> events;


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        events = new ArrayList<String>();
        load();
        super.initialize(stateManager, app);
    }

    @Override
    public void cleanup() {
        save();
        super.cleanup();
    }

    public boolean checkEvent(String event) {
        if (events.contains(event)) return true;

        return false;
    }

    public void setEvent(String event) {
        if (checkEvent(event))
            System.out.println("Story Event: " + event + "already exists! Use checkEvent() before pushing an event");
        events.add(event);
    }

    private void load() {}

    private void save() {}
}
