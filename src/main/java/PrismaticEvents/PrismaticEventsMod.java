package PrismaticEvents;

import basemod.BaseMod;
import basemod.eventUtil.AddEventParams;
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
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class PrismaticEventsMod implements
        PostInitializeSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber {
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

    public static void initialize() {
        PrismaticEventsMod thismod = new PrismaticEventsMod();
        foundmod_hermit = Loader.isModLoaded("downfall");
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addEvent(PowerInsideEvent.ID, PowerInsideEvent.class);
        // BaseMod.addEvent(NeowsFallenEvent.ID, NeowsFallenEvent.class, Exordium.ID);
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

    public static RewardItem PrismaticReward(AbstractPlayer cardPlayer) {
        RewardItem ret = new RewardItem();
        
        ArrayList<AbstractCard> tmp = new ArrayList<>();
        cardPlayer.getCardPool(tmp);
        
        //Populate cardpool
        CardGroup cardPool = new CardGroup(CardGroupType.UNSPECIFIED);
        for (AbstractCard c : tmp){
            if (c.type != CardType.CURSE && c.type != CardType.STATUS && !UnlockTracker.isCardLocked(c.cardID)){
                cardPool.addToBottom(c);
            }
        }

        CardRarity actRarity = CardRarity.COMMON;
        if (AbstractDungeon.actNum == 2){
            actRarity = CardRarity.UNCOMMON;
        }
        if (AbstractDungeon.actNum == 3){
            actRarity = CardRarity.RARE;
        }
        
        ArrayList<AbstractCard> rewardCards = new ArrayList<>();
        AbstractCard reward = null;
        
        for (AbstractCard c : ret.cards){
            switch (c.type){
                case ATTACK:
                    reward = cardPool.getAttacks().getRandomCard(true, actRarity);
                    break;
                case SKILL:
                    reward = cardPool.getSkills().getRandomCard(true, actRarity);
                    break;
                case POWER:
                    reward = cardPool.getPowers().getRandomCard(true, actRarity);
                    break;
                default:
                    reward = cardPool.getRandomCard(true);
                    break;
            }
            if (reward == null){
                reward = cardPool.getRandomCard(true);
            }

            if (c.upgraded){
                reward.upgrade();
            }

            cardPool.removeCard(reward);
            rewardCards.add(reward);
            reward = reward.makeCopy();
        }

        ret.cards = rewardCards;

        return ret;
    }
}
