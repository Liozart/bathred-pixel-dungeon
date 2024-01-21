package com.shatteredpixel.bathredpixeldungeon.actors.buffs;

import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class RolledBullet extends Buff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.UPGRADE;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.15f, 1, 0.15f);
    }

    @Override
    public float iconFadePercent() {
        return 0;
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(dmgBoost);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dmgBoost);
    }

    public int dmgBoost;
    float max;

    public void set(int dmg) {
        dmgBoost = dmg;
    }

    private static final String BOOST = "boost";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( BOOST, dmgBoost );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        dmgBoost = bundle.getInt( BOOST );
    }
}