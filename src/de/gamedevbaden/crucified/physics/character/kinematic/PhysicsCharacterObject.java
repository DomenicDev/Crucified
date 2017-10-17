package de.gamedevbaden.crucified.physics.character.kinematic;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

final class PhysicsCharacterObject implements EntityComponent {

    private final EntityId object;

    public PhysicsCharacterObject(EntityId object) {
        this.object = object;
    }

    public EntityId getObject() {
        return object;
    }
}
