/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.InfiniteBullet;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.bathredpixeldungeon.items.Item;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.bathredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Gun extends MeleeWeapon {
	public static final String AC_SHOOT		= "SHOOT";
	public static final String AC_RELOAD = "RELOAD";

	public int max_round;
	public int round;
	public float reload_time;
	public static final String TXT_STATUS = "%d/%d";

	{
		defaultAction = AC_SHOOT;
		usesTargeting = true;

		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;

		bones = false;
	}

	private static final String ROUND = "round";
	private static final String MAX_ROUND = "max_round";
	private static final String RELOAD_TIME = "reload_time";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(MAX_ROUND, max_round);
		bundle.put(ROUND, round);
		bundle.put(RELOAD_TIME, reload_time);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		max_round = bundle.getInt(MAX_ROUND);
		round = bundle.getInt(ROUND);
		reload_time = bundle.getFloat(RELOAD_TIME);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped( hero )) {
			actions.add(AC_SHOOT);
			actions.add(AC_RELOAD);
		}
		return actions;
	}

	public void reload() {
	}

	public int getRound() { return round; }

	@Override
	public int STRReq(int lvl) {
		int needSTR = STRReq(tier, lvl);
		return needSTR;
	}

	public int Bulletmin(int lvl) {
		return 0;
	}

	public int Bulletmax(int lvl) {
		return 0;
	}

	@Override
	public String info() {
		String info = desc();
		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (STRReq() > hero.STR()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (hero.STR() > STRReq()){
				info += " " + Messages.get(Weapon.class, "excess_str", hero.STR() - STRReq());
			}
			info += "\n\n" + Messages.get(this, "stats_known",
					Bulletmin(this.buffedLvl()),
					Bulletmax(this.buffedLvl()),
					round, max_round, new DecimalFormat("#.##").format(reload_time));
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
			if (STRReq(0) > hero.STR()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
			info += "\n\n" + Messages.get(Gun.class, "stats_unknown",
					Bulletmin(0),
					Bulletmax(0),
					round, max_round, new DecimalFormat("#.##").format(reload_time));
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && isEquipped( hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}

		return info;
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return knockBullet().targetingPos(user, dst);
	}

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

	public Bullet knockBullet(){
		return new Bullet();
	}
	public class Bullet extends MissileWeapon {

		{
			image = ItemSpriteSheet.SINGLE_BULLET;

			hitSound = Assets.Sounds.PUFF;
			tier = Gun.this.tier;
		}

		@Override
		public int buffedLvl(){
			return Gun.this.buffedLvl();
		}

		@Override
		public int damageRoll(Char owner) {
			Hero hero = (Hero)owner;
			int bulletdamage = Random.NormalIntRange(Bulletmin(Gun.this.buffedLvl()),
					Bulletmax(Gun.this.buffedLvl()));

			if (owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()) {
				bulletdamage = Math.round(bulletdamage * (1f + 0.15f * ((Hero) owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM)));
			}
			return bulletdamage;
		}

		@Override
		public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return Gun.this.hasEnchant(type, owner);
		}

		@Override
		public int proc(Char attacker, Char defender, int damage) {
			SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
			if (Gun.this.enchantment == null
					&& Random.Int(3) < hero.pointsInTalent(Talent.SHARED_ENCHANTMENT)
					&& hero.buff(MagicImmune.class) == null
					&& bow != null
					&& bow.enchantment != null) {
				return bow.enchantment.proc(this, attacker, defender, damage);
			} else {
				return Gun.this.proc(attacker, defender, damage);
			}
		}

		@Override
		public float delayFactor(Char user) {
			return Gun.this.delayFactor(user);
		}

		@Override
		public float accuracyFactor(Char owner, Char target) {
			float accFactor = super.accuracyFactor(owner, target);
			return accFactor;
		}

		@Override
		public int STRReq(int lvl) {
			return Gun.this.STRReq();
		}

		@Override
		protected void onThrow( int cell ) {
			Char enemy = Actor.findChar( cell );
			if (enemy == null || enemy == curUser) {
				parent = null;
				CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
				CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
			} else {
				if (!curUser.shoot( enemy, this )) {
					CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
					CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
				}
			}
			if (hero.buff(InfiniteBullet.class) != null) {
				//round preserves
			} else {
				round --;
			}
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.paralysed <= 0
						&& Dungeon.level.distance(curUser.pos, mob.pos) <= 4
						&& mob.state != mob.HUNTING) {
					mob.beckon( curUser.pos );
				}
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
	}
}
