/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.shatteredpixel.bathredpixeldungeon.actors.mobs;

import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.bathredpixeldungeon.items.Generator;
import com.shatteredpixel.bathredpixeldungeon.items.food.Berry;
import com.shatteredpixel.bathredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.bathredpixeldungeon.sprites.RotLasherSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class RotLasher extends Mob {

	{
		spriteClass = RotLasherSprite.class;

		HP = HT = 80;
		defenseSkill = 0;

		EXP = 1;

		loot = Generator.Category.SEED;
		lootChance = 0.75f;

		state = WANDERING = new Waiting();
		viewDistance = 1;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
	}

	boolean isFromGiux = false;
	boolean canRegen = true;
	private static final String CAN_REGEN     = "canRegen";
	private static final String IS_FROM_GIUX  = "isFromGiux";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(IS_FROM_GIUX, isFromGiux);
		bundle.put(CAN_REGEN, canRegen);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		isFromGiux = bundle.getBoolean(IS_FROM_GIUX);
		canRegen = bundle.getBoolean(CAN_REGEN);
	}

	@Override
	protected boolean act() {
		if (canRegen){
			if (enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)) {
				HP = Math.min(HT, HP + 5);
			}
		}
		return super.act();
	}

	public void set(int newhp, boolean canren ) {
		isFromGiux = true;
		alignment = Char.Alignment.ALLY;
		properties.remove(Property.MINIBOSS);
		canRegen = canren;
		HT = HP = newhp;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (src instanceof Burning) {
			destroy();
			sprite.die();
		} else {
			super.damage(dmg, src);
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc( enemy, damage );
		if (isFromGiux) {
			if (Dungeon.hero.hasTalent(Talent.GIUX_IVYPOISON)) {
				int pois = Dungeon.hero.pointsInTalent(Talent.GIUX_IVYPOISON);
				if (pois > 3)
					pois = 3;
				if (Random.Int(100) <= 30 * pois){
					Buff.affect( enemy, Poison.class).set(Random.IntRange(4, 7 + pois));
				}
			}
			if (Dungeon.hero.hasTalent(Talent.GIUX_IVYFOOD)) {
				int poif = Dungeon.hero.pointsInTalent(Talent.GIUX_IVYFOOD);
				int chance = (Dungeon.hero.pointsInTalent(Talent.GIUX_IVYFOOD) == 1) ? 10 : 20;
				if (Random.Int(100) <= chance) {
					int val = Random.Int(poif);
					switch (val) {
						case 0: case 1: default:
							Dungeon.level.drop(new Berry(), pos).sprite.drop();
							break;
						case 2:
							Dungeon.level.drop(new Blandfruit(), pos).sprite.drop();
							break;
						case 3:
							Dungeon.level.drop(Generator.random(Generator.Category.SEED), pos).sprite.drop();
							break;
					}
				}
			}
		} else {
			Buff.affect( enemy, Cripple.class, 2f );
		}
		return super.attackProc(enemy, damage);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(10, 20);
	}

	@Override
	public int attackSkill( Char target ) {
		return 25;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 8);
	}
	
	{
		immunities.add( ToxicGas.class );
	}

	private class Waiting extends Mob.Wandering{

		@Override
		protected boolean noticeEnemy() {
			spend(TICK);
			return super.noticeEnemy();
		}
	}
}
