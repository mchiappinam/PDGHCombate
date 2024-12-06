package me.mchiappinam.pdghcombate;

import java.io.File;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin
  implements Listener {
  protected HashMap<String, BukkitTask> players = new HashMap<String, BukkitTask>();
  protected HashMap<String, Integer> players2 = new HashMap<String, Integer>();
  protected HashMap<String, Integer> Bloqueared = new HashMap<String, Integer>();
  protected FileConfiguration language = null;

  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
    getServer().getPluginCommand("pdghcombate").setExecutor(new Comando(this));

	File file = new File(getDataFolder(),"config.yml");
	if(!file.exists()) {
		try {
			saveResource("config_template.yml",false);
			File file2 = new File(getDataFolder(),"config_template.yml");
			file2.renameTo(new File(getDataFolder(),"config.yml"));
		}
		catch(Exception e) {}
	}
	getServer().getConsoleSender().sendMessage("§3[PDGHCombate] §2ativado - Plugin by: mchiappinam");
	getServer().getConsoleSender().sendMessage("§3[PDGHCombate] §2Acesse: http://pdgh.net/");
  }

  public void onDisable() {
		getServer().getConsoleSender().sendMessage("§3[PDGHCombate] §2desativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHCombate] §2Acesse: http://pdgh.net/");
  }

  @EventHandler(ignoreCancelled=false, priority=EventPriority.MONITOR)
  private void onHit(EntityDamageByEntityEvent e) {
    if (!e.isCancelled()) {
      if ((e.getEntity() instanceof Player)) {
        final Player p = (Player)e.getEntity();
        boolean ja_tava = false;
        if (this.players.containsKey(p.getName().toLowerCase())) {
          ((BukkitTask)this.players.get(p.getName().toLowerCase())).cancel();
          this.players.remove(p.getName().toLowerCase());
          ja_tava = true;
        }
        PlayerEnterPvpEvent event = new PlayerEnterPvpEvent(p, 0);
        if (!ja_tava) {
          getServer().getPluginManager().callEvent(event);
          if (!event.isCancelled())
            p.sendMessage("§c"+ChatColor.BOLD+"➞§cVocê entrou em PvP! Se sair do servidor será morto!");
        }
        if (!event.isCancelled())
          this.players.put(p.getName().toLowerCase(), getServer().getScheduler().runTaskLater(this, new Runnable() {
            public void run() {
              Main.this.players.remove(p.getName().toLowerCase());
              p.sendMessage("§a"+ChatColor.BOLD+"➞§aVocê saiu do PvP, se quiser pode desconectar com segurança!");
            }
          }, getConfig().getInt("TempoEmCombate") * 20));
      }
      if ((e.getDamager() instanceof Player)) {
        final Player p = (Player)e.getDamager();
        boolean ja_tava = false;
        if (this.players.containsKey(p.getName().toLowerCase())) {
          ((BukkitTask)this.players.get(p.getName().toLowerCase())).cancel();
          this.players.remove(p.getName().toLowerCase());
          ja_tava = true;
        }
        PlayerEnterPvpEvent event = new PlayerEnterPvpEvent(p, 0);
        if (!ja_tava) {
          getServer().getPluginManager().callEvent(event);
          if (!event.isCancelled())
            p.sendMessage("§c"+ChatColor.BOLD+"➞§cVocê entrou em PvP, se sair do servidor será morto e banido temporariamente!");
        }
        if (!event.isCancelled())
          this.players.put(p.getName().toLowerCase(), getServer().getScheduler().runTaskLater(this, new Runnable() {
            public void run() {
              Main.this.players.remove(p.getName().toLowerCase());
              p.sendMessage("§a"+ChatColor.BOLD+"➞§aVocê saiu do PvP, se quiser pode desconectar com segurança!");
            }
          }, getConfig().getInt("TempoEmCombate") * 20));
      }
    }
  }

  @EventHandler
  private void onDeath(final PlayerDeathEvent e) {
    removeFromPvp(e.getEntity().getName(), 0);
    	getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	      public void run() {
	    	    removeFromPvp(e.getEntity().getName(), 0);
	      }
	    }, 10L);
    }

  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    if (this.players2.containsKey(e.getPlayer().getName().toLowerCase()))
      this.players2.remove(e.getPlayer().getName().toLowerCase());
  }

  @EventHandler
  private void onKick(PlayerKickEvent e) {
    removeFromPvp(e.getPlayer().getName(), 3);
  }

  @EventHandler
  private void onQuit(PlayerQuitEvent e) {
    if (this.players.containsKey(e.getPlayer().getName().toLowerCase())) {
      if (getConfig().getBoolean("AoDesconectar.MatarJogador")) {
        if (this.players2.containsKey(e.getPlayer().getName().toLowerCase())) {
              removeFromPvp(e.getPlayer().getName(), 1);
              e.getPlayer().setHealth(0);
              getServer().broadcastMessage("§c"+e.getPlayer().getName()+" deslogou em PvP, foi morto de banido");
      }else if (this.players2.containsKey(e.getPlayer().getName().toLowerCase()))
        this.players2.remove(e.getPlayer().getName().toLowerCase());
      if(getConfig().getBoolean("AoDesconectar.MostrarMensagemQueDeuDcEmCombateMaisNaoFoiMorto"))
          getServer().broadcastMessage("§c"+e.getPlayer().getName()+" deslogou em PvP");
      if(getConfig().getString("AoDesconectar.ExecutarCMD").length() > 0)
        getServer().dispatchCommand(getServer().getConsoleSender(), getConfig().getString("AoDesconectar.ExecutarCMD").replaceAll("@player", e.getPlayer().getName()));
        }
      }
    }

  protected boolean isInPvp(String nome) {
    return this.players.containsKey(nome.toLowerCase());
  }

  protected void removeFromPvp(String nome, int cause) {
    if (this.players.containsKey(nome.toLowerCase())) {
      getServer().getPluginManager().callEvent(new PlayerLeavePvpEvent(getServer().getPlayer(nome), cause));
      ((BukkitTask)this.players.get(nome.toLowerCase())).cancel();
      this.players.remove(nome.toLowerCase());
    }
  }

  protected void addToPvp(final Player p) {
    PlayerEnterPvpEvent event = new PlayerEnterPvpEvent(p, 1);
    boolean ja_tava = false;
    if (this.players.containsKey(p.getName().toLowerCase())) {
      ((BukkitTask)this.players.get(p.getName().toLowerCase())).cancel();
      this.players.remove(p.getName().toLowerCase());
      ja_tava = true;
    }
    if (!ja_tava) {
      getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled())
          p.sendMessage("§c"+ChatColor.BOLD+"➞§cVocê entrou em PvP, se sair do servidor será morto e banido temporariamente!");
    }
    if (!event.isCancelled())
      this.players.put(p.getName().toLowerCase(), getServer().getScheduler().runTaskLater(this, new Runnable() {
        public void run() {
          Main.this.players.remove(p.getName().toLowerCase());
          p.sendMessage("§a"+ChatColor.BOLD+"➞§aVocê saiu do PvP, se quiser pode desconectar com segurança!");
        }
      }, getConfig().getInt("TempoEmCombate") * 20));
  }

  @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
  private void onCommand(PlayerCommandPreprocessEvent e) {
    if ((getConfig().getBoolean("Bloquear.Comandos")) && (this.players.containsKey(e.getPlayer().getName().toLowerCase()))) {
      e.getPlayer().sendMessage("§c"+ChatColor.BOLD+"➞§cComandos bloqueados em PvP!");
      e.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
  private void onCommand2(PlayerCommandPreprocessEvent e) {
    if ((getConfig().getBoolean("Bloquear.Comandos")) && (this.players.containsKey(e.getPlayer().getName().toLowerCase()))) {
      e.getPlayer().sendMessage("§c"+ChatColor.BOLD+"➞§cComandos bloqueados em PvP!");
      e.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
  private void onCommand3(PlayerCommandPreprocessEvent e) {
    if ((getConfig().getBoolean("Bloquear.Comandos")) && (this.players.containsKey(e.getPlayer().getName().toLowerCase()))) {
      e.getPlayer().sendMessage("§c"+ChatColor.BOLD+"➞§cComandos bloqueados em PvP!");
      e.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
  private void onCommand4(PlayerCommandPreprocessEvent e) {
    if ((getConfig().getBoolean("Bloquear.Comandos")) && (this.players.containsKey(e.getPlayer().getName().toLowerCase()))) {
      e.getPlayer().sendMessage("§c"+ChatColor.BOLD+"➞§cComandos bloqueados em PvP!");
      e.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  private void onCommand5(PlayerCommandPreprocessEvent e) {
    if ((getConfig().getBoolean("Bloquear.Comandos")) && (this.players.containsKey(e.getPlayer().getName().toLowerCase()))) {
      e.getPlayer().sendMessage("§c"+ChatColor.BOLD+"➞§cComandos bloqueados em PvP!");
      e.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  private void onCommand6(PlayerCommandPreprocessEvent e) {
    if ((getConfig().getBoolean("Bloquear.Comandos")) && (this.players.containsKey(e.getPlayer().getName().toLowerCase()))) {
        e.getPlayer().sendMessage("§c"+ChatColor.BOLD+"➞§cComandos bloqueados em PvP!");
      e.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  private void onTeleport(PlayerTeleportEvent e) {
    if ((getConfig().getBoolean("Bloquear.Teleporte")) && (this.players.containsKey(e.getPlayer().getName().toLowerCase()))) {
      e.getPlayer().sendMessage("§c"+ChatColor.BOLD+"➞§cTeleporte bloqueado em PvP!");
      e.setCancelled(true);
    }
  }
}