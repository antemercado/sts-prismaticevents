package PrismaticEvents.shop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import PrismaticEvents.PrismaticEventsMod;
import basemod.abstracts.CustomScreen;

import static PrismaticEvents.PrismaticEventsMod.makeNPCPath;

public class DevilShopScreen extends CustomScreen{



    public void open(){
        CardCrawlGame.sound.play("SHOP_OPEN");
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = curScreen();
    }

    @Override
    public void close() {
    }

    @Override
    public CurrentScreen curScreen() {
        return PrismaticEventsMod.DEVIL_SHOP;
    }

    @Override
    public void openingSettings() {
        AbstractDungeon.previousScreen = curScreen();
    }

    @Override
    public void render(SpriteBatch arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

    @Override
    public void reopen() {
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.isScreenUp = true;
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
    // private static Texture handImg = null;
    // private float HAND_W;
    // private float HAND_H;
    // private boolean purgeAvailable;

    // public void init() {
    //     handImg = ImageMaster.loadImage(makeNPCPath("devilHand.png"));

    //     HAND_W = handImg.getWidth() * Settings.scale;
    //     HAND_H = handImg.getHeight() * Settings.scale;

    //     initRelics();
    //     initPotions();

    //     this.purgeAvailable = false;
    // }

    // private void initPotions() {
    // }

    // private void initRelics() {
    // }


}
