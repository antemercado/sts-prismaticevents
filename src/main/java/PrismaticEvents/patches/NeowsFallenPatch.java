package PrismaticEvents.patches;

import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_ACT_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_CHAR_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_GOLD_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_RELIC_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_RUN_FAILED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_CARD_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_CARD_UPGRADE_KEY;


import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.screens.DeathScreen;

@SpirePatch(
    clz = DeathScreen.class,
    method=SpirePatch.CONSTRUCTOR
)
public class NeowsFallenPatch {

    @SpirePostfixPatch
    public static void Postfix(DeathScreen __instance){
        if (Settings.isStandardRun()){
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

}
