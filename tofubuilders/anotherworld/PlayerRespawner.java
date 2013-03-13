package tofubuilders.anotherworld;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.LongHashMap;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PlayerRespawner implements IConnectionHandler {

	//SP専用
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		//ディメンションが存在しない場合に通常世界へ強制リスポーン
		if(player instanceof EntityPlayerMP){
			EntityPlayerMP entityplayermp = (EntityPlayerMP)player;
			if(entityplayermp.posY > 0)
				return;
			PlayerManager pm = entityplayermp.getServerForPlayer().getPlayerManager();
			LongHashMap hm = ReflectionHelper.getPrivateValue(PlayerManager.class, entityplayermp.getServerForPlayer().getPlayerManager(), 2);
			ReflectionHelper.setPrivateValue(PlayerManager.class, entityplayermp.getServerForPlayer().getPlayerManager(), new LongHashMap(), 2);
			//リスポーン
			player = (Player) entityplayermp.mcServer.getConfigurationManager().respawnPlayer(entityplayermp, 0, true);
			entityplayermp = (EntityPlayerMP)player;
			ReflectionHelper.setPrivateValue(PlayerManager.class, entityplayermp.getServerForPlayer().getPlayerManager(), hm, 2);
		}
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {return null;}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

	@Override
	public void connectionClosed(INetworkManager manager) {}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {}
	
}
