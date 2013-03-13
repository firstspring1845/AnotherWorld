package tofubuilders.anotherworld.provider;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeGenFlow extends BiomeGenBase
{
	public BiomeGenFlow(int par1)
	{
		super(par1);
		this.spawnableMonsterList.clear();
		//this.spawnableCreatureList.clear();
		//this.spawnableWaterCreatureList.clear();
		this.topBlock = (byte)Block.grass.blockID;
		this.fillerBlock = (byte)Block.dirt.blockID;
		this.theBiomeDecorator = new BiomeDecoratorFlow(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor()
	{
		return 0x227f52;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeFoliageColor()
	{
		return 0x227f52;
	}
}