package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.enums.Type;
import de.gamedevbaden.crucified.es.components.NeedToBeCrafted;
import de.gamedevbaden.crucified.es.components.ObjectType;

import java.util.ArrayList;
import java.util.Map;

/**
 * This state is the crafting system.
 * <p>
 * Created by Domenic on 28.06.2017.
 */
public class CraftingAppState extends AbstractAppState {

    private EntityData entityData;
    private EntitySet itemsToCraft;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.itemsToCraft = entityData.getEntities(NeedToBeCrafted.class);
        super.initialize(stateManager, app);
    }

    /**
     * Applies the specified item for crafting.
     * The {@link NeedToBeCrafted} component will be deleted when the specified item has been crafted.
     * Most items need more than one item so this method needs to be called several times until
     * the item is finally crafted.
     * @param itemToCraft the item to craft
     * @param ingredient the item you want to use (destroy) for crafting.
     */
    public void putItemForCrafting(EntityId itemToCraft, EntityId ingredient) {
        // it is enough to call the update in here
        itemsToCraft.applyChanges();
        if (itemsToCraft.containsId(itemToCraft)) {

            ObjectType objectType = entityData.getComponent(ingredient, ObjectType.class);
            if (objectType != null) {

                Type type = objectType.getObjectType();

                // check whether this item is needed
                NeedToBeCrafted craftComponent = itemsToCraft.getEntity(itemToCraft).get(NeedToBeCrafted.class);
                Map<Type, Integer> neededItems = craftComponent.getNeededItems();

                ArrayList<Type> entriesToRemove = null;

                for (Map.Entry<Type, Integer> e : neededItems.entrySet()) {
                    if (e.getKey().equals(type)) {

                        int count = e.getValue();
                        count--;

                        // if we just add the last item of this type
                        // we later can remove this from the list
                        if (count <= 0) {

                            // create a new list if necessary
                            // we do this this way to not create
                            // unnecessary list objects which are empty later
                            if (entriesToRemove == null) {
                                entriesToRemove = new ArrayList<>();
                            }

                            entriesToRemove.add(e.getKey());

                        } else {
                            // still items needed so we update the list
                            neededItems.put(e.getKey(), count);
                        }

                        // remove the "ingredient" so it can't be used anymore
                        entityData.removeEntity(ingredient);
                    }
                }

                // remove all types which are no longer needed for crafting
                if (entriesToRemove != null) {
                    for (Type t : entriesToRemove) {
                        neededItems.remove(t);
                    }
                }

                // if the map is empty the item is crafted
                if (neededItems.isEmpty()) {
                    entityData.removeComponent(itemToCraft, NeedToBeCrafted.class);
                } else {
                    // else we update the component
                    entityData.setComponent(itemToCraft, new NeedToBeCrafted(neededItems));
                }
            }
        }
    }

    @Override
    public void cleanup() {
        this.itemsToCraft.release();
        this.itemsToCraft.clear();
        this.itemsToCraft = null;
        super.cleanup();
    }
}
