package me.lavinytuttini.areasoundevents.commands;

import me.lavinytuttini.areasoundevents.data.PaginationData;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.utils.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NextPageCommand implements CommandExecutor {
    private final LocalizationManager localization = LocalizationManager.getInstance();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Pagination pagination = Pagination.getInstance();
            PaginationData paginationData = pagination.getPaginationDataMap(player);
            paginationData.nextPage();
            Pagination.getInstance().sendPaginatedMessage(player, paginationData.getCurrentPage());
        } else {
            sender.sendMessage(ChatColor.RED + localization.getString("commands_only_player_can_use"));
        }
        return true;
    }
}
