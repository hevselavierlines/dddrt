package tk.baumi.ddd;

public abstract class Entity {

    public abstract Object[] properties();

    public abstract void insert(Object[] properties);
}