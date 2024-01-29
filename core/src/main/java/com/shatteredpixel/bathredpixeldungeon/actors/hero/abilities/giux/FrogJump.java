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

package com.shatteredpixel.bathredpixeldungeon.actors.hero.abilities.giux;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.bathredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.EscapeRoll;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.bathredpixeldungeon.effects.Speck;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.bathredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.bathredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FrogJump extends ArmorAbility {
    @Override
    public boolean useTargeting(){
        return false;
    }

    {
        baseChargeUse = 30f;
    }

    @Override
    public float chargeUse(Hero hero) {
        return super.chargeUse(hero);
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.buff(FrogJumpBuff.class) == null)
        {
            Buff.affect(hero, FrogJumpBuff.class);
            hero.buff(EscapeRoll.class).updateFreeRolls(hero.pointsInTalent(Talent.GIUX_FROGTIME));
            armor.charge -= chargeUse(hero);
            armor.updateQuickslot();
            Dungeon.observe();
            Invisibility.dispel();
            //hero.spendAndNext(Actor.TICK);
        }
        else {
            GLog.w(Messages.get(this, "cooldown"));
        }
    }

    public static class FrogJumpBuff extends Buff {
        int lasting = 2;

        {
            announced = true;
        }

        public int icon() { return BuffIndicator.LEVITATION; }
        public void update()
        {
            lasting--;
            if (lasting == 0)
                detach();
        }

        @Override
        public void tintIcon(Image icon) { icon.hardlight(0.1f, 0.65f, 0.55f); }
        @Override
        public float iconFadePercent() { return (3 - lasting) / 3; }

        @Override
        public String desc() {
            return Messages.get(this, "desc", lasting);
        }

        private static final String LASTING = "lasting";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( LASTING, lasting );
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            lasting = bundle.getInt( LASTING );
        }

        public int getLasting() {
            return lasting;
        }
    };

    @Override
    public int icon() {
        return HeroIcon.GIUXPOWER_2;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.GIUX_FROGRANGE, Talent.GIUX_FROGTIME, Talent.GIUX_FROGWATER, Talent.HEROIC_ENERGY};
    }
}
