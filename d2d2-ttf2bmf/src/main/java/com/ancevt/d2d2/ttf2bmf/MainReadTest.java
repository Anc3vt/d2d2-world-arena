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
