package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.PlayerControl;

/**
 * Created by Domenic on 12.04.2017.
 */
public class PlayerInputAppState extends AbstractAppState implements ActionListener {

    private InputManager inputManager;
    private EntitySet playerControls;

    public enum InputMapping {

        Forward(KeyInput.KEY_W),
        Backward(KeyInput.KEY_S),
        Left(KeyInput.KEY_A),
        Right(KeyInput.KEY_D),
        Shift(KeyInput.KEY_LSHIFT);

        InputMapping(final int KEY_CODE) {
            this.KEY_CODE = KEY_CODE;
        }

        private final int KEY_CODE;
        private boolean pressed;

        public int getKeyCode() {
            return this.KEY_CODE;
        }

        void setPressed(boolean pressed) {
            this.pressed = pressed;
        }

        public boolean isPressed() {
            return pressed;
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.inputManager = app.getInputManager();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        playerControls = entityData.getEntities(PlayerControl.class);

        // init mappings
        for (InputMapping mapping : InputMapping.values()) {
            inputManager.addMapping(mapping.name(), new KeyTrigger(mapping.getKeyCode()));
            inputManager.addListener(this, mapping.name());
        }
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        playerControls.applyChanges();
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        InputMapping.valueOf(name).setPressed(isPressed);
        System.out.println(name + " " + isPressed);
        for (Entity entity : playerControls) {
            entity.set(new PlayerControl(
                    InputMapping.Forward.isPressed(),
                    InputMapping.Backward.isPressed(),
                    InputMapping.Left.isPressed(),
                    InputMapping.Right.isPressed(),
                    InputMapping.Shift.isPressed())
            );
        }
    }

    @Override
    public void cleanup() {
        for (InputMapping mapping : InputMapping.values()) {
            this.inputManager.deleteMapping(mapping.name());
            mapping.setPressed(false);
        }
        inputManager.removeListener(this);
        super.cleanup();
    }
}
