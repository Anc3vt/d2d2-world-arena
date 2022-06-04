/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
