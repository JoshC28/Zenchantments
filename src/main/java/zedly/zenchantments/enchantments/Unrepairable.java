package zedly.zenchantments.enchantments;

import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.ALL;

public class Unrepairable extends Zenchantment {

	public static final int ID = 73;

	@Override
	public Builder<Unrepairable> defaults() {
		return new Builder<>(Unrepairable::new, ID)
			.maxLevel(1)
			.loreName("Unrepairable")
			.probability(0)
			.enchantable(new Tool[]{ALL})
			.conflicting(new Class[]{})
			.description("Prevents an item from being repaired")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.NONE);
	}
}
