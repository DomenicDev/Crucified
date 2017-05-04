package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import de.gamedevbaden.crucified.enums.InputMapping;

/**
 * This AppState initializes the Key inputs.
 *
 * Created by Domenic on 12.04.2017.
 */
public class InputAppState extends AbstractAppState implements ActionListener {

    private InputManager inputManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.inputManager = app.getInputManager();

        // init mappings
        for (InputMapping mapping : InputMapping.values()) {
            inputManager.addMapping(mapping.name(), new KeyTrigger(mapping.getKeyCode()));
            inputManager.addListener(this, mapping.name());
        }
        super.initialize(stateManager, app);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        InputMapping mapping = InputMapping.valueOf(name);
        mapping.setPressed(isPressed);
    }

    @Override
    public void cleanup() {
        for (InputMapping mapping : InputMapping.values()) {
            this.inputManager.deleteMapping(mapping.name());
            mapping.setPressed(false);
        }
        this.inputManager.removeListener(this);
        super.cleanup();
    }


}
