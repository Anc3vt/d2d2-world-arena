/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2world.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;
import com.ancevt.commons.unix.UnixDisplay;

public class LogInterceptor extends TurboFilter {


    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String s, Object[] objects, Throwable throwable) {
        boolean logChanged = false;

        if (s != null && s.contains("<>")) {
            s = UnixDisplay.colorize(s);
            logChanged = true;
        }

        if (logChanged) {
            switch (level.levelInt) {
                case Level.TRACE_INT -> logger.trace(s, objects);
                case Level.INFO_INT -> logger.info(s, objects);
                case Level.WARN_INT -> logger.warn(s, objects);
                case Level.DEBUG_INT -> logger.debug(s, objects);
                case Level.ERROR_INT -> logger.error(s, objects);
            }
            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;
    }
}


