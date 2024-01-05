package com.shatteredpixel.bathredpixeldungeon.actors.buffs;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.effects.Speck;
import com.shatteredpixel.bathredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.bathredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.bathredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class EscapeRoll extends Buff implements ActionIndicator.Action {

    {
        type = buffType.POSITIVE;

        //acts before the hero
        actPriority = HERO_PRIO+1;
    }

    private int rollCoolDown = 0;
    private  int maxCoolDown = 8;

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    @Override
    public boolean act() {
        if (rollCoolDown > 0) {
            rollCoolDown--;
        }
        else
            ActionIndicator.setAction(this);
        spend(TICK);
        return true;
    }

    @Override
    public int icon() {
        if (rollCoolDown > 0)
            return BuffIndicator.MOMENTUM;
        else return BuffIndicator.NONE;
    }

    @Override
    public void tintIcon(Image icon) {
        if (rollCoolDown == 0){
            icon.hardlight(1,1,0);
        } else {
            icon.hardlight(0.5f,0.5f,1);
        }
    }

    @Override
    public float iconFadePercent() {
        if (rollCoolDown > 0){
            return (maxCoolDown - rollCoolDown) / maxCoolDown;
        } else {
            return 0;
        }
    }

    @Override
    public String iconTextDisplay() {
        if (rollCoolDown > 0){
            return Integer.toString(rollCoolDown);
        } else {
            return "";
        }
    }

    @Override
    public String name() {
        return Messages.get(this, "running");
    }

    @Override
    public String desc() {
        return Messages.get(this, "running_desc", rollCoolDown);
    }

    private static final String ROLL_CD =        "roll_cd";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ROLL_CD, rollCoolDown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        rollCoolDown = bundle.getInt(ROLL_CD);
        if (rollCoolDown > 0){
            ActionIndicator.setAction(this);
        }
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.MOMENTUM;
    }
    @Override
    public Visual secondaryVisual() {
        return null;
    }

    @Override
    public int indicatorColor() {
        return 0x009933;
    }

    @Override
    public void doAction() {
        Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 0.8f);
        target.sprite.emitter().burst(Speck.factory(Speck.JET), 10);
        SpellSprite.show(target, SpellSprite.HASTE, 1, 1, 0);
        rollCoolDown = maxCoolDown;
        Buff.affect(Dungeon.hero, Swiftthistle.TimeBubble.class).setLeft(0.5f);
        BuffIndicator.refreshHero();
        ActionIndicator.clearAction(this);
    }
}
