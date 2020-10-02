package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOOTS;

public class Speed extends Zenchantment {

	public static final int ID = 55;

	@Override
	public Builder<Speed> defaults() {
		return new Builder<>(Speed::new, ID)
			.maxLevel(4)
			.loreName("Speed")
			.probability(0)
			.enchantable(new Tool[]{BOOTS})
			.conflicting(new Class[]{Meador.class, Weight.class})
			.description("Gives the player a speed boost")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		player.setWalkSpeed((float) Math.min((.05f * level * power) + .2f, 1));
		player.setFlySpeed((float) Math.min((.05f * level * power) + .2f, 1));
		player.setMetadata("ze.speed", new FixedMetadataValue(Storage.zenchantments, System.currentTimeMillis()));
		return true;
	}
}
