package com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.gun;

import static com.shatteredpixel.bathredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.bathredpixeldungeon.items.scrolls.ScrollOfTeleportation.appear;
import static com.shatteredpixel.bathredpixeldungeon.items.wands.WandOfCorruption.MAJOR_DEBUFFS;
import static com.shatteredpixel.bathredpixeldungeon.items.wands.WandOfCorruption.MINOR_DEBUFFS;
import static com.shatteredpixel.bathredpixeldungeon.items.wands.WandOfCorruption.debuffEnemy;

import com.shatteredpixel.bathredpixeldungeon.Assets;
import com.shatteredpixel.bathredpixeldungeon.Dungeon;
import com.shatteredpixel.bathredpixeldungeon.actors.Actor;
import com.shatteredpixel.bathredpixeldungeon.actors.Char;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.BathredBullets;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.EscapeRoll;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.InstantBullet;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.PewpewCooldown;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.RolledBullet;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.InfiniteBullet;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.bathredpixeldungeon.actors.buffs.RollCrit;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.abilities.giux.TeleBathr;
import com.shatteredpixel.bathredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.bathredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.bathredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.bathredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.bathredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.bathredpixeldungeon.items.Item;
import com.shatteredpixel.bathredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.bathredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.bathredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.bathredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.bathredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.bathredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.bathredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.bathredpixeldungeon.messages.Messages;
import com.shatteredpixel.bathredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.bathredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.bathredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.bathredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.bathredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.bathredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BaseGun extends MeleeWeapon {
    public static final String AC_SHOOT		= "SHOOT";
    public static final String AC_RELOAD    = "RELOAD";
    public static final String AC_BURST    = "BURST";

    protected int max_round;
    protected int round;
    protected float reload_time = 2f;
    protected int shotPerShoot = 1;
    protected int shotPerShootFired = 0;
    protected int shotPerShootTarget = 0;
    private boolean wandReload = false;
    private boolean doCrit = false;
    boolean doBurst = false;
    int killedWithBurst = 0;
    int oldMobi = 0;
    int buffsToApply = 0;
    protected float shootingSpeed = 1f;
    protected float shootingAccuracy = 1f;
    protected boolean explode = false;
    public static final String TXT_STATUS = "%d/%d";
    private boolean shootAll = false;

    {
        defaultAction = AC_SHOOT;
        usesTargeting = true;

        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 0.8f;
    }

    private static final String ROUND = "round";
    private static final String MAX_ROUND = "max_round";
    private static final String RELOAD_TIME = "reload_time";
    private static final String SHOT_PER_SHOOT = "shotPerShoot";
    private static final String SHOT_PER_FIRED = "shotPerShootFired";
    private static final String SHOT_PER_TARGET = "shotPerShootTarget";
    private static final String WAND_RELOAD = "wandReload";
    private static final String DO_CRIT = "doCrit";
    private static final String SHOOTING_SPEED = "shootingSpeed";
    private static final String SHOOTING_ACCURACY = "shootingAccuracy";
    private static final String EXPLODE = "explode";
    private static final String SHOOTALL = "shootAll";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MAX_ROUND, max_round);
        bundle.put(ROUND, round);
        bundle.put(RELOAD_TIME, reload_time);
        bundle.put(SHOT_PER_SHOOT, shotPerShoot);
        bundle.put(SHOT_PER_FIRED, shotPerShootFired);
        bundle.put(SHOT_PER_TARGET, shotPerShootTarget);
        bundle.put(WAND_RELOAD, wandReload);
        bundle.put(DO_CRIT, doCrit);
        bundle.put(SHOOTING_SPEED, shootingSpeed);
        bundle.put(SHOOTING_ACCURACY, shootingAccuracy);
        bundle.put(EXPLODE, explode);
        bundle.put(SHOOTALL, shootAll);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        max_round = bundle.getInt(MAX_ROUND);
        round = bundle.getInt(ROUND);
        reload_time = bundle.getFloat(RELOAD_TIME);
        shotPerShoot = bundle.getInt(SHOT_PER_SHOOT);
        shotPerShootFired = bundle.getInt(SHOT_PER_FIRED);
        shotPerShootTarget = bundle.getInt(SHOT_PER_TARGET);
        wandReload = bundle.getBoolean(WAND_RELOAD);
        doCrit = bundle.getBoolean(DO_CRIT);
        shootingSpeed = bundle.getFloat(SHOOTING_SPEED);
        shootingAccuracy = bundle.getFloat(SHOOTING_ACCURACY);
        explode = bundle.getBoolean(EXPLODE);
        shootAll = bundle.getBoolean(SHOOTALL);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped( hero )) {
            actions.add(AC_SHOOT);
            actions.add(AC_RELOAD);
        }
        if (hero.subClass == HeroSubClass.PEWPEW){
            actions.add(AC_BURST);
        }
        return actions;
    }

    public Item random() {
        int n = 0;
        if (Random.Int(3) == 0) {
            n++;
            if (Random.Int(3) == 0) {
                n++;
            }
        }
        level(n);

        float effectRoll = Random.Float();
        if (effectRoll < 0.3f) {
            enchant(Enchantment.randomCurse());
            cursed = true;
        } else if (effectRoll >= 0.8f){
            enchant();
        }

        return this;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {
            if (!isEquipped( hero )) {
                usesTargeting = false;
                GLog.w(Messages.get(this, "not_equipped"));
            } else {
                if (round <= 0) {
                    execute(hero, AC_RELOAD);
                } else {
                    usesTargeting = true;
                    curUser = hero;
                    curItem = this;
                    GameScene.selectCell(shooter);
                }
            }
        }
        if (action.equals(AC_RELOAD)) {
            if (isAllLoaded()){
                GLog.w(Messages.get(this, "already_loaded"));
            } else {
                reload();
            }
        }
        if (action.equals(AC_BURST)) {
            if (hero.buff(PewpewCooldown.class) == null){
                usesTargeting = true;
                curUser = hero;
                curItem = this;
                doBurst = true;
                GameScene.selectCell(burster);
            }
            else {
                GLog.w(Messages.get(this, "burst_cooldown"));
            }
        }
    }

    public boolean isAllLoaded() {
        return round >= maxRound();
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target){
        return 2;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Dagger.sneakAbility(hero, 6, 4, this);
    }

    public void reload() {
        quickReload();
        hero.busy();
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        hero.spendAndNext(reloadTime());
        GLog.i(Messages.get(this, "reloading"));
    }

    public void quickReload() {
        round = maxRound();
        wandReload = false;
        updateQuickslot();
    }

    public void manualReload() {
        manualReload(1, false, false);
    }

    public void manualReload(int amount, boolean overReload, boolean wandRel) {
        round += amount;
        if (!overReload) {
            if (round > maxRound()) {
                round = maxRound();
            }
        }
        wandReload = wandRel;
        updateQuickslot();
    }

    public boolean canWandReload() { return !wandReload;}

    public int shotPerShoot() { //발사 당 탄환의 수
        return shotPerShoot;
    }

    public int maxRound() {
        int amount = max_round;

        return amount;
    }

    public int round() {
        return round;
    }

    public float reloadTime() {
        float amount = reload_time;

        return amount;
    }

    @Override
    public int max(int lvl) {
        int damage = 3*(tier+1) +
                lvl*(tier+1);
        return damage;

    }

    @Override
    protected float baseDelay(Char owner) {
        return super.baseDelay(owner) - (hero.pointsInTalent(Talent.GIUX_INSTANTBULLET) == 3 ? 0.2f : 0);
    }

    @Override
    public String status() {
        return Messages.format(TXT_STATUS, round, maxRound());
    }

    public int Bulletmin(int lvl) {
        return 2 * tier +
                lvl      +
                RingOfSharpshooting.levelDamageBonus(hero);
    }

    public int Bulletmax(int lvl) {
        return 4 * (tier+1)   +
                lvl * (tier+1)  +
                RingOfSharpshooting.levelDamageBonus(hero);
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return knockBullet().targetingPos(user, dst);
    }

    //needs to be overridden
    public Bullet knockBullet(){
        return new Bullet();
    }

    public class Bullet extends MissileWeapon {

        {
            hitSound = Assets.Sounds.PUFF;
            tier = 1;
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return BaseGun.this.proc(attacker, defender, damage);
        }

        @Override
        public int buffedLvl() {
            return BaseGun.this.buffedLvl();
        }

        @Override
        public int damageRoll(Char owner) {
            Hero hero = (Hero) owner;
            Char enemy = hero.enemy();
            int bulletdamage = Random.NormalIntRange(Bulletmin(buffedLvl()),
                    Bulletmax(buffedLvl()));

            if (owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()) {
                bulletdamage = Math.round(bulletdamage * (1f + 0.15f * ((Hero) owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM)));
            }
            RolledBullet emp = hero.buff(RolledBullet.class);
            if (emp != null) {
                bulletdamage += emp.dmgBoost;
                emp.detach();
            }
            BathredBullets bat = hero.buff(BathredBullets.class);
            if (bat != null) {
                if (Random.Float() < 0.5) {
                    debuffEnemy((Mob) enemy, MAJOR_DEBUFFS, buffedLvl());
                } else {
                    debuffEnemy((Mob) enemy, MINOR_DEBUFFS, buffedLvl());
                }
                bat.bulletsLeft--;
                if (bat.bulletsLeft == 0)
                    bat.detach();
            }
            if (doCrit) {
                doCrit = false;
                bulletdamage = Bulletmax(buffedLvl()) + 1;
                hero.sprite.showStatus( CharSprite.POSITIVE, Messages.get(Hero.class, "crit") );
            }
            return bulletdamage;
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return BaseGun.this.hasEnchant(type, owner);
        }

        @Override
        public float delayFactor(Char user) {
            return BaseGun.this.delayFactor(user) * shootingSpeed - (hero.pointsInTalent(Talent.GIUX_INSTANTBULLET) == 3 ? 0.2f : 0);
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            float ACC = super.accuracyFactor(owner, target);
            if (owner instanceof Hero) {
                ACC *= shootingAccuracy;
            }
            if (shootAll || doBurst) {
                if (hero.pointsInTalent(Talent.GIUX_PEWPEWRANGE) == 3)
                    ACC *= 0.85f;
                else
                    ACC *= 0.65f;
            }
            return ACC;
        }

        @Override
        public int STRReq(int lvl) {
            return BaseGun.this.STRReq();
        }

        @Override
        protected void onThrow(int cell) {
            if (explode) {
                Char chInPos = Actor.findChar(cell);
                ArrayList<Char> targets = new ArrayList<>();
                int shootArea[] = PathFinder.NEIGHBOURS9;

                for (int i : shootArea) {
                    int c = cell + i;
                    if (c >= 0 && c < Dungeon.level.length()) {
                        if (Dungeon.level.heroFOV[c]) {
                            CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                            CellEmitter.center(cell).burst(BlastParticle.FACTORY, 4);
                        }
                        if (Dungeon.level.flamable[c]) {
                            Dungeon.level.destroy(c);
                            GameScene.updateMap(c);
                        }
                        Char ch = Actor.findChar(c);
                        if (ch != null && !targets.contains(ch)) {
                            targets.add(ch);
                        }
                    }
                }

                for (Char target : targets) {
                    for (int i = 0; i < shotPerShoot(); i++) {
                        curUser.shoot(target, this);
                    }
                    if (target == hero && !target.isAlive()) {
                        Dungeon.fail(getClass());
                        GLog.n(Messages.get(this, "ondeath"));
                    }
                }

                Sample.INSTANCE.play(Assets.Sounds.BLAST);
            } else {
                Char enemy = Actor.findChar(cell);
                if (enemy == null || enemy == curUser) {
                    parent = null;
                    CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                    CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                } else {
                    if (!curUser.shoot(enemy, this)) {
                        CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                    }
                    if (!enemy.isAlive()){
                        if (doBurst && hero.hasTalent(Talent.GIUX_PEWPEWRANGE)) {
                            boolean gettarg = false;
                            killedWithBurst++;
                            for (int i = oldMobi; i < hero.visibleEnemies(); i++){
                                if (shotPerShootTarget != hero.visibleEnemy(i).pos){
                                    shotPerShootTarget = hero.visibleEnemy(i).pos;
                                    oldMobi = i;
                                    gettarg = true;
                                    break;
                                }
                            }
                            if (hero.pointsInTalent(Talent.GIUX_PEWPEWRANGE) == 1 && killedWithBurst >= 2)
                                gettarg = false;
                            if (!gettarg)
                                shotPerShootTarget = cell;
                        }
                        if (hero.hasTalent(Talent.GIUX_PEWPEWKILL)){
                            round += 1 + hero.pointsInTalent(Talent.GIUX_PEWPEWKILL);
                        }
                        if (hero.hasTalent(Talent.GIUX_PEWPEWBUFF)){
                            if (Random.Int(100) <= (5 + (hero.pointsInTalent(Talent.GIUX_PEWPEWBUFF) * 15))) {
                                buffsToApply++;
                            }
                        }
                    }
                }
            }

            if (hero.buff(InfiniteBullet.class) == null) {
                round--;
            }

            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob.paralysed <= 0
                        && Dungeon.level.distance(curUser.pos, mob.pos) <= 4
                        && mob.state != mob.HUNTING) {
                    mob.beckon(curUser.pos);
                }
            }
            updateQuickslot();
        }

        @Override
        public void throwSound() {
            RollCrit crit = hero.buff(RollCrit.class);
            if (crit != null) {
                if (Random.Int(100) <= crit.getCritChance()) {
                    Sample.INSTANCE.play(Assets.Sounds.HIT_ARROW);
                    doCrit = true;
                } else
                    Sample.INSTANCE.play(Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f));
            }
            else
                Sample.INSTANCE.play(Assets.Sounds.HIT_CRUSH,1,Random.Float(0.33f,0.66f));
        }

        @Override
        public void cast(final Hero user, final int dst) {

            final int cell = throwPos(user, dst);
            user.sprite.zap(cell);
            user.busy();

            throwSound();

            Char enemy = Actor.findChar(cell);
            QuickSlotButton.target(enemy);

            if (enemy != null) {
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).reset(user.sprite, enemy.sprite, this,
                    new Callback() {
                        @Override
                        public void call() {
                            curUser = user;
                            onThrow(cell);
                            AfterShoot();
                        }
                    });
            } else {
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).reset(user.sprite, cell,this,
                    new Callback() {
                        @Override
                        public void call() {
                            curUser = user;
                            onThrow(cell);
                            AfterShoot();
                        }
                    });
            }
        }
    }

    public void AfterShoot()
    {
        shotPerShootFired++;
        if (shotPerShootFired < shotPerShoot)
            knockBullet().cast(curUser, shotPerShootTarget);
        else
        {
            shotPerShootFired = 0;
            if (doBurst && round > 0){
                knockBullet().cast(curUser, shotPerShootTarget);
            } else
            {
                for (int i = 0; i < buffsToApply; i++)
                    EscapeRoll.affectRandomBuff(hero, 10f);
                shotPerShootTarget = 0;
                oldMobi = killedWithBurst = 0;
                buffsToApply = 0;
                if (hero.subClass == HeroSubClass.PEWPEW && doBurst){
                    Buff.affect(hero, PewpewCooldown.class).set(100f);
                    doBurst = false;
                }
                if (hero.buff(TeleBathr.TeleBathrBuff.class) != null){
                    TeleBathr.teleBathrport();
                }

                //END TURN (OR NOT)
                if (hero.buff(InstantBullet.class) == null){
                    hero.spendAndNext(delayFactor(hero));
                }
                else {
                    hero.buff(InstantBullet.class).detach();
                    hero.spendAndNext(0f);
                }
            }
        }
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (target == curUser.pos) {
                    execute(hero, AC_RELOAD);
                } else {
                    shotPerShootTarget = target;
                    knockBullet().cast(curUser, target);
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    private CellSelector.Listener burster = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (target == curUser.pos) {
                    execute(hero, AC_RELOAD);
                }
                shotPerShootTarget = target;
                knockBullet().cast(curUser, target);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };
}