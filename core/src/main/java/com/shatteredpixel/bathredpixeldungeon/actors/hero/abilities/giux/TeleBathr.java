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

import static com.shatteredpixel.bathredpixeldungeon.Dungeon.depth;
import static com.shatteredpixel.bathredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.bathredpixeldungeon.items.scrolls.ScrollOfTeleportation.appear;

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
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.EscapeRoll;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.bathredpixeldungeon.effects.CheckedCell;
import com.shatteredpixel.bathredpixeldungeon.effects.Speck;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.bathredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.bathredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.bathredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.bathredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.bathredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.bathredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.bathredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TeleBathr extends ArmorAbility {
    @Override
    public boolean useTargeting(){
        return false;
    }

    {
        baseChargeUse = 15f;
    }

    @Override
    public float chargeUse(Hero hero) {
        return super.chargeUse(hero);
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.buff(TeleBathrBuff.class) == null)
        {
            int add = hero.pointsInTalent(Talent.GIUX_TELETIME);
            if (add > 2)
                add = 2;
            Buff.affect(hero, TeleBathrBuff.class, 5 + add * 2);
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

    public static void teleBathrport() {
        ArrayList<Integer> candidates = new ArrayList<>();
        ArrayList<Integer> candidatesChars = new ArrayList<>();
        int cell;

        if (!(Dungeon.level instanceof RegularLevel)) {

            boolean[] passable = Dungeon.level.passable;

            PathFinder.buildDistanceMap(hero.pos, passable);

            for (int i = 0; i < Dungeon.level.length(); i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE
                        && !Dungeon.level.secret[i]) {
                    if (Actor.findChar(i) != null) {
                        if (Actor.findChar(i) instanceof Mob && hero.hasTalent(Talent.GIUX_TELEFRAG)) {
                            candidatesChars.add(i);
                        }
                    } else {
                        candidates.add(i);
                    }
                }
            }
        }
        else {
            RegularLevel level = (RegularLevel) Dungeon.level;

            // First choose a cell in the current room
            for (Room r : level.rooms()) {
                if (r.inside(Dungeon.level.cellToPoint(hero.pos))) {
                    for (Point p : r.charPlaceablePoints(level)) {
                        cell = level.pointToCell(p);
                        if (level.passable[cell]) {
                            if (Actor.findChar(cell) != null) {
                                if (Actor.findChar(cell) instanceof Mob && hero.hasTalent(Talent.GIUX_TELEFRAG)) {
                                    candidatesChars.add(cell);
                                }
                            } else {
                                candidates.add(cell);
                            }
                        }
                    }
                }
            }
            // If not in a room then check surrounding cells
            if (candidates.size() == 0) {
                Point c = Dungeon.level.cellToPoint(hero.pos);
                int left, right;
                int curr;
                for (int y = Math.max(0, c.y - 5); y <= Math.min(Dungeon.level.height() - 1, c.y + 5); y++) {
                    left = c.x - 5;
                    right = Math.min(Dungeon.level.width() - 1, c.x + c.x - left);
                    left = Math.max(0, left);
                    for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++) {
                        if (level.passable[curr]) {
                            if (Actor.findChar(curr) != null) {
                                if (Actor.findChar(curr) instanceof Mob && hero.hasTalent(Talent.GIUX_TELEFRAG)) {
                                    candidatesChars.add(curr);
                                }
                            } else {
                                candidates.add(curr);
                            }
                        }
                    }
                }
            }
            // If no places grab a random cell in the level
            if (candidates.size() == 0) {
                for (Room r : level.rooms()) {
                    for (Point p : r.charPlaceablePoints(level)) {
                        cell = level.pointToCell(p);
                        if (level.passable[cell]) {
                            if (Actor.findChar(cell) != null) {
                                if (Actor.findChar(cell) instanceof Mob && hero.hasTalent(Talent.GIUX_TELEFRAG)) {
                                    candidatesChars.add(cell);
                                }
                            } else {
                                candidates.add(cell);
                            }
                        }
                    }
                }
            }
        }
        if (candidates.size() == 0){
            GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
        }
        else {
            int pos = Random.element(candidates);
            if (candidatesChars.size() > 0) {
                if (hero.pointsInTalent(Talent.GIUX_TELEFRAG) == 4) {
                    pos = Random.element(candidatesChars);
                }
                else {
                    candidates.addAll(candidatesChars);
                    pos = Random.element(candidates);
                }
            }
            if (Actor.findChar(pos) != null) {
                Char m = Actor.findChar(pos);
                appear(m, hero.pos);
                Dungeon.level.occupyCell(m);
                int poib = hero.pointsInTalent(Talent.GIUX_TELEFRAG);
                if (poib >= 2) {
                    m.damage(m.HT / 5, hero);
                }
                if (poib >= 3) {
                    Buff.affect(m, Daze.class, 3f);
                }
            }
            switch (hero.pointsInTalent(Talent.GIUX_TELEFIRE)) {
                case 1:
                    if (Random.Int(2) == 0) {
                        Buff.affect(hero, FrostImbue.class, 1f);
                    }
                    break;
                case 2: if (Random.Int(2) == 0) {
                        Buff.affect(hero, FireImbue.class).set(1f);
                    }
                    break;
                case 3:
                    Buff.affect(hero, FrostImbue.class, 1f);
                    break;
                case 4:
                    Buff.affect(hero, FireImbue.class).set(1f);
                    break;
                default:
                    break;
            }

            if (hero.pointsInTalent(Talent.GIUX_TELETIME) >= 3){
                Point c = Dungeon.level.cellToPoint(pos);
                int DIST = (hero.pointsInTalent(Talent.GIUX_TELETIME) - 2) * 2;
                int[] rounding = ShadowCaster.rounding[DIST];
                int left, right;
                int curr;
                boolean noticed = false;
                for (int y = Math.max(0, c.y - DIST); y <= Math.min(Dungeon.level.height()-1, c.y + DIST); y++) {
                    if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
                        left = c.x - rounding[Math.abs(c.y - y)];
                    } else {
                        left = DIST;
                        while (rounding[left] < rounding[Math.abs(c.y - y)]){
                            left--;
                        }
                        left = c.x - left;
                    }
                    right = Math.min(Dungeon.level.width()-1, c.x + c.x - left);
                    left = Math.max(0, left);
                    for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++){

                        GameScene.effectOverFog( new CheckedCell( curr, pos ) );
                        Dungeon.level.mapped[curr] = true;

                        if (Dungeon.level.secret[curr]) {
                            Dungeon.level.discover(curr);
                            if (Dungeon.level.heroFOV[curr]) {
                                GameScene.discoverTile(curr, Dungeon.level.map[curr]);
                                ScrollOfMagicMapping.discover(curr);
                            }
                        }
                    }
                }
            }

            appear( hero, pos);
            Dungeon.level.occupyCell( hero );
            Dungeon.observe();
            GameScene.updateFog();
        }
    }

    public static class TeleBathrBuff extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }
        @Override
        public int icon() {
            return BuffIndicator.DAZE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.9f, 0.1f, 0.9f);
        }
    };

    @Override
    public int icon() {
        return HeroIcon.GIUXPOWER_3;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.GIUX_TELEFRAG, Talent.GIUX_TELEFIRE, Talent.GIUX_TELETIME, Talent.HEROIC_ENERGY};
    }
}
