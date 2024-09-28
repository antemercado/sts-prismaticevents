package PrismaticEvents.events;

import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import java.util.ArrayList;
import java.util.Collections;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import PrismaticEvents.PrismaticEventsMod;
import PrismaticEvents.util.PrismaticRewardHelper;

public class PrismaBlueEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(PrismaBlueEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    
    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(PrismaBlueEvent.class.getSimpleName() + ".png");

    private CurScreen screen = CurScreen.INTRO;

    private int failRate;
    private int successRate;
    private int changeRate;
    private String combatKey;

    private enum CurScreen {
        INTRO, GAME, REWARD, LEAVE, FIGHT, POST_COMBAT;
        
    }

    public PrismaBlueEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15){
            this.failRate = 20;
            this.successRate = 20;
        } else {
            this.failRate = 15;
            this.successRate = 25;
        }
        this.changeRate = failRate;
        this.noCardsInRewards = true;
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPress) {
        switch(this.screen){
            case INTRO:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.screen = CurScreen.GAME;
                this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.successRate + OPTIONS[5] + this.failRate + OPTIONS[6]);
                this.imageEventText.setDialogOption(OPTIONS[2]);
                return;
            case GAME:
                switch(buttonPress){
                    case 0:
                        int randomNum = AbstractDungeon.eventRng.random(0, 100);
                        if (randomNum < successRate){ // Success!
                            this.screen = CurScreen.REWARD;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                            this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                            this.imageEventText.clearRemainingOptions();

                        } else if (randomNum >= 100 - failRate){ // Ambushed
                            this.screen = CurScreen.FIGHT;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                            this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                            this.imageEventText.clearRemainingOptions();

                        } else { // Nothing!
                            this.failRate += this.changeRate;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.successRate + OPTIONS[5] + this.failRate + OPTIONS[6]);
                        }
                        break;
                    case 1:
                        this.screen = CurScreen.LEAVE;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                return;
            case REWARD:
                switch(buttonPress){
                    case 0:
                    this.screen = CurScreen.LEAVE;
                    this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                    reward();
                    break;
                }
                return;
            case FIGHT:
                switch(buttonPress){
                    case 0:
                        this.screen = CurScreen.POST_COMBAT;
                        (AbstractDungeon.getCurrRoom()).rewardAllowed = false;
                        (AbstractDungeon.getCurrRoom()).monsters = MonsterHelper.getEncounter(getMonster());
                        applyBuffs();
                        enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = this.combatKey;
                        break;
                }
                this.imageEventText.clearRemainingOptions();
                return;
            case POST_COMBAT:
                (AbstractDungeon.getCurrRoom()).rewardAllowed = true;
                switch(buttonPress){
                    case 0:
                        this.screen = CurScreen.LEAVE;
                        reward();
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        break;
                }
                return;
        }
        openMap();
    }

    @Override
    public void reopen() {
        if (this.screen != CurScreen.LEAVE){
            AbstractDungeon.resetPlayer();
            AbstractDungeon.player.drawX = Settings.WIDTH * 0.25F;
            AbstractDungeon.player.preBattlePrep();
            enterImageFromCombat();
            this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
            this.imageEventText.updateDialogOption(0, OPTIONS[4]);
            this.imageEventText.clearRemainingOptions();
        }
    }

    private void applyBuffs() {
    }

    private String getMonster() {
        int actNum = AbstractDungeon.actNum;
        ArrayList<String> combats = new ArrayList<String>();
        if (actNum == 1){
            combats.add("Cultist");
            combats.add("Jaw Worm");
            combats.add("2 Louse");
        }
        if (actNum == 2){
            combats.add("Spheric Guardian");
            combats.add("Shell Parasite");
            combats.add("2 Thieves");
        }
        if (actNum == 3){
            combats.add("Orb Walker");
            combats.add("3 Darklings");
            combats.add("3 Shapes");
        }
        Collections.shuffle(combats, AbstractDungeon.eventRng.random);
        this.combatKey = combats.get(0);
        return combatKey;
    }

    private void reward() {
        (AbstractDungeon.getCurrRoom()).rewards.clear();
        AbstractDungeon.getCurrRoom().addCardReward(PrismaticRewardHelper.PrismaticReward(CardCrawlGame.characterManager.getCharacter(PlayerClass.DEFECT)));
        (AbstractDungeon.getCurrRoom()).phase = RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = CurScreen.LEAVE;
    }
    
}
