package me.mchiappinam.pdghcombate;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Comando
  implements CommandExecutor {
  private Main plugin;

  public Comando(Main main) {
    plugin = main;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
    if (cmd.getName().equalsIgnoreCase("pdghcombate")) {
      if (!sender.hasPermission("pdgh.admin")) {
        sender.sendMessage("§cSem permissões");
        return true;
      }
      if (args.length == 0) {
        sender.sendMessage("§cLimpe a lista de pessoas em combate com /pdghcombate clear");
      }
      else if (args[0].equalsIgnoreCase("clear")) {
    	List<String> names = new ArrayList<String>();
        names.addAll(plugin.players.keySet());
        for (String n : names)
        	plugin.removeFromPvp(n, 2);
        	sender.sendMessage("§aLista de pessoas em combate limpa!");
      }
      return true;
    }
    return false;
  }
}