package com.shatteredpixel.bathredpixeldungeon.actors.mobs.npcs;

import static com.shatteredpixel.bathredpixeldungeon.Dungeon.depth;

import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.Statistics;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.bathredpixeldungeon.items.Generator;
import com.shatteredpixel.bathredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.bathredpixeldungeon.items.quest.BathrToken;
import com.shatteredpixel.bathredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.bathredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.bathredpixeldungeon.journal.Notes;
import com.shatteredpixel.bathredpixeldungeon.levels.BathrLevel;
import com.shatteredpixel.bathredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.bathredpixeldungeon.levels.Terrain;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.bathredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.bathredpixeldungeon.windows.WndImp;
import com.shatteredpixel.bathredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class BathrNPC extends NPC {

    {
        spriteClass = TenguSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    @Override
    protected boolean act() {
        return super.act();
    }
    @Override
    public int defenseSkill( Char enemy ) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage( int dmg, Object src ) {
        //do nothing
    }

    @Override
    public boolean add( Buff buff ) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact(Char c) {

        sprite.turnTo( pos, Dungeon.hero.pos );

        if (c != Dungeon.hero){
            return true;
        }

        if (BathrNPC.Quest.given) {
            BathrToken tokens = Dungeon.hero.belongings.getItem( BathrToken.class );
            if (tokens != null && (tokens.quantity() >= 20)) {
                Dungeon.level.drop(new CrystalKey(depth), pos + 1);
                tokens.detachAll( Dungeon.hero.belongings.backpack );
                flee();
                BathrNPC.Quest.complete();
            } else {
                tell(Messages.get(this, "rem_quest", Messages.titleCase(Dungeon.hero.name())));
            }

        } else {
            tell( Messages.get(this, "give_quest") );
            BathrNPC.Quest.given = true;
            BathrNPC.Quest.completed = false;
        }

        return true;
    }

    private void tell( String text ) {
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                GameScene.show( new WndQuest( BathrNPC.this, text ));
            }
        });
    }

    public void flee() {

        yell( Messages.get(this, "cya", Messages.titleCase(Dungeon.hero.name())) );

        destroy();
        sprite.die();
    }

    public static class Quest {
        private static boolean spawned;
        private static boolean given;
        private static boolean completed;

        public static void reset() {
            spawned = false;
            given = false;
            completed = false;
        }

        private static final String NODE		= "bathrnpc";

        private static final String SPAWNED		= "spawned";
        private static final String GIVEN		= "given";
        private static final String COMPLETED	= "completed";

        public static void storeInBundle( Bundle bundle ) {

            Bundle node = new Bundle();

            node.put( SPAWNED, spawned );

            if (spawned) {
                node.put( GIVEN, given );
                node.put( COMPLETED, completed );
            }

            bundle.put( NODE, node );
        }

        public static void restoreFromBundle( Bundle bundle ) {

            Bundle node = bundle.getBundle( NODE );

            if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
                given = node.getBoolean( GIVEN );
                completed = node.getBoolean( COMPLETED );
            }
        }

        public static void spawn( BathrLevel level, Room entrance ) {
            if (!spawned) {
                BathrNPC npc = new BathrNPC();
                do {
                    npc.pos = level.pointToCell(entrance.random(2));
                } while (
                        npc.pos == -1 ||
                                level.heaps.get(npc.pos) != null ||
                                level.traps.get(npc.pos) != null ||
                                level.findMob(npc.pos) != null ||
                                //The imp doesn't move, so he cannot obstruct a passageway
                                !(level.passable[npc.pos + PathFinder.CIRCLE4[0]] && level.passable[npc.pos + PathFinder.CIRCLE4[2]]) ||
                                !(level.passable[npc.pos + PathFinder.CIRCLE4[1]] && level.passable[npc.pos + PathFinder.CIRCLE4[3]]));

                level.mobs.add(npc);

                spawned = true;
                given = false;
            }
        }

        public static void complete() {
            completed = true;
            Statistics.exploreScore += 20000;
        }

        public static boolean isCompleted() {
            return completed;
        }
    }
}
