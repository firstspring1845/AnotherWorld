package tofubuilders.anotherworld.provider;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import tofubuilders.anotherworld.AnotherWorldManager;

public class WorldProviderFakeSurface extends WorldProvider {

	@Override
	protected void registerWorldChunkManager() {
		worldChunkMgr = new WorldChunkManager(worldObj);
	}

	@Override
	public String getDimensionName() {
		return AnotherWorldManager.instance().getName(this.dimensionId);
	}

	@Override
	public String getSaveFolder() {
		return "/AnotherWorld/Worlds/" + getDimensionName();
	}

	@Override
	public long getSeed() {
		return AnotherWorldManager.instance().getSeed(this.dimensionId);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), true);
	}

}
