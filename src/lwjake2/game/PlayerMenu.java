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

package lwjake2.game;

import lwjake2.Defines;
import lwjake2.qcommon.Com;
import lwjake2.util.Vargs;

public class PlayerMenu {
	
	public static final int PMENU_ALIGN_LEFT = 0;
	public static final int PMENU_ALIGN_CENTER = 1;
	public static final int PMENU_ALIGN_RIGHT = 2;
	
	// typedef void (*SelectFunc_t)(edict_t *ent, pmenuhnd_t *hnd);
	
	public static pmenuhnd_t PMenu_Open(edict_t ent, pmenu_t[] entries, int cur, int num, String arg) {
		pmenuhnd_t hnd = new pmenuhnd_t();
		pmenu_t[] p;
		int i;
		
		if (ent.client == null)
			return null;
		
		if (ent.client.menu != null) {
			GameBase.gi.dprintf("warning, end already has a menu\n");
			PMenu_Close(ent);
		}
		
		hnd.arg = arg;
		hnd.entries = entries;
		hnd.num = num;
		
		if (cur < 0 || entries[cur].SelectFunc == null) {
			for (i = 0, p = entries; i < num; i++, p++)
				if (p[i].SelectFunc != null)
					break;
		} else
			i = cur;
		
		if (i >= num)
			hnd.cur = -1;
		else
			hnd.cur = i;
		
		ent.client.showscores = true;
		ent.client.inmenu = true;
		ent.client.menu = hnd;
		
		PMenu_Do_Update(ent);
		GameBase.gi.unicast(ent, true);
		
		return hnd;
	}
	
	public static void PMenu_Close(edict_t ent) {
		ent.client.menu = null;
		ent.client.showscores = false;
	}
	
	public static void PMenu_UpdateEntry(pmenu_t entry, String text, int align, SelectFunc_t SelectFunc) {
		if (entry.text != null)
			entry.text = null;
		entry.text = text;
		entry.align = align;
		entry.SelectFunc = SelectFunc;
	}
	
	public static void PMenu_Do_Update(edict_t ent) {
		String string;
		int i;
		pmenu_t[] p;
		int x;
		pmenuhnd_t hnd;
		String t;
		boolean alt = false;
		
		if (ent.client.menu == null) {
			GameBase.gi.dprintf("warning:  ent has no menu\n");
			return;
		}
		
		hnd = ent.client.menu;
		
		string = "xv 32 yv 8 picn inventory";
		
		for (i = 0, p = hnd.entries; i < hnd.num; i++) {
			if (p[i].text == null)
				continue; // blank line
			t = p[i].text;
			if (t.equals('*')) {
				alt = true;
				// t++; Pointer increment? How, what, I don't even - flibit
			}
			Com.sprintf("yv %d ", new Vargs().add(32 + i * 8));
			if (p[i].align == PMENU_ALIGN_CENTER)
				x = 196/2 - t.length() * 4 + 64;
			else if (p[i].align == PMENU_ALIGN_RIGHT)
				x = 64 + 196 - t.length() * 8;
			else
				x = 64;
			
			Com.sprintf("yv %d",
				new Vargs().add(x - ((hnd.cur == i) ? 8 : 0)));
			
			if (hnd.cur == i)
				string = "string2 \"\r%s\" " + t;
			else if (alt)
				string = "string2 \"%s\" " + t;
			else
				string = "string \"%s\" " + t;
			alt = false;
		}
		GameBase.gi.WriteByte(Defines.svc_layout);
		GameBase.gi.WriteString(string);
	}
	
	public static void PMenu_Update(edict_t ent) {
		if (ent.client.menu == null) {
			GameBase.gi.dprintf("warning:  end has no menu\n");
			return;
		}
		
		if (GameBase.level.time - ent.client.menutime >= 1.0) {
			// been a second or more since last update, update now
			PMenu_Do_Update(ent);
			GameBase.gi.unicast(ent, true);
			ent.client.menutime = GameBase.level.time;
			ent.client.menudirty = false;
		}
		ent.client.menutime = GameBase.level.time + 0.2f;
		ent.client.menudirty = true;
	}
	
	public static void PMenu_Next(edict_t ent) {
		pmenuhnd_t hnd;
		int i;
		pmenu_t[] p;
		
		if (ent.client.menu == null) {
			GameBase.gi.dprintf("warning:  ent has no menu\n");
			return;
		}
		
		hnd = ent.client.menu;
		
		if (hnd.cur < 0)
			return; // no selectable entries
		
		i = hnd.cur;
		p = hnd.entries;
		do {
			i++;
			if (i == hnd.num) {
				i = 0;
				p = hnd.entries;
			}
			if (p[i].SelectFunc != null)
				break;
		} while (i != hnd.cur);
		
		hnd.cur = i;
		
		PMenu_Update(ent);
	}
	
	public static void PMenu_Prev(edict_t ent) {
		pmenuhnd_t hnd;
		int i;
		pmenu_t[] p;
		
		if (ent.client.menu == null) {
			GameBase.gi.dprintf("warning:  ent has no menu\n");
			return;
		}
		
		hnd = ent.client.menu;
		
		if (hnd.cur < 0)
			return; // no selectable entries
		
		i = hnd.cur;
		p = hnd.entries;
		do {
			if (i == 0)
				i = hnd.num - 1;
			else
				i--;
			if (p[i].SelectFunc != null)
				break;
		} while (i != hnd.cur);
		
		hnd.cur = i;
		PMenu_Update(ent);
	}
	
	public static void PMenu_Select(edict_t ent) {
		pmenuhnd_t hnd;
		pmenu_t[] p;
		
		if (ent.client.menu == null) {
			GameBase.gi.dprintf("warning:  ent has no menu\n");
			return;
		}
		
		hnd = ent.client.menu;
		
		if (hnd.cur < 0)
			return; // no selectable entries
		
		p = hnd.entries;
		
		if (p[hnd.cur].SelectFunc != null)
			p[hnd.cur].SelectFunc;
	}
}