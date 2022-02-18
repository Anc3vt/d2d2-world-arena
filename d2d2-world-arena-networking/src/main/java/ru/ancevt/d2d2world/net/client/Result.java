/*
 *   D2D2 World Arena Networking
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
package ru.ancevt.d2d2world.net.client;

public class Result<T> {

    private final ErrorInfo errorInfo;
    private final T data;

    private Result(T data) {
        this.errorInfo = null;
        this.data = data;
    }

    private Result(ErrorInfo error) {
        this.errorInfo = error;
        this.data = null;
    }

    public boolean isError() {
        return errorInfo != null;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public T getData() {
        return data;
    }

    public static <T> Result<T> of(ErrorInfo error) {
        return new Result<T>(error);
    }

    public static <T> Result<T> of(T data) {
        return new Result<T>(data);
    }

}
