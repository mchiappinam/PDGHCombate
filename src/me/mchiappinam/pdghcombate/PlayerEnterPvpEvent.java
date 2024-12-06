package me.mchiappinam.pdghcombate;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEnterPvpEvent extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private boolean cancelled = false;
  private Player p;
  private EnterPvpReason r = null;

  public PlayerEnterPvpEvent(Player p, int type) {
    this.p = p;
    if (type == 0)
      this.r = EnterPvpReason.PVP;
    else
      this.r = EnterPvpReason.CUSTOM;
  }

  public Player getPlayer() {
    return this.p;
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

  public EnterPvpReason getReason() {
    return this.r;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public static enum EnterPvpReason
  {
    PVP, 
    CUSTOM;
  }
}