package de.gamedevbaden.crucified.appstates.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

/**
 * Created by Domenic on 30.05.2017.
 */
public class HudAppState extends AbstractAppState {

    private BitmapFont guiFont;
    private BitmapText hudText;
    private Node guiNode;
    private AssetManager assetManager;
    private Application app;

    private BitmapText script;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
        this.guiNode = ((SimpleApplication) app).getGuiNode();
        this.assetManager = app.getAssetManager();
        this.guiFont = app.getAssetManager().loadFont("Interface/Fonts/Console.fnt");

        int width = app.getCamera().getWidth();
        int height = app.getCamera().getHeight();

        this.hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize() + 10);      // font size
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

    public void showPaper(String text) {

        int width = app.getContext().getSettings().getWidth();
        int height = app.getContext().getSettings().getHeight();

        Picture pic = new Picture("PaperPicture");
        pic.setImage(assetManager, "Textures/Paper/paper.jpg", false);
        pic.setWidth(width / 2);
        pic.setHeight(height / 2);
        pic.setPosition(width / 4, height / 4);
        guiNode.attachChild(pic);

        this.script = new BitmapText(guiFont, false);
        this.script.setLocalTranslation(width / 4, height / 4, 0);
        this.script.setColor(ColorRGBA.White);
        this.script.setText(text);
        guiNode.attachChild(script);


    }
}
