package de.gamedevbaden.crucified.appstates.story;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.*;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.NameAppState;
import de.gamedevbaden.crucified.appstates.listeners.StoryEventListener;
import de.gamedevbaden.crucified.enums.InteractionType;
import de.gamedevbaden.crucified.enums.ItemType;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.components.Name;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryManager extends AbstractAppState {

    private EntityData entityData;
    private AppStateManager stateManager;

    private List<StoryPoint> storyPoints;
    private StoryPoint currentStoryPoint;

    private List<StoryEventListener> listeners = new ArrayList<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();

        // directly create game story (for testing)
        List<StoryPoint> storyPoints = createGameStory();
        setStoryPoints(storyPoints);

        super.initialize(stateManager, app);
    }

    public void addListener(StoryEventListener listener) {
        this.listeners.add(listener);
    }

    public void setStoryPoints(List<StoryPoint> storyPoints) {
        // cleanup old story points (never used)
        if (this.storyPoints != null) {
            for (StoryPoint sp : this.storyPoints) {
                sp.cleanup();
            }
            this.storyPoints = null;
            this.currentStoryPoint = null;
        }
        if (storyPoints == null || storyPoints.isEmpty()) {
            return;
        }
        // add story points
        this.storyPoints = storyPoints;
        initAndSetCurrentStoryPoint(storyPoints.get(0));
    }

    private void initAndSetCurrentStoryPoint(StoryPoint storyPoint) {
        storyPoint.initStoryPoint(entityData, stateManager);
        for (StoryEventListener l : listeners) {
            l.onStoryPointBegun(storyPoint);
        }
        this.currentStoryPoint = storyPoint;
        System.out.println(storyPoint.getDescription());
    }

    @Override
    public void update(float tpf) {
        if (currentStoryPoint == null) {
            return;
        }

        this.currentStoryPoint.update();
        if (this.currentStoryPoint.isFinished()) {
            this.currentStoryPoint.cleanup();
            for (StoryEventListener l : listeners) {
                l.onStoryPointFinished(this.currentStoryPoint);
            }

            // init next story point
            int indexOfCurrentStoryPoint = this.storyPoints.indexOf(this.currentStoryPoint);
            if (indexOfCurrentStoryPoint+1 >= this.storyPoints.size()) {
                // there are no story points anymore
                this.currentStoryPoint = null;
            } else {
                StoryPoint nextStoryPoint = this.storyPoints.get(indexOfCurrentStoryPoint+1);
                if (nextStoryPoint != null) {
                    initAndSetCurrentStoryPoint(nextStoryPoint);
                }
            }
        }
    }

    private static List<StoryPoint> createGameStory() {
        List<StoryPoint> storyPoints = new ArrayList<>();

        // create story

        storyPoints.add(new StoryPoint("Pick up 2 exemplars of firewood") {

            private EntitySet players;
            private EntitySet woodenSticks;

            @Override
            protected void initStoryPoint(EntityData entityData, AppStateManager stateManager) {
                this.players = entityData.getEntities(PlayerControlled.class);
                this.woodenSticks = entityData.getEntities(new FieldFilter<>(ItemComponent.class, "itemType", ItemType.Firewood), StoredIn.class, ItemComponent.class);
            }

            @Override
            protected void update() {
                this.players.applyChanges();
                this.woodenSticks.applyChanges();
            }

            @Override
            protected boolean isFinished() {
                int amountOfPickedUpWood = 0;
                for (Entity wood : woodenSticks) {
                    StoredIn in = wood.get(StoredIn.class);
                    EntityId container = in.getContainer(); // player
                    if (players.containsId(container)) {
                        amountOfPickedUpWood++;
                    }
                }
                return amountOfPickedUpWood >= 2;
            }
        });

        // next we want the players to make a campfire
        storyPoints.add(new StoryPoint("Use the wood to craft a campfire") {

            WatchedEntity watchedCampfireEntity;

            @Override
            protected void initStoryPoint(EntityData entityData, AppStateManager stateManager) {
                // create campfire
                EntityId campFire = entityData.createEntity();
                HashMap<ItemType, Integer> items = new HashMap<>();
                items.put(ItemType.Firewood, 2);
                entityData.setComponents(campFire,
                        new Model(ModelType.Campfire),
                        new PhysicsRigidBody(0, false, CollisionShapeType.BOX_COLLISION_SHAPE),
                        new InteractionComponent(InteractionType.TurnOnCampfire, true),
                        new NeedToBeCrafted(items),
                        new Transform(new Vector3f(0, 0.2f, 0)),
                        new FireState(false),
                        new Name("CampfireToBuild"));

                watchedCampfireEntity = entityData.watchEntity(campFire, NeedToBeCrafted.class);
            }

            @Override
            protected boolean isFinished() {
                if (watchedCampfireEntity.applyChanges()) {
                    if (watchedCampfireEntity.get(NeedToBeCrafted.class) == null) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            protected void cleanup() {
                watchedCampfireEntity.release();
            }
        });


        // the players have built the campfire, now they have to set it on fire
        storyPoints.add(new StoryPoint("Turn on the campfire") {

            WatchedEntity campfire;

            @Override
            protected void initStoryPoint(EntityData entityData, AppStateManager stateManager) {
                NameAppState nameAppState = stateManager.getState(NameAppState.class);
                EntityId campfireId = nameAppState.findEntityByName("CampfireToBuild"); // the campfire from above
                campfire = entityData.watchEntity(campfireId, FireState.class);
            }

            @Override
            protected void update() {
                campfire.applyChanges();
            }

            @Override
            protected boolean isFinished() {
                return campfire.get(FireState.class).isOn();
            }

            @Override
            protected void cleanup() {
                campfire.release();
            }
        });

        return storyPoints;
    }
}
