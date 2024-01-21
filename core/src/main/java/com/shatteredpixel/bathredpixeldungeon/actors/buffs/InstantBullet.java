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

package com.shatteredpixel.bathredpixeldungeon.actors.buffs;

import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class InstantBullet extends FlavourBuff {

    public static final float DURATION	= 5f;

    {
        type = buffType.POSITIVE;
        announced = false;
    }

    @Override
    public boolean attachTo( Char target ) {
        if (super.attachTo( target )) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
    }
    @Override
    public int icon() {
        return BuffIndicator.DUEL_DANCE;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.5f,1f,1);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", DURATION);
    }
    public static final String DUR	= "duration";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
    }
}
