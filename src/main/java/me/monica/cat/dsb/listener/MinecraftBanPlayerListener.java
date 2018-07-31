public class MinecraftBanPlayerListener implements Listener {
    
    @EventHandler
    public void onBanPlayer(BanPlayerEvent e) {
        DiscordMessageHandler dmh = new DiscordMessageHandler();
        Main.getPlugin().unlink(e.getPlayer().getUniqueId().toString());
    }
}