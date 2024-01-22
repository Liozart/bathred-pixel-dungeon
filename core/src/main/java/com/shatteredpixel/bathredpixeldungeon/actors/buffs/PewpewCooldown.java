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

import com.shatteredpixel.bathredpixeldungeon.items.Item;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import java.text.DecimalFormat;

public class PewpewCooldown extends Buff {

    {
        type = buffType.POSITIVE;
    }
    private float duration = 10f;
    private float maxTime = 0f;

    public void set(float duration ) {
        this.duration = duration+1f;
        maxTime = this.duration-1f;
    }

    @Override
    public void detach() {
        super.detach();
        Item.updateQuickslot();
    }

    @Override
    public int icon() {
        return BuffIndicator.RECHARGING;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.8f, 0.2f, 0.8f);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", new DecimalFormat("#").format(duration));
    }

    @Override
    public float iconFadePercent() {
        return Math.max((maxTime - duration)/maxTime, 0);
    }

    @Override
    public boolean act() {
        duration-=1f;
        spend(1f);
        if (duration <= 0) {
            detach();
        }
        return true;
    }

    private static final String DURATION = "duration";
    private static final String MAXTIME = "maxTime";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DURATION, duration);
        bundle.put(MAXTIME, maxTime);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        duration = bundle.getFloat(DURATION);
        maxTime = bundle.getFloat(MAXTIME);
    }
}
