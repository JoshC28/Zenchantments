package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

public class Blizzard extends Zenchantment {
    public static final String KEY = "blizzard";

    private static final String                             NAME        = "Blizzard";
    private static final String                             DESCRIPTION = "Spawns a blizzard where the arrow strikes freezing nearby entities";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Firestorm.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Blizzard(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, Blizzard.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return Blizzard.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return Blizzard.DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return Blizzard.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return Blizzard.HAND_USE;
    }

    @Override
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        BlizzardArrow arrow = new BlizzardArrow((Arrow) event.getProjectile(), level, this.getPower());
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}