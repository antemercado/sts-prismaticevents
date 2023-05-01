package PrismaticEvents;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.localization.EventStrings;

import PrismaticEvents.events.NeowsFallenEvent;

import java.nio.charset.StandardCharsets;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class PrismaticEventsMod implements
    PostInitializeSubscriber,
    EditStringsSubscriber {

    public static final String modID = "prismaticEvents";

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }

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
        BaseMod.addEvent(NeowsFallenEvent.ID, NeowsFallenEvent.class, Exordium.ID);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(EventStrings.class,
                modID + "Resources/localization/eng/Event-Strings.json");
    }
}
