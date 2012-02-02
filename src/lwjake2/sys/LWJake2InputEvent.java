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

package lwjake2.sys;

import java.awt.AWTEvent;

/**
 * LWJake2InputEvent
 */
class LWJake2InputEvent {
	static final int KeyPress = 0;
	static final int KeyRelease = 1;
	static final int MotionNotify = 2;
	static final int ButtonPress = 3;
	static final int ButtonRelease = 4;
	static final int CreateNotify = 5;
	static final int ConfigureNotify = 6;
	static final int WheelMoved = 7;
	int type;
	AWTEvent ev;
	
	LWJake2InputEvent(int type, AWTEvent ev) {
		this.type = type;
		this.ev = ev;
	}
}
