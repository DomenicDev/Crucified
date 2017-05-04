package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.ModelType;

/**
 * Tells the ES what model the entity has.
 * Created by Domenic on 11.04.2017.
 */
@Serializable
public class Model implements EntityComponent {

    private ModelType modelType;

    /**
     * Do not use!
     */
    public Model() {
        // just for serialization
    }


    public Model(ModelType modelType) {
        this.modelType = modelType;
    }

    public ModelType getModelType() {
        return modelType;
    }
}
