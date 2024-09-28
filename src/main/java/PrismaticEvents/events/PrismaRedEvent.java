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

public class PrismaRedEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(PrismaRedEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    
    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(PrismaRedEvent.class.getSimpleName() + ".png");

    private CurScreen screen = CurScreen.INTRO;
    private int damage;

    private enum CurScreen {
        INTRO, INTRO_2, ACCEPT, LEAVE;
    }

    public PrismaRedEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.damage = (int)(AbstractDungeon.player.maxHealth * 0.12F * AbstractDungeon.actNum);
        } else {
            this.damage = (int)(AbstractDungeon.player.maxHealth * 0.08F * AbstractDungeon.actNum);
        }
        if (this.damage > AbstractDungeon.player.currentHealth){
            this.damage = AbstractDungeon.player.currentHealth - 1;
        }
        if (this.damage < 1){
            this.damage = 1;
        }
        this.noCardsInRewards = true;
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPress) {
        switch(this.screen){
            case INTRO:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.damage + OPTIONS[2]);
                this.imageEventText.setDialogOption(OPTIONS[3]);
                this.screen = CurScreen.INTRO_2;
                return;
            case INTRO_2:
                switch(buttonPress){
                    case 0:
                        this.screen = CurScreen.ACCEPT;
                        reward();
                        AbstractDungeon.player.damage(new DamageInfo(null, this.damage, DamageInfo.DamageType.HP_LOSS));
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        break;
                    case 1:
                        this.screen = CurScreen.LEAVE;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        break;
                }

                this.imageEventText.clearRemainingOptions();
                return;
            case ACCEPT:
                reward();
                break;
        }
        openMap();
    }

    private void reward(){
        (AbstractDungeon.getCurrRoom()).rewards.clear();
        AbstractDungeon.getCurrRoom().addCardReward(PrismaticRewardHelper.PrismaticReward(CardCrawlGame.characterManager.getCharacter(PlayerClass.IRONCLAD)));
        (AbstractDungeon.getCurrRoom()).phase = RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = CurScreen.LEAVE;
    }
    
}
