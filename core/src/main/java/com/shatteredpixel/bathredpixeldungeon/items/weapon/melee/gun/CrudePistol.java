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

import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class CrudePistol extends BaseGun {

    {
        image = ItemSpriteSheet.CRUDE_PISTOL;
        tier = 1;
        max_round = 3;
        round = max_round;
    }

    public static void shootAbility(Hero hero, MeleeWeapon carbine) {
    }

    @Override
    public int bulletMax(int lvl) {
        return 3 * (tier + 1) +
                lvl * (tier + 1) +
                RingOfSharpshooting.levelDamageBonus(hero);
    }

    @Override
    public Bullet knockBullet(){
        return new CrudeBullet();
    }

    public class CrudeBullet extends Bullet {
        {
            image = ItemSpriteSheet.SINGLE_BULLET;
        }
    }

    @Override
    public String info() {

        String info = super.info();

        if (levelKnown) {
            info += "\n\n" + Messages.get(this, "stats_known",
                    Bulletmin(this.buffedLvl()),
                    Bulletmax(this.buffedLvl()),
                    round, max_round, new DecimalFormat("#.##").format(reload_time));
        } else {
            info += "\n\n" + Messages.get(this, "stats_unknown",
                    Bulletmin(0),
                    Bulletmax(0),
                    round, max_round, new DecimalFormat("#.##").format(reload_time));
        }

        return info;
    }
}