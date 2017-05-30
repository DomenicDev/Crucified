package de.gamedevbaden.crucified.appstates.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * Created by Domenic on 30.05.2017.
 */
public class HudAppState extends AbstractAppState {

    private BitmapText hudText;
    private Node guiNode;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.guiNode = ((SimpleApplication) app).getGuiNode();
        BitmapFont guiFont = app.getAssetManager().loadFont("Interface/Fonts/Console.fnt");

        int width = app.getCamera().getWidth();
        int height = app.getCamera().getHeight();

        this.hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText("+");                                           // the text
        hudText.setLocalTranslation(width / 2 + hudText.getLineWidth(), height / 2 + hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);

        super.initialize(stateManager, app);
    }

    @Override
    public void cleanup() {
        this.guiNode.detachChild(hudText);
        super.cleanup();
    }
}
