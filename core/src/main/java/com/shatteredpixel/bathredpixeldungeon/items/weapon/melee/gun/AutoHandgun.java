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

package com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun;

import static com.shatteredpixel.bathredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.bathredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.bathredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class AutoHandgun extends BaseGun {

    {
        image = ItemSpriteSheet.AUTOHANDGUN;
        tier = 5;
        max_round = 8;
        round = max_round;
    }

    public int Bulletmin(int lvl) {
            return 2 * tier + 1 +
                    lvl      +
                    RingOfSharpshooting.levelDamageBonus(hero);
    }

    public int Bulletmax(int lvl) {
            return 4 * (tier+1)   +
                    lvl * (tier+2)  +
                    RingOfSharpshooting.levelDamageBonus(hero);
    }
    @Override
    public String info() {

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

    public Bullet knockBullet(){
        return new AutoHandBullet();
    }
    public class AutoHandBullet extends Bullet {
        {
            image = ItemSpriteSheet.SINGLE_BULLET;
            tier = 5;
        }
    }

    public static class Recipe1 extends com.shatteredpixel.bathredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{Magnum.class, LiquidMetal.class};
            inQuantity = new int[]{1, 40};

            cost = 0;

            output = AutoHandgun.class;
            outQuantity = 1;
        }
    }
}