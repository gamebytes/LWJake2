/*
 * Copyright (C) 1997-2001 Id Software, Inc.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package lwjake2.render;

import lwjake2.client.refexport_t;

import java.util.Vector;

/**
 * Renderer
 * 
 * @author cwei
 */
public class Renderer {

	static Vector<Ref> drivers = new Vector<Ref>(1);

	static {
		try {
			try {
				Class.forName("org.lwjgl.opengl.GL11");
				Class.forName("lwjake2.render.LWJGLRenderer");
			} catch (ClassNotFoundException e) {
				// ignore the lwjgl driver if runtime not in classpath
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	};

	public static void register(Ref impl) {
		if (impl == null) {
			throw new IllegalArgumentException("Ref implementation can't be null");
		}
		if (!drivers.contains(impl)) {
			drivers.add(impl);
		}
	}

	/**
	 * Factory method to get the Renderer implementation.
	 * @return refexport_t (Renderer singleton)
	 */
	public static refexport_t getDriver(String driverName) {
		// find a driver
		Ref driver = null;
		int count = drivers.size();
		for (int i = 0; i < count; i++) {
			driver = drivers.get(i);
			if (driver.getName().equals(driverName)) {
				return driver.GetRefAPI();
			}
		}
		// null if driver not found
		return null;
	}
	
	public static String getDefaultName() {
		return (drivers.isEmpty()) ? null : (drivers.firstElement()).getName();
	}

	public static String getPreferedName() {
		return (drivers.isEmpty()) ? null :  (drivers.lastElement()).getName();
	}

	public static String[] getDriverNames() {
		if (drivers.isEmpty()) return null;
		int count = drivers.size();
		String[] names = new String[count];
		for (int i = 0; i < count; i++) {
			names[i] = (drivers.get(i)).getName();
		}
		return names;
	}
}