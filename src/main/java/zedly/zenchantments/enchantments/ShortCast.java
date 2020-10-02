package zedly.zenchantments.enchantments;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.ROD;

public class ShortCast extends Zenchantment {

	public static final int ID = 51;

	@Override
	public Builder<ShortCast> defaults() {
		return new Builder<>(ShortCast::new, ID)
			.maxLevel(2)
			.loreName("Short Cast")
			.probability(0)
			.enchantable(new Tool[]{ROD})
			.conflicting(new Class[]{LongCast.class})
			.description("Launches fishing hooks closer in when casting")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
		if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
			evt.getEntity()
			   .setVelocity(evt.getEntity().getVelocity().normalize().multiply((.8f / (level * power))));
		}
		return true;
	}
}
