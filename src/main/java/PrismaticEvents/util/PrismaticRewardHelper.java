package PrismaticEvents.util;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class PrismaticRewardHelper {
    public static RewardItem PrismaticReward(AbstractPlayer cardPlayer) {
        RewardItem ret = new RewardItem();
        
        ArrayList<AbstractCard> tmp = new ArrayList<>();
        cardPlayer.getCardPool(tmp);
        
        //Populate cardpool
        CardGroup cardPool = new CardGroup(CardGroupType.UNSPECIFIED);
        for (AbstractCard c : tmp){
            if (c.type != CardType.CURSE && c.type != CardType.STATUS && !UnlockTracker.isCardLocked(c.cardID)){
                cardPool.addToBottom(c);
            }
        }

        CardRarity actRarity = CardRarity.COMMON;
        if (AbstractDungeon.actNum == 2){
            actRarity = CardRarity.UNCOMMON;
        }
        if (AbstractDungeon.actNum == 3){
            actRarity = CardRarity.RARE;
        }
        
        ArrayList<AbstractCard> rewardCards = new ArrayList<>();
        AbstractCard reward = null;
        
        for (AbstractCard c : ret.cards){
            switch (c.type){
                case ATTACK:
                    reward = cardPool.getAttacks().getRandomCard(true, actRarity);
                    break;
                case SKILL:
                    reward = cardPool.getSkills().getRandomCard(true, actRarity);
                    break;
                case POWER:
                    reward = cardPool.getPowers().getRandomCard(true, actRarity);
                    break;
                default:
                    reward = cardPool.getRandomCard(true);
                    break;
            }
            if (reward == null){
                reward = cardPool.getRandomCard(true);
            }

            if (c.upgraded){
                reward.upgrade();
            }

            cardPool.removeCard(reward);
            rewardCards.add(reward);
            reward = reward.makeCopy();
        }

        ret.cards = rewardCards;

        return ret;
    }
}
