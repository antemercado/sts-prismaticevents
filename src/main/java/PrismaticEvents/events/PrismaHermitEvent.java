package PrismaticEvents.events;

import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import java.util.ArrayList;
import java.util.Collections;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.cards.curses.Normality;
import com.megacrit.cardcrawl.cards.curses.Pain;
import com.megacrit.cardcrawl.cards.curses.Parasite;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import PrismaticEvents.PrismaticEventsMod;
import PrismaticEvents.util.PrismaticRewardHelper;
import downfall.monsters.gauntletbosses.Hermit;
import hermit.characters.hermit;
import hermit.cards.MementoCard;

public class PrismaHermitEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(PrismaHermitEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    
    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(PrismaHermitEvent.class.getSimpleName() + ".png");

    private CurScreen screen = CurScreen.INTRO;
    private AbstractCard curse;

    private enum CurScreen {
        INTRO, ACCEPT, LEAVE;
    }

    public PrismaHermitEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;
        this.curse = generateCurse();
        this.imageEventText.setDialogOption(OPTIONS[0] + this.curse.name, this.curse.makeCopy());
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }

    private AbstractCard generateCurse() {
        int actNum = AbstractDungeon.actNum;
        ArrayList<AbstractCard> curses = new ArrayList<AbstractCard>();
        if (actNum == 1){
            curses.add(new Doubt());
            curses.add(new Shame());
            curses.add(new MementoCard());
        }
        if (actNum == 2){
            curses.add(new Regret());
            curses.add(new Pain());
            curses.add(new Decay());
        }
        if (actNum == 3){
            curses.add(new Normality());
            curses.add(new Parasite());
            curses.add(new Pride());
        }
        Collections.shuffle(curses, AbstractDungeon.eventRng.random);
        return curses.get(0);
    }

    @Override
    protected void buttonEffect(int buttonPress) {
        switch(this.screen){
            case INTRO:
                switch(buttonPress){
                    case 0:
                        reward();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.curse, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
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

    private void reward(){
        (AbstractDungeon.getCurrRoom()).rewards.clear();
        AbstractDungeon.getCurrRoom().addCardReward(PrismaticRewardHelper.PrismaticReward(CardCrawlGame.characterManager.getCharacter(hermit.Enums.HERMIT)));
        (AbstractDungeon.getCurrRoom()).phase = RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
    }
    
}
