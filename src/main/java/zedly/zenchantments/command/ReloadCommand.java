package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends ZenchantmentsCommand {
    public ReloadCommand(ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}