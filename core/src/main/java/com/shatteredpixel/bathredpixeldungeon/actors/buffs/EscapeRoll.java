package com.shatteredpixel.bathredpixeldungeon.actors.buffs;

import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_FROGRANGE;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_FROGTIME;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_FROGWATER;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_INSTANTBULLET;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLCRIT;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLDEG1;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLDIST;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLERCONF;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLERRANDOM;
import static com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent.GIUX_ROLLERRANGE;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.abilities.giux.FrogJump;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.effects.Splash;
import com.shatteredpixel.bathredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.bathredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.bathredpixeldungeon.levels.Terrain;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.bathredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.bathredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.bathredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.bathredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class EscapeRoll extends Buff implements ActionIndicator.Action {

    {
        type = buffType.POSITIVE;

        //acts before the hero
        actPriority = HERO_PRIO+1;
    }

    private int rollCoolDown = 0;
    private  int maxCoolDown = 10;

    private int distance = 2;
    private int freeRolls = 0;
    private boolean giveHaste = false;
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
    private static final String ROLL_MAXCD =        "roll_maxcd";
    private static final String ROLL_DISTANCE =        "roll_distance";
    private static final String FREE_ROLLS =        "free_rolls";
    private static final String GIVE_HASTE =        "give_haste";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ROLL_CD, rollCoolDown);
        bundle.put(ROLL_MAXCD, maxCoolDown);
        bundle.put(ROLL_DISTANCE, distance);
        bundle.put(FREE_ROLLS, freeRolls);
        bundle.put(GIVE_HASTE, giveHaste);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        rollCoolDown = bundle.getInt(ROLL_CD);
        maxCoolDown = bundle.getInt(ROLL_MAXCD);
        distance = bundle.getInt(ROLL_DISTANCE);
        freeRolls = bundle.getInt(FREE_ROLLS);
        giveHaste = bundle.getBoolean(GIVE_HASTE);
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
        int dist = distance;
        if (hero.hasTalent(GIUX_FROGRANGE)){
            int poir = hero.pointsInTalent(GIUX_FROGRANGE);
            if (poir != 3) {
                dist += (poir == 4) ? 3 : poir;
            } else {
                dist += (Random.Int(2) == 0) ? 3 : 2;
            }
        }
        for (int y = Math.max(0, c.y - dist); y <= Math.min(Dungeon.level.height()-1, c.y + dist); y++) {
            left = c.x - dist;
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
                if (Dungeon.level.distance(hero.pos, cell) > distance + hero.pointsInTalent(GIUX_FROGRANGE)){
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
        maxCoolDown = 10 + 5 * h.pointsInTalent(GIUX_ROLLDIST) + (h.subClass == HeroSubClass.ROLLER ? 5 : 0);
    }

    public void updateFreeRolls(int c) {
        freeRolls = c;
        if (c > 2)
            freeRolls--;
        if (c > 3)
            giveHaste = true;
    }

    private void DoRoll(Hero hero, int target) {
        Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        int backTrace = route.dist - 1;
        while (Actor.findChar(cell) != null && cell != hero.pos) {
            cell = route.path.get(backTrace);
            backTrace--;
        }

        if (hero.subClass == HeroSubClass.ROLLER) {
            ArrayList<Actor> affectedMobs = new ArrayList<>();
            for (int i : route.path) {
                Actor act = Actor.findChar(i);
                if (act != null) {
                    if (act instanceof Mob) {
                        Mob b = (Mob) act;
                        if (hero.pointsInTalent(GIUX_ROLLERCONF) == 3) {
                            Buff.prolong(b, Vertigo.class, 5);
                            Buff.prolong(b, Daze.class, 5);
                        } else {
                            Buff.prolong(b, Vertigo.class, 3 + hero.pointsInTalent(GIUX_ROLLERCONF));
                        }
                        affectedMobs.add(act);
                    }
                }
            }
            if (hero.hasTalent(GIUX_ROLLERRANGE)) {
                int dist = 1;
                int maxTouch = hero.pointsInTalent(GIUX_ROLLERRANGE);
                for (int x : route.path) {
                    Point c = Dungeon.level.cellToPoint(x);
                    int left, right;
                    int curr;
                    for (int y = Math.max(0, c.y - dist); y <= Math.min(Dungeon.level.height() - 1, c.y + dist); y++) {
                        left = c.x - dist;
                        right = Math.min(Dungeon.level.width() - 1, c.x + c.x - left);
                        left = Math.max(0, left);
                        for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++) {
                            Actor act = Actor.findChar(curr);
                            if (act != null) {
                                if (act instanceof Mob && !affectedMobs.contains(act)) {
                                    Mob b = (Mob) act;
                                    if (hero.pointsInTalent(GIUX_ROLLERCONF) == 3) {
                                        Buff.prolong(b, Vertigo.class, 5);
                                        Buff.prolong(b, Daze.class, 3);
                                    } else {
                                        Buff.prolong(b, Vertigo.class, 3 + hero.pointsInTalent(GIUX_ROLLERCONF));
                                    }
                                    affectedMobs.add(act);
                                    maxTouch--;
                                }
                            }
                            if (maxTouch == 0)
                                break;
                        }
                    }
                }
            }
            if (affectedMobs.size() > 0) {
                if (hero.hasTalent(GIUX_ROLLERRANDOM)) {
                    if (Random.Int(100) < hero.pointsInTalent(GIUX_ROLLERRANDOM) * 10) {
                        affectRandomBuff(hero, 20f);
                    }
                }
            }
            BuffIndicator.refreshHero();
        }

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
                if (hero.hasTalent(GIUX_ROLLDEG1)) {
                    Buff.affect(hero, RolledBullet.class).set(1 + hero.pointsInTalent(GIUX_ROLLDEG1));
                }
                if (hero.hasTalent(GIUX_ROLLCRIT)) {
                    Buff.affect(hero, RollCrit.class, 1f).set(hero.pointsInTalent(GIUX_ROLLCRIT));
                }
                if (hero.pointsInTalent(GIUX_INSTANTBULLET) >= 2) {
                    Buff.affect(hero, InstantBullet.class, 5);
                }
                if (hero.buff(FrogJump.FrogJumpBuff.class) == null){
                    rollCoolDown = maxCoolDown + 1;
                    ActionIndicator.clearAction(hero.buff(EscapeRoll.class));
                }
                else {
                    if (hero.hasTalent(GIUX_FROGWATER)) {
                        frogSplash(dest);
                    }
                    rollCoolDown = 0;
                    hero.buff(FrogJump.FrogJumpBuff.class).update();
                }
                if (Dungeon.level.map[dest] == Terrain.WATER && hero.pointsInTalent(GIUX_FROGWATER) >= 2) {
                    Buff.affect(hero, Healing.class).setHeal(5, 0.3f, 2);
                }

                if (freeRolls > 0) {
                    freeRolls--;
                    if (freeRolls == 0 && giveHaste) {
                        giveHaste = false;
                        Buff.affect(hero, Swiftthistle.TimeBubble.class).setLeft(3f);
                    }
                    hero.spendAndNext(0);
                }
                else {
                    hero.spendAndNext(Actor.TICK);
                }
            }
        });
    }

    public void frogSplash(int pos){
        Sample.INSTANCE.play(Assets.Sounds.WATER, 1f, 0.75f);
        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
        //Splash.at( DungeonTilemap.tileCenterToWorld( pos ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 100, 0.01f);
        //PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
        int chna = hero.pointsInTalent(GIUX_FROGWATER);
        if (chna > 2)
            chna = 3;
        else chna = 1;
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Random.Int(5) < chna) {
                Dungeon.level.setCellToWater(true, pos + i);
                if (fire != null){
                    fire.clear(pos + i);
                }
            }
            if (hero.pointsInTalent(GIUX_FROGWATER) == 4){
                Char ch = Actor.findChar(pos + i);
                if (ch != null && ch != hero) {
                    //trace a ballistica to our target (which will also extend past them)
                    Ballistica trajectory = new Ballistica(pos, ch.pos, Ballistica.STOP_TARGET);
                    //trim it to just be the part that goes past them
                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                    //knock them back along that ballistica
                    WandOfBlastWave.throwChar(ch, trajectory, 4, true, true, hero);
                }
            }
        }
    }

    public static void affectRandomBuff (Hero hero, float time)
    {
        switch (Random.Int(10)) {
            case 0:
                Buff.affect(hero, Adrenaline.class, time);
                break;
            case 1:
                Buff.affect(hero, Barrier.class).setShield((int)time);
                break;
            case 2:
                Buff.affect(hero, BathredBullets.class);
                break;
            case 3:
                Buff.affect(hero, Bless.class, time);
                break;
            case 4:
                Buff.affect(hero, MagicImmune.class, time);
                break;
            case 5:
                Buff.affect(hero, FireImbue.class).set(time);
                break;
            case 6:
                Buff.affect(hero, FrostImbue.class, time);
                break;
            case 7:
                Buff.affect(hero, Invisibility.class, time);
                break;
            case 8:
                Buff.affect(hero, Haste.class, time);
                break;
            case 9:
                Buff.affect(hero, Healing.class).setHeal((int)time, (int)time / 5, 3);
                break;
        }
    }
}
