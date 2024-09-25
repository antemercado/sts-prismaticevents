package PrismaticEvents.events;

import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;

import PrismaticEvents.PrismaticEventsMod;

public class DevilishTraderEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(DevilishTraderEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    
    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(DevilishTraderEvent.class.getSimpleName() + ".png");

    public DevilishTraderEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
    }

    @Override
    protected void buttonEffect(int arg0) {
    }
    
}
