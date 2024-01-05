/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.bathredpixeldungeon.items.weapon.melee;

import static com.shatteredpixel.bathredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.Challenges;
import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.InfiniteBullet;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.bathredpixeldungeon.items.Item;
import com.shatteredpixel.bathredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.bathredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AutoRifle extends MeleeWeapon {

    public static final String AC_SHOOT		= "SHOOT";
    public static final String AC_RELOAD = "RELOAD";
    public static final String AC_AUTO = "AUTO";

    public int max_round;
    public int round = 0;
    public float reload_time;
    public boolean auto = false;
    private static final String TXT_STATUS = "%d/%d";

    {

        defaultAction = AC_SHOOT;
        usesTargeting = true;

        image = ItemSpriteSheet.AUTO_RIFLE;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 0.8f;

        tier = 5;

        gun = true;
        machineGun = true;
    }

    private static final String ROUND = "round";
    private static final String MAX_ROUND = "max_round";
    private static final String RELOAD_TIME = "reload_time";
    private static final String SILENCER = "silencer";
    private static final String SHORT_BARREL = "short_barrel";
    private static final String LONG_BARREL = "long_barrel";
    private static final String MAGAZINE = "magazine";
    private static final String LIGHT = "light";
    private static final String HEAVY = "heavy";
    private static final String FLASH = "flash";
    private static final String AUTO = "auto";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MAX_ROUND, max_round);
        bundle.put(ROUND, round);
        bundle.put(RELOAD_TIME, reload_time);
        bundle.put(AUTO, auto);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        max_round = bundle.getInt(MAX_ROUND);
        round = bundle.getInt(ROUND);
        reload_time = bundle.getFloat(RELOAD_TIME);
        auto = bundle.getBoolean(AUTO);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped( hero )) {
            actions.add(AC_SHOOT);
            actions.add(AC_RELOAD);
            actions.add(AC_AUTO);
        }
        return actions;
    }



    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {

            if (!isEquipped( hero )) {
                usesTargeting = false;
                GLog.w(Messages.get(this, "not_equipped"));
            } else {
                if (round <= 0) {
                    reload_time = 2f;
                    reload();
                } else {
                    reload_time = 2f;
                    usesTargeting = true;
                    curUser = hero;
                    curItem = this;
                    GameScene.selectCell(shooter);
                }
            }
        }
        if (action.equals(AC_RELOAD)) {
            max_round = 12;

            if (round == max_round){
                GLog.w(Messages.get(this, "already_loaded"));
            } else {
                reload();
            }
        }
        if (action.equals(AC_AUTO)) {
            if (auto) {
                auto = false;
                GLog.i(Messages.get(AutoHandgun.class, "semi"));
            } else {
                auto = true;
                GLog.i(Messages.get(AutoHandgun.class, "auto"));
            }
            curUser.spend(Actor.TICK);
            curUser.busy();
            Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2, 1.1f);
            curUser.sprite.operate(curUser.pos);
        }
    }

    public void reload() {
        max_round = 12;

        curUser.spend(reload_time);
        curUser.busy();
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2, 1.1f);
        curUser.sprite.operate(curUser.pos);
        round = Math.max(max_round, round);

        GLog.i(Messages.get(this, "reloading"));

        updateQuickslot();
    }


    public int getRound() { return this.round; }

    public void oneReload() {
        max_round = 12;
        round ++;
        if (round > max_round) {
            round = max_round;
        }
    }

    @Override
    public String status() {
        max_round = 12;
        return Messages.format(TXT_STATUS, round, max_round);
    }

    @Override
    public int STRReq(int lvl) {
        int needSTR = STRReq(tier, lvl);
        return needSTR;
    }

    public int min(int lvl) {
        return tier +
                lvl;
    }

    public int max(int lvl) {
        return 3 * (tier + 1) +
                lvl * (tier + 1);
    }

    public int Bulletmin(int lvl) {
        return tier +
                lvl +
                RingOfSharpshooting.levelDamageBonus(Dungeon.hero);
    }

    public int Bulletmax(int lvl) {
        if (auto) {
            return 2 * (tier)   +
                    lvl * (tier-2) +
                    RingOfSharpshooting.levelDamageBonus(Dungeon.hero);
        } else {
            return 4 * (tier)   +
                    lvl * (tier) +
                    RingOfSharpshooting.levelDamageBonus(Dungeon.hero);
        }
    }

    @Override
    public int proc( Char attacker, Char defender, int damage ) {
        return super.proc( attacker, defender, damage );
    }

    @Override
    public String info() {

        max_round = 12;
        reload_time = 2f;
        String info = super.info();

        if (levelKnown) {
            info += "\n\n" + Messages.get(CrudePistol.class, "stats_known",
                    Bulletmin(this.buffedLvl()),
                    Bulletmax(this.buffedLvl()),
                    round, max_round, new DecimalFormat("#.##").format(reload_time));
        } else {
            info += "\n\n" + Messages.get(CrudePistol.class, "stats_unknown",
                    Bulletmin(0),
                    Bulletmax(0),
                    round, max_round, new DecimalFormat("#.##").format(reload_time));
        }

        return info;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return knockBullet().targetingPos(user, dst);
    }

    private int targetPos;

    @Override
    public int damageRoll(Char owner) {
        int damage = augment.damageFactor(super.damageRoll(owner));

        if (owner instanceof Hero) {
            int exStr = ((Hero)owner).STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange( 0, exStr );
            }
        }

        return damage;
    }                           //초과 힘에 따른 추가 데미지

    @Override
    protected float baseDelay(Char owner) {
        float delay = augment.delayFactor(this.DLY);
        if (owner instanceof Hero) {
            int encumbrance = STRReq() - ((Hero)owner).STR();
            if (encumbrance > 0){
                delay *= Math.pow( 1.2, encumbrance );
            }
        }
        return delay;
    }

    public AutoRifle.Bullet knockBullet(){
        return new AutoRifle.Bullet();
    }
    public class Bullet extends MissileWeapon {

        {
            if (auto) {
                image = ItemSpriteSheet.TRIPLE_BULLET;
            } else {
                image = ItemSpriteSheet.SINGLE_BULLET;
            }
            hitSound = Assets.Sounds.PUFF;
            tier = 5;
            ACC = 0.7f;

            bullet = true;
            machineGunBullet = true;
        }

        @Override
        public int buffedLvl(){
            return AutoRifle.this.buffedLvl();
        }

        @Override
        public int damageRoll(Char owner) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            int bulletdamage = Random.NormalIntRange(Bulletmin(AutoRifle.this.buffedLvl()),
                    Bulletmax(AutoRifle.this.buffedLvl()));

            if (owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()) {
                bulletdamage = Math.round(bulletdamage * (1f + 0.15f * ((Hero) owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM)));
            }
            return bulletdamage;
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return AutoRifle.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
            if (AutoRifle.this.enchantment == null
                    && Random.Int(3) < hero.pointsInTalent(Talent.SHARED_ENCHANTMENT)
                    && hero.buff(MagicImmune.class) == null
                    && bow != null
                    && bow.enchantment != null) {
                return bow.enchantment.proc(this, attacker, defender, damage);
            } else {
                return AutoRifle.this.proc(attacker, defender, damage);
            }
        }

        @Override
        public float delayFactor(Char user) {

            return AutoRifle.this.delayFactor(user);
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            float accFactor = super.accuracyFactor(owner, target);
            return accFactor;
        }

        @Override
        public int STRReq(int lvl) {
            return AutoRifle.this.STRReq();
        }

        @Override
        protected void onThrow( int cell ) {
            if (auto) {
                for (int i=1; i<=3; i++) {                                                           //i<=n에서 n이 반복하는 횟수, 즉 발사 횟수
                    if (round <= 0) {
                        break;
                    }
                    Char enemy = Actor.findChar(cell);
                    if (enemy == null || enemy == curUser) {
                        parent = null;
                        CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                    } else {
                        if (!curUser.shoot(enemy, this)) {
                            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                            CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                        }
                    }
                    if (hero.buff(InfiniteBullet.class) != null) {
                        //round preserves
                    } else {
                        round --;
                    }
                }
            } else {
                Char enemy = Actor.findChar(cell);
                if (enemy == null || enemy == curUser) {
                    parent = null;
                    CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                    CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                } else {
                    if (!curUser.shoot(enemy, this)) {
                        CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                    }
                }

                if (hero.buff(InfiniteBullet.class) != null) {
                    //round preserves
                } else {
                    round --;
                }
            }

            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                int dist = 4;
                if (mob.paralysed <= 0
                        && Dungeon.level.distance(curUser.pos, mob.pos) <= dist
                        && mob.state != mob.HUNTING) {
                    mob.beckon( curUser.pos ); }
        }
            updateQuickslot();
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f) );
        }

        @Override
        public void cast(final Hero user, final int dst) {
            super.cast(user, dst);
        }
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (target == curUser.pos) {
                    reload();
                } else {
                    knockBullet().cast(curUser, target);
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static class Recipe1 extends com.shatteredpixel.bathredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{HeavyMachinegun.class, LiquidMetal.class};
            inQuantity = new int[]{1, 40};

            cost = 0;

            output = AutoRifle.class;
            outQuantity = 1;
        }
    }
}