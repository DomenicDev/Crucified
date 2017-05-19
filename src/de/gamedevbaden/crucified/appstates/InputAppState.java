package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import de.gamedevbaden.crucified.enums.InputCommand;

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
        for (InputCommand mapping : InputCommand.values()) {
            inputManager.addMapping(mapping.name(), new KeyTrigger(mapping.getKeyCode()));
            inputManager.addListener(this, mapping.name());
        }
        super.initialize(stateManager, app);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        InputCommand mapping = InputCommand.valueOf(name);
        mapping.setPressed(isPressed);
    }

    @Override
    public void cleanup() {
        for (InputCommand mapping : InputCommand.values()) {
            this.inputManager.deleteMapping(mapping.name());
            mapping.setPressed(false);
        }
        this.inputManager.removeListener(this);
        super.cleanup();
    }


}
