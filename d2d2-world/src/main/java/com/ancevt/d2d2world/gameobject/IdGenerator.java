package com.ancevt.d2d2world.gameobject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IdGenerator {

    private static IdGenerator instance;

    public static IdGenerator getInstance() {
        return instance == null ? instance = new IdGenerator() : instance;
    }

    private final List<Integer> ids;

    private IdGenerator() {
        ids = new CopyOnWriteArrayList<>();
    }

    public synchronized int getNewId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if(!ids.contains(i)) {
                ids.add(i);
                return i;
            }
        }
        throw new IllegalStateException("All ids taken");
    }

    public void clear() {
        ids.clear();
    }

    public void addId(int id) {
        ids.add(id);
    }

    public void removeId(int id) {
        ids.removeAll(List.of(id));
    }

    public boolean contains(int id) {
        return ids.contains(id);
    }


}
