package tofubuilders.anotherworld;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.google.common.collect.Lists;

public class CommandAnotherWorld extends CommandBase {

	@Override
	public String getCommandName() {
		return "anotherworld";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/anotherworld help : /aw help";
	}

	@Override
	public List getCommandAliases() {
		return Lists.newArrayList("aw");
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {

		if (arguments.length <= 0)
			throw new WrongUsageException("Type '" + this.getCommandUsage(sender) + "' for help.");

		if (arguments[0].matches("create")) {
			commandCreateWorld(sender, arguments);
			return;
		} else if (arguments[0].matches("delete")){
			commandDeleteWorld(sender, arguments);
			return;
		} else if (arguments[0].matches("tp")){
			commandTeleport(sender, arguments);
			return;
		} else if (arguments[0].matches("teleport")){
			commandTeleport(sender, arguments);
			return;
		} else if (arguments[0].matches("set")){
			commandMovePoint(sender, arguments);
			return;
		} else if (arguments[0].matches("goto")){
			commandMovePoint(sender, arguments);
			return;
		} else if (arguments[0].matches("list")){
			commandList(sender, arguments);
			return;
		} else if (arguments[0].matches("help")){
			sender.sendChatToPlayer("Format: '" + this.getCommandName() + " <command> <arguments>'");
			sender.sendChatToPlayer("Available commands:");
			sender.sendChatToPlayer("- create : Create new dimension.");
			sender.sendChatToPlayer("- delete : Schedule delete dimension.");
			sender.sendChatToPlayer("- teleport / tp : Teleport to dimension.");
			sender.sendChatToPlayer("- movepoint / mp : Regist player position and teleport.");
			sender.sendChatToPlayer("- list : Display dimension list");
			return;
		}

		throw new WrongUsageException(this.getCommandUsage(sender));
	}

	private void commandCreateWorld(ICommandSender sender, String[] arguments) {
		int newDimension = DimensionManager.getNextFreeDimId();
		if(AnotherWorldManager.instance().existDimensionName(arguments[1])){
			sender.sendChatToPlayer("Exist " + arguments[1] + " Dimension");
			return;
		}
		int provider = Integer.parseInt(arguments[2]);
		AnotherWorldManager.instance().addDimensionInfo(newDimension, arguments[1], new Random(System.currentTimeMillis()).nextLong(), Integer.parseInt(arguments[2]));
		AnotherWorldManager.instance().registerDimension(newDimension, provider);
		try {
			AnotherWorldManager.instance().save();
		} catch (IOException e) {
			System.out.println("File Error Caused By : WorldInfo Save");
		}
		sender.sendChatToPlayer("Created Dimension " + newDimension);
	}
	
	private void commandDeleteWorld(ICommandSender sender, String[] arguments) {
		AnotherWorldManager.instance().scheduleDeleteWorld(Integer.parseInt(arguments[1]));
		sender.sendChatToPlayer("Schedule Remove Dimension " + arguments[1]);
	}
	
	private void commandTeleport(ICommandSender sender, String[] arguments){
		if(sender instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP)sender;
			int toDimension = Integer.parseInt(arguments[1]);
			if(!existDimension(toDimension, sender))
				return;
			WorldServer world = player.mcServer.worldServerForDimension(toDimension);
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, toDimension, new AnotherWorldTeleporter(world));
		}
	}
	
	private void commandMovePoint(ICommandSender sender, String[] arguments){
		if(sender instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP)sender;
			if(arguments[0].equals("set")){
				sender.sendChatToPlayer("Set Point " + arguments[1] + " to here.");
				AnotherWorldManager.instance().createMovePoint(arguments[1], new Point(player.dimension, player.posX, player.posY, player.posZ));
			}else if(arguments[0].equals("goto")){
				Point p = AnotherWorldManager.instance().getMovePoint(arguments[1]);
				if(p == null){
					sender.sendChatToPlayer("Point " + arguments[1] + " not found.");
					return;
				}
				if(!existDimension(p.dimension, sender))
					return;
				if(player.dimension != p.dimension){
					WorldServer world = player.mcServer.worldServerForDimension(p.dimension);
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, p.dimension, new AnotherWorldTeleporter(world));
				}
				player.setPositionAndUpdate(p.x, p.y, p.z);
			}
		}
	}
	
	private void commandList(ICommandSender sender, String[] arguments){
		Map dims = AnotherWorldManager.instance().getDimensionInformations();
		for(Iterator<Map.Entry> i = dims.entrySet().iterator(); i.hasNext();){
			Map.Entry<Integer, AnotherWorldInfo> entry = i.next();
			int dim = entry.getKey();
			AnotherWorldInfo inf = entry.getValue();
			if(inf.scheduleRemove)
				continue;
			sender.sendChatToPlayer(String.format("%d : %s", dim, inf.name));
		}
	}
	
	private boolean existDimension(int dimension, ICommandSender sender){
		Integer[] dims = DimensionManager.getStaticDimensionIDs();
		boolean found = false;
		for(Integer dim : dims)
			if(dim.equals(dimension))
				found = true;
		if(!found){
			sender.sendChatToPlayer("Dimension " + dimension + " Not found.");
			return false;
		}
		return true;
	}
	
	private int getPlayerYCoord(WorldServer world, int x, int z){
		int yMax = 254;
		if(world.provider.isHellWorld)
			yMax = 126;
		for(int y = yMax; y > 0; y--){
			int id = world.getBlockId(x, y, z);
			int id2 = world.getBlockId(x, y + 1, z);
			int id3 = world.getBlockId(x, y + 2, z);
			if(id != 0 && Block.blocksList[id].isBlockSolid(world, x, y, z, 0) && id2 == 0 && id3 == 0)
				return y + 1;
		}
		return -1;
	}
}
