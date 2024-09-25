package PrismaticEvents.events;

import static PrismaticEvents.PrismaticEventsMod.makeEventPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

import PrismaticEvents.PrismaticEventsMod;

public class PowerInsideEvent extends AbstractImageEvent{

    public static final String ID = PrismaticEventsMod.makeID(PowerInsideEvent.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    
    private static final String NAME = eventStrings.NAME;
    private static String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = makeEventPath(PowerInsideEvent.class.getSimpleName() + ".png");
    private HashMap<PlayerClass, ArrayList<String>> relicHash = new HashMap<>();
    private ArrayList<PlayerClass> classArray = new ArrayList<>();
    private PlayerClass class1;
    private PlayerClass class2;
    private PlayerClass class3;
    private AbstractRelic[] relicArray;
    private int damage;
    private int maxHPLoss;
    private AbstractRelic relicLoss = null;
    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO, PAYMENT, LEAVE;
    }

    public PowerInsideEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        
        //Generate HashMap with base game Character's Relics
        AbstractPlayer[] tmpPlayers = {
            CardCrawlGame.characterManager.getCharacter(PlayerClass.IRONCLAD), 
            CardCrawlGame.characterManager.getCharacter(PlayerClass.THE_SILENT),
            CardCrawlGame.characterManager.getCharacter(PlayerClass.DEFECT),
            CardCrawlGame.characterManager.getCharacter(PlayerClass.WATCHER),
        };

        for (AbstractPlayer p: tmpPlayers){
            if (p.chosenClass != AbstractDungeon.player.chosenClass){
                relicHash.put(p.chosenClass,p.getStartingRelics());
                classArray.add(p.chosenClass);
            }
        }

        Collections.shuffle(classArray, AbstractDungeon.eventRng.random);
        this.class1 = classArray.get(0);
        this.class2 = classArray.get(1);

        //Generate HashMap with all Character's Relics
        ArrayList<AbstractPlayer> chars = CardCrawlGame.characterManager.getAllCharacters();
        for (AbstractPlayer p : chars){
            if (p.chosenClass == AbstractDungeon.player.chosenClass){
                continue;
            }
            if (classArray.contains(p.chosenClass)){
                continue;
            }
            relicHash.put(p.chosenClass,p.getStartingRelics());
            classArray.add(p.chosenClass);
        }
        Collections.shuffle(classArray, AbstractDungeon.eventRng.random);

        this.class3 = classArray.get(2);
        
        AbstractRelic[] tmpArray = {
            RelicLibrary.getRelic(relicHash.get(class1).get(0)),
            RelicLibrary.getRelic(relicHash.get(class2).get(0)),
            RelicLibrary.getRelic(relicHash.get(class3).get(0))
        };
        
        this.relicArray = tmpArray;
        
        PlayerClass[] selectedClassArray = {class1,class2,class3};

        for (int i = 0; i < 3; i++){
            switch(selectedClassArray[i].toString()){
                case "IRONCLAD":
                    imageEventText.setDialogOption(OPTIONS[0] + relicArray[i].name, relicArray[i].makeCopy());
                    break;
                case "THE_SILENT":
                    imageEventText.setDialogOption(OPTIONS[1] + relicArray[i].name, relicArray[i].makeCopy());
                    break;
                case "DEFECT":
                    imageEventText.setDialogOption(OPTIONS[2] + relicArray[i].name, relicArray[i].makeCopy());
                    break;
                case "WATCHER":
                    imageEventText.setDialogOption(OPTIONS[3] + relicArray[i].name, relicArray[i].makeCopy());
                    break;
                case "HERMIT":
                    imageEventText.setDialogOption(OPTIONS[4] + relicArray[i].name, relicArray[i].makeCopy());
                    break;
                default:
                    imageEventText.setDialogOption(OPTIONS[5] + relicArray[i].name, relicArray[i].makeCopy());
                    break;
            }
        }

        if (AbstractDungeon.ascensionLevel >= 15){
            this.damage = (int)(AbstractDungeon.player.maxHealth * 0.25F);
            this.maxHPLoss = (int)(AbstractDungeon.player.maxHealth * 0.15F);
        } else {
            this.damage = (int)(AbstractDungeon.player.maxHealth * 0.15F);
            this.maxHPLoss = (int)(AbstractDungeon.player.maxHealth * 0.10F);
        }

        ArrayList<AbstractRelic> playerRelics = new ArrayList<>();
        for (AbstractRelic r : AbstractDungeon.player.relics){
            if (r.tier.equals(RelicTier.STARTER)){
                playerRelics.add(r);
            }
        }
        if (playerRelics.size() > 0){
            this.relicLoss = playerRelics.get(0); 
        }

    }

    @Override
    protected void buttonEffect(int buttonPress) {
        switch(this.screen){
            case INTRO:
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.relicArray[buttonPress].makeCopy());
                this.screen = CurScreen.PAYMENT;
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.updateDialogOption(0, OPTIONS[6] + this.damage + OPTIONS[11]);
                this.imageEventText.updateDialogOption(1, OPTIONS[7] + this.maxHPLoss + OPTIONS[12]);
                if (this.relicLoss != null){
                    this.imageEventText.updateDialogOption(2, OPTIONS[8] + this.relicLoss.name);
                } else {
                    imageEventText.updateDialogOption(2, OPTIONS[9], true);
                }
                return;
            case PAYMENT:
                this.screen = CurScreen.LEAVE;
                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                imageEventText.updateDialogOption(0, OPTIONS[10]);
                this.imageEventText.clearRemainingOptions();
                switch(buttonPress){
                    case 0:
                        AbstractDungeon.player.damage(new DamageInfo(null, this.damage, DamageType.HP_LOSS));
                        return;
                    case 1:
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHPLoss);
                        return;
                    case 2:
                        AbstractDungeon.player.loseRelic(this.relicLoss.relicId);
                        return;
                }
                break;
            default:
                openMap();
                return;
            
        }
    }
    
}
