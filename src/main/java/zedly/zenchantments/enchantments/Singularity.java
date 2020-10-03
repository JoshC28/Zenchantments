package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.admin.SingularityArrow;
import zedly.zenchantments.task.Frequency;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.GameMode.CREATIVE;
import static zedly.zenchantments.Tool.BOW;

public class Singularity extends Zenchantment {

	// Locations of black holes from the singularity enchantment and whether or not they are attracting or repelling
	public static final Map<Location, Boolean> blackholes = new HashMap<>();
	public static final int                    ID         = 72;

	@Override
	public Builder<Singularity> defaults() {
		return new Builder<>(Singularity::new, ID)
			.maxLevel(1)
			.name("Singularity")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Creates a black hole that attracts nearby entities and then discharges them")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		SingularityArrow arrow = new SingularityArrow((Arrow) event.getProjectile(), level);
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		return true;
	}

	// Moves entities towards the black hole from the Singularity enchantment in pull state
	// Throws entities in the black hole out in reverse state
	@EffectTask(Frequency.HIGH)
	public static void blackholes() {
		for (Location l : blackholes.keySet()) {
			for (Entity e : l.getWorld().getNearbyEntities(l, 10, 10, 10)) {
				if (e instanceof Player) {
					if (((Player) e).getGameMode().equals(CREATIVE)) {
						continue;
					}
				}
				if (blackholes.get(l)) {
					Vector v = l.clone().subtract(e.getLocation()).toVector();
					v.setX(v.getX() + (-.5f + Storage.rnd.nextFloat()) * 10);
					v.setY(v.getY() + (-.5f + Storage.rnd.nextFloat()) * 10);
					v.setZ(v.getZ() + (-.5f + Storage.rnd.nextFloat()) * 10);
					e.setVelocity(v.multiply(.35f));
					e.setFallDistance(0);
				} else {
					Vector v = e.getLocation().subtract(l.clone()).toVector();
					v.setX(v.getX() + (-.5f + Storage.rnd.nextFloat()) * 2);
					v.setY(v.getY() + Storage.rnd.nextFloat());
					v.setZ(v.getZ() + (-.5f + Storage.rnd.nextFloat()) * 2);
					e.setVelocity(v.multiply(.35f));
				}
			}
		}
	}
}
