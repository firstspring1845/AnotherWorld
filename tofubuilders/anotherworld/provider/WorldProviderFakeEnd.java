package tofubuilders.anotherworld.provider;

import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import tofubuilders.anotherworld.AnotherWorldManager;

public class WorldProviderFakeEnd extends WorldProviderEnd {

	@Override
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.5F, 0.0F);
        this.hasNoSky = true;
	}

	@Override
	public boolean canRespawnHere() {
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

}
