package PrismaticEvents;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class PrismaticEventsMod implements PostInitializeSubscriber {

    public static final String modID = "launchpad"; //TODO: Change this.

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    public PrismaticEventsMod() {
        BaseMod.subscribe(this);
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static void initialize() {
        PrismaticEventsMod thismod = new PrismaticEventsMod();
    }

    @Override
    public void receivePostInitialize() {

    }
}
