package tofubuilders.anotherworld;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.relauncher.FMLInjectionData;

public class AnotherWorldManager {
	private static final AnotherWorldManager instance = new AnotherWorldManager();
	//TreeMapは自動ソート
	private TreeMap<Integer, AnotherWorldInfo> dims = new TreeMap();
	private Map<String, Point> movepoints = new HashMap();
	private List<String> names = new ArrayList();
	
	private List<Integer> scheduledRemoveDimensions = new ArrayList();
	
	private AnotherWorldManager(){
		
	}
	
	public void addDimensionInfo(int dimension, String name, long seed, int provider){
		names.add(name);
		dims.put(dimension, new AnotherWorldInfo(name,seed,provider));
	}
	
	protected Map getDimensionInformations(){
		return dims;
	}
	
	public void registerDimension(int dimension, int providerType){
		int provider = AnotherWorld.fakePSID;
		switch(providerType){
		case 1:
			provider = AnotherWorld.fakePHID;
			break;
		case 2:
			provider = AnotherWorld.fakePEID;
			break;
		case 3:
			provider = AnotherWorld.flowID;
		}
		DimensionManager.registerDimension(dimension, provider);
	}
	
	public boolean existDimensionName(String string) {
		return names.contains(string);
	}
	
	public long getSeed(int dim){
		if(!dims.containsKey(dim))
			return 0;
		return dims.get(dim).seed;
	}

	public String getName(int dim){
		if(!dims.containsKey(dim))
			return "";
		String name = dims.get(dim).name;
		return name;
	}
	
	public Point getMovePoint(String str){
		if(movepoints.containsKey(str))
			return movepoints.get(str).copy();
		return null;
	}
	
	public File getDirectory(){
		File saves = new File((File)FMLInjectionData.data()[6], "saves");
		File dir = new File(saves, MinecraftServer.getServer().getFolderName() + "/AnotherWorld");
		if(!dir.exists())
			dir.mkdirs();
		return dir;
	}
	
	public void createMovePoint(String str, Point p){
		movepoints.put(str, p);
		NBTTagList tag = new NBTTagList();
		for(Iterator i = movepoints.entrySet().iterator(); i.hasNext();){
			Map.Entry entry = (Map.Entry)i.next();
			Point pt = (Point)entry.getValue();
			NBTTagCompound nbt = pt.getNBT();
			nbt.setString("Name", (String)entry.getKey());
			tag.appendTag(nbt);
			}
		File cfg = new File(getDirectory(), "MovePoints.bin");
		try{
		cfg.createNewFile();
		if(!cfg.isFile())
			return;
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(cfg));
		NBTBase.writeNamedTag(tag, dos);
		dos.close();
		}catch(IOException e){
			System.out.println("File Error Caused By : MovePoint Set");
		}
	}
	
	public static AnotherWorldManager instance(){
		return instance;
	}
	
	protected void initialize(){
		dims = new TreeMap();
		movepoints = new HashMap();
		names = new ArrayList();
	}
	
	protected void unloadWorlds(){
		for(int dim : scheduledRemoveDimensions){
			DimensionManager.unloadWorld(dim);
			DimensionManager.unregisterDimension(dim);
		}
	}
	
	public void scheduleUnloadWorld(){
		scheduledRemoveDimensions = new ArrayList(dims.keySet());
	}
	
	protected void scheduleDeleteWorld(int dim){
		if(dims.containsKey(dim))
			dims.get(dim).scheduleRemove = true;
	}
	
	protected void load(){
		File dir = getDirectory();
		File cfg = new File(dir, "WorldInfo.bin");
		try{
			loadWorldsFromFile(cfg);
		}catch(IOException e){
		}
		cfg = new File(dir, "MovePoints.bin");
		try{
			loadMovepointsFromFile(cfg);
		}catch(IOException e){
		}
		
	}
	
	private void loadWorldsFromFile(File f) throws IOException{
		NBTTagList tag;
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		tag = (NBTTagList)NBTBase.readNamedTag(dis);
		dis.close();
		for(int i = 0; i < tag.tagCount(); i++){
			NBTTagCompound nbt = (NBTTagCompound)tag.tagAt(i);
			//skip if remove scheduled
			if(nbt.getBoolean("Remove"))
				continue;
			addDimensionInfo(nbt.getInteger("Dimension"), nbt.getString("Name"), nbt.getLong("Seed"), nbt.getInteger("Provider"));
			registerDimension(nbt.getInteger("Dimension"), nbt.getInteger("Provider"));
		}
	}
	
	private void loadMovepointsFromFile(File f) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		NBTTagList tag = (NBTTagList)NBTBase.readNamedTag(dis);
		dis.close();
		for(int i = 0; i < tag.tagCount(); i++){
			NBTTagCompound nbt = (NBTTagCompound)tag.tagAt(i);
			movepoints.put(nbt.getString("Name"), Point.createPointFromNBT(nbt));
		}
	}

	public void save() throws IOException{
		NBTTagList tag = new NBTTagList();
		for(Iterator i = dims.entrySet().iterator(); i.hasNext();){
			Map.Entry entry = (Map.Entry)i.next();
			AnotherWorldInfo info = (AnotherWorldInfo)entry.getValue();
			int dim = (Integer)entry.getKey();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Name", info.name);
			nbt.setLong("Seed", info.seed);
			nbt.setInteger("Provider", info.provider);
			nbt.setInteger("Dimension", dim);
			nbt.setBoolean("Remove", info.scheduleRemove);
			tag.appendTag(nbt);
		}
		File dir = getDirectory();
		File cfg = new File(dir, "WorldInfo.bin");
		cfg.createNewFile();
		if(!cfg.isFile())
			return;
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(cfg));
		NBTBase.writeNamedTag(tag, dos);
		dos.close();
	}

	

}
