package zedly.zenchantments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enchantments.*;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

// CustomEnchantment is the defualt structure for any enchantment. Each enchantment below it will extend this class
//      and will override any methods as neccecary in its behavior
public abstract class Zenchantment implements Comparable<Zenchantment> {

    private static final Pattern ENCH_LORE_PATTERN = Pattern.compile("§[a-fA-F0-9]([^§]+?)(?:$| $| (I|II|III|IV|V|VI|VII|VIII|IX|X)$)");

    protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;
    protected int id;

    protected int maxLevel;         // Max level the given enchant can naturally obtain
    protected String loreName;      // Name the given enchantment will appear as; with &7 (Gray) color
    protected float probability;    // Relative probability of obtaining the given enchantment
    protected Tool[] enchantable;   // Enums that represent tools that can receive and work with given enchantment
    protected Class[] conflicting;  // Classes of enchantments that don't work with given enchantment
    protected String description;   // Description of what the enchantment does
    protected int cooldown;         // Cooldown for given enchantment given in ticks; Default is 0
    protected double power;         // Power multiplier for the enchantment's effects; Default is 0; -1 means no
    // effect
    protected Hand handUse;
    // Which hands an enchantment has actiosn for; 0 = none, 1 = left, 2 = right, 3 = both
    private boolean used;
    // Indicates that an enchantment has already been applied to an event, avoiding infinite regress
    protected boolean isCursed;

    public abstract Builder<? extends Zenchantment> defaults();

    //region Enchanment Events
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onBlockInteractInteractable(PlayerInteractEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityInteract(PlayerInteractEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityDamage(EntityDamageEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerFish(PlayerFishEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onHungerChange(FoodLevelChangeEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerDeath(PlayerDeathEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onScanHands(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onCombust(EntityCombustByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScanHands(Player player, int level, boolean usedHand) {
        return false;
    }

    //endregion
    //region Getters and Setters
    int getMaxLevel() {
        return maxLevel;
    }

    void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    String getLoreName() {
        return loreName;
    }

    void setLoreName(String loreName) {
        this.loreName = loreName;
    }

    float getProbability() {
        return probability;
    }

    void setProbability(float probability) {
        this.probability = probability;
    }

    Tool[] getEnchantable() {
        return enchantable;
    }

    void setEnchantable(Tool[] enchantable) {
        this.enchantable = enchantable;
    }

    Class[] getConflicting() {
        return conflicting;
    }

    void setConflicting(Class[] conflicting) {
        this.conflicting = conflicting;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    int getCooldown() {
        return cooldown;
    }

    void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    double getPower() {
        return power;
    }

    void setPower(double power) {
        this.power = power;
    }

    Hand getHandUse() {
        return handUse;
    }

    void setHandUse(Hand handUse) {
        this.handUse = handUse;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Zenchantment o) {
        return this.getLoreName().compareTo(o.getLoreName());
    }

    //endregion
    public static void applyForTool(Player player, ItemStack tool, BiPredicate<Zenchantment, Integer> action) {
        getEnchants(tool, player.getWorld()).forEach((Zenchantment ench, Integer level) -> {
            if (!ench.used && Utilities.canUse(player, ench.id)) {
                try {
                    ench.used = true;
                    if (action.test(ench, level)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.id, ench.cooldown);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ench.used = false;
            }
        });
    }

    // Updates lore enchantments and descriptions to new format. This will be removed eventually
    public static ItemStack updateToNewFormat(ItemStack stk, World world) {
        if (stk != null) {
            if (stk.hasItemMeta()) {
                if (stk.getItemMeta().hasLore()) {
                    boolean hasEnch = false;
                    ItemMeta meta = stk.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    Zenchantment lastEnch = null;

                    List<String> tempLore = new LinkedList<>();
                    for (String str : meta.getLore()) {

                        Zenchantment ench = null;
                        int level = 0;
                        if (str.startsWith(ChatColor.GRAY + "")) {
                            String stripString = ChatColor.stripColor(str);

                            int splitIndex = stripString.lastIndexOf(" ");
                            if (splitIndex != -1) {
                                if (stripString.length() > 2) {
                                    String enchant;
                                    level = Utilities.getNumber(stripString.substring(splitIndex + 1));
                                    try {
                                        enchant = stripString.substring(0, splitIndex);
                                    } catch (Exception e) {
                                        enchant = "";
                                    }
                                    ench = Config.get(world).enchantFromString(enchant);
                                }
                            }
                        }

                        if (ench != null) {
                            lastEnch = ench;
                            hasEnch = true;
                            lore.add(ench.getShown(level, world));
                            lore.addAll(tempLore);
                            tempLore.clear();
                            continue;
                        }

                        if (lastEnch != null) {
                            tempLore.add(str);

                            StringBuilder bldr = new StringBuilder();
                            for (String ls : tempLore) {
                                bldr.append(ChatColor.stripColor(ls));
                            }
                            if (lastEnch.description.equals(bldr.toString())) {
                                lastEnch = null;
                                tempLore.clear();
                            }
                        } else {
                            lore.add(str);
                        }
                    }
                    lore.addAll(tempLore);

                    meta.setLore(lore);
                    stk.setItemMeta(meta);
                    if (hasEnch) {
                        setGlow(stk, true, world);
                    }
                    return stk;
                }
            }
        }
        return stk;
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static LinkedHashMap<Zenchantment, Integer> getEnchants(ItemStack stk, World world,
                                                                   List<String> outExtraLore) {
        return getEnchants(stk, false, world, outExtraLore);
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static LinkedHashMap<Zenchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                                                                   World world) {
        return getEnchants(stk, acceptBooks, world, null);
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static LinkedHashMap<Zenchantment, Integer> getEnchants(ItemStack stk, World world) {
        return getEnchants(stk, false, world, null);
    }

    public static LinkedHashMap<Zenchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                                                                   World world,
                                                                   List<String> outExtraLore) {
        Map<Zenchantment, Integer> map = new LinkedHashMap<>();
        if (stk != null && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
            if (stk.hasItemMeta()) {
                if (stk.getItemMeta().hasLore()) {
                    List<String> lore = stk.getItemMeta().getLore();
                    for (String raw : lore) {
                        Map.Entry<Zenchantment, Integer> ench = getEnchant(raw, world);
                        if (ench != null) {
                            map.put(ench.getKey(), ench.getValue());
                        } else {
                            if (outExtraLore != null) {
                                outExtraLore.add(raw);
                            }
                        }
                    }
                }
            }
        }
        LinkedHashMap<Zenchantment, Integer> finalMap = new LinkedHashMap<>();
        for (int id : new int[]{Lumber.ID, Shred.ID, Mow.ID, Pierce.ID, Extraction.ID, Plough.ID}) {
            Zenchantment e = null;
            for (Zenchantment en : Config.allEnchants) {
                if (en.getId() == id) {
                    e = en;
                }
            }
            if (map.containsKey(e)) {
                finalMap.put(e, map.get(e));
                map.remove(e);
            }
        }
        finalMap.putAll(map);
        return finalMap;
    }

    // Returns the custom enchantment from the lore name
    private static Map.Entry<Zenchantment, Integer> getEnchant(String raw, World world) {
        Matcher m = ENCH_LORE_PATTERN.matcher(raw);
        if (!m.find()) {
            return null;
        }

        String enchName = m.group(1);
        enchName = ChatColor.stripColor(enchName);
        int enchLvl = m.group(2) == null || m.group(2).equals("") ? 1 : Utilities.getNumber(m.group(2));

        Zenchantment ench = Config.get(world).enchantFromString(enchName);
        if (ench == null) {
            return null;
        }

        return new AbstractMap.SimpleEntry<>(ench, enchLvl);
    }

    /**
     * Determines if the material provided is enchantable with this enchantment.
     *
     * @param m The material to test.
     *
     * @return true iff the material can be enchanted with this enchantment.
     */
    // Returns true if the given material (tool) is compatible with the enchantment, otherwise false
    public boolean validMaterial(Material m) {
        for (Tool t : enchantable) {
            if (t.contains(m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the stack of material provided is enchantable with this
     * enchantment.
     *
     * @param m The stack of material to test.
     *
     * @return true iff the stack of material can be enchanted with this
     * enchantment.
     */
    public boolean validMaterial(ItemStack m) {
        return validMaterial(m.getType());
    }

    public String getShown(int level, World world) {
        String levelStr = Utilities.getRomanString(level);
        return (isCursed ? Config.get(world).getCurseColor() : Config.get(world).getEnchantmentColor()) + loreName
                + (maxLevel == 1 ? " " : " " + levelStr);
    }

    public List<String> getDescription(World world) {
        List<String> desc = new LinkedList<>();
        if (Config.get(world).descriptionLore()) {
            String strStart = Utilities.toInvisibleString("ze.desc." + getId())
                    + Config.get(world).getDescriptionColor() + "" + ChatColor.ITALIC + " ";
            StringBuilder bldr = new StringBuilder();

            int i = 0;
            for (char c : description.toCharArray()) {
                if (i < 30) {
                    i++;
                    bldr.append(c);
                } else {
                    if (c == ' ') {
                        desc.add(strStart + bldr.toString());
                        bldr = new StringBuilder(" ");
                        i = 1;
                    } else {
                        bldr.append(c);
                    }
                }
            }
            if (i != 0) {
                desc.add(strStart + bldr.toString());
            }
        }
        return desc;
    }

    public static boolean isDescription(String str) {
        Map<String, Boolean> unescaped = Utilities.fromInvisibleString(str);
        for (Map.Entry<String, Boolean> entry : unescaped.entrySet()) {
            if (!entry.getValue()) {
                String[] vals = entry.getKey().split("\\.");
                if (vals.length == 3 && vals[0].equals("ze") && vals[1].equals("desc")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setEnchantment(ItemStack stk, int level, World world) {
        setEnchantment(stk, this, level, world);
    }

    public static void setEnchantment(ItemStack stk, Zenchantment ench, int level, World world) {
        if (stk == null) {
            return;
        }
        ItemMeta meta = stk.getItemMeta();
        List<String> lore = new LinkedList<>();
        List<String> normalLore = new LinkedList<>();
        boolean customEnch = false;
        if (meta.hasLore()) {
            for (String loreStr : meta.getLore()) {
                Map.Entry<Zenchantment, Integer> enchEntry = getEnchant(loreStr, world);
                if (enchEntry == null && !isDescription(loreStr)) {
                    normalLore.add(loreStr);
                } else if (enchEntry != null && enchEntry.getKey() != ench) {
                    customEnch = true;
                    lore.add(enchEntry.getKey().getShown(enchEntry.getValue(), world));
                    lore.addAll(enchEntry.getKey().getDescription(world));
                }
            }
        }

        if (ench != null && level > 0 && level <= ench.maxLevel) {
            lore.add(ench.getShown(level, world));
            lore.addAll(ench.getDescription(world));
            customEnch = true;
        }

        lore.addAll(normalLore);
        meta.setLore(lore);
        stk.setItemMeta(meta);

        if (customEnch && stk.getType() == BOOK) {
            stk.setType(ENCHANTED_BOOK);
        }

        setGlow(stk, customEnch, world);
    }

    public static void setGlow(ItemStack stk, boolean customEnch, World world) {
        if (Config.get(world) == null || !Config.get(world).enchantGlow()) {
            return;
        }
        ItemMeta itemMeta = stk.getItemMeta();
        EnchantmentStorageMeta bookMeta = null;

        boolean isBook = stk.getType() == BOOK || stk.getType() == ENCHANTED_BOOK;

        boolean containsNormal = false;
        boolean containsHidden = false;
        int duraLevel = 0;
        Map<Enchantment, Integer> enchs;

        if (stk.getType() == ENCHANTED_BOOK) {
            bookMeta = (EnchantmentStorageMeta) stk.getItemMeta();
            enchs = bookMeta.getStoredEnchants();
        } else {
            enchs = itemMeta.getEnchants();
        }

        for (Map.Entry<Enchantment, Integer> set : enchs.entrySet()) {
            if (!(set.getKey().equals(Enchantment.DURABILITY) && (duraLevel = set.getValue()) == 0)) {
                containsNormal = true;
            } else {
                containsHidden = true;
            }
        }
        if (containsNormal || (!customEnch && containsHidden)) {
            if (stk.getType() == ENCHANTED_BOOK) {
                if (duraLevel == 0) {
                    bookMeta.removeStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY);
                }
                bookMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                if (duraLevel == 0) {
                    itemMeta.removeEnchant(Enchantment.DURABILITY);
                }
                itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } else if (customEnch) {
            if (stk.getType() == BOOK) {
                stk.setType(ENCHANTED_BOOK);
                bookMeta = (EnchantmentStorageMeta) stk.getItemMeta();
                bookMeta.addStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 0, true);
                bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                itemMeta.addEnchant(Enchantment.DURABILITY, 0, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        stk.setItemMeta(isBook ? bookMeta : itemMeta);
    }

    protected static final class Builder<T extends Zenchantment> {

        private final T customEnchantment;

        public Builder(Supplier<T> sup, int id) {
            customEnchantment = sup.get();
            customEnchantment.setId(id);
        }

        public Builder<T> maxLevel(int maxLevel) {
            customEnchantment.setMaxLevel(maxLevel);
            return this;
        }

        public int maxLevel() {
            return customEnchantment.getMaxLevel();
        }

        public Builder<T> loreName(String loreName) {
            customEnchantment.setLoreName(loreName);
            return this;
        }

        public String loreName() {
            return customEnchantment.getLoreName();
        }

        public Builder<T> probability(float probability) {
            customEnchantment.setProbability(probability);
            return this;
        }

        public float probability() {
            return customEnchantment.getProbability();
        }

        public Builder<T> enchantable(Tool[] enchantable) {
            customEnchantment.setEnchantable(enchantable);
            return this;
        }

        public Tool[] enchantable() {
            return customEnchantment.getEnchantable();
        }

        public Builder<T> conflicting(Class[] conflicting) {
            customEnchantment.setConflicting(conflicting);
            return this;
        }

        public Class[] conflicting() {
            return customEnchantment.getConflicting();
        }

        public Builder<T> description(String description) {
            customEnchantment.setDescription(description);
            return this;
        }

        public String description() {
            return customEnchantment.getDescription();
        }

        public Builder<T> cooldown(int cooldown) {
            customEnchantment.setCooldown(cooldown);
            return this;
        }

        public int cooldown() {
            return customEnchantment.getCooldown();
        }

        public Builder<T> power(double power) {
            customEnchantment.setPower(power);
            return this;
        }

        public double power() {
            return customEnchantment.getPower();
        }

        public Builder<T> handUse(Hand handUse) {
            customEnchantment.setHandUse(handUse);
            return this;
        }

        public Hand handUse() {
            return customEnchantment.getHandUse();
        }

        public T build() {
            return customEnchantment;
        }
    }
}
