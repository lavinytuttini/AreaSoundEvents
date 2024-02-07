package me.lavinytuttini.areasoundevents.commands;

import me.lavinytuttini.areasoundevents.data.PaginationData;
import me.lavinytuttini.areasoundevents.utils.Pagination;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NextPageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Pagination pagination = Pagination.getInstance();
            PaginationData paginationData = pagination.getPaginationDataMap(player);
            paginationData.nextPage();
            Pagination.getInstance().sendPaginatedMessage(player, paginationData.getCurrentPage(), 2);
        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
}
