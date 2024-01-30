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

package com.shatteredpixel.bathredpixeldungeon.items.quest;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.items.Item;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class BathrToken extends Item {

    {
        image = ItemSpriteSheet.BATHRTOKEN;

        stackable = true;
        unique = true;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (collect( hero.belongings.backpack )) {
            GameScene.pickUp( this, pos );
            Sample.INSTANCE.play( Assets.Sounds.ITEM );
            hero.spendAndNext( TIME_TO_PICK_UP );
            hero.earnExp(3, BathrToken.class);
            return true;

        } else {
            return false;
        }
    }
    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
    @Override
    public String name() {
        return trueName();
    }
}
