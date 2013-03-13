package tofubuilders.anotherworld.provider;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import tofubuilders.anotherworld.AnotherWorld;
import tofubuilders.anotherworld.AnotherWorldManager;

public class WorldProviderFlow extends WorldProvider
{
	@Override
	public void registerWorldChunkManager()
	{
		//super.registerWorldChunkManager();
		this.worldChunkMgr = new WorldChunkManagerHell(AnotherWorld.flowBiome, 0.5F, 0.0F);
	}
	
	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderFlow(this.worldObj, this.worldObj.getSeed());//new ChunkProviderEnd(this.worldObj, this.worldObj.getSeed());
	}
	
	@Override
	protected void generateLightBrightnessTable()
	{
		for (int i = 0; i <= 15; ++i)
			this.lightBrightnessTable[i] = 1.0F;
	}
	

	@Override
	public int getAverageGroundLevel()
	{
		return 50;
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return true;
	}

	@Override
	public boolean canRespawnHere()
	{
		return false;
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
	
	public float calculateCelestialAngle(long par1, float par3)
    {
        return 1.0F;
    }

	@Override
	public long getWorldTime() {
		return 6000;
	}

}