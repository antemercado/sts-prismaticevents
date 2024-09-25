package PrismaticEvents.events;

import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.WarpedTongs;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import PrismaticEvents.PrismaticEventsMod;
import PrismaticEvents.relics.DevoutSoulRelic;

public class PrismaPurpleEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(PrismaPurpleEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(PrismaPurpleEvent.class.getSimpleName() + ".png");

    private CurScreen screen = CurScreen.INTRO;
    private int relicCounter = AbstractDungeon.actNum;
    
    private enum CurScreen {
        INTRO, ACCEPT, LEAVE;
    }

    public PrismaPurpleEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15){
            this.relicCounter = this.relicCounter + 1;
        }

        this.noCardsInRewards = true;
        this.imageEventText.setDialogOption(OPTIONS[0] + this.relicCounter + OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPress) {
        switch(this.screen){
            case INTRO:
                switch(buttonPress){
                    case 0:
                        reward();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        break;
                }
                this.screen = CurScreen.LEAVE;
                this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                this.imageEventText.clearRemainingOptions();
                return;
        }
        openMap();

    }

    private void reward() {
        AbstractRelic rewardRelic = new DevoutSoulRelic();
        rewardRelic.setCounter(relicCounter);
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), rewardRelic);
        (AbstractDungeon.getCurrRoom()).rewards.clear();
        AbstractDungeon.getCurrRoom().addCardReward(PrismaticEventsMod.PrismaticReward(CardCrawlGame.characterManager.getCharacter(PlayerClass.WATCHER)));
        (AbstractDungeon.getCurrRoom()).phase = RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
    }
    
}
