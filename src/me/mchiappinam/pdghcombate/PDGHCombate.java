package me.mchiappinam.pdghcombate;

import org.bukkit.entity.Player;

public class PDGHCombate
{
  private static Main plugin;

  public PDGHCombate(Main main)
  {
    plugin = main;
  }

  public static boolean isInPvp(String nome) {
    return plugin.isInPvp(nome);
  }

  public static boolean isInPvp(Player player) {
    return plugin.isInPvp(player.getName());
  }

  public static void removeFromPvp(String nome) {
    if (isInPvp(nome))
      plugin.removeFromPvp(nome, 2);
  }

  public static void removeFromPvp(Player player) {
    removeFromPvp(player.getName());
  }

  public static void addToPvp(Player player) {
    plugin.addToPvp(player);
  }
}