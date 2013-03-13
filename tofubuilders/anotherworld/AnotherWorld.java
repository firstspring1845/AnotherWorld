package tofubuilders.anotherworld;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import tofubuilders.anotherworld.provider.BiomeGenFlow;
import tofubuilders.anotherworld.provider.WorldProviderFakeEnd;
import tofubuilders.anotherworld.provider.WorldProviderFakeHell;
import tofubuilders.anotherworld.provider.WorldProviderFakeSurface;
import tofubuilders.anotherworld.provider.WorldProviderFlow;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid="AnotherWorld", name="AnotherWorld", version="Build 1")
public class AnotherWorld {
	public static int fakePSID = 1845;
	public static int fakePHID = 1846;
	public static int fakePEID = 1847;
	public static int flowID = 1848;
	public static int flowBiomeID = 147;
	
	public static BiomeGenBase flowBiome;

	@PreInit
	public void preInitializePhase(FMLPreInitializationEvent event){
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		cfg.load();
		fakePSID = cfg.get("provider", "Fake_Surface_ID", 1845).getInt();
		fakePHID = cfg.get("provider", "Fake_Hell_ID", 1846).getInt();
		fakePEID = cfg.get("provider", "Fake_END", 1847).getInt();
		flowID = cfg.get("provider", "Flow_ID", 1848).getInt();
		flowBiomeID = cfg.get("biome", "Flow_Biome_ID", 147).getInt();
		cfg.save();
	}


	@Init
	public void load(FMLInitializationEvent event){
		this.flowBiome = (new BiomeGenFlow(147)).setColor(9286496).setBiomeName("Flow").setDisableRain().setTemperatureRainfall(2.0F, 0.0F).setMinMaxHeight(0.1F, 0.2F);
		DimensionManager.registerProviderType(fakePSID, WorldProviderFakeSurface.class, true);
		DimensionManager.registerProviderType(fakePHID, WorldProviderFakeHell.class, true);
		DimensionManager.registerProviderType(fakePEID, WorldProviderFakeEnd.class, true);
		DimensionManager.registerProviderType(flowID, WorldProviderFlow.class, true);
		PlayerRespawner pr = new PlayerRespawner();
		NetworkRegistry.instance().registerConnectionHandler(pr);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event){
		AnotherWorldManager.instance().unloadWorlds();
		//手動でディメンションを削除した際にディメンションマップの不整合が起きるので再生成
		DimensionManager.loadDimensionDataMap(null);
		AnotherWorldManager.instance().initialize();
		AnotherWorldManager.instance().load();
		event.registerServerCommand(new CommandAnotherWorld());
		//PlayerRespawnerの前処理(SP専用)
		NBTTagCompound nbt = event.getServer().worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
		if(nbt != null){
			boolean found = false;
			int dim = nbt.getInteger("Dimension");
			Integer[] dims = DimensionManager.getStaticDimensionIDs();
			for(Integer i : dims)
				if(i.equals(dim))
					found = true;
			if(!found){
				nbt.setInteger("Dimension", 0);
				//Y座標がマイナスな事はありえないからリスポーンフラグに
				nbt.setTag("Pos", this.newDoubleNBTList(new double[] {0, -1, 0}));
			}
		}
	}

	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent event){
		try{
			AnotherWorldManager.instance().save();
		}catch(IOException e){
			System.out.println("File Error Caused By : WorldInfo Save");
		}
		//DimensionManagerから消えてさえいなければいいんだよ・・・多分
		AnotherWorldManager.instance().scheduleUnloadWorld();
		AnotherWorldManager.instance().initialize();
	}

	public static NBTTagList newDoubleNBTList(double ... par1ArrayOfDouble)
	{
		NBTTagList var2 = new NBTTagList();
		double[] var3 = par1ArrayOfDouble;
		int var4 = par1ArrayOfDouble.length;

		for (int var5 = 0; var5 < var4; ++var5)
		{
			double var6 = var3[var5];
			var2.appendTag(new NBTTagDouble((String)null, var6));
		}

		return var2;
	}
}
