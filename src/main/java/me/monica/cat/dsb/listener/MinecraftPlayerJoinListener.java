
public class MinecraftPlayerJoinListener {
    
    @EventHandler
    public void onPlayerJoin(PlayerjoinEvent e) {
        //update offlineLinkMap's name
        Player player = e.getPlayer();
        Main.getPlugin().detectNameChanged(player);
        e.setJoinMessage("Welcome to the Summon's Rift")
        
        
        
    }
}