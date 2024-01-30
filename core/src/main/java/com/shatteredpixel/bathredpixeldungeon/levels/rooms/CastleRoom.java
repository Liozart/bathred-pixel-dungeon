package com.shatteredpixel.bathredpixeldungeon.levels.rooms;

import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.items.Generator;
import com.shatteredpixel.bathredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.bathredpixeldungeon.levels.Level;
import com.shatteredpixel.bathredpixeldungeon.levels.Terrain;
import com.shatteredpixel.bathredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.connection.PerimeterRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.sewerboss.DiamondGooRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.sewerboss.GooBossRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.sewerboss.ThickPillarsGooRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.sewerboss.ThinPillarsGooRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.sewerboss.WalledGooRoom;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class CastleRoom extends StandardRoom {

    @Override
    public float[] sizeCatProbs() {
        return new float[]{0, 1, 0};
    }

    @Override
    public boolean canMerge(Level l, Point p, int mergeTerrain) {
        return false;
    }

    @Override
    public void paint(Level level) {

        int wid = 12;
        int hei = 12;

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY_SP);

        int pillarW = 2;
        int pillarH = 2;

        Painter.fill(level, left + (wid - pillarW) / 2, top + 3, pillarW, 1, Terrain.BOOKSHELF);
        Painter.fill(level, left + (wid - pillarW) / 2, bottom - 3, pillarW, 1, Terrain.BOOKSHELF);

        Painter.fill(level, left + 3, top + (hei - pillarH) / 2, 1, pillarH, Terrain.STATUE);
        Painter.fill(level, right - 3, top + (hei - pillarH) / 2, 1, pillarH, Terrain.STATUE);

        PerimeterRoom.fillPerimiterPaths(level, this, Terrain.EMPTY_SP);

        for (Door door : connected.values()) {
                door.set(Door.Type.CRYSTAL);
        }

        for (int i = 0; i < 7; i++){
            int itemPos;
            do{
                itemPos = level.pointToCell(random());
            } while ( level.map[itemPos] != Terrain.EMPTY_SP
                    || level.heaps.get(itemPos) != null);
            if (i == 0)
                level.drop( new MeatPie(), itemPos );
            else
                level.drop(Generator.randomGun(Random.Int(3) + 2).upgrade(), itemPos);
        }
    }
}
