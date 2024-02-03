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
import com.shatteredpixel.bathredpixeldungeon.Statistics;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.bathredpixeldungeon.effects.Pushing;
import com.shatteredpixel.bathredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.bathredpixeldungeon.journal.Notes;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DemonSpawner extends Mob {

	{
		spriteClass = SpawnerSprite.class;

		HP = HT = 120;
		defenseSkill = 0;

		EXP = 15;
		maxLvl = 29;

		state = PASSIVE;

		loot = PotionOfHealing.class;
		lootChance = 1f;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
		properties.add(Property.DEMONIC);
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 12);
	}

	@Override
	public void beckon(int cell) {
		//do nothing
	}

	@Override
	public boolean reset() {
		return true;
	}

	private float spawnCooldown = 0;

	public boolean spawnRecorded = false;

	@Override
	protected boolean act() {
		if (!spawnRecorded){
			Statistics.spawnersAlive++;
			spawnRecorded = true;
		}

		if (Dungeon.level.visited[pos]){
			Notes.add( Notes.Landmark.DEMON_SPAWNER );
		}

		if (Dungeon.hero.buff(AscensionChallenge.class) != null && spawnCooldown > 20){
			spawnCooldown = 20;
		}

		spawnCooldown--;
		if (spawnCooldown <= 0){

			//we don't want spawners to store multiple ripper demons
			if (spawnCooldown < -20){
				spawnCooldown = -20;
			}

			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				if (Dungeon.level.passable[pos+n] && Actor.findChar( pos+n ) == null) {
					candidates.add( pos+n );
				}
			}

			if (!candidates.isEmpty()) {
				RipperDemon spawn = new RipperDemon();

				spawn.pos = Random.element( candidates );
				spawn.state = spawn.HUNTING;

				GameScene.add( spawn, 1 );
				Dungeon.level.occupyCell(spawn);

				if (sprite.visible) {
					Actor.add(new Pushing(spawn, pos, spawn.pos));
				}

				spawnCooldown += 60;
				if (Dungeon.depth > 21){
					//60/53.33/46.67/40 turns to spawn on floor 21/22/23/24
					spawnCooldown -= Math.min(20, (Dungeon.depth-21)*6.67);
				}
			}
		}
		alerted = false;
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if (dmg >= 20){
			//takes 20/21/22/23/24/25/26/27/28/29/30 dmg
			// at   20/22/25/29/34/40/47/55/64/74/85 incoming dmg
			dmg = 19 + (int)(Math.sqrt(8*(dmg - 19) + 1) - 1)/2;
		}
		spawnCooldown -= dmg;
		super.damage(dmg, src);
	}

	@Override
	public void die(Object cause) {
		if (spawnRecorded){
			Statistics.spawnersAlive--;
			Notes.remove(Notes.Landmark.DEMON_SPAWNER);
		}
		GLog.h(Messages.get(this, "on_death"));
		super.die(cause);
	}

	public static final String SPAWN_COOLDOWN = "spawn_cooldown";
	public static final String SPAWN_RECORDED = "spawn_recorded";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPAWN_COOLDOWN, spawnCooldown);
		bundle.put(SPAWN_RECORDED, spawnRecorded);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spawnCooldown = bundle.getFloat(SPAWN_COOLDOWN);
		spawnRecorded = bundle.getBoolean(SPAWN_RECORDED);
	}

	{
		immunities.add( Paralysis.class );
		immunities.add( Amok.class );
		immunities.add( Sleep.class );
		immunities.add( Dread.class );
		immunities.add( Terror.class );
		immunities.add( Vertigo.class );
	}
}