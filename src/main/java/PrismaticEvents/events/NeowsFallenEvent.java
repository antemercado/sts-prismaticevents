package PrismaticEvents.events;

import java.util.ArrayList;

import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

import PrismaticEvents.PrismaticEventsMod;

public class NeowsFallenEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(NeowsFallenEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    
    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(NeowsFallenEvent.class.getSimpleName() + ".png");

    private int screenNum = 0;
    private AbstractRelic startingRelic = null;
    private AbstractRelic gift1 = new Circlet();
    private AbstractRelic gift2 = new Circlet();
    
    
    public NeowsFallenEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        
        ArrayList<AbstractRelic> playerStarterRelics = new ArrayList<>();
        for (AbstractRelic r : AbstractDungeon.player.relics){
            if (r.tier.equals(RelicTier.STARTER)){
                playerStarterRelics.add(r);
            }
        }
        if (playerStarterRelics.size() > 0){
            this.startingRelic = playerStarterRelics.get(0);
        }
        ArrayList<AbstractRelic> relics = new ArrayList<>();

        for (AbstractRelic r : RelicLibrary.starterList){
            for (AbstractRelic r2 : playerStarterRelics){
                if (!r.relicId.equals(r2.relicId)){
                    relics.add(r);
                }
            }
        }
        if (relics.size() > 0){
            this.gift1 = relics.get(AbstractDungeon.relicRng.random(relics.size()));
            relics.remove(this.gift1);
            this.gift2 = relics.get(AbstractDungeon.relicRng.random(relics.size()));
        }

        if (this.startingRelic == null){
            imageEventText.setDialogOption(OPTIONS[5], true);
            imageEventText.setDialogOption(OPTIONS[5], true);
        } else {
            imageEventText.setDialogOption(OPTIONS[0] + startingRelic.name + OPTIONS[1] + this.gift1.name, this.gift1.makeCopy()); // Lose starting relic, gain starting relic
            imageEventText.setDialogOption(OPTIONS[0] + startingRelic.name + OPTIONS[2]); // Lose starting relic, gain random starting relic
        }
        imageEventText.setDialogOption(OPTIONS[3]); // Leave
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum){
            case 0: // Starting Screen
                switch(i){
                    case 0: //Lose starting relic, gain starting relic
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;

                        AbstractDungeon.player.loseRelic(this.startingRelic.relicId);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.gift1.makeCopy());
                        
                        return;
                    case 1: //Lose starting relic, gain random starting elic
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;

                        AbstractDungeon.player.loseRelic(this.startingRelic.relicId);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.gift2.makeCopy());

                        return;
                    case 2: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        
                        return;
                }
            case 1: // Confirmation Screen
                switch(i){
                    case 0:
                        openMap();
                        return;
                }
                return;
        }
    }
    
}
