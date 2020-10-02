package zedly.zenchantments.enchantments;

import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import static zedly.zenchantments.Tool.PICKAXE;

public class Extraction extends Zenchantment {

	public static final int ID = 12;

	@Override
	public Builder<Extraction> defaults() {
		return new Builder<>(Extraction::new, ID)
			.maxLevel(3)
			.loreName("Extraction")
			.probability(0)
			.enchantable(new Tool[]{PICKAXE})
			.conflicting(new Class[]{Switch.class})
			.description("Smelts and yields more product from ores")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onBlockBreak(BlockBreakEvent evt, final int level, boolean usedHand) {
		if (evt.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return false;
		}
		if (evt.getBlock().getType() == GOLD_ORE || evt.getBlock().getType() == IRON_ORE) {
			Utilities.damageTool(evt.getPlayer(), 1, usedHand);
			for (int x = 0; x < Storage.rnd.nextInt((int) Math.round(power * level + 1)) + 1; x++) {
				evt.getBlock().getWorld().dropItemNaturally(evt.getBlock().getLocation(),
					new ItemStack(evt.getBlock().getType() == GOLD_ORE ?
						GOLD_INGOT : IRON_INGOT));
			}
			ExperienceOrb o = (ExperienceOrb) evt.getBlock().getWorld()
			                                     .spawnEntity(evt.getBlock().getLocation(), EXPERIENCE_ORB);
			o.setExperience(
				evt.getBlock().getType() == IRON_ORE ? Storage.rnd.nextInt(5) + 1 : Storage.rnd.nextInt(5) + 3);
			evt.getBlock().setType(AIR);
			Utilities.display(evt.getBlock().getLocation(), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
			return true;
		}
		return false;
	}
}
