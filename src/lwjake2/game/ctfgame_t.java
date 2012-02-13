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

public class ctfgame_t {
	int team1, team2;
	int total1, total2; // these are only set when going into intermission!
	float last_flag_capture;
	int last_capture_team;
	
	int match; // match state
	float matchtime; // time for match start/end (depends on state)
	int lasttime; // last time update
	
	int election; // election type
	edict_t etarget; // for admin election, who's being elected
	char elevel[] = new char[32]; // for map election, target level
	int evotes; // votes so far
	int needvotes; // votes needed
	float electtime; // remaining time until election times out
	char emsg[] = new char[256]; // election name
	
	ghost_t ghosts[] = new ghost_t[Defines.MAX_CLIENTS];
}
