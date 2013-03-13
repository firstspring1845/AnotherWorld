package tofubuilders.anotherworld.provider;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenLakes;

public class BiomeDecoratorFlow extends BiomeDecorator
{
	public BiomeDecoratorFlow(BiomeGenBase biomegenbase)
	{
		super(biomegenbase);
		this.generateLakes = true;
		this.treesPerChunk = 2;
	}
	
	@Override
	protected void decorate()
	{
		int var3;
		int var4;
		int var7;
		
		if (this.generateLakes)
		{
			for (int var2 = 0; var2 < 2; ++var2)
			{
				var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
				var4 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(120) + 8);
				var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
				(new WorldGenLakes(Block.waterMoving.blockID)).generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
			}
		}
		
		super.decorate();
	}
}