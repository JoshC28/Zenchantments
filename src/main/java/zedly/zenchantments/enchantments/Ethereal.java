package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import java.util.Map;

import static zedly.zenchantments.Tool.ALL;

public class Ethereal extends Zenchantment {

	public static final int ID = 70;

	@Override
	public Builder<Ethereal> defaults() {
		return new Builder<>(Ethereal::new, ID)
			.maxLevel(1)
			.loreName("Ethereal")
			.probability(0)
			.enchantable(new Tool[]{ALL})
			.conflicting(new Class[]{})
			.description("Prevents tools from breaking")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScanHands(Player player, int level, boolean usedHand) {
		ItemStack stk = Utilities.usedStack(player, usedHand);
		int dura = Utilities.getDamage(stk);
		Utilities.setDamage(stk, 0);
		if (dura != 0) {
			if (usedHand) {
				player.getInventory().setItemInMainHand(stk);
			} else {
				player.getInventory().setItemInOffHand(stk);
			}
		}
		return dura != 0;
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		for (ItemStack s : player.getInventory().getArmorContents()) {
			if (s != null) {
				Map<Zenchantment, Integer> map = Zenchantment.getEnchants(s, player.getWorld());
				if (map.containsKey(zedly.zenchantments.enchantments.Ethereal.this)) {
					Utilities.setDamage(s, 0);
				}
			}
		}
		return true;
	}
}
