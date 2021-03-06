package de.gamedevbaden.crucified.game;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.ActionType;

/**
 * This listener informs about in-game actions and interactions.
 * Created by Domenic on 03.05.2017.
 */
public interface GameEventListener {

    void onItemPickup(EntityId actor, EntityId itemToPickup);

    void onItemDrop(EntityId playerId, EntityId itemToDrop);

    void onItemEquipped(EntityId player, EntityId itemToEquip);

    void onItemUnequipped(EntityId player, EntityId itemToRemove, EntityId containerId);

    void onInputChange(EntityId entityId, String mappingName, boolean isPressed);

    void onViewDirectionChange(EntityId entityId, Vector3f newViewDirection);

    void onInteraction(EntityId playerId, EntityId interactedEntity);

    void onFlashLightToggled(EntityId playerId, EntityId flashLightId);

    void onItemPutForCraft(EntityId itemToCraft, EntityId ingredient);

    void onPutArtifactIntoContainer(EntityId containerId, EntityId artifactId);

    void onPerformAction(EntityId performerId, ActionType actionType);

    void onSettingCurse(EntityId player);
}
