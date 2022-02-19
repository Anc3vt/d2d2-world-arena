/*
 *   D2D2 World Arena Server
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.ancevt.d2d2world.server;

import static ru.ancevt.d2d2world.server.Config.SERVER_LOOP_DELAY;
import static ru.ancevt.d2d2world.server.ModuleContainer.modules;

public class ServerTimer {

    private long tickCounter;
    private int interval;
    private boolean active;
    private ServerTimerListener timerListener;

    ServerTimer() {
        setInterval(modules.get(Config.class).getInt(SERVER_LOOP_DELAY, 1));
    }

    public void start() {
        if (active) throw new IllegalStateException("Timer is already started");

        active = true;
        new Thread(() -> {
            while (active) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }

                if (active && timerListener != null) {
                    timerListener.globalTimerTick(++tickCounter);
                    if (tickCounter == Long.MAX_VALUE) tickCounter = 0;
                }
            }
        }, "timerThread").start();
    }

    public void stop() {
        active = false;
        tickCounter = 0;
    }

    public long getTickCounter() {
        return tickCounter;
    }

    public void resetTickCounter() {
        tickCounter = 0;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public void setTimerListener(ServerTimerListener timerListener) {
        this.timerListener = timerListener;
    }

    public ServerTimerListener getTimerListener() {
        return timerListener;
    }
}