package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.potion.PotionEffectType.NIGHT_VISION;
import static zedly.zenchantments.enums.Tool.HELMET;

public class NightVision extends Zenchantment {

	public static final int ID = 40;

	@Override
	public Builder<NightVision> defaults() {
		return new Builder<>(NightVision::new, ID)
			.maxLevel(1)
			.loreName("Night Vision")
			.probability(0)
			.enchantable(new Tool[]{HELMET})
			.conflicting(new Class[]{})
			.description("Lets the player see in the darkness")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		Utilities.addPotion(player, NIGHT_VISION, 610, 5);
		return true;
	}
}
