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

package com.shatteredpixel.bathredpixeldungeon.sprites;

import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.items.Item;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.AssultRifle;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.AutoHandgun;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.AutoRifle;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.Carbine;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.CrudePistol;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.DualPistol;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.GoldenPistol;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.Handgun;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.HeavyMachinegun;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.HuntingRifle;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.Magnum;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.MarksmanRifle;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.Pistol;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.Revolver;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.SubMachinegun;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun.TacticalHandgun;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.bathredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

import java.util.HashMap;

public class MissileSprite extends ItemSprite implements Tweener.Listener {

	private static final float SPEED	= 240f;
	
	private Callback callback;
	
	public void reset( int from, int to, Item item, Callback listener ) {
		reset(Dungeon.level.solid[from] ? DungeonTilemap.raisedTileCenterToWorld(from) : DungeonTilemap.raisedTileCenterToWorld(from),
				Dungeon.level.solid[to] ? DungeonTilemap.raisedTileCenterToWorld(to) : DungeonTilemap.raisedTileCenterToWorld(to),
				item, listener);
	}

	public void reset( Visual from, int to, Item item, Callback listener ) {
		reset(from.center(),
				Dungeon.level.solid[to] ? DungeonTilemap.raisedTileCenterToWorld(to) : DungeonTilemap.raisedTileCenterToWorld(to),
				item, listener );
	}

	public void reset( int from, Visual to, Item item, Callback listener ) {
		reset(Dungeon.level.solid[from] ? DungeonTilemap.raisedTileCenterToWorld(from) : DungeonTilemap.raisedTileCenterToWorld(from),
				to.center(),
				item, listener );
	}

	public void reset( Visual from, Visual to, Item item, Callback listener ) {
		reset(from.center(), to.center(), item, listener );
	}

	public void reset( PointF from, PointF to, Item item, Callback listener) {
		revive();

		if (item == null)   view(0, null);
		else                view( item );

		setup( from,
				to,
				item,
				listener );
	}
	
	private static final int DEFAULT_ANGULAR_SPEED = 720;
	
	private static final HashMap<Class<?extends Item>, Integer> ANGULAR_SPEEDS = new HashMap<>();
	static {
		ANGULAR_SPEEDS.put(Dart.class,          0);
		ANGULAR_SPEEDS.put(ThrowingKnife.class, 0);
		ANGULAR_SPEEDS.put(ThrowingSpike.class, 0);
		ANGULAR_SPEEDS.put(FishingSpear.class,  0);
		ANGULAR_SPEEDS.put(ThrowingSpear.class, 0);
		ANGULAR_SPEEDS.put(Kunai.class,         0);
		ANGULAR_SPEEDS.put(Javelin.class,       0);
		ANGULAR_SPEEDS.put(Trident.class,       0);
		
		ANGULAR_SPEEDS.put(SpiritBow.SpiritArrow.class,       0);
		ANGULAR_SPEEDS.put(ScorpioSprite.ScorpioShot.class,   0);

		ANGULAR_SPEEDS.put(CrudePistol.Bullet.class,          0);
		ANGULAR_SPEEDS.put(Pistol.Bullet.class,               0);
		ANGULAR_SPEEDS.put(GoldenPistol.Bullet.class,         0);
		ANGULAR_SPEEDS.put(Handgun.Bullet.class,              0);
		ANGULAR_SPEEDS.put(Magnum.Bullet.class,               0);
		ANGULAR_SPEEDS.put(TacticalHandgun.Bullet.class,      0);
		ANGULAR_SPEEDS.put(AutoHandgun.Bullet.class,          0);
		ANGULAR_SPEEDS.put(DualPistol.Bullet.class,           0);
		ANGULAR_SPEEDS.put(SubMachinegun.Bullet.class,        0);
		ANGULAR_SPEEDS.put(AssultRifle.Bullet.class,          0);
		ANGULAR_SPEEDS.put(HeavyMachinegun.Bullet.class,      0);
		ANGULAR_SPEEDS.put(AutoRifle.Bullet.class,            0);
		ANGULAR_SPEEDS.put(Revolver.Bullet.class,         	  0);
		ANGULAR_SPEEDS.put(HuntingRifle.Bullet.class,         0);
		ANGULAR_SPEEDS.put(Carbine.Bullet.class,       		  0);
		ANGULAR_SPEEDS.put(MarksmanRifle.Bullet.class,        0);
		
		//720 is default
		
		ANGULAR_SPEEDS.put(HeavyBoomerang.class,1440);
		ANGULAR_SPEEDS.put(Bolas.class,         1440);
		
		ANGULAR_SPEEDS.put(Shuriken.class,      2160);
		
		ANGULAR_SPEEDS.put(TenguSprite.TenguShuriken.class,      2160);
	}

	//TODO it might be nice to have a source and destination angle, to improve thrown weapon visuals
	private void setup( PointF from, PointF to, Item item, Callback listener ){

		originToCenter();

		//adjust points so they work with the center of the missile sprite, not the corner
		from.x -= width()/2;
		to.x -= width()/2;
		from.y -= height()/2;
		to.y -= height()/2;

		this.callback = listener;

		point( from );

		PointF d = PointF.diff( to, from );
		speed.set(d).normalize().scale(SPEED);
		
		angularSpeed = DEFAULT_ANGULAR_SPEED;
		for (Class<?extends Item> cls : ANGULAR_SPEEDS.keySet()){
			if (cls.isAssignableFrom(item.getClass())){
				angularSpeed = ANGULAR_SPEEDS.get(cls);
				break;
			}
		}
		
		angle = 135 - (float)(Math.atan2( d.x, d.y ) / 3.1415926 * 180);
		
		if (d.x >= 0){
			flipHorizontal = false;
			updateFrame();
			
		} else {
			angularSpeed = -angularSpeed;
			angle += 90;
			flipHorizontal = true;
			updateFrame();
		}
		
		float speed = SPEED;
		if (item instanceof Dart
				&& (Dungeon.hero.belongings.weapon() instanceof Crossbow
				|| Dungeon.hero.belongings.secondWep() instanceof Crossbow)){
			speed *= 3f;
			
		} else if (item instanceof SpiritBow.SpiritArrow
				|| item instanceof ScorpioSprite.ScorpioShot
				|| item instanceof TenguSprite.TenguShuriken){
			speed *= 1.5f;
		}
		
		PosTweener tweener = new PosTweener( this, to, d.length() / speed );
		tweener.listener = this;
		parent.add( tweener );
	}

	@Override
	public void onComplete( Tweener tweener ) {
		kill();
		if (callback != null) {
			callback.call();
		}
	}
}
