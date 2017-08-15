package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.enums.ItemType;
import de.gamedevbaden.crucified.es.components.ItemComponent;
import de.gamedevbaden.crucified.es.components.NeedToBeCrafted;

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
    private EntitySet items;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.itemsToCraft = entityData.getEntities(NeedToBeCrafted.class);
        this.items = entityData.getEntities(ItemComponent.class);
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
        items.applyChanges();

        if (itemsToCraft.containsId(itemToCraft) && items.containsId(ingredient)) {

            ItemType itemType = items.getEntity(ingredient).get(ItemComponent.class).getItemType();
            if (itemType != null) {

                // check whether this item is needed
                NeedToBeCrafted craftComponent = itemsToCraft.getEntity(itemToCraft).get(NeedToBeCrafted.class);
                Map<ItemType, Integer> neededItems = craftComponent.getNeededItems();

                ArrayList<ItemType> entriesToRemove = null;

                for (Map.Entry<ItemType, Integer> e : neededItems.entrySet()) {
                    // if true the item is still needed for crafting
                    if (e.getKey().equals(itemType)) {

                        int count = e.getValue();
                        count--;

                        // if we just have added the last item of this type
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
                            // still items needed to craft the specified object, so we update the list
                            neededItems.put(e.getKey(), count);
                        }

                        // remove the "ingredient" so it can't be used anymore
                        entityData.removeEntity(ingredient);
                    }
                }

                // remove all types which are no longer needed for crafting
                if (entriesToRemove != null) {
                    for (ItemType t : entriesToRemove) {
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

        this.items.release();
        this.items.clear();
        this.items = null;
        super.cleanup();
    }
}
