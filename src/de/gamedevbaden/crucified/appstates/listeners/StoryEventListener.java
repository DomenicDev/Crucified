package de.gamedevbaden.crucified.appstates.listeners;

import de.gamedevbaden.crucified.appstates.story.StoryPoint;

public interface StoryEventListener {

    void onStoryPointBegun(StoryPoint storyPoint);

    void onStoryPointFinished(StoryPoint storyPoint);

}
