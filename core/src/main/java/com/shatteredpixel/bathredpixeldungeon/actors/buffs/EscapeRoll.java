package com.shatteredpixel.bathredpixeldungeon.actors.buffs;

import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.EMPOWERING_MEAL;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLDEG1;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLDIST;

import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.bathredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.bathredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;

public class EscapeRoll extends Buff implements ActionIndicator.Action {

    {
        type = buffType.POSITIVE;

        //acts before the hero
        actPriority = HERO_PRIO+1;
    }

    private int rollCoolDown = 0;
    private  int maxCoolDown = 10;

    private int distance = 2;

    private Hero hero;

    public void setHero(Hero h){
        hero = h;
    }
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

    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public void doAction() {
        if (hero.rooted) {
            PixelScene.shake(0.5f, 0.5f);
            return;
        }

        Point c = Dungeon.level.cellToPoint(hero.pos);
        int left, right;
        int curr;
        for (int y = Math.max(0, c.y - distance); y <= Math.min(Dungeon.level.height()-1, c.y + distance); y++) {
            left = c.x - distance;
            right = Math.min(Dungeon.level.width()-1, c.x + c.x - left);
            left = Math.max(0, left);
            for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++) {
                hero.sprite.parent.add(new TargetedCell(curr, 0x00CC66));
            }
        }

        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public void onSelect(Integer cell) {
                if (cell == null) return;
                if (Dungeon.level.distance(hero.pos, cell) > distance){
                    GLog.w(Messages.get(Combo.class, "bad_target"));
                }
                else
                    DoRoll(hero, cell);
            }

            @Override
            public String prompt() {
                return targetingPrompt();
            }
        });

        /*Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 0.8f);
        target.sprite.emitter().burst(Speck.factory(Speck.JET), 10);
        SpellSprite.show(target, SpellSprite.HASTE, 1, 1, 0);
        Buff.affect(Dungeon.hero, Swiftthistle.TimeBubble.class).setLeft(0.5f);*/
    }

    public void UpdateDistance(Hero h)
    {
        distance = 2 + h.pointsInTalent(GIUX_ROLLDIST);
        maxCoolDown = 10 + 5 * h.pointsInTalent(GIUX_ROLLDIST);
    }

    private void DoRoll(Hero hero, int target) {
        Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        int backTrace = route.dist-1;
        while (Actor.findChar( cell ) != null && cell != hero.pos) {
            cell = route.path.get(backTrace);
            backTrace--;
        }

        rollCoolDown = maxCoolDown + 1;
        BuffIndicator.refreshHero();
        ActionIndicator.clearAction(this);

        final int dest = cell;
        hero.busy();
        hero.sprite.jump(hero.pos, cell, new Callback() {
            public void call() {
                hero.move(dest);
                Dungeon.level.occupyCell(hero);
                Dungeon.observe();
                GameScene.updateFog();
                PixelScene.shake(0.2f, 0.5f);
                Invisibility.dispel();
                if (hero.hasTalent(GIUX_ROLLDEG1)){
                    Buff.affect( hero, ExtraBullet.class).set(1 + hero.pointsInTalent(GIUX_ROLLDEG1));
                }

                hero.spendAndNext(Actor.TICK);
            }
        });
    }
}
