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
package com.ancevt.d2d2world.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Properties {

    public static Object setProperties(Object object, @NotNull DataEntry dataEntry) {
        try {
            for (int i = 0; i < dataEntry.size(); i++) {
                String key = dataEntry.getString(i);

                Method method = getSetter(object, key);

                if (method == null) continue;

                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> type = parameterTypes[0];

                if (type == long.class) {
                    method.invoke(object, dataEntry.getLong(key));
                } else if (type == int.class) {
                    method.invoke(object, dataEntry.getInt(key));
                } else if (type == float.class) {
                    method.invoke(object, dataEntry.getFloat(key));
                }else if (type == double.class) {
                    method.invoke(object, dataEntry.getFloat(key));
                } else if (type == boolean.class) {
                    method.invoke(object, dataEntry.getBoolean(key));
                } else if (type == String.class) {
                    method.invoke(object, dataEntry.getString(key));
                }
            }

            return object;
        } catch (InvocationTargetException | IllegalAccessException e) {
            printStackTrace(e);

            throw new IllegalStateException(e);
        }
    }

    private static void printStackTrace(Throwable t) {
        System.out.println(t);
        for (StackTraceElement ste : t.getStackTrace()) {
            System.out.println("\tat " + ste);
        }
        Throwable cause = t.getCause();
        if (cause != null) {
            System.out.print("Caused by ");
            printStackTrace(cause);
        }
    }

    public static DataEntry getProperties(Object object) {
        return getProperties(object, DataEntry.newInstance());
    }

    public static DataEntry getProperties(Object object, DataEntry dataEntry) {
        try {
            List<Class<?>> ifaces = getAllInterfaces(object);
            for (Class<?> iface : ifaces) {
                for (Method method : iface.getDeclaredMethods()) {
                    Class<?> type = method.getReturnType();
                    if (!method.isAnnotationPresent(Property.class) || type == void.class) continue;
                    String property = methodNameToProperty(method.getName());
                    if (!dataEntry.containsKey(property)) {
                        dataEntry.add(property, method.invoke(object));
                    }
                }
            }

            List<Method> methods = getAllObjectMethods(object);
            for (Method method : methods) {
                Class<?> type = method.getReturnType();
                if (!method.isAnnotationPresent(Property.class) || type == void.class) continue;
                String property = methodNameToProperty(method.getName());

                if (!dataEntry.containsKey(property)) {
                    dataEntry.add(property, method.invoke(object));
                }
            }

            return dataEntry;

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static @Nullable Method getSetter(Object object, String property) {
        List<Class<?>> ifaces = getAllInterfaces(object);
        for (Class<?> iface : ifaces) {
            for (Method method : iface.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Property.class) &&
                        method.getName().equals(propertyToSetterMethodName(property))) {
                    return method;
                }
            }
        }

        List<Method> methods = getAllObjectMethods(object);
        for (Method method : methods) {
            if (method.isAnnotationPresent(Property.class)) {
                if (method.getName().equals(propertyToSetterMethodName(property))) {
                    return method;
                }
            }
        }

        return null;
    }

    private static @NotNull List<Class<?>> getAllInterfaces(@NotNull Object object) {
        List<Class<?>> ifaces = new ArrayList<>();
        collectAllInterfaces(ifaces, object.getClass());
        return ifaces;
    }

    private static void collectAllInterfaces(@NotNull List<Class<?>> ifaces, @NotNull Class<?> clazz) {
        List<Class<?>> currentInterfaces = Arrays.stream(clazz.getInterfaces()).toList();
        ifaces.addAll(currentInterfaces);
        currentInterfaces.forEach(c -> collectAllInterfaces(ifaces, c));
        if (clazz.getSuperclass() != null)
            collectAllInterfaces(ifaces, clazz.getSuperclass());
    }

    private static @NotNull List<Method> getAllObjectMethods(@NotNull Object object) {
        List<Method> methods = new ArrayList<>();
        collectAllObjectMethods(methods, object.getClass());
        return methods;
    }

    private static void collectAllObjectMethods(@NotNull List<Method> methods, @NotNull Class<?> clazz) {
        List<Method> currentMethods = Arrays.stream(clazz.getMethods()).toList();
        methods.addAll(currentMethods);
        if (clazz.getSuperclass() != Object.class) collectAllObjectMethods(methods, clazz.getSuperclass());
    }

    private static String methodNameToProperty(String methodName) {
        return firstCharLowerCase(removeMethodAccessor(methodName));
    }

    private static @NotNull String propertyToSetterMethodName(String propertyName) {
        return addSetterMethodAccessor(propertyName);
    }

    private static String removeMethodAccessor(String methodName) {
        if (isNotEmptyWithTrim(methodName)) {
            if (methodName.startsWith("get")) {
                return methodName.length() > 3 ? methodName.substring(3) : methodName;
            } else if (methodName.startsWith("is")) {
                return methodName.length() > 2 ? methodName.substring(2) : methodName;
            } else if (methodName.startsWith("set")) {
                return methodName.length() > 3 ? methodName.substring(3) : methodName;
            }
        }
        return methodName;
    }

    private static @NotNull String addSetterMethodAccessor(String methodName) {
        return "set" + firstCharUpperCase(methodName);
    }

    private static String firstCharLowerCase(String methodName) {
        if (isNotEmptyWithTrim(methodName)) {
            return Character.toLowerCase(methodName.charAt(0))
                    + (methodName.length() > 1 ? methodName.substring(1) : "");
        }
        return methodName;
    }

    private static String firstCharUpperCase(String methodName) {
        if (isNotEmptyWithTrim(methodName)) {
            return Character.toUpperCase(methodName.charAt(0))
                    + (methodName.length() > 1 ? methodName.substring(1) : "");
        }
        return methodName;
    }

    private static boolean isNotEmptyWithTrim(String s) {
        return !isEmptyWithTrim(s);
    }

    private static boolean isEmptyWithTrim(String s) {
        return s == null || s.trim().length() == 0;
    }

}
