package tofubuilders.anotherworld;

import net.minecraft.nbt.NBTTagCompound;

public class Point {
	public int dimension;
	public double x, y, z;
	
	private Point(){}

	public Point(int dimension, double x, double y, double z){
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public NBTTagCompound getNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Dimension", dimension);
		nbt.setDouble("x", x);
		nbt.setDouble("y", y);
		nbt.setDouble("z", z);
		return nbt;
	}
	
	public static Point createPointFromNBT(NBTTagCompound nbt){
		Point p = new Point();
		p.dimension = nbt.getInteger("Dimension");
		p.x = nbt.getDouble("x");
		p.y = nbt.getDouble("y");
		p.z = nbt.getDouble("z");
		return p;
	}
	
	public Point copy(){
		Point p = new Point(dimension, x, y, z);
		return p;
	}

}
