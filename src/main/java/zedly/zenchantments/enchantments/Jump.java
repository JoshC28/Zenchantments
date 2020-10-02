package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.potion.PotionEffectType.JUMP;
import static zedly.zenchantments.enums.Tool.BOOTS;

public class Jump extends Zenchantment {

	public static final int ID = 30;

	@Override
	public Builder<Jump> defaults() {
		return new Builder<>(Jump::new, ID)
			.maxLevel(4)
			.loreName("Jump")
			.probability(0)
			.enchantable(new Tool[]{BOOTS})
			.conflicting(new Class[]{})
			.description("Gives the player a jump boost")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		Utilities.addPotion(player, JUMP, 610, (int) Math.round(level * power));
		return true;
	}
}
