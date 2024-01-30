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

package com.shatteredpixel.bathredpixeldungeon.levels;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.Challenges;
import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.Statistics;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.npcs.BathrNPC;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.bathredpixeldungeon.items.Amulet;
import com.shatteredpixel.bathredpixeldungeon.items.Generator;
import com.shatteredpixel.bathredpixeldungeon.items.Torch;
import com.shatteredpixel.bathredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.bathredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.bathredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.bathredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.bathredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.bathredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.CastleRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.connection.BridgeRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.quest.MineLargeRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.quest.MineSmallRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.standard.EntranceRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.standard.ExitRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.bathredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.bathredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.bathredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.shatteredpixel.bathredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.bathredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Iterator;

public class BathrLevel extends RegularLevel {

    {
        color1 = 0x66ff66;
        color2 = 0x009900;
    }

    Room entranceRoom;

    public static final String[] CAVES_TRACK_LIST
            = new String[]{Assets.Music.CAVES_1, Assets.Music.CAVES_2, Assets.Music.CAVES_2,
            Assets.Music.CAVES_1, Assets.Music.CAVES_3, Assets.Music.CAVES_3};
    public static final float[] CAVES_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.playTracks(CAVES_TRACK_LIST, CAVES_TRACK_CHANCES, false);
    }

    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();
        entranceRoom = new EntranceRoom();
        initRooms.add (entranceRoom);
        CastleRoom carom = new CastleRoom();
        initRooms.add(carom);

        int standards = standardRooms(true);
        StandardRoom s;
        for (int i = 0; i < standards; i++) {
            do {
                s = StandardRoom.createRoom();
            } while (!s.setSizeCat( standards-i ));
            i += s.sizeCat.roomValue-1;
            initRooms.add(s);
        }
        for (int i = 0; i < standardRooms(true) / 10; i++){
            s = new MineLargeRoom();
            s.setSizeCat();
            initRooms.add(s);
        }
        int specials = specialRooms(true);
        SpecialRoom.initForFloor();
        for (int i = 0; i < specials; i++) {
            SpecialRoom sp = SpecialRoom.createRoom();
            if (sp instanceof PitRoom) specials++;
            initRooms.add(sp);
        }
        int secrets = SecretRoom.secretsForFloor(Dungeon.depth) + 1;
        //one additional secret for secret levels
        if (feeling == Feeling.SECRETS) secrets++;
        for (int i = 0; i < secrets; i++) {
            initRooms.add(SecretRoom.createRoom());
        }

        return initRooms;
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        return 40;
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        return 10;
    }
    @Override
    public int mobLimit() {
        return 30;
    }

    @Override
    protected void createMobs() {
        int mobsToSpawn = mobLimit();

        ArrayList<Room> stdRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room instanceof StandardRoom && room != roomEntrance && !(room instanceof CastleRoom)) {
                for (int i = 0; i < ((StandardRoom) room).sizeCat.roomValue; i++) {
                    stdRooms.add(room);
                }
            }
        }
        Random.shuffle(stdRooms);
        Iterator<Room> stdRoomIter = stdRooms.iterator();

        while (mobsToSpawn > 0) {
            Mob mob = createMob();
            Class<?extends ChampionEnemy> buffCls;
            switch (Random.Int(6)){
                case 0: default:    buffCls = ChampionEnemy.Blazing.class;      break;
                case 1:             buffCls = ChampionEnemy.Projecting.class;   break;
                case 2:             buffCls = ChampionEnemy.AntiMagic.class;    break;
                case 3:             buffCls = ChampionEnemy.Giant.class;        break;
                case 4:             buffCls = ChampionEnemy.Blessed.class;      break;
                case 5:             buffCls = ChampionEnemy.Growing.class;      break;
            }
            Buff.affect(mob, buffCls);
            mob.state = Random.Int(2) == 0 ? mob.WANDERING : mob.SLEEPING;

            Room roomToSpawn;

            if (!stdRoomIter.hasNext()) {
                stdRoomIter = stdRooms.iterator();
            }
            roomToSpawn = stdRoomIter.next();

            int tries = 30;
            do {
                mob.pos = pointToCell(roomToSpawn.random());
                tries--;
            } while (tries >= 0 && (findMob(mob.pos) != null
                    || !passable[mob.pos]
                    || solid[mob.pos]
                    || !roomToSpawn.canPlaceCharacter(cellToPoint(mob.pos), this)
                    || mob.pos == exit()
                    || traps.get(mob.pos) != null || plants.get(mob.pos) != null
                    || (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

            if (tries >= 0) {
                mobsToSpawn--;
                mobs.add(mob);

                //chance to add a second mob to this room, except on floor 1
                if (Dungeon.depth > 1 && mobsToSpawn > 0 && Random.Int(4) == 0){
                    mob = createMob();

                    tries = 30;
                    do {
                        mob.pos = pointToCell(roomToSpawn.random());
                        tries--;
                    } while (tries >= 0 && (findMob(mob.pos) != null
                            || !passable[mob.pos]
                            || solid[mob.pos]
                            || !roomToSpawn.canPlaceCharacter(cellToPoint(mob.pos), this)
                            || mob.pos == exit()
                            || traps.get(mob.pos) != null || plants.get(mob.pos) != null
                            || (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

                    if (tries >= 0) {
                        mobsToSpawn--;
                        mobs.add(mob);
                    }
                }
            }
        }

        for (Mob m : mobs){
            if (map[m.pos] == Terrain.HIGH_GRASS || map[m.pos] == Terrain.FURROWED_GRASS) {
                map[m.pos] = Terrain.GRASS;
                losBlocking[m.pos] = false;
            }
        }

        BathrNPC.Quest.spawn( this, entranceRoom );
    }
    @Override
    protected void createItems() {
        super.createItems();
        int cell;
        for (int i = 0; i < 3; i++){
            cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
            drop( new MeatPie(), cell );
        }
    }

    @Override
    protected Painter painter() {
        return new CavesPainter()
                .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
                .setGrass(feeling == Feeling.GRASS ? 0.65f : 0.40f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        return super.activateTransition(hero, transition);
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_BATHR;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CITY;
    }

    @Override
    protected Class<?>[] trapClasses() {
        return new Class[]{
                BurningTrap.class, PoisonDartTrap.class, FrostTrap.class, StormTrap.class, CorrosionTrap.class,
                GrippingTrap.class, RockfallTrap.class,  GuardianTrap.class,
                ConfusionTrap.class, SummoningTrap.class, WarpingTrap.class, PitfallTrap.class, GatewayTrap.class, GeyserTrap.class };
    }

    @Override
    protected float[] trapChances() {
        return new float[]{
                4, 4, 4, 4, 4,
                2, 2, 2,
                1, 1, 1, 1, 1, 1 };
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.GRASS:
                return Messages.get(CavesLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(CavesLevel.class, "high_grass_name");
            case Terrain.WATER:
                return Messages.get(CavesLevel.class, "water_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc( int tile ) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CavesLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CavesLevel.class, "exit_desc");
            case Terrain.HIGH_GRASS:
                return Messages.get(CavesLevel.class, "high_grass_desc");
            case Terrain.WALL_DECO:
                return Messages.get(CavesLevel.class, "wall_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CavesLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addCavesVisuals( this, visuals );
        return visuals;
    }

    public static void addCavesVisuals( Level level, Group group ) {
        addCavesVisuals(level, group, false);
    }

    public static void addCavesVisuals( Level level, Group group, boolean overHang ) {
        for (int i=0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add( new Vein( i, overHang ) );
            }
        }
    }

    private static class Vein extends Group {

        private int pos;

        private boolean includeOverhang;

        private float delay;

        public Vein( int pos ) {
            this(pos, false);
        }

        public Vein( int pos, boolean includeOverhang ) {
            super();

            this.pos = pos;
            this.includeOverhang = includeOverhang;

            delay = Random.Float( 2 );
        }

    }
}