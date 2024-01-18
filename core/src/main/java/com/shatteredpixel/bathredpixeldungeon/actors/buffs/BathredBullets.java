package com.shatteredpixel.bathredpixeldungeon.actors.buffs;

import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class BathredBullets extends Buff {

    public int bulletsLeft = 3;

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.RAGE;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.15f, 1, 0.15f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (bulletsLeft - visualcooldown()) / bulletsLeft);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(bulletsLeft);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", bulletsLeft);
    }

    private static final String NMB = "nmb";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( NMB, bulletsLeft );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        bulletsLeft = bundle.getInt( NMB );
    }
}