package PrismaticEvents.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import PrismaticEvents.PrismaticEventsMod;
import PrismaticEvents.util.TexLoader;
import basemod.abstracts.CustomRelic;

import static PrismaticEvents.PrismaticEventsMod.makeRelicOutlinePath;
import static PrismaticEvents.PrismaticEventsMod.makeRelicPath;

public class DevoutSoulRelic extends CustomRelic {

    public static final String ID = PrismaticEventsMod.makeID("DevoutSoulRelic");

    private static final Texture IMG = TexLoader.getTexture(makeRelicPath("purple_prisma.png"));
    private static final Texture OUTLINE = TexLoader.getTexture(makeRelicOutlinePath("purple_prisma.png"));
    
    private boolean firstTurn = true;
    

    public DevoutSoulRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }
    
    @Override
    public void atPreBattle() {
        this.firstTurn = true;
    }

    @Override
    public void atTurnStart() {
        if (!this.firstTurn) {
            return;
        }
        if (this.counter < 0){
            return;
        }
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        addToBot(new LoseEnergyAction(this.counter));
        this.firstTurn = false;
        setCounter(-2);
    }

    @Override
    public void setCounter(int counter) {
        this.counter = counter;
        if (counter <= 0){
            usedUp();
            this.description = this.DESCRIPTIONS[2];
            this.tips.clear();
            this.tips.add(new PowerTip(this.name, this.description));
            return;
        }
        this.description = DESCRIPTIONS[0] + this.counter + DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + this.counter + DESCRIPTIONS[1];
    }
}
