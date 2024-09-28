package PrismaticEvents.events;

import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import PrismaticEvents.PrismaticEventsMod;
import PrismaticEvents.util.PrismaticRewardHelper;

public class PrismaGreenEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(PrismaGreenEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    
    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(PrismaGreenEvent.class.getSimpleName() + ".png");

    private CurScreen screen = CurScreen.INTRO;
    private int goldLoss = 75 * AbstractDungeon.actNum;
    private int goldGain;
    private boolean winGame;

    private enum CurScreen {
        INTRO, INTRO_2, WIN_CARD, LEAVE;
    }

    public PrismaGreenEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldGain = this.goldLoss;
        } else {
            this.goldGain = this.goldLoss * 2;
        }
        this.noCardsInRewards = true;
        this.winGame = AbstractDungeon.eventRng.randomBoolean();
        imageEventText.setDialogOption(OPTIONS[5]);
    }

    @Override
    protected void buttonEffect(int buttonPress) {
        switch(this.screen){
            case INTRO:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                
                if (AbstractDungeon.player.gold < goldLoss){
                    imageEventText.updateDialogOption(0, OPTIONS[3] + goldLoss + OPTIONS[4], true);
                } else {
                    imageEventText.updateDialogOption(0, OPTIONS[0] + goldLoss + OPTIONS[1]);
                }
                this.imageEventText.setDialogOption(OPTIONS[2]);
                
                this.screen = CurScreen.INTRO_2;
                return;
            case INTRO_2:
                switch(buttonPress){
                    case 0:
                    if (winGame){
                            this.screen = CurScreen.WIN_CARD;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2] + DESCRIPTIONS[3]);
                            this.imageEventText.updateDialogOption(0, OPTIONS[6]);
                        } else {
                            this.screen = CurScreen.LEAVE;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2] + DESCRIPTIONS[4]);
                            AbstractDungeon.player.loseGold(goldLoss);
                            this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        }
                        break;
                    case 1:
                        this.screen = CurScreen.LEAVE;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        break;
                }

                this.imageEventText.clearRemainingOptions();
                return;
            case WIN_CARD:
                switch(buttonPress){
                    case 0:
                        reward();
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        break;
                }

                this.imageEventText.clearRemainingOptions();
                return;
        }
        openMap();
    }

    private void reward(){
        (AbstractDungeon.getCurrRoom()).rewards.clear();
        AbstractDungeon.getCurrRoom().addCardReward(PrismaticRewardHelper.PrismaticReward(CardCrawlGame.characterManager.getCharacter(PlayerClass.THE_SILENT)));
        (AbstractDungeon.getCurrRoom()).phase = RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = CurScreen.LEAVE;
    }
    
}
