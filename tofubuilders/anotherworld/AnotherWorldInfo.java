package tofubuilders.anotherworld;

public class AnotherWorldInfo {
	protected String name;
	protected long seed;
	protected int provider;
	public boolean scheduleRemove = false;

	public AnotherWorldInfo(String name, long seed, int provider){
		this.name = name;
		this.seed = seed;
		this.provider = provider;
	}
}
