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


