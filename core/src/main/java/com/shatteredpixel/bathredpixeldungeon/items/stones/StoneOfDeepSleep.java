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

package com.shatteredpixel.bathredpixeldungeon.items.stones;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.effects.Speck;
import com.shatteredpixel.bathredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class StoneOfDeepSleep extends Runestone {
	
	{
		image = ItemSpriteSheet.STONE_SLEEP;
	}
	
	@Override
	protected void activate(int cell) {

		if (Actor.findChar(cell) != null) {

			Char c = Actor.findChar(cell);

			if (c instanceof Mob){

				Buff.affect(c, MagicalSleep.class);
				c.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );

			}

		}
		
		Sample.INSTANCE.play( Assets.Sounds.LULLABY );
		
	}
}
