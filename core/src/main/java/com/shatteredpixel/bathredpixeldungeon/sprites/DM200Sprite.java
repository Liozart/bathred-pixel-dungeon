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

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.bathredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.bathredpixeldungeon.effects.Speck;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class DM200Sprite extends MobSprite {

	public DM200Sprite () {
		super();

		texture( Assets.Sprites.DM200 );

		TextureFilm frames = new TextureFilm( texture, 21, 18 );

		idle = new Animation( 10, true );
		idle.frames( frames, 0, 1 );

		run = new Animation( 10, true );
		run.frames( frames, 2, 3 );

		attack = new Animation( 15, false );
		attack.frames( frames, 4, 5, 6 );

		zap = new Animation( 15, false );
		zap.frames( frames, 7, 8, 8, 7 );

		die = new Animation( 8, false );
		die.frames( frames, 9, 10, 11 );

		play( idle );
	}

	public void zap( int cell ) {

		super.zap( cell );

		MagicMissile.boltFromChar( parent,
				MagicMissile.TOXIC_VENT,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((DM200)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.GAS );
		GLog.w(Messages.get(DM200.class, "vent"));
	}

	@Override
	public void place(int cell) {
		if (parent != null) parent.bringToFront(this);
		super.place(cell);
	}

	@Override
	public void die() {
		emitter().burst( Speck.factory( Speck.WOOL ), 8 );
		super.die();
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}

}
