package tk.baumi.test;

public abstract class Entity {
	public abstract Object[] properties();
	public abstract void insert(Object[] properties);
	public Entity() {
	}
}
