package tofubuilders.anotherworld;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class AnotherWorldTeleporter extends Teleporter {
	WorldServer world;

	public AnotherWorldTeleporter(WorldServer world) {
		super(world);
		this.world = world;
	}
	
	public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8){
		for(int x = -100; x <= 100; x++)
			for(int z = -100; z <= 100; z++){
				int y = getPlayerYCoord(world, x, z);
				if(y != -1){
					par1Entity.setLocationAndAngles((double)x + 0.5D, (double)y, (double)z + 0.5D, par1Entity.rotationYaw, 0.0F);
					par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
				}
			}
	}
	
	public int getPlayerYCoord(WorldServer world, int x, int z){
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
