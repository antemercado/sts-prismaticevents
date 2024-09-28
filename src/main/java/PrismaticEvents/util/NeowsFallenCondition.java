package PrismaticEvents.util;

import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_ACT_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_CHAR_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_RUN_FAILED;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class NeowsFallenCondition implements basemod.eventUtil.util.Condition{

    private static final String ACT_KEY = NEOWS_FALLEN_ACT_KEY;
    private static final String CHAR_KEY = NEOWS_FALLEN_CHAR_KEY;
    private static final String RUN_FAILED = NEOWS_FALLEN_RUN_FAILED;

    @Override
    public boolean test() {

        if (!Settings.isStandardRun()){
            return false;
        }
        if (AbstractDungeon.ascensionLevel >= 15){
            return false;
        }
        if (!CardCrawlGame.playerPref.getBoolean(RUN_FAILED, true)){
            return false;
        }
        if (CardCrawlGame.playerPref.getInteger(ACT_KEY, -1) != AbstractDungeon.actNum){
            return false;
        }
        // if (CardCrawlGame.playerPref.getString(CHAR_KEY, AbstractDungeon.player.chosenClass.name()) == AbstractDungeon.player.chosenClass.name()){
        //     return false;
        // }
        
        return true;
    }
    
}
