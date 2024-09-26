package PrismaticEvents.events;

import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_CARD_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_CARD_UPGRADE_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_GOLD_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_RELIC_KEY;
import static PrismaticEvents.PrismaticEventsMod.NEOWS_FALLEN_RUN_FAILED;
import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import PrismaticEvents.PrismaticEventsMod;

public class NeowsFallenEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(NeowsFallenEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath(NeowsFallenEvent.class.getSimpleName() + ".png");
    private static final String RELIC_KEY = NEOWS_FALLEN_RELIC_KEY;
    private static final String CARD_KEY = NEOWS_FALLEN_CARD_KEY;
    private static final String CARD_UPGRADE_KEY = NEOWS_FALLEN_CARD_UPGRADE_KEY;
    private static final String GOLD_KEY = NEOWS_FALLEN_GOLD_KEY;
    private static final String RUN_FAILED = NEOWS_FALLEN_RUN_FAILED;
    private AbstractCard obtainCard;
    private AbstractRelic obtainRelic;
    private String obtainRelicID;
    private CurScreen screen = CurScreen.INTRO;
    private int goldReward;

    private static final Logger logger = LogManager.getLogger(NeowsFallenEvent.class.getName());

    private enum CurScreen {
        INTRO, INTRO_2, COMPLETE;
    }

    public NeowsFallenEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.imageEventText.setDialogOption(OPTIONS[0]);
        CardCrawlGame.playerPref.putBoolean(RUN_FAILED, false);
        initializeRewards();
    }

    public void initializeRewards(){
        
        relicRewardInitialize();

        this.obtainCard = CardLibrary.getCard(CardCrawlGame.playerPref.getString(CARD_KEY, "Iron Wave"));
        this.goldReward = CardCrawlGame.playerPref.getInteger(GOLD_KEY, 300);
        
        if (this.obtainCard == null){
            this.obtainCard = new IronWave();
            logger.info("Neows Fallen Card was null. Generated an Iron Wave");
        }
        logger.info("Neows Fallen Card: " + this.obtainCard.cardID);
        
        this.goldReward = (int)(this.goldReward * 0.33f);
        logger.info("Neows Fallen Gold: " + this.goldReward);

        
        for (int i=0; i < CardCrawlGame.playerPref.getInteger(CARD_UPGRADE_KEY, 0); i++){
            this.obtainCard.upgrade();
        }
    }
    
    private void relicRewardInitialize() {
        this.obtainRelicID = CardCrawlGame.playerPref.getString(RELIC_KEY, "Red Skull");
        this.obtainRelic = RelicLibrary.getRelic(this.obtainRelicID);

        switch(this.obtainRelic.tier){
            case BOSS:
                removeRelicFromPool(AbstractDungeon.bossRelicPool, this.obtainRelicID);
                logger.info("Neows Fallen relic removed from Boss pool");
                break;
            case COMMON:
                removeRelicFromPool(AbstractDungeon.commonRelicPool, this.obtainRelicID);
                logger.info("Neows Fallen relic removed from Common pool");
                break;
            case RARE:
                removeRelicFromPool(AbstractDungeon.rareRelicPool, this.obtainRelicID);
                logger.info("Neows Fallen relic removed from Rare pool");
                break;
            case SHOP:
                removeRelicFromPool(AbstractDungeon.shopRelicPool, this.obtainRelicID);
                logger.info("Neows Fallen relic removed from Shop pool");
                break;
            case UNCOMMON:
                removeRelicFromPool(AbstractDungeon.uncommonRelicPool, this.obtainRelicID);
                logger.info("Neows Fallen relic removed from Uncommon pool");
                break;
            default:
                break;
            
        }

        logger.info("Neows Fallen Relic: " + this.obtainRelic.relicId);
    }

    private void removeRelicFromPool(ArrayList<String> relicPool, String relicID) {
        Iterator<String> s;
        for (s = relicPool.iterator(); s.hasNext();){
            String x = s.next();
            if (x.equals(relicID)){
                s.remove();
                break;
            }
        }
    }

    @Override
    protected void buttonEffect(int buttonPress) {
        switch (this.screen) {
            case INTRO:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.obtainCard.name, this.obtainCard);
                this.imageEventText.setDialogOption(OPTIONS[2] + this.obtainRelic.name, this.obtainRelic);
                this.imageEventText.setDialogOption(OPTIONS[3] + this.goldReward + OPTIONS[5]);
                this.screen = CurScreen.INTRO_2;
                return;
            case INTRO_2:
                switch (buttonPress) {
                    case 0:
                        //Card
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.obtainCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        break;
                    case 1:
                        //Relic
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), obtainRelic);
                        break;
                    case 2:
                        //Gold
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldReward));
                        AbstractDungeon.player.gainGold(this.goldReward);
                    break;
                }
                this.screen = CurScreen.COMPLETE;
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                this.imageEventText.clearRemainingOptions();
                (AbstractDungeon.getCurrRoom()).phase = RoomPhase.COMPLETE;
                return;
        }
        openMap();
    }
}
