/*
 *   TTF2BMF
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
package com.ancevt.d2d2.ttf2bmf;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainReadTest {

	public static void main(String[] args) {
		try {
			final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File("out/consola.bmf")));
			
			final int metaSize = dataInputStream.readUnsignedShort();
			
			System.out.println("metaSize: " + metaSize);
			
			int metaSizeCounter = 0;
			
			while(dataInputStream.available() > 0) {
				final StringBuilder sb = new StringBuilder();
				
				sb.append("char: " 		+ dataInputStream.readChar() + " ");
				sb.append("x: " 		+ dataInputStream.readUnsignedShort() + " ");
				sb.append("y: " 		+ dataInputStream.readUnsignedShort() + " ");
				sb.append("width: " 	+ dataInputStream.readUnsignedShort() + " ");
				sb.append("height: " 	+ dataInputStream.readUnsignedShort() + " ");
				
				metaSizeCounter += Character.BYTES;
				metaSizeCounter += Short.BYTES * 4;

				System.out.println(sb.toString());
				
				if(metaSizeCounter >= metaSize) break;
			}
			
			dataInputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
