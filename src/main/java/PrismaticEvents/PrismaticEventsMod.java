package PrismaticEvents;

import basemod.BaseMod;
import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils.EventType;
import basemod.eventUtil.util.Condition;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import PrismaticEvents.events.NeowsFallenEvent;
import PrismaticEvents.events.PowerInsideEvent;
import PrismaticEvents.events.PrismaBlueEvent;
import PrismaticEvents.events.PrismaGreenEvent;
import PrismaticEvents.events.PrismaHermitEvent;
import PrismaticEvents.events.PrismaPurpleEvent;
import PrismaticEvents.events.PrismaRedEvent;
import PrismaticEvents.relics.DevoutSoulRelic;
import PrismaticEvents.util.NeowsFallenCondition;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class PrismaticEventsMod implements
        PostInitializeSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostDeathSubscriber{
    public static final Logger logger = LogManager.getLogger(PrismaticEventsMod.class.getName());

    @SpireEnum
    public static CurrentScreen DEVIL_SHOP;

    public static final String modID = "prismaticEvents";

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }
    public static String makeNPCPath(String resourcePath) {
        return modID + "Resources/images/npcs/" + resourcePath;
    }

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }
    
    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }
    
    public static String makeRelicOutlinePath(String resourcePath) {
        return modID + "Resources/images/relics/outline/" + resourcePath;
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

    public static boolean foundmod_hermit = false;

    // Neows Fallen IDs
    public static final String NEOWS_FALLEN_ACT_KEY = makeID("NF_ACT");
    public static final String NEOWS_FALLEN_CHAR_KEY = makeID("NF_CHAR");
    public static final String NEOWS_FALLEN_RELIC_KEY = makeID("NF_RELIC");
    public static final String NEOWS_FALLEN_CARD_KEY = makeID("NF_CARD");
    public static final String NEOWS_FALLEN_CARD_UPGRADE_KEY = makeID("NF_CARD_UPS");
    public static final String NEOWS_FALLEN_GOLD_KEY = makeID("NF_GOLD");
    public static final String NEOWS_FALLEN_RUN_FAILED = makeID("NF_RUN_FAILED");

    public static void initialize() {
        PrismaticEventsMod thismod = new PrismaticEventsMod();
        foundmod_hermit = Loader.isModLoaded("downfall");
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addEvent(PowerInsideEvent.ID, PowerInsideEvent.class, Exordium.ID);
        BaseMod.addEvent(new AddEventParams.Builder(NeowsFallenEvent.ID, NeowsFallenEvent.class).bonusCondition(new NeowsFallenCondition()).create());
        BaseMod.addEvent(PrismaRedEvent.ID, PrismaRedEvent.class);
        BaseMod.addEvent(PrismaGreenEvent.ID, PrismaGreenEvent.class);
        BaseMod.addEvent(PrismaBlueEvent.ID, PrismaBlueEvent.class);
        BaseMod.addEvent(PrismaPurpleEvent.ID, PrismaPurpleEvent.class);
        if (foundmod_hermit){
            BaseMod.addEvent(PrismaHermitEvent.ID, PrismaHermitEvent.class);
            // BaseMod.addEvent(new AddEventParams.Builder(PrismaHermitEvent.ID, PrismaHermitEvent.class).endsWithRewardsUI(true).create());
        }
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(EventStrings.class,
                modID + "Resources/localization/eng/Event-Strings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                modID + "Resources/localization/eng/Relic-Strings.json");
    }

    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");
        BaseMod.addRelic(new DevoutSoulRelic(), RelicType.SHARED);
    }
    
    @Override
    public void receivePostDeath() {
        if (!Settings.isStandardRun()){
            return;
        }
        ArrayList<AbstractCard> neowFallenCardList = new ArrayList<AbstractCard>();
        ArrayList<AbstractRelic> neowFallenRelicList = new ArrayList<AbstractRelic>();
        
        AbstractCard neowFallenCard = AbstractDungeon.player.masterDeck.getRandomCard(true);
        for (AbstractRelic r : AbstractDungeon.player.relics){
            if (r.tier != RelicTier.DEPRECATED && r.tier != RelicTier.SPECIAL)
                neowFallenRelicList.add(r);
        }
        Collections.shuffle(neowFallenRelicList);
        if (neowFallenRelicList.size() > 0){
            AbstractRelic neowFallenRelic = neowFallenRelicList.get(0);
            CardCrawlGame.playerPref.putString(NEOWS_FALLEN_RELIC_KEY, neowFallenRelic.relicId);
        } else {
            CardCrawlGame.playerPref.putString(NEOWS_FALLEN_RELIC_KEY, "Circlet");
        }

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group){
            if (c.type != CardType.CURSE && c.type != CardType.STATUS)
                neowFallenCardList.add(c);
        }
        Collections.shuffle(neowFallenCardList);
        if (neowFallenCardList.size() > 0){
            neowFallenCard = neowFallenCardList.get(0);
        } else {
            neowFallenCard = new IronWave();
        }
        CardCrawlGame.playerPref.putInteger(NEOWS_FALLEN_ACT_KEY, AbstractDungeon.actNum);
        CardCrawlGame.playerPref.putString(NEOWS_FALLEN_CHAR_KEY, AbstractDungeon.player.chosenClass.name());
        CardCrawlGame.playerPref.putString(NEOWS_FALLEN_CARD_KEY, neowFallenCard.cardID);
        CardCrawlGame.playerPref.putInteger(NEOWS_FALLEN_CARD_UPGRADE_KEY, neowFallenCard.timesUpgraded);
        CardCrawlGame.playerPref.putInteger(NEOWS_FALLEN_GOLD_KEY, AbstractDungeon.player.gold);
        CardCrawlGame.playerPref.putBoolean(NEOWS_FALLEN_RUN_FAILED, true);
    }
}
