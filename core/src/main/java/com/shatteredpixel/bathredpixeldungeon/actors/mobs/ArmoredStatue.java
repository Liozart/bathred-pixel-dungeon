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

package com.shatteredpixel.bathredpixeldungeon.actors.mobs;

import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.bathredpixeldungeon.items.Generator;
import com.shatteredpixel.bathredpixeldungeon.items.Item;
import com.shatteredpixel.bathredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.bathredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.bathredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.bathredpixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ArmoredStatue extends Statue {

	{
		spriteClass = StatueSprite.class;
	}

	protected Armor armor;

	public ArmoredStatue(){
		super();

		//double HP
		HP = HT = 25 + Dungeon.depth * 10;
	}

	@Override
	public void createWeapon(boolean useDecks) {
		super.createWeapon(useDecks);

		armor = Generator.randomArmor();
		armor.cursed = false;
		armor.inscribe(Armor.Glyph.random());
	}

	private static final String ARMOR	= "armor";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARMOR, armor );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		armor = (Armor)bundle.get( ARMOR );
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( armor.DRMin(), armor.DRMax());
	}

	//used in some glyph calculations
	public Armor armor(){
		return armor;
	}

	@Override
	public boolean isImmune(Class effect) {
		if (effect == Burning.class
				&& armor != null
				&& armor.hasGlyph(Brimstone.class, this)){
			return true;
		}
		return super.isImmune(effect);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		damage = armor.proc(enemy, this, damage);
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int dmg, Object src) {
		//TODO improve this when I have proper damage source logic
		if (armor != null && armor.hasGlyph(AntiMagic.class, this)
				&& AntiMagic.RESISTS.contains(src.getClass())){
			dmg -= AntiMagic.drRoll(this, armor.buffedLvl());
		}

		super.damage( dmg, src );

		//for the rose status indicator
		Item.updateQuickslot();
	}

	@Override
	public CharSprite sprite() {
		CharSprite sprite = super.sprite();
		((StatueSprite)sprite).setArmor(armor.tier);
		return sprite;
	}

	@Override
	public float speed() {
		return armor.speedFactor(this, super.speed());
	}

	@Override
	public float stealth() {
		return armor.stealthFactor(this, super.stealth());
	}

	@Override
	public int defenseSkill(Char enemy) {
		return Math.round(armor.evasionFactor(this, super.defenseSkill(enemy)));
	}

	@Override
	public void die( Object cause ) {
		armor.identify(false);
		Dungeon.level.drop( armor.upgrade(), pos ).sprite.drop();
		super.die( cause );
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", weapon.name(), armor.name());
	}

}
