package de.gamedevbaden.crucified.physics.character.kinematic;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

//TODO move to lib ESCharacter
public final class ESCharacter implements EntityComponent {

    private final EntityId character;

    public ESCharacter(EntityId target) {
        this.character = target;
    }

    public EntityId getCharacter() {
        return character;
    }
}
