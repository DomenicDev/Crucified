package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 11.04.2017.
 */
public class Model implements EntityComponent {

    private String modelPath;



    /**
     * Do not use!
     */
    public Model() {
        // just for serialization
    }

    /**
     * Creates a Models component with the given model path
     * @param modelPath for all available models have look at the globally defined models in this class
     */
    public Model(String modelPath) {
        this.modelPath = modelPath;
    }


    public String getModelPath() {
        return modelPath;
    }
}
