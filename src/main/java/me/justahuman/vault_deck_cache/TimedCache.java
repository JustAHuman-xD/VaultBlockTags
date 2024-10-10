package me.justahuman.vault_deck_cache;

public class TimedCache<V> {
    private final V value;
    private long lastAccessed = System.currentTimeMillis();

    public TimedCache(V value) {
        this.value = value;
    }

    public V value() {
        this.lastAccessed = System.currentTimeMillis();
        return this.value;
    }

    public boolean expired() {
        return System.currentTimeMillis() - this.lastAccessed > 60000;
    }
}
