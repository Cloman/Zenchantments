package zedly.zenchantments;

import java.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.*;
import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.Material.*;
import org.bukkit.block.Biome;
import static org.bukkit.block.Biome.*;
import org.bukkit.block.Block;
import static org.bukkit.block.BlockFace.*;
import static org.bukkit.enchantments.Enchantment.*;
import static org.bukkit.entity.EntityType.*;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.block.Action;
import static org.bukkit.event.block.Action.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import static org.bukkit.potion.PotionEffectType.*;
import org.bukkit.util.Vector;
import particles.ParticleEffect;
import particles.ParticleEffect.BlockData;

public class CustomEnchantment {

    protected int maxLevel;
    protected String loreName;
    protected float chance;
    protected Material[] enchantable;
    protected Class[] conflicting;
    protected String description;
    protected int cooldown;
    protected double power;

    public String getName() {
        return loreName;
    }

//Empty Methods
    public boolean onBlockBreak(BlockBreakEvent evt, int level) {
        return false;
    }

    public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
        return false;
    }

    public boolean onEntityInteract(PlayerInteractEntityEvent evt, int level) {
        return false;
    }

    public boolean onEntityKill(EntityDeathEvent evt, int level) {
        return false;
    }

    public boolean onHitting(EntityDamageByEntityEvent evt, int level) {
        return false;
    }

    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level) {
        return false;
    }

    public boolean onEntityDamage(EntityDamageEvent evt, int level) {
        return false;
    }

    public boolean onPlayerFish(PlayerFishEvent evt, int level) {
        return false;
    }

    public boolean onHungerChange(FoodLevelChangeEvent evt, int level) {
        return false;
    }

    public boolean onShear(PlayerShearEntityEvent evt, int level) {
        return false;
    }

    public boolean onProjectileHit(ProjectileHitEvent evt, int level) {
        return false;
    }

    public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
        return false;
    }

    public boolean onPotionSplash(PotionSplashEvent evt, int level) {
        return false;
    }

    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level) {
        return false;
    }

    public boolean onPlayerDeath(PlayerDeathEvent evt, int level) {
        return false;
    }

    public boolean onScan(Player player, int level) {
        return false;
    }

    public boolean onScanHand(Player player, int level) {
        return false;
    }

    public boolean onFastScan(Player player, int level) {
        return false;
    }

    public boolean onFastScanHand(Player player, int level) {
        return false;
    }

    public boolean onMove(PlayerMoveEvent evt, int level) {
        return false;
    }

//Enchantments
    public static class Anthropomorphism extends CustomEnchantment {

        public Anthropomorphism() {
            maxLevel = 1;
            loreName = "Anthropomorphism";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new Class[]{Pierce.class, Switch.class};
            description = "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            Player player = evt.getPlayer();
            if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                if (player.isSneaking()) {
                    if (!Storage.anthVortex.contains(player)) {
                        Storage.anthVortex.add(player);
                    }
                    int counter = 0;
                    for (Entity p : Storage.idleBlocks.values()) {
                        if (p.equals(player)) {
                            counter++;
                        }
                    }
                    if (counter < 64 && player.getInventory().contains(COBBLESTONE)) {
                        Utilities.removeItem(player, COBBLESTONE, 1);
                        Utilities.addUnbreaking(player.getItemInHand(), 2, player);
                        player.updateInventory();
                        Location loc = player.getLocation();
                        Material mat[] = new Material[]{STONE, GRAVEL, DIRT, GRASS};
                        FallingBlock bk = loc.getWorld().spawnFallingBlock(loc, mat[Storage.rnd.nextInt(4)], (byte) 0x0);
                        bk.setDropItem(false);
                        Storage.idleBlocks.put(bk, player);
                    }
                }
                return true;
            } else if ((evt.getAction() == LEFT_CLICK_AIR || evt.getAction() == LEFT_CLICK_BLOCK)
                    || player.getItemInHand().getType() == AIR) {
                Storage.anthVortex.remove(player);
                List<FallingBlock> toRemove = new ArrayList<>();
                for (FallingBlock blk : Storage.idleBlocks.keySet()) {
                    if (Storage.idleBlocks.get(blk).equals(player)) {
                        Storage.attackBlocks.put(blk, power);
                        toRemove.add(blk);
                        blk.setVelocity(player.getTargetBlock((HashSet<Byte>) null, 7)
                                .getLocation().subtract(player.getLocation()).toVector().multiply(.25));
                    }
                }
                for (FallingBlock blk : toRemove) {
                    Storage.idleBlocks.remove(blk);
                }
            }
            return false;
        }
    }

    public static class Arborist extends CustomEnchantment {

        public Arborist() {
            maxLevel = 3;
            loreName = "Arborist";
            chance = 0;
            enchantable = Storage.axes;
            conflicting = new Class[]{};
            description = "Drops more apples, sticks, and saplings when used on leaves and wood";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            Block blk = evt.getBlock();
            if (blk.getType() == LOG || blk.getType() == LOG_2 || blk.getType() == LEAVES_2 || blk.getType() == LEAVES) {
                short s = (short) blk.getData();
                if (s >= 8) {
                    s -= 8;
                }
                ItemStack stk;
                if (blk.getType() == LOG_2 || blk.getType() == LEAVES_2) {
                    stk = new ItemStack(SAPLING, 1, (short) (s + 4));
                } else {
                    stk = new ItemStack(SAPLING, 1, s);
                }
                if (Storage.rnd.nextInt(10) >= (9 - level) / (power + .001)) {
                    if (Storage.rnd.nextInt(3) % 3 == 0) {
                        evt.getBlock().getWorld()
                                .dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), stk);
                    }
                    if (Storage.rnd.nextInt(3) % 3 == 0) {
                        evt.getBlock().getWorld()
                                .dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(STICK, 1));
                    }
                    if (Storage.rnd.nextInt(3) % 3 == 0) {
                        evt.getBlock().getWorld()
                                .dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(APPLE, 1));
                    }
                    if (Storage.rnd.nextInt(65) == 25) {
                        evt.getBlock().getWorld()
                                .dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(GOLDEN_APPLE, 1));
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public static class Archaeology extends CustomEnchantment {

        public Archaeology() {
            maxLevel = 3;
            loreName = "Archaeology";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.picks, Storage.spades);
            conflicting = new Class[]{};
            description = "Occasionally drops ancient artifacts when mining";
            cooldown = 0;
            power = .0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            if (evt.getBlock().getType() == STONE || evt.getBlock().getType() == DIRT) {
                if (Storage.rnd.nextInt((int) Math.round(300.0 / (level * power + .001))) == 20) {
                    Artifact.drop(evt.getBlock());
                    return true;
                }
            }
            return false;
        }
    }

    public static class Bind extends CustomEnchantment {

        public Bind() {
            maxLevel = 1;
            loreName = "Bind";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.axes, ArrayUtils.addAll(Storage.boots, ArrayUtils.addAll(Storage.bows, ArrayUtils.addAll(Storage.chestplates, ArrayUtils.addAll(Storage.helmets, ArrayUtils.addAll(Storage.hoes, ArrayUtils.addAll(Storage.leggings, ArrayUtils.addAll(Storage.lighters, ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.rods, ArrayUtils.addAll(Storage.shears, ArrayUtils.addAll(Storage.spades, Storage.swords))))))))))));
            conflicting = new Class[]{};
            description = "Keeps items with this enchantment in your inventory after death";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onPlayerDeath(final PlayerDeathEvent evt, int level) {
            Config config = Config.get(evt.getEntity().getWorld());

            final List<ItemStack> contents = new ArrayList<>();
            final Map<ItemStack, Integer> armorContents = new HashMap<>();

            for (ItemStack stk : evt.getEntity().getInventory().getContents()) {
                if (stk != null) {
                    if (config.getEnchants(stk).containsKey(this)) {
                        contents.add(stk);
                    }
                }
            }

            for (int i = 0; i < evt.getEntity().getInventory().getArmorContents().length; i++) {
                ItemStack stk = evt.getEntity().getInventory().getArmorContents()[i];
                if (stk != null) {
                    if (config.getEnchants(stk).containsKey(this)) {
                        armorContents.put(stk, i);
                    }
                }
            }
            evt.getDrops().removeAll(contents);
            Iterator it = evt.getDrops().iterator();
            while (it.hasNext()) {
                ItemStack stk = (ItemStack) it.next();
                if (armorContents.containsKey(stk)) {
                    it.remove();
                }
            }
            final ItemStack[] invFinal = new ItemStack[contents.size()];
            for (int i = 0; i < contents.size(); i++) {
                invFinal[i] = contents.get(i);
            }
            final ItemStack[] armorFinal = new ItemStack[4];
            for (ItemStack s : armorContents.keySet()) {
                armorFinal[armorContents.get(s)] = s;
            }
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                public void run() {
                    evt.getEntity().getInventory().setContents(invFinal);
                    evt.getEntity().getInventory().setArmorContents(armorFinal);
                }
            }, 1);
            return true;
        }

    }

    public static class BlazesCurse extends CustomEnchantment {

        public BlazesCurse() {
            maxLevel = 1;
            loreName = "Blaze's Curse";
            chance = 0;
            enchantable = Storage.chestplates;
            conflicting = new Class[]{};
            description = "Causes the player to be unharmed in lava and fire, but damages them in water and rain";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityDamage(EntityDamageEvent evt, int level) {
            if (evt.getCause() == EntityDamageEvent.DamageCause.LAVA || evt.getCause() == EntityDamageEvent.DamageCause.FIRE || evt.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                evt.setCancelled(true);
                return true;
            }
            return false;
        }

        public boolean onScan(Player player, int level) {
            Location loc = player.getLocation();
            Material mat = player.getLocation().getBlock().getType();
            if (mat == STATIONARY_WATER || mat == WATER) {
                EntityDamageEvent evt = new EntityDamageEvent(player, DamageCause.DROWNING, 100);
                Bukkit.getPluginManager().callEvent(evt);
                player.setLastDamageCause(evt);
                if (!evt.isCancelled()) {
                    player.damage(1.5f);
                }
            }
            if (player.getWorld().hasStorm() == true) {
                List<Boolean> b = new ArrayList<>();
                if (player.getLocation().getBlockY() > 256) {
                    return false;
                }
                for (int x = player.getLocation().getBlockY() + 1; x <= 256; x++) {
                    loc.setY(x);
                    if (loc.getBlock().getType() != AIR) {
                        b.add(Boolean.FALSE);
                    } else {
                        b.add(Boolean.TRUE);
                    }
                }
                if (!b.contains(Boolean.FALSE)) {
                    Biome[] biomes = new Biome[]{DESERT, FROZEN_OCEAN, FROZEN_RIVER, ICE_FLATS,
                        ICE_MOUNTAINS, DESERT_HILLS, COLD_BEACH, TAIGA_COLD, TAIGA_COLD_HILLS,
                        SAVANNA, SAVANNA_ROCK, MESA, MESA_ROCK, MESA_CLEAR_ROCK, MUTATED_DESERT,
                        MUTATED_ICE_FLATS, MUTATED_TAIGA_COLD, MUTATED_SAVANNA, MUTATED_SAVANNA_ROCK,
                        MUTATED_MESA, MUTATED_MESA_ROCK, MUTATED_MESA_CLEAR_ROCK};
                    if (!ArrayUtils.contains(biomes, player.getLocation().getBlock().getBiome())) {
                        EntityDamageEvent evt = new EntityDamageEvent(player, DamageCause.DROWNING, .5);
                        Bukkit.getPluginManager().callEvent(evt);
                        player.setLastDamageCause(evt);
                        if (!evt.isCancelled()) {
                            player.damage(.5f);
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Blizzard extends CustomEnchantment {

        public Blizzard() {
            maxLevel = 3;
            loreName = "Blizzard";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{Firestorm.class};
            description = "Spawns a blizzard where the arrow strikes freezing nearby entities";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantBlizzard arrow = new EnchantArrow.ArrowEnchantBlizzard((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
    }

    public static class Bounce extends CustomEnchantment {

        public Bounce() {
            maxLevel = 5;
            loreName = "Bounce";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{};
            description = "Preserves momentum when on slime blocks";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onFastScan(Player player, int level) {
            if (player.getVelocity().getY() < 0 && (player.getLocation().getBlock().getRelative(0, -1, 0).getType() == SLIME_BLOCK
                    || player.getLocation().getBlock().getType() == SLIME_BLOCK
                    || (player.getLocation().getBlock().getRelative(0, -2, 0).getType() == SLIME_BLOCK) && (level * power) > 2.0)) {
                if (!player.isSneaking()) {
                    player.setVelocity(player.getVelocity().setY(.56 * level * power));
                    return true;
                }
                player.setFallDistance(0);
            }
            return false;
        }
    }

    public static class Burst extends CustomEnchantment {

        public Burst() {
            maxLevel = 3;
            loreName = "Burst";
            chance = 0;
            enchantable = enchantable = Storage.bows;
            conflicting = new Class[]{Spread.class};
            description = "Rapidly fires arrows in series";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(final PlayerInteractEvent evt, int level) {
            final Player player = evt.getPlayer();
            if (evt.getAction().equals(RIGHT_CLICK_AIR) || evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
                if (Utilities.removeItemCheck(player, Material.ARROW, (short) 0, 1)) {
                    Utilities.addUnbreaking(player.getItemInHand(), (int) Math.round(level / 2.0 + 1), player);
                    player.setItemInHand(player.getItemInHand());
                    for (int i = 0; i <= (int) Math.round((power * level) + 1); i++) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                            public void run() {
                                Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(), player.getLocation().getDirection(), 1, 0);
                                arrow.setShooter(player);
                                arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(1.7));

                                EntityShootBowEvent event = new EntityShootBowEvent(player, player.getItemInHand(), arrow, 1f);
                                Bukkit.getPluginManager().callEvent(event);

                                Bukkit.getPluginManager().callEvent(new ProjectileLaunchEvent(arrow));

                                arrow.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));
                                arrow.setCritical(true);
                                Utilities.putArrow(arrow, new EnchantArrow.ArrowGenericMulitple(arrow), player);
                            }
                        }, i * 2);
                    }
                    return true;
                }
            }
            return false;
        }

    }

    public static class Combustion extends CustomEnchantment {

        public Combustion() {
            maxLevel = 4;
            loreName = "Combustion";
            chance = 0;
            enchantable = Storage.chestplates;
            conflicting = new Class[]{};
            description = "Lights attacking entities on fire when player is attacked";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBeingHit(EntityDamageByEntityEvent evt, int level) {
            Entity ent;
            if (evt.getDamager().getType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) evt.getDamager();
                if (arrow.getShooter() instanceof LivingEntity) {
                    ent = (Entity) arrow.getShooter();
                } else {
                    return false;
                }
            } else {
                ent = evt.getDamager();
            }
            if (Utilities.canDamage(ent, evt.getEntity())) {
                ent.setFireTicks((int) (50 * level * power));
                return true;
            }
            return false;
        }
    }

    public static class Conversion extends CustomEnchantment {

        public Conversion() {
            maxLevel = 4;
            loreName = "Conversion";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new Class[]{};
            description = "Converts XP to health when right clicking and sneaking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                final Player player = (Player) evt.getPlayer();
                if (player.isSneaking()) {
                    if (player.getLevel() > 1) {
                        if (player.getHealth() < 20) {
                            player.setLevel((int) (player.getLevel() - 1));
                            player.setHealth(Math.min(20, player.getHealth() + 2 * power * level));
                            for (int i = 0; i < 3; i++) {
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                    public void run() {
                                        ParticleEffect.HEART.display(.5f, .5f, .5f, .1f, 10, Utilities.getCenter(player.getLocation()), 32);
                                    }
                                }, ((i * 5) + 1));
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public static class Decapitation extends CustomEnchantment {

        public Decapitation() {
            maxLevel = 4;
            loreName = "Decapitation";
            chance = 0;
            enchantable = enchantable = Storage.swords;
            conflicting = new Class[]{};
            description = "Increases the chance for dropping the enemies head on death";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityKill(EntityDeathEvent evt, int level) {
            EntityType[] t = new EntityType[]{SKELETON, WITHER_SKULL, ZOMBIE, PLAYER, CREEPER};
            short id = (short) ArrayUtils.indexOf(t, evt.getEntityType());
            if (id != -1 && id != 1) {
                if (id == 0) {
                    if (((Skeleton) evt.getEntity()).getSkeletonType() == SkeletonType.WITHER) {
                        return false;
                    }
                }
                ItemStack stk = new ItemStack(Material.SKULL_ITEM, 1, id);
                if (id == 3) {
                    SkullMeta meta = (SkullMeta) stk.getItemMeta();
                    meta.setOwner(evt.getEntity().getName());
                    stk.setItemMeta(meta);
                }
                if ((id != 3 && Storage.rnd.nextInt((int) Math.round(150.0 / (level * power))) == 0)
                        || Storage.rnd.nextInt((int) Math.round(35.0 / level * power)) == 0) {
                    evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(), stk);
                    return true;
                }
            }
            return false;
        }
    }

    public static class Extraction extends CustomEnchantment {

        public Extraction() {
            maxLevel = 3;
            loreName = "Extraction";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new Class[]{Switch.class};
            description = "Smelts and yields more product from ores";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, final int level) {
            if (evt.getBlock().getType() == GOLD_ORE || evt.getBlock().getType() == IRON_ORE) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                for (int x = 0; x < Storage.rnd.nextInt((int) Math.round(power * level + 1)) + 1; x++) {
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()),
                            new ItemStack(evt.getBlock().getType() == GOLD_ORE ? GOLD_INGOT : IRON_INGOT));
                }
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                ParticleEffect.FLAME.display(.5f, .5f, .5f, .1f, 10, Utilities.getCenter(evt.getBlock().getLocation()), 32);
                return true;
            }
            return false;
        }
    }

    public static class Fire extends CustomEnchantment {

        public Fire() {
            maxLevel = 1;
            loreName = "Fire";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.axes, Storage.spades));
            conflicting = new Class[]{Switch.class, Variety.class};
            description = "Drops the smelted version of the block broken";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            Material mat = AIR;
            short itemInfo = 0;
            short s = evt.getBlock().getData();
            if (ArrayUtils.contains(Storage.picks, evt.getPlayer().getItemInHand().getType())) {
                if (evt.getBlock().getType() == STONE) {
                    if (s == 1 || s == 3 || s == 5) {
                        s++;
                        mat = STONE;
                        itemInfo = s;
                    } else if (s == 2 || s == 4 || s == 6) {
                        return false;
                    } else {
                        mat = SMOOTH_BRICK;
                    }
                } else if (evt.getBlock().getType() == IRON_ORE) {
                    mat = IRON_INGOT;
                } else if (evt.getBlock().getType() == GOLD_ORE) {
                    mat = GOLD_INGOT;
                } else if (evt.getBlock().getType() == COBBLESTONE) {
                    mat = STONE;
                } else if (evt.getBlock().getType() == SPONGE && s == 1) {
                    mat = SPONGE;
                } else if (evt.getBlock().getType() == MOSSY_COBBLESTONE) {
                    mat = SMOOTH_BRICK;
                    itemInfo = 1;
                } else if (evt.getBlock().getType() == NETHERRACK) {
                    mat = NETHER_BRICK_ITEM;
                } else if (evt.getBlock().getType() == SMOOTH_BRICK && evt.getBlock().getData() == 0) {
                    mat = SMOOTH_BRICK;
                    itemInfo = 2;
                }
            }
            if (evt.getBlock().getType() == CHORUS_PLANT) {
                if (Storage.rnd.nextBoolean()) {
                    mat = CHORUS_FRUIT_POPPED;
                } else {
                    Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                    evt.setCancelled(true);
                    evt.getBlock().setType(AIR);
                    ParticleEffect.FLAME.display(.5f, .5f, .5f, .1f, 10, Utilities.getCenter(evt.getBlock().getLocation()), 32);
                    return true;
                }
            }
            if (evt.getBlock().getType() == SAND) {
                mat = GLASS;
            } else if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                mat = COAL;
                itemInfo = 1;
            } else if (evt.getBlock().getType() == CLAY) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                ParticleEffect.FLAME.display(.5f, .5f, .5f, .1f, 10, Utilities.getCenter(evt.getBlock().getLocation()), 32);
                for (int x = 0; x < 4; x++) {
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(CLAY_BRICK));
                }
                return true;
            } else if (evt.getBlock().getType() == CACTUS) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                Location location = evt.getBlock().getLocation().clone();
                double height = location.getY();
                for (double i = location.getY(); i <= 256; i++) {
                    location.setY(i);
                    if (location.getBlock().getType() == CACTUS) {
                        height++;
                    } else {
                        break;
                    }
                }
                for (double i = height - 1; i >= evt.getBlock().getLocation().getY(); i--) {
                    location.setY(i);
                    ParticleEffect.FLAME.display(.5f, .5f, .5f, .1f, 10, Utilities.getCenter(evt.getBlock().getLocation()), 32);
                    evt.getBlock().setType(AIR);
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(location), new ItemStack(INK_SACK, 1, (short) 2));
                    return true;
                }
            }
            if (mat != AIR) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack((mat), 1, itemInfo));
                ParticleEffect.FLAME.display(.5f, .5f, .5f, .1f, 10, Utilities.getCenter(evt.getBlock().getLocation()), 32);
                return true;
            }
            return false;
        }
    }

    public static class Firestorm extends CustomEnchantment {

        public Firestorm() {
            maxLevel = 3;
            loreName = "Firestorm";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{Blizzard.class};
            description = "Spawns a firestorm where the arrow strikes burning nearby entities";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantFirestorm arrow = new EnchantArrow.ArrowEnchantFirestorm((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

    }

    public static class Fireworks extends CustomEnchantment {

        public Fireworks() {
            maxLevel = 4;
            loreName = "Fireworks";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Shoots arrows that burst into fireworks upon impact";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantFirework arrow = new EnchantArrow.ArrowEnchantFirework((Projectile) evt.getProjectile(), level);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

    }

    public static class Force extends CustomEnchantment {

        public Force() {
            maxLevel = 3;
            loreName = "Force";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new Class[]{RainbowSlam.class, Gust.class};
            description = "Pushes and pulls nearby mobs, configurable through shift clicking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (!Storage.forceModes.containsKey(evt.getPlayer())) {
                Storage.forceModes.put(evt.getPlayer(), 1);
            }
            if (evt.getPlayer().isSneaking()) {
                if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                    if (Storage.forceModes.get(evt.getPlayer()) < 3) {
                        Storage.forceModes.put(evt.getPlayer(), Storage.forceModes.get(evt.getPlayer()) + 1);
                    } else {
                        Storage.forceModes.put(evt.getPlayer(), 1);
                    }
                    switch (Storage.forceModes.get(evt.getPlayer())) {
                        case 1:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Push Mode");
                            break;
                        case 2:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Pull Mode");
                            break;
                    }
                }
                return false;
            }
            int mode = Storage.forceModes.get(evt.getPlayer());
            if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                List<Entity> nearEnts = evt.getPlayer().getNearbyEntities(5, 5, 5);
                if (!nearEnts.isEmpty()) {
                    if (evt.getPlayer().getFoodLevel() >= 2) {
                        if (Storage.rnd.nextInt(10) == 5) {
                            FoodLevelChangeEvent event = new FoodLevelChangeEvent(evt.getPlayer(), 2);
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                evt.getPlayer().setFoodLevel(evt.getPlayer().getFoodLevel() - 2);
                            }
                        }
                        for (Entity ent : nearEnts) {
                            Location playLoc = evt.getPlayer().getLocation();
                            Location entLoc = ent.getLocation();
                            Location total = mode == 1 ? entLoc.subtract(playLoc) : playLoc.subtract(entLoc);
                            Vector vect = new Vector(total.getX(), total.getY(), total.getZ()).multiply((.1f + (power * level * .2f)));
                            vect.setY(vect.getY() > 1 ? 1 : -1);
                            if (Utilities.canDamage(evt.getPlayer(), ent)) {
                                ent.setVelocity(vect);
                            }
                        }
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class FrozenStep extends CustomEnchantment {

        public FrozenStep() {
            maxLevel = 3;
            loreName = "Frozen Step";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{NetherStep.class};
            description = "Allows the player to walk on water and safely emerge from it when sneaking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            if (player.isSneaking() && player.getLocation().getBlock().getType() == STATIONARY_WATER && !player.isFlying()) {
                player.setVelocity(player.getVelocity().setY(.4));
            }
            Block block = (Block) player.getLocation().getBlock();
            int radius = (int) Math.round(power * level + 2);
            for (int x = -(radius); x <= radius; x++) {
                for (int z = -(radius); z <= radius; z++) {
                    if (block.getRelative(x, -1, z).getLocation().distanceSquared(block.getRelative(DOWN).getLocation()) < radius * radius - 2) {
                        if (block.getRelative(x, -1, z).getData() == 0 && block.getRelative(x, -1, z).getType() == STATIONARY_WATER && block.getRelative(x, 0, z).getType() == AIR) {
                            if (Utilities.canEdit(player, block)) {
                                block.getRelative(x, -1, z).setType(PACKED_ICE);
                                Storage.waterLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                            }
                        }
                        if (Storage.waterLocs.containsKey(block.getRelative(x, -1, z).getLocation())) {
                            Storage.waterLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Fuse extends CustomEnchantment {

        public Fuse() {
            maxLevel = 1;
            loreName = "Fuse";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Instantly ignites anything explosive";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantFuse arrow = new EnchantArrow.ArrowEnchantFuse((Projectile) evt.getProjectile());
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

    }

    public static class Germination extends CustomEnchantment {

        public Germination() {
            maxLevel = 3;
            loreName = "Germination";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new Class[]{};
            description = "Uses bonemeal from the player's inventory to grow nearby plants";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = (int) Math.round(power * level + 2);
                int radiusY = 2;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                if (((block.getRelative(x, y, z).getType() == CROPS
                                        || block.getRelative(x, y, z).getType() == POTATO
                                        || block.getRelative(x, y, z).getType() == CARROT
                                        || block.getRelative(x, y, z).getType() == MELON_STEM
                                        || block.getRelative(x, y, z).getType() == PUMPKIN_STEM) && block.getRelative(x, y, z).getData() < 7)
                                        || ((block.getRelative(x, y, z).getType() == COCOA) && block.getRelative(x, y, z).getData() < 8)
                                        || ((block.getRelative(x, y, z).getType() == BEETROOT_BLOCK) && block.getRelative(x, y, z).getData() < 3)) {
                                    if (!Utilities.canEdit(evt.getPlayer(), block.getRelative(x, y, z))) {
                                        continue;
                                    }
                                    if (Utilities.removeItemCheck(evt.getPlayer(), INK_SACK, (short) 15, 1) || evt.getPlayer().getGameMode().equals(CREATIVE)) {
                                        if (Storage.rnd.nextBoolean()) {
                                            Utilities.grow(block.getRelative(x, y, z));
                                            Utilities.grow(block.getRelative(x, y, z));
                                        } else {
                                            Utilities.grow(block.getRelative(x, y, z));
                                        }
                                        ParticleEffect.VILLAGER_HAPPY.display(.3f, .3f, .3f, 1f, 30, Utilities.getCenter(block.getRelative(x, y, z).getLocation()), 32);
                                        evt.getPlayer().updateInventory();
                                        if (Storage.rnd.nextInt(10) == 3) {
                                            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Glide extends CustomEnchantment {

        public Glide() {
            maxLevel = 3;
            loreName = "Glide";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new Class[]{};
            description = "Gently brings the player back to the ground when sneaking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onFastScan(Player player, int level) {
            if (!Storage.sneakGlide.containsKey(player)) {
                Storage.sneakGlide.put(player, player.getLocation().getY());
            }
            if (!player.isSneaking() || Storage.sneakGlide.get(player) == player.getLocation().getY()) {
                return false;
            }
            boolean b = false;
            for (int i = -5; i < 0; i++) {
                if (player.getLocation().getBlock().getRelative(0, i, 0).getType() != AIR) {
                    b = true;
                }
            }
            if (!b) {
                double sinPitch = Math.sin(Math.toRadians(player.getLocation().getPitch()));
                double cosPitch = Math.cos(Math.toRadians(player.getLocation().getPitch()));
                double sinYaw = Math.sin(Math.toRadians(player.getLocation().getYaw()));
                double cosYaw = Math.cos(Math.toRadians(player.getLocation().getYaw()));
                double y = -1 * (sinPitch);
                Vector v = new Vector(-cosPitch * sinYaw, 0, -1 * (-cosPitch * cosYaw));
                v.multiply(level * power / 2);
                v.setY(-1);
                player.setVelocity(v);
                player.setFallDistance((float) (6 - level * power));
                Location l = player.getLocation().clone();
                l.setY(l.getY() - 3);
                ParticleEffect.CLOUD.display(0, 0, 0, .1f, 1, l, 32);
            }
            if (Storage.rnd.nextInt(5 * level) == 5) {
                ItemStack[] s = player.getInventory().getArmorContents();
                for (int i = 0; i < 4; i++) {
                    if (s[i] != null) {
                        Map<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(s[i]);
                        if (map.containsKey(this)) {
                            Utilities.addUnbreaking(player, s[i], 1);
                        }
                        if (s[i].getDurability() > s[i].getType().getMaxDurability()) {
                            s[i] = null;
                        }
                    }
                }
                player.getInventory().setArmorContents(s);
            }
            Storage.sneakGlide.put(player, player.getLocation().getY());
            return true;
        }

    }

    public static class Gluttony extends CustomEnchantment {

        public Gluttony() {
            maxLevel = 1;
            loreName = "Gluttony";
            chance = 0;
            enchantable = Storage.helmets;
            conflicting = new Class[]{};
            description = "Automatically eats for the player";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            Material[] mat = new Material[]{RABBIT_STEW, COOKED_BEEF, PUMPKIN_PIE,
                GRILLED_PORK, BAKED_POTATO, BEETROOT_SOUP, COOKED_CHICKEN, COOKED_MUTTON,
                MUSHROOM_SOUP, COOKED_FISH, COOKED_FISH, BREAD, APPLE, CARROT_ITEM, COOKIE,
                MELON, BEETROOT};
            int[] ints = {10, 8, 8, 8, 6, 6, 6, 6, 6, 6, 5, 5, 4, 3, 2, 2, 1};
            int check = 0;
            for (int i = 0; i < mat.length; i++) {
                if (mat[i] == COOKED_FISH) {
                    check = (check + 1) % 2;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(mat[i], 1, (short) check), 1)
                        && player.getFoodLevel() <= 20 - ints[i]) {
                    Utilities.removeItem(player, mat[i], (short) check, 1);
                    player.setFoodLevel(player.getFoodLevel() + ints[i]);
                    if (mat[i] == RABBIT_STEW || mat[i] == MUSHROOM_SOUP) {
                        player.getInventory().addItem(new ItemStack(BOWL));
                    }
                }
            }
            return true;
        }
    }

    public static class GoldRush extends CustomEnchantment {

        public GoldRush() {
            maxLevel = 3;
            loreName = "Gold Rush";
            chance = 0;
            enchantable = Storage.spades;
            conflicting = new Class[]{};
            description = "Randomly drops gold nuggets when mining sand";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            if (evt.getBlock().getType() == SAND && Storage.rnd.nextInt(100) >= (100 - (level * power * 3))) {
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(GOLD_NUGGET));
            }
            return true;
        }
    }

    public static class Grab extends CustomEnchantment {

        public Grab() {
            maxLevel = 1;
            loreName = "Grab";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.axes, Storage.spades));
            conflicting = new Class[]{};
            description = "Teleports mined items and XP directly to the player";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(final BlockBreakEvent evt, int level) {
            Storage.grabLocs.put(evt.getBlock(), evt.getPlayer().getLocation());
            final Block block = evt.getBlock();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                public void run() {
                    Storage.grabLocs.remove(block);
                }
            }, 3);
            return true;
        }
    }

    public static class GreenThumb extends CustomEnchantment {

        public GreenThumb() {
            maxLevel = 3;
            loreName = "Green Thumb";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new Class[]{};
            description = "Grows the foliage around the player";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            Location loc = player.getLocation().clone();
            int radius = (int) Math.round(power * level + 2);
            for (int x = -(radius); x <= radius; x++) {
                for (int y = -(radius) - 1; y <= radius - 1; y++) {
                    for (int z = -(radius); z <= radius; z++) {
                        Block block = (Block) loc.getBlock();
                        if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radius * radius) {
                            int pr = Storage.rnd.nextInt((int) (300 / (power * level / 2)));
                            if (pr == 1 || level == 10) {
                                byte data = 0;
                                Material mat = AIR;
                                boolean test = false;
                                int t = 0;
                                switch (block.getRelative(x, y, z).getType()) {
                                    case DIRT:
                                        if (block.getRelative(x, y, z).getData() != 2) {
                                            if (block.getRelative(x, y + 1, z).getType() == AIR) {
                                                test = true;
                                                switch (block.getBiome()) {
                                                    case MUSHROOM_ISLAND:
                                                    case MUSHROOM_ISLAND_SHORE:
                                                        mat = MYCEL;
                                                        break;
                                                    case REDWOOD_TAIGA:
                                                    case REDWOOD_TAIGA_HILLS:
                                                    case TAIGA_COLD:
                                                    case TAIGA_COLD_HILLS:
                                                        mat = GRASS;
                                                        data = (byte) 2;
                                                        break;
                                                    default:
                                                        mat = GRASS;
                                                        data = (byte) 0;
                                                }
                                            }
                                        }
                                        break;
                                    case POTATO:
                                    case CROPS:
                                    case CARROT:
                                    case NETHER_WARTS:
                                    case PUMPKIN_STEM:
                                    case MELON_STEM:
                                    case COCOA:
                                    case BEETROOT_BLOCK:
                                        test = Utilities.grow(block.getRelative(x, y, z));

                                        break;
                                }
                                if (block.getRelative(x, y, z).getType() == DIRT && test) {
                                    block.getRelative(x, y, z).setType(mat);
                                    block.getRelative(x, y, z).setData(data);
                                }
                                if (test) {
                                    ParticleEffect.VILLAGER_HAPPY.display(.3f, .3f, .3f, 1f, 20, Utilities.getCenter(block.getRelative(x, y + 1, z).getLocation()), 32);
                                }
                                if (test) {
                                    int chc = Storage.rnd.nextInt(50);
                                    if (chc > 42 && level != 10) {
                                        ItemStack[] s = player.getInventory().getArmorContents();
                                        for (int i = 0; i < 4; i++) {
                                            if (s[i] != null) {
                                                Map<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(s[i]);
                                                if (map.containsKey(this)) {
                                                    Utilities.addUnbreaking(player, s[i], 1);
                                                }
                                                if (s[i].getDurability() > s[i].getType().getMaxDurability()) {
                                                    s[i] = null;
                                                }
                                            }
                                        }
                                        player.getInventory().setArmorContents(s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Gust extends CustomEnchantment {

        public Gust() {
            maxLevel = 1;
            loreName = "Gust";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new Class[]{Force.class, RainbowSlam.class};
            description = "Pushes the user through the air at the cost of their health";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(final PlayerInteractEvent evt, int level) {
            if (evt.getAction().equals(RIGHT_CLICK_AIR) || evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
                if (evt.getPlayer().getHealth() > 2) {
                    final Block blk = evt.getPlayer().getTargetBlock((HashSet<Byte>) null, 10);
                    evt.getPlayer().setVelocity(blk.getLocation().toVector().subtract(evt.getPlayer().getLocation().toVector()).multiply(.25 * power));
                    evt.getPlayer().setFallDistance(0);
                    EntityDamageEvent event = new EntityDamageEvent(evt.getPlayer(), DamageCause.MAGIC, 2);
                    Bukkit.getPluginManager().callEvent(event);
                    evt.getPlayer().setLastDamageCause(event);
                    if (!event.isCancelled()) {
                        evt.getPlayer().damage(2f);
                    }
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                        public void run() {
                            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 5, evt.getPlayer());
                        }
                    }, 1);
                }
            }
            return true;
        }
    }

    public static class Harvest extends CustomEnchantment {

        public Harvest() {
            maxLevel = 3;
            loreName = "Harvest";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new Class[]{};
            description = "Harvests fully grown crops within a radius when clicked";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = (int) Math.round(power * level + 2);
                int radiusY = 1;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                if ((block.getRelative(x, y + 1, z).getType() == MELON_BLOCK || block.getRelative(x, y + 1, z).getType() == PUMPKIN)
                                        || ((block.getRelative(x, y + 1, z).getType() == NETHER_WARTS || block.getRelative(x, y + 1, z).getType() == BEETROOT_BLOCK) && block.getRelative(x, y + 1, z).getData() == 3)
                                        || ((block.getRelative(x, y + 1, z).getType() == CROPS || block.getRelative(x, y + 1, z).getType() == POTATO
                                        || (block.getRelative(x, y + 1, z).getType() == CARROT)) && block.getRelative(x, y + 1, z).getData() == 7)) {
                                    if (!Utilities.canEdit(evt.getPlayer(), block.getRelative(x, y + 1, z))) {
                                        continue;
                                    }
                                    if (Storage.rnd.nextBoolean()) {
                                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                    }
                                    Storage.grabLocs.put(block.getRelative(x, y + 1, z), evt.getPlayer().getLocation());
                                    final Block blk = block.getRelative(x, y + 1, z);
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                        public void run() {
                                            Storage.grabLocs.remove(blk);
                                        }
                                    }, 3);
                                    block.getRelative(x, y + 1, z).breakNaturally();
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Haste extends CustomEnchantment {

        public Haste() {
            maxLevel = 4;
            loreName = "Haste";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.spades, Storage.axes));
            conflicting = new Class[]{};
            description = "Gives the player a mining boost";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScanHand(Player player, int level) {
            Utilities.addPotion(player, FAST_DIGGING, 610, (int) Math.round(level * power));
            player.setMetadata("ze.haste", new FixedMetadataValue(Storage.zenchantments, null));
            return false;
        }

    }

    public static class Haul extends CustomEnchantment {

        public Haul() {
            maxLevel = 1;
            loreName = "Haul";
            chance = 0;
            enchantable = enchantable = Storage.picks;
            conflicting = new Class[]{Laser.class};
            description = "Allows for dragging blocks around";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(final PlayerInteractEvent evt, int level) {
            if (evt.getAction().equals(RIGHT_CLICK_BLOCK) && Utilities.canUse(evt.getPlayer(), loreName)) {
                if (!Storage.haulBlocks.containsKey(evt.getPlayer())) {
                    int[] bad = new int[]{6, 7, 8, 9, 10, 11, 23, 25, 26, 31, 32, 34,
                        36, 37, 38, 39, 40, 50, 51, 52, 54, 59, 61, 62, 63, 64,
                        65, 68, 69, 71, 75, 76, 77, 78, 81, 83, 84, 90, 96, 104,
                        105, 106, 111, 115, 117, 119, 120, 127, 131, 132, 137,
                        140, 141, 142, 143, 144, 146, 154, 158, 166, 167, 171, 175,
                        176, 177, 193, 194, 195, 196, 197, 209, 255};
                    if (!ArrayUtils.contains(bad, evt.getClickedBlock().getTypeId())) {
                        Storage.haulBlocks.put(evt.getPlayer(), evt.getClickedBlock());
                    }
                }
                Storage.haulBlockDelay.put(evt.getPlayer(), 0);
                if (Storage.haulBlocks.get(evt.getPlayer()) == null) {
                    return false;
                }
                if (!Storage.haulBlocks.get(evt.getPlayer()).equals(evt.getClickedBlock())) {
                    Block toUse = evt.getPlayer().getTargetBlock((HashSet<Byte>) null, 4).getRelative(evt.getBlockFace());
                    if (toUse.getType() == AIR && evt.getPlayer().getTargetBlock((HashSet<Byte>) null, 4).getType() != AIR) {
                        if (Utilities.canEdit(evt.getPlayer(), toUse)) {
                            toUse.setType(Storage.haulBlocks.get(evt.getPlayer()).getType());
                            toUse.setData(Storage.haulBlocks.get(evt.getPlayer()).getData());
                            Block toBreak = Storage.haulBlocks.get(evt.getPlayer());
                            toBreak.setType(AIR);
                            Storage.haulBlocks.put(evt.getPlayer(), toUse);
                            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                        }
                    }
                }
            }
            EnchantPlayer.matchPlayer(evt.getPlayer()).setCooldown(loreName, cooldown == 0 ? 1 : cooldown);
            return false;
        }
    }

    public static class IceAspect extends CustomEnchantment {

        public IceAspect() {
            maxLevel = 2;
            loreName = "Ice Aspect";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new Class[]{};
            description = "Temporarily freezes the target";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onHitting(EntityDamageByEntityEvent evt, int level) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                Utilities.addPotion((LivingEntity) evt.getEntity(), SLOW,
                        (int) Math.round(40 + level * power * 40), (int) Math.round(power * level * 2));
                ParticleEffect.CLOUD.display(1f, 2f, 1f, .1f, 10, Utilities.getCenter(Utilities.getCenter(evt.getEntity().getLocation())), 32);
            }
            return true;
        }
    }

    public static class Jump extends CustomEnchantment {

        public Jump() {
            maxLevel = 4;
            loreName = "Jump";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{};
            description = "Gives the player a jump boost";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            Utilities.addPotion(player, JUMP, 610, (int) Math.round(level * power));
            return true;
        }
    }

    public static class Laser extends CustomEnchantment {

        public Laser() {
            maxLevel = 3;
            loreName = "Laser";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.picks, Storage.axes);
            conflicting = new Class[]{Haul.class};
            description = "Breaks blocks and damages mobs using a powerful beam of light";
            cooldown = 0;
            power = 1.0;
        }

        public void shoot(Location blk, Player player, int level) {
            Location playLoc = player.getLocation();
            Location target = Utilities.getCenter(blk);
            target.setY(target.getY() + .5);
            Location c = playLoc;
            c.setY(c.getY() + 1.1);
            double d = target.distance(c);
            for (int i = 0; i < (int) d * 5; i++) {
                Location tempLoc = target.clone();
                tempLoc.setX(c.getX() + (i * ((target.getX() - c.getX()) / (d * 5))));
                tempLoc.setY(c.getY() + (i * ((target.getY() - c.getY()) / (d * 5))));
                tempLoc.setZ(c.getZ() + (i * ((target.getZ() - c.getZ()) / (d * 5))));
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), tempLoc, 32);
                for (Entity ent : Bukkit.getWorld(playLoc.getWorld().getName()).getEntities()) {
                    if (ent.getLocation().distance(tempLoc) < 1.5) {
                        if (Utilities.canDamage(player, ent)) {
                            LivingEntity e = (LivingEntity) ent;
                            if (e.getEntityId() != player.getEntityId()) {
                                EntityDamageByEntityEvent evt = new EntityDamageByEntityEvent(player, e, DamageCause.ENTITY_ATTACK, (double) (1 + (level * 2)));
                                Bukkit.getPluginManager().callEvent(evt);
                                LivingEntity theE = (LivingEntity) evt.getEntity();
                                theE.setLastDamageCause(evt);
                                if (!evt.isCancelled()) {
                                    if (Storage.rnd.nextInt(20) == 6) {
                                        Utilities.addUnbreaking(player.getItemInHand(), 1, player);
                                    }
                                    theE.damage(1 + (level * power * 2));
                                }
                            }
                        }
                    }
                }
            }
        }

        public boolean onEntityInteract(PlayerInteractEntityEvent evt, int level) {
            if (!evt.getPlayer().isSneaking()) {
                Storage.laserTimes.put(evt.getPlayer(), System.nanoTime());
                final Block blk = evt.getPlayer().getTargetBlock((HashSet<Byte>) null, 6
                        + (int) Math.round(level * power * 3));
                shoot(blk.getLocation(), evt.getPlayer(), level);
            }
            return true;
        }

        public boolean onBlockInteract(final PlayerInteractEvent evt, int level) {
            if (Storage.laserTimes.containsKey(evt.getPlayer()) && System.nanoTime() - Storage.laserTimes.get(evt.getPlayer()) < 500000000) {
                Storage.laserTimes.remove(evt.getPlayer());
                return false;
            }
            boolean b = false;
            for (CustomEnchantment e : Config.get(evt.getPlayer().getWorld()).getEnchants(evt.getPlayer().getItemInHand()).keySet()) {
                if (e.getClass().equals(Lumber.class)) {
                    b = e.getClass().equals(Lumber.class);
                }
            }
            if ((!evt.getPlayer().isSneaking() || b) && (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
                final Block blk = evt.getPlayer().getTargetBlock((HashSet<Byte>) null, 6
                        + (int) Math.round(level * power * 3)).getRelative(0, 0, 0);
                shoot(blk.getLocation(), evt.getPlayer(), level);
                int[] nobreak = new int[]{0, 7, 23, 52, 54, 61, 62, 63, 64, 68, 69, 71, 77, 90, 96, 107, 116, 117, 119, 120, 130, 137, 138, 143, 145, 146, 158, 166, 167, 183, 184, 185, 186, 187, 193, 194, 195, 196, 197};
                if (ArrayUtils.contains(nobreak, blk.getTypeId())) {
                    return false;
                }
                BlockBreakEvent event = new BlockBreakEvent(blk, evt.getPlayer());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                    public void run() {
                        if (evt.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                            for (ItemStack st : Utilities.silktouchDrops(blk)) {
                                evt.getPlayer().getWorld().dropItem(Utilities.getCenter(blk.getLocation()), st);
                            }
                            blk.setType(AIR);
                        } else if (evt.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                            for (ItemStack st : Utilities.fortuneDrops(evt.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), blk)) {
                                evt.getPlayer().getWorld().dropItem(Utilities.getCenter(blk.getLocation()), st);
                            }
                            blk.setType(AIR);
                        } else {
                            blk.breakNaturally();
                        }
                    }
                }, 1);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            }
            return true;
        }
    }

    public static class Level extends CustomEnchantment {

        public Level() {
            maxLevel = 3;
            loreName = "Level";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.bows, Storage.swords));
            conflicting = new Class[]{};
            description = "Drops more XP when killing mobs or mining ores";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityKill(EntityDeathEvent evt, int level) {
            if (Storage.rnd.nextBoolean()) {
                evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (level * power * .5))));
                return true;
            }
            return false;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            if (Storage.rnd.nextBoolean()) {
                evt.setExpToDrop((int) (evt.getExpToDrop() * (1.3 + (level * power * .5))));
                return true;
            }
            return false;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            if (Storage.rnd.nextBoolean()) {
                EnchantArrow.ArrowEnchantLevel arrow = new EnchantArrow.ArrowEnchantLevel((Projectile) evt.getProjectile(), level, power);
                Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
                return true;
            }
            return false;
        }

    }

    public static class LongCast extends CustomEnchantment {

        public LongCast() {
            maxLevel = 2;
            loreName = "Long Cast";
            chance = 0;
            enchantable = Storage.rods;
            conflicting = new Class[]{ShortCast.class};
            description = "Launches fishing hooks farther out when casting";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level) {
            if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
                evt.getEntity().setVelocity(evt.getEntity().getVelocity().normalize().multiply(Math.min(1.9 + (power * level - 1.2), 2.7)));
            }
            return true;
        }
    }

    public static class Lumber extends CustomEnchantment {

        private void bk(Block blk, List<Block> bks, List<Block> total, List<Block> tester, int i) {
            i++;
            if (i >= 150 || total.size() >= 150) {
                return;
            }
            bks.add(blk);
            Location loc = blk.getLocation().clone();
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Location loc2 = loc.clone();
                        loc2.setX(loc2.getX() + x);
                        loc2.setY(loc2.getY() + y);
                        loc2.setZ(loc2.getZ() + z);
                        Material mat = loc2.getBlock().getType();
                        if (mat != LOG && mat != LOG_2 && mat != LEAVES && mat != LEAVES_2 && mat != DIRT && mat != GRASS && mat != VINE && mat != SNOW
                                && mat != COCOA && mat != AIR && mat != RED_ROSE && mat != YELLOW_FLOWER && mat != LONG_GRASS && mat != GRAVEL
                                && mat != STONE && mat != DOUBLE_PLANT && mat != WATER && mat != STATIONARY_WATER && mat != SAND && mat != SAPLING
                                && mat != BROWN_MUSHROOM && mat != RED_MUSHROOM && mat != MOSSY_COBBLESTONE && mat != CLAY && mat != HUGE_MUSHROOM_1 && mat != HUGE_MUSHROOM_2
                                && mat != SUGAR_CANE_BLOCK) {
                            tester.add(loc2.getBlock());
                        }
                        if (!bks.contains(loc2.getBlock()) && (loc2.getBlock().getType() == LOG || loc2.getBlock().getType() == LOG_2)) {
                            bk(loc2.getBlock(), bks, total, tester, i);
                            total.add(loc2.getBlock());
                        }
                    }
                }
            }
        }

        public Lumber() {
            maxLevel = 1;
            loreName = "Lumber";
            chance = 0;
            enchantable = Storage.axes;
            conflicting = new Class[]{};
            description = "Breaks the entire tree at once";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            if (!evt.getPlayer().isSneaking()) {
                return false;
            }
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                evt.setCancelled(true);
                List<Block> used = new ArrayList<>();
                List<Block> total = new ArrayList<>();
                List<Block> tester = new ArrayList<>();
                if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                    bk(evt.getBlock(), used, total, tester, 0);
                }
                if ((!(total.size() >= 150)) && tester.isEmpty()) {
                    int i = 1;
                    if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                        total.add(evt.getBlock());
                    }
                    for (Block b : total) {
                        final int i2 = i + 1;
                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                        final BlockBreakEvent event = new BlockBreakEvent(b, evt.getPlayer());
                        Bukkit.getPluginManager().callEvent(event);
                        if (Utilities.canEdit(evt.getPlayer(), b)) {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                public void run() {
                                    final Block block = event.getBlock();
                                    if (event.getBlock().getType() != AIR) {
                                        event.getBlock().breakNaturally();
                                    }
                                }
                            }, 1);
                            i++;
                        }
                    }
                }
                Utilities.eventEnd(evt.getPlayer(), loreName);
            }
            return true;
        }
    }

    public static class Magnetism extends CustomEnchantment {

        public Magnetism() {
            maxLevel = 3;
            loreName = "Magnetism";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new Class[]{};
            description = "Slowly attracts nearby items to the players inventory";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onFastScan(Player player, int level) {
            int radius = (int) Math.round(power * level * 2 + 3);
            for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
                if (e.getType().equals(DROPPED_ITEM) && e.getTicksLived() > 160) {
                    e.setVelocity(player.getLocation().toVector().subtract(e.getLocation().toVector()).multiply(.05));
                }
            }
            return true;
        }
    }

    public static class Meador extends CustomEnchantment {

        public Meador() {
            maxLevel = 1;
            loreName = "Meador";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{Weight.class, Speed.class, Jump.class};
            description = "Gives the player both a speed and jump boost";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            player.setWalkSpeed((float) Math.min(.5f + level * power * .05f, 1));
            player.setFlySpeed((float) Math.min(.5f + level * power * .05f, 1));
            Storage.speed.add(player);
            Utilities.addPotion(player, JUMP, 610, (int) Math.round(power * level + 2));
            return true;
        }
    }

    public static class Mow extends CustomEnchantment {

        public Mow() {
            maxLevel = 3;
            loreName = "Mow";
            chance = 0;
            enchantable = Storage.shears;
            conflicting = new Class[]{};
            description = "Shears all nearby sheep";
            cooldown = 0;
            power = 1.0;
        }

        private boolean shear(PlayerEvent evt, int level) {
            boolean hasSheep = false;
            int radius = (int) Math.round(level * power + 2);
            for (Entity ent : evt.getPlayer().getNearbyEntities(radius, radius, radius)) {
                Entity e = ent;
                if (ent.getType() == SHEEP) {
                    hasSheep = true;
                    Sheep sheep = (Sheep) ent;
                    PlayerShearEntityEvent event = new PlayerShearEntityEvent(evt.getPlayer(), ent);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (sheep.isAdult() && !sheep.isSheared() && !event.isCancelled()) {
                        short s = sheep.getColor().getData();
                        int number = Storage.rnd.nextInt(3) + 1;
                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                        sheep.setSheared(true);
                        evt.getPlayer().getWorld().dropItemNaturally(Utilities.getCenter(e.getLocation()), new ItemStack(WOOL, number, s));
                    }
                }
            }
            return hasSheep;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                return shear(evt, level);
            }
            return false;
        }

        public boolean onShear(PlayerShearEntityEvent evt, int level) {
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                boolean result = shear(evt, level);
                Utilities.eventEnd(evt.getPlayer(), loreName);
                return result;
            }
            return false;
        }
    }

    public static class MysteryFish extends CustomEnchantment {

        public MysteryFish() {
            maxLevel = 3;
            loreName = "Mystery Fish";
            chance = 0;
            enchantable = Storage.rods;
            conflicting = new Class[]{};
            description = "Catches water mobs like Squid and Guardians";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onPlayerFish(final PlayerFishEvent evt, int level) {
            if (Storage.rnd.nextInt(10) < level * power) {
                if (evt.getCaught() != null) {
                    Location location = evt.getCaught().getLocation();
                    final Entity ent;
                    if (Storage.rnd.nextBoolean()) {
                        ent = evt.getPlayer().getWorld().spawnEntity(location, SQUID);
                    } else {
                        ent = evt.getPlayer().getWorld().spawnEntity(location, GUARDIAN);
                        Guardian g = (Guardian) evt.getPlayer().getWorld().spawnEntity(location, GUARDIAN);
                        g.setElder(Storage.rnd.nextBoolean());
                        Storage.guardianMove.put(g, evt.getPlayer());
                    }
                    evt.getCaught().setPassenger(ent);
                }
            }
            return true;
        }
    }

    public static class NetherStep extends CustomEnchantment {

        public NetherStep() {
            maxLevel = 3;
            loreName = "Nether Step";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{FrozenStep.class};
            description = "Allows the player to slowly but safely walk on lava";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            if (player.isSneaking() && player.getLocation().getBlock().getType() == STATIONARY_LAVA && !player.isFlying()) {
                player.setVelocity(player.getVelocity().setY(.4));
            }
            Block block = (Block) player.getLocation().getBlock();
            int radius = (int) Math.round(power * level + 2);
            for (int x = -(radius); x <= radius; x++) {
                for (int z = -(radius); z <= radius; z++) {
                    if (block.getRelative(x, -1, z).getLocation().distanceSquared(block.getLocation()) < radius * radius - 2) {
                        if (Storage.fireLocs.containsKey(block.getRelative(x, -1, z).getLocation())) {
                            Storage.fireLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                        }
                        if (block.getRelative(x, -1, z).getData() == 0 && block.getRelative(x, -1, z).getType() == STATIONARY_LAVA && block.getRelative(x, 0, z).getType() == AIR) {
                            if (Utilities.canEdit(player, block)) {
                                block.getRelative(x, -1, z).setType(OBSIDIAN);
                                Storage.fireLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class NightVision extends CustomEnchantment {

        public NightVision() {
            maxLevel = 1;
            loreName = "Night Vision";
            chance = 0;
            enchantable = Storage.helmets;
            conflicting = new Class[]{};
            description = "Lets the player see in the darkness";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            Utilities.addPotion(player, NIGHT_VISION, 610, 5);
            return true;
        }
    }

    public static class Persephone extends CustomEnchantment {

        public Persephone() {
            maxLevel = 3;
            loreName = "Persephone";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new Class[]{};
            description = "Plants seeds from the player's inventory around them";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Material mats[] = new Material[]{CARROT_ITEM, POTATO_ITEM, SEEDS, NETHER_STALK, BEETROOT_SEEDS};
                Material mat = AIR;
                for (int i = 0; i < 36; i++) {
                    if (evt.getPlayer().getInventory().getItem(i) != null) {
                        if (ArrayUtils.contains(mats, evt.getPlayer().getInventory().getItem(i).getType())) {
                            mat = evt.getPlayer().getInventory().getItem(i).getType();
                            break;
                        }
                    }
                }
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = (int) Math.round(power * level + 2);
                int radiusY = 1;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                if (!Utilities.canEdit(evt.getPlayer(), block.getRelative(x, y, z))) {
                                    continue;
                                }
                                if ((block.getRelative(x, y, z).getType() == SOUL_SAND || block.getRelative(x, y, z).getType() == SOIL) && block.getRelative(x, y + 1, z).getType() == AIR) {
                                    if (evt.getPlayer().getInventory().contains(mat)) {
                                        Utilities.removeItem(evt.getPlayer(), mat, 1);
                                        evt.getPlayer().updateInventory();
                                    } else {
                                        continue;
                                    }
                                    Material m = AIR;
                                    switch (mat) {
                                        case CARROT_ITEM:
                                            m = CARROT;
                                            break;
                                        case POTATO_ITEM:
                                            m = POTATO;
                                            break;
                                        case SEEDS:
                                            m = CROPS;
                                            break;
                                        case NETHER_STALK:
                                            m = NETHER_WARTS;
                                            break;
                                        case BEETROOT_SEEDS:
                                            m = BEETROOT_BLOCK;
                                            break;
                                    }
                                    Boolean b = true;
                                    if (block.getRelative(x, y, z).getType() == SOUL_SAND && m == NETHER_WARTS) {
                                        block.getRelative(x, y + 1, z).setType(m);
                                        b = false;
                                    } else if (ArrayUtils.contains(mats, mat) && m != NETHER_WARTS && block.getRelative(x, y, z).getType() == SOIL) {
                                        block.getRelative(x, y + 1, z).setType(m);
                                        b = false;
                                    }
                                    evt.getPlayer().updateInventory();
                                    if (Storage.rnd.nextBoolean() && !b) {
                                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Pierce extends CustomEnchantment {

        private void bk(Block blk, List<Block> bks, List<Block> total, Material[] mat, int i) {
            i++;
            if (i >= 128 || total.size() >= 128) {
                return;
            }
            bks.add(blk);
            Location loc = blk.getLocation().clone();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Location loc2 = loc.clone();
                        loc2.setX(loc2.getX() + x);
                        loc2.setY(loc2.getY() + y);
                        loc2.setZ(loc2.getZ() + z);
                        if (!bks.contains(loc2.getBlock()) && (ArrayUtils.contains(mat, loc2.getBlock().getType()))) {
                            bk(loc2.getBlock(), bks, total, mat, i);
                            total.add(loc2.getBlock());
                        }
                    }
                }
            }
        }

        public Pierce() {
            maxLevel = 1;
            loreName = "Pierce";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new Class[]{Anthropomorphism.class, Switch.class, Shred.class};
            description = "Lets the player mine in several modes which can be changed through shift clicking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (!Storage.pierceModes.containsKey(evt.getPlayer())) {
                Storage.pierceModes.put(evt.getPlayer(), 1);
            }
            if (evt.getPlayer().isSneaking()) {
                if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                    if (Utilities.canUse(evt.getPlayer(), loreName)) {
                        EnchantPlayer.matchPlayer(evt.getPlayer()).setCooldown(this.loreName, 1);
                        if (Storage.pierceModes.get(evt.getPlayer()) < 5) {
                            Storage.pierceModes.put(evt.getPlayer(), Storage.pierceModes.get(evt.getPlayer()) + 1);
                        } else {
                            Storage.pierceModes.put(evt.getPlayer(), 1);
                        }
                        switch (Storage.pierceModes.get(evt.getPlayer())) {
                            case 1:
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "1x Normal Mode");
                                break;
                            case 2:
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Wide Mode");
                                break;
                            case 3:
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Long Mode");
                                break;
                            case 4:
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Tall Mode");
                                break;
                            case 5:
                                evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ore Mode");
                                break;
                        }
                    }
                }
            }
            return false;
        }

        public boolean onBlockBreak(final BlockBreakEvent evt, int level) {
            //1 = normal; 2 = wide; 3 = deep; 4 = tall; 5 = ore
            if (!Storage.pierceModes.containsKey(evt.getPlayer())) {
                Storage.pierceModes.put(evt.getPlayer(), 1);
            }
            final int mode = Storage.pierceModes.get(evt.getPlayer());
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                int counter = 0;
                Player player = (Player) evt.getPlayer();
                final Location blkLoc = evt.getBlock().getLocation();
                if (mode != 1 && mode != 5) {
                    int add = -1;
                    boolean b = false;
                    int[][] ints = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
                    switch (Utilities.getSimpleDirection(player.getLocation().getYaw(), 0)) {
                        case 1:
                            ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                            add = 1;
                            b = true;
                            break;
                        case 2:
                            ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                            break;
                        case 3:
                            ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                            b = true;
                            break;
                        case 4:
                            ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                            add = 1;
                            break;
                    }
                    int[] rads = ints[mode - 2];
                    if (mode == 3) {
                        if (b) {
                            blkLoc.setZ(blkLoc.getZ() + add);
                        } else {
                            blkLoc.setX(blkLoc.getX() + add);
                        }
                    }
                    if (mode == 4) {
                        if (evt.getPlayer().getLocation().getPitch() > 65) {
                            blkLoc.setY(blkLoc.getY() - 1);
                        } else if (evt.getPlayer().getLocation().getPitch() < -65) {
                            blkLoc.setY(blkLoc.getY() + 1);
                        }
                    }
                    for (int x = -(rads[0]); x <= rads[0]; x++) {
                        for (int y = -(rads[1]); y <= rads[1]; y++) {
                            for (int z = -(rads[2]); z <= rads[2]; z++) {
                                evt.setCancelled(true);
                                final BlockBreakEvent event = new BlockBreakEvent(blkLoc.getBlock().getRelative(x, y, z), player);
                                Bukkit.getServer().getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    if (event.getBlock().getType() != BEDROCK && event.getBlock().getType() != ENDER_PORTAL_FRAME
                                            && event.getBlock().getType() != ENDER_PORTAL && event.getBlock().getType() != BARRIER && event.getBlock().getType() != PORTAL
                                            && event.getBlock().getType() != STRUCTURE_BLOCK && event.getBlock().getType() != END_GATEWAY) {
                                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                            public void run() {
                                                if (event.getBlock().getType() != AIR) {
                                                    if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                                                        for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                                            event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                        }
                                                        event.getBlock().setType(AIR);
                                                    } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                                                        for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                                            event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                        }
                                                        event.getBlock().setType(AIR);
                                                    } else {
                                                        event.getBlock().breakNaturally();
                                                    }
                                                }
                                            }
                                        }, 1);
                                        counter++;
                                    }
                                }
                            }
                        }
                    }
                } else if (mode == 5) {
                    Material mats[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
                        IRON_ORE, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};
                    List<Block> used = new ArrayList<>();
                    List<Block> total = new ArrayList<>();
                    if (ArrayUtils.contains(mats, evt.getBlock().getType())) {
                        Material mat[];
                        if (evt.getBlock().getType() != REDSTONE_ORE && evt.getBlock().getType() != GLOWING_REDSTONE_ORE) {
                            mat = new Material[]{evt.getBlock().getType()};
                        } else {
                            mat = new Material[]{REDSTONE_ORE, GLOWING_REDSTONE_ORE};
                        }
                        bk(evt.getBlock(), used, total, mat, 0);
                    } else {
                        Utilities.eventEnd(evt.getPlayer(), loreName);
                        return false;
                    }
                    if (!(total.size() >= 128)) {
                        for (Block b : total) {
                            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                            final BlockBreakEvent event = new BlockBreakEvent(b, evt.getPlayer());
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                if (evt.getBlock().getType() != AIR) {
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                        public void run() {
                                            if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                                                for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                }
                                                event.getBlock().setType(AIR);
                                            } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                                                for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                }
                                                event.getBlock().setType(AIR);
                                            } else {
                                                event.getBlock().breakNaturally();
                                            }
                                        }
                                    }, 1);
                                }
                            }
                        }
                    }
                }
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), (int) (counter / (float) 1.5), evt.getPlayer());
                Utilities.eventEnd(evt.getPlayer(), loreName);
            }
            return true;
        }
    }

    public static class Plough extends CustomEnchantment {

        public Plough() {
            maxLevel = 3;
            loreName = "Plough";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new Class[]{};
            description = "Tills all soil within a radius";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = (int) Math.round(power * level + 2);
                int radiusY = 1;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                if (!Utilities.canEdit(evt.getPlayer(), (block.getRelative(x, y, z)))) {
                                    continue;
                                }
                                if ((block.getRelative(x, y, z).getType() == DIRT || block.getRelative(x, y, z).getType() == GRASS || block.getRelative(x, y, z).getType() == MYCEL) && block.getRelative(x, y + 1, z).getType() == AIR) {
                                    block.getRelative(x, y, z).setType(SOIL);
                                    if (Storage.rnd.nextBoolean()) {
                                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Potion extends CustomEnchantment {

        PotionEffectType[] potions;

        public Potion() {
            maxLevel = 3;
            loreName = "Potion";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Gives the shooter random positive potion effects when attacking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantPotion arrow = new EnchantArrow.ArrowEnchantPotion((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
    }

    public static class PotionResistance extends CustomEnchantment {

        public PotionResistance() {
            maxLevel = 4;
            loreName = "Potion Resistance";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.helmets, ArrayUtils.addAll(Storage.chestplates, ArrayUtils.addAll(Storage.leggings, Storage.boots)));
            conflicting = new Class[]{};
            description = "Lessens the effects of all potions on players";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onPotionSplash(PotionSplashEvent evt, int level) {
            for (LivingEntity ent : evt.getAffectedEntities()) {
                if (ent instanceof Player) {
                    int effect = 0;
                    for (ItemStack stk : ((Player) ent).getInventory().getArmorContents()) {
                        Map<CustomEnchantment, Integer> map = Config.get(ent.getWorld()).getEnchants(stk);
                        for (CustomEnchantment e : map.keySet()) {
                            if (e.equals(this)) {
                                effect += map.get(e);
                            }
                        }
                    }
                    evt.setIntensity(ent, evt.getIntensity(ent) / ((effect * power + 1.3) / 2));
                }
            }
            return true;
        }
    }

    public static class QuickShot extends CustomEnchantment {

        public QuickShot() {
            maxLevel = 1;
            loreName = "Quick Shot";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Shoots arrows at full speed, instantly";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantQuickShot arrow = new EnchantArrow.ArrowEnchantQuickShot((Projectile) evt.getProjectile());
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
    }

    public static class Rainbow extends CustomEnchantment {

        public Rainbow() {
            maxLevel = 1;
            loreName = "Rainbow";
            chance = 0;
            enchantable = Storage.shears;
            conflicting = new Class[]{};
            description = "Drops random flowers and wool colors when used";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            short itemInfo;
            Material dropMaterial;
            if (evt.getBlock().getType() == RED_ROSE || evt.getBlock().getType() == YELLOW_FLOWER) {
                short sh = (short) Storage.rnd.nextInt(9);
                dropMaterial = (sh == 7) ? YELLOW_FLOWER : RED_ROSE;
                itemInfo = (sh == 7) ? 0 : (short) Storage.rnd.nextInt(9);
            } else if (evt.getBlock().getType() == DOUBLE_PLANT && (evt.getBlock().getData() == 0 || evt.getBlock().getData() == 1 || evt.getBlock().getData() == 4 || evt.getBlock().getData() == 5)) {
                short[] shorts = new short[]{0, 1, 4, 5};
                dropMaterial = DOUBLE_PLANT;
                itemInfo = (short) Storage.rnd.nextInt(4);
                itemInfo = shorts[itemInfo];
            } else {
                Utilities.eventEnd(evt.getPlayer(), loreName);
                return false;
            }
            evt.setCancelled(true);
            evt.getBlock().setType(AIR);
            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            evt.getPlayer().getWorld().dropItem(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(dropMaterial, 1, itemInfo));
            return true;
        }

        public boolean onShear(PlayerShearEntityEvent evt, int level) {
            Sheep sheep = (Sheep) evt.getEntity();
            if (!sheep.isSheared()) {
                int color = Storage.rnd.nextInt(16);
                int number = Storage.rnd.nextInt(3) + 1;
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                sheep.setSheared(true);
                evt.getEntity().getWorld().dropItemNaturally(Utilities.getCenter(evt.getEntity().getLocation()), new ItemStack(WOOL, number, (short) color));
            }
            return true;
        }
    }

    public static class RainbowSlam extends CustomEnchantment {

        public RainbowSlam() {
            maxLevel = 4;
            loreName = "Rainbow Slam";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new Class[]{Force.class, Gust.class};
            description = "Attacks enemy mobs with a powerful swirling slam";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityInteract(final PlayerInteractEntityEvent evt, final int level) {
            if (!Utilities.canDamage(evt.getPlayer(), evt.getRightClicked())) {
                return false;
            }
            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 9, evt.getPlayer());
            final LivingEntity ent = (LivingEntity) evt.getRightClicked();
            final Location l = ent.getLocation().clone();
            ent.teleport(l);
            for (int i = 0; i < 1200; i++) {
                final float j = i;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                    public void run() {
                        if (ent.isDead()) {
                            return;
                        }
                        float x, y, z;
                        Location loc = l.clone();
                        float j1 = j;
                        loc.setY(loc.getY() + (j1 / 100));
                        loc.setX(loc.getX() + Math.sin(Math.toRadians(j1)) * j1 / 330);
                        loc.setZ(loc.getZ() + Math.cos(Math.toRadians(j1)) * j1 / 330);
                        ParticleEffect.REDSTONE.display(0, 0, 0, 10f, 1, loc, 32);
                        loc.setY(loc.getY() + 1.3);
                        ent.setVelocity(loc.toVector().subtract(ent.getLocation().toVector()));
                        ent.setFallDistance((float) (-10 + ((level * power * 2) + 8)));
                    }
                }, (int) (i / 40));
            }
            final List<Integer> tester = new ArrayList<>();
            tester.add(1);
            for (int i = 0; i < 3; i++) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                    public void run() {
                        ent.setVelocity(l.toVector().subtract(ent.getLocation().toVector()).multiply(.3));
                        if (ent.isOnGround() && tester.size() == 1) {
                            tester.clear();
                            Location ground = ent.getLocation().clone();
                            ground.setY(l.getY() - 1);
                            for (int c = 0; c < 1000; c++) {
                                Vector v = new Vector(Math.sin(Math.toRadians(c)), Storage.rnd.nextFloat(), Math.cos(Math.toRadians(c))).multiply(.75);
                                ParticleEffect.BLOCK_DUST.display(new BlockData(ground.getBlock().getType(), ground.getBlock().getData()), v, 1, Utilities.getCenter(l), 32);
                            }
                        }
                    }
                }, 35 + (i * 5));
            }
            return true;
        }
    }

    public static class Reaper extends CustomEnchantment {

        public Reaper() {
            maxLevel = 4;
            loreName = "Reaper";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.bows, Storage.swords);
            conflicting = new Class[]{};
            description = "Gives the target temporary wither effect and blindness";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantReaper arrow = new EnchantArrow.ArrowEnchantReaper((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

        public boolean onHitting(EntityDamageByEntityEvent evt, int level) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                int pow = (int) Math.round(level * power);
                int dur = (int) Math.round(10 + level * 20 * power);
                Utilities.addPotion((LivingEntity) evt.getEntity(), PotionEffectType.WITHER, dur, pow);
                Utilities.addPotion((LivingEntity) evt.getEntity(), BLINDNESS, dur, pow);
            }
            return true;
        }
    }

    public static class Saturation extends CustomEnchantment {

        public Saturation() {
            maxLevel = 3;
            loreName = "Saturation";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new Class[]{};
            description = "Uses less of the player's hunger";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onHungerChange(FoodLevelChangeEvent evt, int level) {
            if (evt.getFoodLevel() < ((Player) evt.getEntity()).getFoodLevel() && Storage.rnd.nextInt(10) > 10 - 2 * level * power) {
                evt.setCancelled(true);
            }
            return true;
        }
    }

    public static class ShortCast extends CustomEnchantment {

        public ShortCast() {
            maxLevel = 2;
            loreName = "Short Cast";
            chance = 0;
            enchantable = Storage.rods;
            conflicting = new Class[]{LongCast.class};
            description = "Launches fishing hooks closer in when casting";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level) {
            if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
                evt.getEntity().setVelocity(evt.getEntity().getVelocity().normalize().multiply((.8f / (level * power))));
            }
            return true;
        }
    }

    public static class Shred extends CustomEnchantment {

        public Shred() {
            maxLevel = 5;
            loreName = "Shred";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.picks, Storage.spades);
            conflicting = new Class[]{Pierce.class, Switch.class};
            description = "Breaks the blocks within a radius of the original block mined";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            final Config config = Config.get(evt.getBlock().getWorld());
            int original = level;
            final int l = level;
            level++;
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                Material mats[] = new Material[]{STONE, COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE, IRON_ORE,
                    NETHERRACK, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GRASS, SOUL_SAND, GLOWING_REDSTONE_ORE,
                    DIRT, MYCEL, SAND, GRAVEL, SOUL_SAND, CLAY, HARD_CLAY, STAINED_CLAY, SANDSTONE, RED_SANDSTONE};
                final Material shovel[] = new Material[]{GLOWSTONE, GRASS, DIRT, MYCEL, SOUL_SAND, SAND, GRAVEL, SOUL_SAND, CLAY};
                int counter = 0;
                if (!ArrayUtils.contains(mats, evt.getBlock().getType()) && !evt.getBlock().getType().equals(AIR)) {
                    Utilities.eventEnd(evt.getPlayer(), loreName);
                    return false;
                }
                evt.setCancelled(true);
                Player player = (Player) evt.getPlayer();
                final Material itemType = player.getItemInHand().getType();
                int radius = level * 2 + 4;
                final Block block = evt.getBlock();
                int x1, x2, x3, y1, y2, y3, z1, z2, z3;
                x1 = x2 = x3 = y1 = y2 = y3 = z1 = z2 = z3 = 1;
                int x, y, z;
                x = y = z = radius;
                int j = Utilities.getSimpleDirection(player.getLocation().getYaw(), player.getLocation().getPitch());
                switch (j) {
                    case 3:
                        x3 = radius;
                        y3 = radius;
                        z1 = radius;
                        break;
                    case 1:
                        x3 = radius;
                        y3 = radius;
                        z2 = radius;
                        break;
                    case 2:
                        x1 = radius;
                        y3 = radius;
                        z3 = radius;
                        break;
                    case 4:
                        x2 = radius;
                        y3 = radius;
                        z3 = radius;
                        break;
                    case 6:
                        x3 = radius;
                        y1 = radius;
                        z3 = radius;
                        break;
                    case 5:
                        x3 = radius;
                        y2 = radius;
                        z3 = radius;
                        break;
                }
                for (int x01 = 0; x01 < x1; x01++) {
                    for (int x02 = x2; x02 > 0; x02--) {
                        for (int y01 = 0; y01 < y1; y01++) {
                            for (int y02 = y2; y02 > 0; y02--) {
                                for (int z01 = 0; z01 < z1; z01++) {
                                    for (int z02 = z2; z02 > 0; z02--) {
                                        for (int z03 = z3; z03 > 0; z03--) {
                                            for (int x03 = x3; x03 > 0; x03--) {
                                                for (int y03 = y3; y03 > 0; y03--) {
                                                    switch (j) {
                                                        case 3:
                                                            x = x03;
                                                            y = y03;
                                                            z = z01;
                                                            break;
                                                        case 1:
                                                            x = x03;
                                                            y = y03;
                                                            z = z02;
                                                            break;
                                                        case 2:
                                                            x = x01;
                                                            y = y03;
                                                            z = z03;
                                                            break;
                                                        case 4:
                                                            x = x02;
                                                            y = y03;
                                                            z = z03;
                                                            break;
                                                        case 6:
                                                            x = x03;
                                                            y = y01;
                                                            z = z03;
                                                            break;
                                                        case 5:
                                                            x = x03;
                                                            y = y02;
                                                            z = z03;
                                                            break;
                                                    }
                                                    x = level - x + 1;
                                                    y = level - y + 1;
                                                    z = level - z + 1;
                                                    int r = Storage.rnd.nextInt(2);
                                                    double limit;
                                                    if (original > 2) {
                                                        limit = level + 3 + r + 1.7;
                                                    } else if (level == 3) {
                                                        limit = level + 3 + r + 1;
                                                    } else {
                                                        limit = (float) (level + 3 + r);
                                                    }
                                                    int timer = (int) (counter / (l * 1.6));
                                                    if (config.getEnchants(player.getItemInHand()).size() > 1 && config.getShredDrops() != 2) {
                                                        timer = 1;
                                                    }
                                                    if (block.getRelative(x, y, z).getLocation().distanceSquared(block.getLocation()) < limit) {
                                                        if (ArrayUtils.contains(mats, block.getRelative(x, y, z).getType())) {
                                                            final BlockBreakEvent event = new BlockBreakEvent(block.getRelative(x, y, z), player);
                                                            Bukkit.getServer().getPluginManager().callEvent(event);
                                                            if (!event.isCancelled()) {
                                                                counter++;
                                                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                                                    public void run() {
                                                                        if (config.getShredDrops() == 1) {
                                                                            Material[] ores = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE, IRON_ORE, LAPIS_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};
                                                                            if (ArrayUtils.contains(ores, event.getBlock().getType())) {
                                                                                event.getBlock().setType(STONE);
                                                                            } else if (event.getBlock().getType().equals(QUARTZ_ORE)) {
                                                                                event.getBlock().setType(NETHERRACK);
                                                                            }
                                                                        } else if (config.getShredDrops() == 2) {
                                                                            event.getBlock().setType(AIR);
                                                                        }
                                                                        if (event.getBlock().getType() == GRASS) {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_GRASS_BREAK, 10, 1);
                                                                        } else if (event.getBlock().getType() == DIRT || event.getBlock().getType() == GRAVEL || event.getBlock().getType() == CLAY) {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_GRAVEL_BREAK, 10, 1);
                                                                        } else if (event.getBlock().getType() == SAND) {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_SAND_BREAK, 10, 1);
                                                                        } else {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_STONE_BREAK, 10, 1);
                                                                        }
                                                                        if (ArrayUtils.contains(Storage.picks, itemType) || ArrayUtils.contains(shovel, event.getBlock().getType())) {
                                                                            if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                                                                                for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                                                }
                                                                                event.getBlock().setType(AIR);
                                                                            } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                                                                                for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                                                }
                                                                                event.getBlock().setType(AIR);
                                                                            } else {
                                                                                event.getBlock().breakNaturally();
                                                                            }
                                                                        }
                                                                    }
                                                                }, timer);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), (int) (counter / 4.0), evt.getPlayer());
                Utilities.eventEnd(evt.getPlayer(), loreName);
            }
            return true;
        }
    }

    public static class Siphon extends CustomEnchantment {

        public Siphon() {
            maxLevel = 4;
            loreName = "Siphon";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.swords, Storage.bows);
            conflicting = new Class[]{};
            description = "Drains the health of the mob that you attack, giving it to you";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onHitting(EntityDamageByEntityEvent evt, int level) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                Player p = (Player) evt.getDamager();
                LivingEntity ent = (LivingEntity) evt.getEntity();
                int difference = (int) Math.round(level * power);
                if (Storage.rnd.nextInt(4) == 2) {
                    while (difference > 0) {
                        if (p.getHealth() <= 19) {
                            p.setHealth(p.getHealth() + 1);
                        }
                        if (ent.getHealth() > 2) {
                            ent.setHealth(ent.getHealth() - 1);
                        }
                        difference--;
                    }
                }
            }
            return true;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantSiphon arrow = new EnchantArrow.ArrowEnchantSiphon((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
    }

    public static class Spectral extends CustomEnchantment {

        private static int increase(int old, int add) {
            if (old < add) {
                return ++old;
            } else {
                return 0;
            }
        }

        public Spectral() {
            maxLevel = 1;
            loreName = "Spectral";
            chance = 0;
            enchantable = enchantable = Storage.spades;
            conflicting = new Class[]{};
            description = "Allows for cycling through a block's types";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            Material original = evt.getClickedBlock().getType();
            int originalInt = evt.getClickedBlock().getData();
            if (!Utilities.canEdit(evt.getPlayer(), evt.getClickedBlock()) || !evt.getPlayer().isSneaking()) {
                return false;
            }
            int data = evt.getClickedBlock().getData();
            switch (evt.getClickedBlock().getType()) {
                case WOOL:
                case STAINED_GLASS:
                case STAINED_GLASS_PANE:
                case CARPET:
                case STAINED_CLAY:
                    data = increase(data, 15);
                    break;
                case WOOD:
                case WOOD_STEP:
                case WOOD_DOUBLE_STEP:
                case SAPLING:
                    data = increase(data, 6);
                    break;
                case RED_SANDSTONE:
                    if (data < 2) {
                        data++;
                    } else {
                        data = 0;
                        evt.getClickedBlock().setType(SANDSTONE);
                    }
                    break;
                case SANDSTONE:
                    if (data < 2) {
                        data++;
                    } else {
                        data = 0;
                        evt.getClickedBlock().setType(RED_SANDSTONE);
                    }
                    break;
                case RED_SANDSTONE_STAIRS:
                    evt.getClickedBlock().setType(SANDSTONE_STAIRS);
                    break;
                case SANDSTONE_STAIRS:
                    evt.getClickedBlock().setType(RED_SANDSTONE_STAIRS);
                    break;
                case SAND:
                    data = increase(data, 2);
                    break;
                case LONG_GRASS:
                    data = increase(data, 3);
                    break;
                case QUARTZ_BLOCK:
                    data = increase(data, 4);
                    break;
                case COBBLE_WALL:
                    data = increase(data, 2);
                    break;
                case STONE:
                    data = increase(data, 7);
                    break;
                case SMOOTH_BRICK:
                    data = increase(data, 4);
                    break;
                case COBBLESTONE:
                    evt.getClickedBlock().setType(MOSSY_COBBLESTONE);
                    break;
                case MOSSY_COBBLESTONE:
                    evt.getClickedBlock().setType(COBBLESTONE);
                    break;
                case BROWN_MUSHROOM:
                    evt.getClickedBlock().setType(RED_MUSHROOM);
                    break;
                case RED_MUSHROOM:
                    evt.getClickedBlock().setType(BROWN_MUSHROOM);
                    break;
                case HUGE_MUSHROOM_1:
                    evt.getClickedBlock().setType(HUGE_MUSHROOM_2);
                    break;
                case HUGE_MUSHROOM_2:
                    evt.getClickedBlock().setType(HUGE_MUSHROOM_1);
                    break;
                case STEP:
                    if (evt.getClickedBlock().getData() == 1) {
                        evt.getClickedBlock().setType(STONE_SLAB2);
                        data = 0;
                    }
                    break;
                case STONE_SLAB2:
                    if (evt.getClickedBlock().getData() == 0) {
                        evt.getClickedBlock().setType(STEP);
                        data = 1;
                    }
                    break;
                case DOUBLE_STEP:
                    if (evt.getClickedBlock().getData() == 1) {
                        evt.getClickedBlock().setType(DOUBLE_STONE_SLAB2);
                        data = 0;
                    }
                    break;
                case DOUBLE_STONE_SLAB2:
                    if (evt.getClickedBlock().getData() == 0) {
                        evt.getClickedBlock().setType(DOUBLE_STEP);
                        data = 1;
                    }
                    break;
                case DOUBLE_PLANT:
                    if (evt.getClickedBlock().getRelative(DOWN).getType().equals(DOUBLE_PLANT)) {
                        evt.getClickedBlock().getRelative(DOWN).setData((byte) increase(evt.getClickedBlock().getRelative(DOWN).getData(), 6));
                    } else if (evt.getClickedBlock().getRelative(UP).getType().equals(DOUBLE_PLANT)) {
                        data = increase(data, 6);
                    }
                    break;
                case LEAVES:
                    if ((data + 1) % 4 != 0 || data == 0) {
                        data++;
                    } else {
                        data -= 3;
                        evt.getClickedBlock().setType(LEAVES_2);
                    }
                    break;
                case LEAVES_2:
                    if ((data + 1) % 2 != 0 || data == 0) {
                        data++;
                    } else {
                        evt.getClickedBlock().setType(LEAVES);
                        data -= 1;
                    }
                    break;
                case LOG:
                    if ((data + 1) % 4 != 0 || data == 0) {
                        data++;
                    } else {
                        data -= 3;
                        evt.getClickedBlock().setType(LOG_2);
                    }
                    break;
                case LOG_2:
                    if ((data + 1) % 2 != 0 || data == 0) {
                        data++;
                    } else {
                        evt.getClickedBlock().setType(LOG);
                        data -= 1;
                    }
                    break;
                case YELLOW_FLOWER:
                    evt.getClickedBlock().setType(RED_ROSE);
                    break;
                case RED_ROSE:
                    if (data < 8) {
                        data++;
                    } else {
                        data = 0;
                        evt.getClickedBlock().setType(YELLOW_FLOWER);
                    }
                    break;
                case GRASS:
                    evt.getClickedBlock().setType(DIRT);
                    break;
                case DIRT:
                    if (data < 2) {
                        data++;
                    } else {
                        data = 0;
                        evt.getClickedBlock().setType(GRASS);
                    }
                    break;
                case FENCE:
                case SPRUCE_FENCE:
                case BIRCH_FENCE:
                case JUNGLE_FENCE:
                case DARK_OAK_FENCE:
                case ACACIA_FENCE: {
                    Material[] mats = new Material[]{FENCE, SPRUCE_FENCE, BIRCH_FENCE,
                        JUNGLE_FENCE, DARK_OAK_FENCE, ACACIA_FENCE};
                    int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                    if (index < mats.length - 1) {
                        evt.getClickedBlock().setType(mats[index + 1]);
                    } else {
                        evt.getClickedBlock().setType(mats[0]);
                    }
                    break;
                }
                case FENCE_GATE:
                case SPRUCE_FENCE_GATE:
                case BIRCH_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case ACACIA_FENCE_GATE: {
                    Material[] mats = new Material[]{FENCE_GATE, SPRUCE_FENCE_GATE,
                        BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, DARK_OAK_FENCE_GATE, ACACIA_FENCE_GATE};
                    int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                    if (index < mats.length - 1) {
                        evt.getClickedBlock().setType(mats[index + 1]);
                    } else {
                        evt.getClickedBlock().setType(mats[0]);
                    }
                    break;
                }
                case WOOD_STAIRS:
                case SPRUCE_WOOD_STAIRS:
                case BIRCH_WOOD_STAIRS:
                case JUNGLE_WOOD_STAIRS:
                case DARK_OAK_STAIRS:
                case ACACIA_STAIRS: {
                    Material[] mats = new Material[]{WOOD_STAIRS, SPRUCE_WOOD_STAIRS,
                        BIRCH_WOOD_STAIRS, JUNGLE_WOOD_STAIRS, DARK_OAK_STAIRS, ACACIA_STAIRS};
                    int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                    if (index < mats.length - 1) {
                        evt.getClickedBlock().setType(mats[index + 1]);
                    } else {
                        evt.getClickedBlock().setType(mats[0]);
                    }
                    break;
                }
                case WOODEN_DOOR:
                case SPRUCE_DOOR:
                case BIRCH_DOOR:
                case JUNGLE_DOOR:
                case DARK_OAK_DOOR:
                case ACACIA_DOOR: {
                    Material type;
                    Material[] mats = new Material[]{WOODEN_DOOR, SPRUCE_DOOR,
                        BIRCH_DOOR, JUNGLE_DOOR, DARK_OAK_DOOR, ACACIA_DOOR};
                    int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                    if (index < mats.length - 1) {
                        type = mats[index + 1];
                    } else {
                        type = mats[0];
                    }
                    if (evt.getClickedBlock().getRelative(UP).getType().equals(evt.getClickedBlock().getType())) {
                        evt.getClickedBlock().setTypeIdAndData(type.getId(), (byte) data, false);
                        evt.getClickedBlock().getRelative(UP).setTypeIdAndData(type.getId(), (byte) 8, true);
                    } else if (evt.getClickedBlock().getRelative(DOWN).getType().equals(evt.getClickedBlock().getType())) {
                        evt.getClickedBlock().setTypeIdAndData(type.getId(), (byte) 8, false);
                        evt.getClickedBlock().getRelative(DOWN).setTypeIdAndData(type.getId(), evt.getClickedBlock().getRelative(DOWN).getData(), true);
                    }
                    break;
                }
            }
            if (!evt.getClickedBlock().getType().equals(original) || data != originalInt) {
                evt.getClickedBlock().setData((byte) data);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            }
            return true;
        }

    }

    public static class Speed extends CustomEnchantment {

        public Speed() {
            maxLevel = 4;
            loreName = "Speed";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{Meador.class, Weight.class};
            description = "Gives the player a speed boost";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScan(Player player, int level) {
            player.setWalkSpeed((float) Math.min((.05f * level * power) + .2f, 1));
            player.setFlySpeed((float) Math.min((.05f * level * power) + .2f, 1));
            Storage.speed.add(player);
            return true;
        }
    }

    public static class Spikes extends CustomEnchantment {

        public Spikes() {
            maxLevel = 3;
            loreName = "Spikes";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{};
            description = "Damages entities the player jumps onto";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onFastScan(Player player, int level) {
            if (player.getVelocity().getY() < -0.45) {
                for (Entity e : player.getNearbyEntities(0.0, 0.25, 0.0)) {
                    double fall = Math.min(player.getFallDistance(), 20.0);
                    if (Utilities.canDamage(player, e)) {
                        ((LivingEntity) e).damage(power * level * fall * 0.25);
                    }
                }
            }
            return true;
        }
    }

    public static class Spread extends CustomEnchantment {

        public Spread() {
            maxLevel = 5;
            loreName = "Spread";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{Burst.class};
            description = "Fires an array of arrows simultaneously";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level) {
            Arrow originalArrow = (Arrow) evt.getEntity();
            Player player = (Player) originalArrow.getShooter();
            EnchantArrow.ArrowGenericMulitple ar = new EnchantArrow.ArrowGenericMulitple(originalArrow);
            Utilities.putArrow(originalArrow, ar, player);
            Bukkit.getPluginManager().callEvent(new EntityShootBowEvent(player, player.getItemInHand(), originalArrow, (float) originalArrow.getVelocity().length()));
            if (!Utilities.eventStart(player, loreName)) {
                Utilities.addUnbreaking(player.getItemInHand(), (int) Math.round(level / 2.0 + 1), player);
                for (int i = 0; i < (int) Math.round(power * level * 4); i++) {
                    Vector v = originalArrow.getVelocity();
                    v.setX(v.getX() + Math.max(Math.min(Storage.rnd.nextGaussian() / 8, 0.75), -0.75));
                    v.setZ(v.getZ() + Math.max(Math.min(Storage.rnd.nextGaussian() / 8, 0.75), -0.75));

                    Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.0)), v, 1, 0);
                    arrow.setShooter(player);
                    arrow.setVelocity(v.normalize().multiply(originalArrow.getVelocity().length()));

                    arrow.setFireTicks(originalArrow.getFireTicks());
                    arrow.setKnockbackStrength(originalArrow.getKnockbackStrength());
                    EntityShootBowEvent event = new EntityShootBowEvent(player, player.getItemInHand(), arrow, (float) originalArrow.getVelocity().length());
                    Bukkit.getPluginManager().callEvent(event);
                    arrow.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));
                    arrow.setCritical(originalArrow.isCritical());
                    Utilities.putArrow(originalArrow, new EnchantArrow.ArrowGenericMulitple(originalArrow), player);
                }
                Utilities.eventEnd(player, loreName);
            }
            return true;
        }
    }

    public static class Stationary extends CustomEnchantment {

        public Stationary() {
            maxLevel = 1;
            loreName = "Stationary";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.swords, Storage.bows);
            conflicting = new Class[]{};
            description = "Negates any knockback when attacking mobs, leaving them clueless as to who is attacking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onHitting(EntityDamageByEntityEvent evt, int level) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                LivingEntity ent = (LivingEntity) evt.getEntity();
                if (evt.getDamage() < ent.getHealth()) {
                    evt.setCancelled(true);
                    Utilities.addUnbreaking(((Player) evt.getDamager()).getItemInHand(), 1, ((Player) evt.getDamager()));
                    ent.damage(evt.getDamage());
                }
            }
            return true;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantStationary arrow = new EnchantArrow.ArrowEnchantStationary((Projectile) evt.getProjectile());
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

    }

    public static class Stock extends CustomEnchantment {

        public Stock() {
            maxLevel = 1;
            loreName = "Stock";
            chance = 0;
            enchantable = Storage.chestplates;
            conflicting = new Class[]{};
            description = "Refills the player's item in hand when they run out";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(final PlayerInteractEvent evt, int level) {
            final ItemStack stk = evt.getPlayer().getItemInHand().clone();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                public void run() {
                    int current = -1;
                    for (int i = 0; i < evt.getPlayer().getInventory().getContents().length; i++) {
                        ItemStack s = evt.getPlayer().getInventory().getContents()[i];
                        if (s != null && stk != null && evt.getPlayer().getItemInHand().getType().equals(AIR)) {
                            if (s.getType().equals(stk.getType())) {
                                if (s.getData().getData() == stk.getData().getData()) {
                                    current = i;
                                    break;
                                }
                                current = i;
                            }
                        }
                    }
                    if (current != -1) {
                        evt.getPlayer().setItemInHand(evt.getPlayer().getInventory().getContents()[current]);
                        evt.getPlayer().getInventory().setItem(current, null);
                        evt.getPlayer().updateInventory();
                    }
                }
            }, 1);
            return true;
        }
    }

    public static class Switch extends CustomEnchantment {

        public Switch() {
            maxLevel = 1;
            loreName = "Switch";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new Class[]{Shred.class, Anthropomorphism.class, Fire.class, Extraction.class, Pierce.class};
            description = "Replaces the clicked block with the leftmost block in your inventory when sneaking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(final PlayerInteractEvent evt, int level) {
            if (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getPlayer().isSneaking()) {
                BlockBreakEvent event = new BlockBreakEvent(evt.getClickedBlock(), evt.getPlayer());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }
                int ints[] = {6, 7, 23, 25, 29, 31, 32, 33, 37, 38, 39, 40, 49, 52, 54, 58, 61, 62, 63, 65, 68,
                    69, 77, 81, 83, 84, 90, 106, 107, 111, 119, 120, 130, 131, 143, 145, 146, 151, 158, 166, 166, 175, 178,
                    183, 184, 185, 186, 187, 209, 255, 323, 324, 330, 355, 397, 427, 428, 429, 430, 431};
                Material mat = AIR;
                byte bt = 0;
                int c = -1;
                for (int i = 0; i < 9; i++) {
                    if (evt.getPlayer().getInventory().getItem(i) != null) {
                        if (evt.getPlayer().getInventory().getItem(i).getType().isBlock() && !ArrayUtils.contains(ints, evt.getPlayer().getInventory().getItem(i).getType().getId())) {
                            mat = evt.getPlayer().getInventory().getItem(i).getType();
                            c = i;
                            bt = evt.getPlayer().getInventory().getItem(i).getData().getData();
                            break;
                        }
                    }
                }
                if (mat == AIR) {
                    return false;
                }
                if (mat == HUGE_MUSHROOM_1 || mat == HUGE_MUSHROOM_2) {
                    bt = 14;
                }
                if (ArrayUtils.contains(ints, mat.getId()) || ArrayUtils.contains(ints, evt.getClickedBlock().getTypeId())) {
                    return false;
                }
                if (!(mat == evt.getClickedBlock().getType() && evt.getClickedBlock().getData() == bt)) {
                    if ((!evt.getClickedBlock().isLiquid()) || evt.getClickedBlock().getType().isSolid()) {
                        Storage.grabLocs.put(event.getBlock(), event.getPlayer().getLocation());
                        final Block block = event.getBlock();
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                            public void run() {
                                Storage.grabLocs.remove(block);
                            }
                        }, 3);
                        evt.setCancelled(true);
                        if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                            for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                            }
                        } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                            for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                            }
                        } else {
                            event.getBlock().breakNaturally();
                        }
                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                        final Material m = mat;
                        final Byte b = bt;
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                            public void run() {
                                evt.getClickedBlock().setType(m);
                                evt.getClickedBlock().setData(b);
                            }
                        }, 1);
                        Utilities.removeItem(evt.getPlayer(), evt.getPlayer().getInventory().getItem(c).getType(), (short) bt, 1);
                        evt.getPlayer().updateInventory();
                    }
                }
            }
            return true;
        }
    }

    public static class Terraformer extends CustomEnchantment {

        private void bk(Block blk, List<Block> bks, List<Block> total, int i) {
            i++;
            if (i > 16 || total.size() > 64) {
                return;
            }
            bks.add(blk);
            Location[] locs = new Location[]{blk.getRelative(-1, 0, 0).getLocation(), blk.getRelative(1, 0, 0).getLocation(),
                blk.getRelative(0, 0, -1).getLocation(), blk.getRelative(0, 0, 1).getLocation(), blk.getRelative(0, -1, 0).getLocation()};
            for (Location l : locs) {
                if (!bks.contains(l.getBlock()) && l.getBlock().getType().equals(AIR)) {
                    if (total.size() > 64) {
                        continue;
                    }
                    bk(l.getBlock(), bks, total, i);
                    if (l.distance(bks.get(0).getLocation()) < 6) {
                        total.add(l.getBlock());
                    }
                }
            }
        }

        public Terraformer() {
            maxLevel = 1;
            loreName = "Terraformer";
            chance = 0;
            enchantable = Storage.spades;
            conflicting = new Class[]{};
            description = "Places the leftmost blocks in the players inventory within a 7 block radius";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getPlayer().isSneaking()) {
                if (evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
                    List<Block> used = new ArrayList<>();
                    List<Block> total = new ArrayList<>();
                    Location l = evt.getClickedBlock().getLocation();
                    if (evt.getClickedBlock().getRelative(0, 0, 1).getType() != AIR
                            && evt.getClickedBlock().getRelative(0, 0, -1).getType() != AIR
                            && evt.getClickedBlock().getRelative(-1, 0, 0).getType() != AIR
                            && evt.getClickedBlock().getRelative(1, 0, 0).getType() != AIR) {
                        l.setY(l.getY() + 1);
                    }
                    if (l.getBlock().getType().equals(AIR)) {
                        total.add(l.getBlock());
                    }
                    bk(l.getBlock(), used, total, 0);
                    int ints[] = {1, 2, 3, 4, 5, 12, 13, 14, 15, 16, 17, 18, 21, 24, 35, 43, 45, 46, 47, 48, 79, 80, 82, 87, 88, 99,
                        100, 110, 112, 121, 125, 129, 153, 155, 159, 161, 162, 165, 168, 172, 174, 179, 181};
                    Material mat = AIR;
                    byte bt = 0;
                    int c = -1;
                    for (int i = 0; i < 9; i++) {
                        if (evt.getPlayer().getInventory().getItem(i) != null) {
                            if (evt.getPlayer().getInventory().getItem(i).getType().isBlock() && ArrayUtils.contains(ints, evt.getPlayer().getInventory().getItem(i).getType().getId())) {
                                mat = evt.getPlayer().getInventory().getItem(i).getType();
                                c = i;
                                bt = evt.getPlayer().getInventory().getItem(i).getData().getData();
                                break;
                            }
                        }
                    }
                    if (mat == HUGE_MUSHROOM_1 || mat == HUGE_MUSHROOM_2) {
                        bt = 14;
                    }
                    for (Block b : total) {
                        if (Utilities.canEdit(evt.getPlayer(), b) && b.getType().equals(AIR)) {
                            if (Utilities.removeItemCheck(evt.getPlayer(), mat, bt, 1)) {
                                b.setType(mat);
                                b.setData(bt);
                                evt.getPlayer().updateInventory();
                                if (Storage.rnd.nextInt(10) == 5) {
                                    Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public static class Toxic extends CustomEnchantment {

        public Toxic() {
            maxLevel = 4;
            loreName = "Toxic";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.swords, Storage.bows);
            conflicting = new Class[]{};
            description = "Sickens the target, making them nauseous and unable to eat";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantToxic arrow = new EnchantArrow.ArrowEnchantToxic((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

        public boolean onHitting(final EntityDamageByEntityEvent evt, int level) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                final int value = (int) Math.round(level * power);
                Utilities.addPotion((LivingEntity) evt.getEntity(), CONFUSION, 80 + 60 * value, 4);
                Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 40 + 60 * value, 4);
                if (evt.getEntity() instanceof Player) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                        public void run() {
                            ((LivingEntity) evt.getEntity()).removePotionEffect(HUNGER);
                            Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 60 + 40 * value, 0);
                        }
                    }, 20 + 60 * value);
                    Storage.hungerPlayers.put((Player) evt.getEntity(), (1 + value) * 100);
                }
            }
            return true;
        }

    }

    public static class Tracer extends CustomEnchantment {

        public Tracer() {
            maxLevel = 4;
            loreName = "Tracer";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Guides the arrow to targets and then attacks";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantTracer arrow = new EnchantArrow.ArrowEnchantTracer((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

    }

    public static class Transformation extends CustomEnchantment {

        private final EntityType[] ENTITY_TYPES = new EntityType[]{SILVERFISH, ENDERMITE, ZOMBIE, PIG_ZOMBIE, VILLAGER, WITCH, COW, MUSHROOM_COW, SLIME, MAGMA_CUBE, WITHER_SKULL, SKELETON, OCELOT, WOLF};

        public Transformation() {
            maxLevel = 3;
            loreName = "Transformation";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new Class[]{};
            description = "Occasionally causes the attacked mob to be transformed into its similar cousin";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onHitting(EntityDamageByEntityEvent evt, int level) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                if (Storage.rnd.nextInt(100) > (100 - (level * power * 5))) {
                    int position = ArrayUtils.indexOf(ENTITY_TYPES, evt.getEntity().getType());
                    if (position != -1) {
                        if (evt.getDamage() > ((LivingEntity) evt.getEntity()).getHealth()) {
                            evt.setCancelled(true);
                        }
                        int newPosition = position + 1 - 2 * (position % 2);
                        ParticleEffect.HEART.display(.5f, 2f, .5f, .1f, 70, Utilities.getCenter(evt.getEntity().getLocation()), 32);
                        evt.getEntity().remove();
                        ((Player) evt.getDamager()).getWorld().spawnEntity(evt.getEntity().getLocation(), ENTITY_TYPES[newPosition]);
                    }
                }
            }
            return true;
        }
    }

    public static class Variety extends CustomEnchantment {

        ItemStack[] logs = new ItemStack[]{new ItemStack(LOG, 1, (short) 0), new ItemStack(LOG, 1, (short) 1), new ItemStack(LOG, 1, (short) 2), new ItemStack(LOG, 1, (short) 3), new ItemStack(LOG_2, 1, (short) 0), new ItemStack(LOG_2, 1, (short) 1)};
        ItemStack[] leaves = new ItemStack[]{new ItemStack(LEAVES, 1, (short) 0), new ItemStack(LEAVES, 1, (short) 1), new ItemStack(LEAVES, 1, (short) 2), new ItemStack(LEAVES, 1, (short) 3), new ItemStack(LEAVES_2, 1, (short) 0), new ItemStack(LEAVES_2, 1, (short) 1)};

        public Variety() {
            maxLevel = 1;
            loreName = "Variety";
            chance = 0;
            enchantable = Storage.axes;
            conflicting = new Class[]{Fire.class};
            description = "Drops random types of wood or leaves";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBlockBreak(BlockBreakEvent evt, int level) {
            if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), logs[Storage.rnd.nextInt(6)]);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            } else if (evt.getBlock().getType() == LEAVES || evt.getBlock().getType() == LEAVES_2) {
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), leaves[Storage.rnd.nextInt(6)]);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            }
            return true;
        }
    }

    public static class Vortex extends CustomEnchantment {

        public Vortex() {
            maxLevel = 1;
            loreName = "Vortex";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.bows, Storage.swords);
            conflicting = new Class[]{};
            description = "Teleports mob loot and XP directly to the player";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityKill(final EntityDeathEvent evt, int level) {
            Storage.vortexLocs.put(evt.getEntity().getLocation().getBlock(), evt.getEntity().getKiller().getLocation());
            int i = evt.getDroppedExp();
            evt.setDroppedExp(0);
            evt.getEntity().getKiller().giveExp(i);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                public void run() {
                    Storage.vortexLocs.remove(evt.getEntity().getLocation().getBlock());
                }
            }, 3);
            return true;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowEnchantVortex arrow = new EnchantArrow.ArrowEnchantVortex((Projectile) evt.getProjectile());
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

    }

    public static class Weight extends CustomEnchantment {

        public Weight() {
            maxLevel = 4;
            loreName = "Weight";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new Class[]{Meador.class, Speed.class};
            description = "Slows the player down but makes them stronger and more resistant to knockback";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onBeingHit(EntityDamageByEntityEvent evt, int level) {
            if (Utilities.canDamage(evt.getDamager(), evt.getEntity())) {
                if (evt.getEntity() instanceof Player) {
                    Player player = (Player) evt.getEntity();
                    if (evt.getDamage() < player.getHealth()) {
                        evt.setCancelled(true);
                        player.damage(evt.getDamage());
                        player.setVelocity(player.getLocation().subtract(evt.getDamager().getLocation()).toVector().multiply((float) (1 / (level * power + 1.5))));
                        ItemStack[] s = player.getInventory().getArmorContents();
                        for (int i = 0; i < 4; i++) {
                            if (s[i] != null) {
                                Utilities.addUnbreaking(player, s[i], 1);
                                if (s[i].getDurability() > s[i].getType().getMaxDurability()) {
                                    s[i] = null;
                                }
                            }
                        }
                        player.getInventory().setArmorContents(s);
                    }
                }
            }
            return true;
        }

        public boolean onScan(Player player, int level) {
            player.setWalkSpeed((float) (.164f - level * power * .014f));
            Storage.speed.add(player);
            Utilities.addPotion(player, INCREASE_DAMAGE, 610, (int) Math.round(power * level));
            return true;
        }
    }

//In-Development
//OP-Enchantments
    public static class Apocalypse extends CustomEnchantment {

        public Apocalypse() {
            maxLevel = 1;
            loreName = "Apocalypse";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Unleashes hell";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowAdminApocalypse arrow = new EnchantArrow.ArrowAdminApocalypse((Projectile) evt.getProjectile());
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
    }

    public static class Ethereal extends CustomEnchantment {

        public Ethereal() {
            maxLevel = 1;
            loreName = "Ethereal";
            chance = 0;
            enchantable = ArrayUtils.addAll(Storage.axes, ArrayUtils.addAll(Storage.boots, ArrayUtils.addAll(Storage.bows, ArrayUtils.addAll(Storage.chestplates, ArrayUtils.addAll(Storage.helmets, ArrayUtils.addAll(Storage.hoes, ArrayUtils.addAll(Storage.leggings, ArrayUtils.addAll(Storage.lighters, ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.rods, ArrayUtils.addAll(Storage.shears, ArrayUtils.addAll(Storage.spades, Storage.swords))))))))))));
            conflicting = new Class[]{};
            description = "Prevents tools from breaking";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onScanHand(Player player, int level) {
            player.getItemInHand().setDurability((short) 0);
            return true;
        }

        public boolean onScan(Player player, int level) {
            for (ItemStack s : player.getInventory().getArmorContents()) {
                if (s != null) {
                    Map<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(s);
                    if (map.containsKey(CustomEnchantment.Ethereal.this)) {
                        s.setDurability((short) 0);
                    }
                }
            }
            return true;
        }
    }

    public static class Missile extends CustomEnchantment {

        public Missile() {
            maxLevel = 1;
            loreName = "Missile";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Shoots a missile from the bow";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowAdminMissile arrow = new EnchantArrow.ArrowAdminMissile((Projectile) evt.getProjectile());
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            evt.setCancelled(true);
            Utilities.addUnbreaking(((Player) evt.getEntity()).getItemInHand(), 1, (Player) evt.getEntity());
            Utilities.removeItem(((Player) evt.getEntity()), Material.ARROW, 1);
            return true;
        }
    }

    public static class Singularity extends CustomEnchantment {

        public Singularity() {
            maxLevel = 1;
            loreName = "Singularity";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new Class[]{};
            description = "Creates a black hole that attracts nearby entities and then discharges them";
            cooldown = 0;
            power = 1.0;
        }

        public boolean onEntityShootBow(EntityShootBowEvent evt, int level) {
            EnchantArrow.ArrowAdminSingularity arrow = new EnchantArrow.ArrowAdminSingularity((Projectile) evt.getProjectile(), level);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }

    }

    public static class Unrepairable extends CustomEnchantment {

        public Unrepairable() {
            maxLevel = 1;
            loreName = "Unrepairable";
            chance = 0;
            enchantable = enchantable = ArrayUtils.addAll(Storage.axes, ArrayUtils.addAll(Storage.boots, ArrayUtils.addAll(Storage.bows, ArrayUtils.addAll(Storage.chestplates, ArrayUtils.addAll(Storage.helmets, ArrayUtils.addAll(Storage.hoes, ArrayUtils.addAll(Storage.leggings, ArrayUtils.addAll(Storage.lighters, ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.rods, ArrayUtils.addAll(Storage.shears, ArrayUtils.addAll(Storage.spades, Storage.swords))))))))))));
            conflicting = new Class[]{};
            description = "Prevents an item from being repaired";
            cooldown = 0;
            power = 1.0;
        }
    }
}
