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

/* These are the files we need to add to LWJake2, from ctf/ - flibit
game.h
g_cmds.c
g_combat.c
g_items.c
g_main.c
g_misc.c
g_save.c
g_spawn.c
g_weapon.c
p_client.c
p_hud.c
p_view.c
p_weapon.c
q_shared.h
git clone https://github.com/id-Software/Quake-2.git
*/

package lwjake2.game;

import lwjake2.Defines;
import lwjake2.Globals;
import lwjake2.qcommon.Com;
import lwjake2.util.Lib;
import lwjake2.util.Math3D;
import lwjake2.util.Vargs;

public class CTF {
	public static final String CTF_VERSION = "1.09b";
	public static final int STAT_CTF_TEAM1_PIC = 17;
	public static final int STAT_CTF_TEAM1_CAPS = 18;
	public static final int STAT_CTF_TEAM2_PIC = 19;
	public static final int STAT_CTF_TEAM2_CAPS = 20;
	public static final int STAT_CTF_FLAG_PIC = 21;
	public static final int STAT_CTF_JOINED_TEAM1_PIC = 22;
	public static final int STAT_CTF_JOINED_TEAM2_PIC = 23;
	public static final int STAT_CTF_TEAM1_HEADER = 24;
	public static final int STAT_CTF_TEAM2_HEADER = 25;
	public static final int STAT_CTF_TECH = 26;
	public static final int STAT_CTF_ID_VIEW = 27;
	public static final int STAT_CTF_MATCH = 28;
	public static final int CONFIG_CTF_MATCH = Defines.CS_MAXCLIENTS - 1;
	
	public static final int CTF_NOTEAM = 0;
	public static final int CTF_TEAM1 = 1;
	public static final int CTF_TEAM2 = 2;
	
	public static final int CTF_GRAPPLE_STATE_FLY = 0;
	public static final int CTF_GRAPPLE_STATE_PULL = 1;
	public static final int CTF_GRAPPLE_STATE_HANG = 2;
	
	public static final String CTF_TEAM1_SKIN = "ctf_r";
	public static final String CTF_TEAM2_SKIN = "ctf_b";
	
	public static final int DF_CTF_FORCEJOIN = 131072;
	public static final int DF_ARMOR_PROTECT = 262144;
	public static final int DF_CTF_NO_TECH = 524288;
	
	public static final int CTF_CAPTURE_BONUS = 15; // what you get for capture
	public static final int CTF_TEAM_BONUS = 10; // what your team gets for capture
	public static final int CTF_RECOVERY_BONUS = 1; // what you get for recovery
	public static final int CTF_FLAG_BONUS = 0; // what you get for picking up enemy flag
	public static final int CTF_FRAG_CARRIER_BONUS = 2; // what you get for fragging enemy flag carrier
	public static final int CTF_FLAG_RETURN_TIME = 40; // seconds until auto return
	
	public static final int CTF_CARRIER_DANGER_PROTECT_BONUS = 2; // bonus for fraggin someone who has recently hurt your flag carrier
	public static final int CTF_CARRIER_PROTECT_BONUS = 1; // bonus for fraggin someone while either you or your target are near your flag carrier
	public static final int CTF_FLAG_DEFENSE_BONUS = 1; // bonus for fraggin someone while either you or your target are near your flag
	public static final int CTF_RETURN_FLAG_ASSIST_BONUS = 1; // awarded for returning a flag that causes a capture to happen almost immediately
	public static final int CTF_FRAG_CARRIER_ASSIST_BONUS = 2; // award for fragging a flag carrier if a capture happens almost immediately
	
	public static final int CTF_TARGET_PROTECT_RADIUS = 400; // the radius around an object being defended where a target will be worth extra frags
	public static final int CTF_ATTACKER_PROTECT_RADIUS = 400; // the radius around an object being defended where an attacker will get extra frags when making kills
	
	public static final int CTF_CARRIER_DANGER_PROTECT_TIMEOUT = 8;
	public static final int CTF_FRAG_CARRIER_ASSIST_TIMEOUT = 10;
	public static final int CTF_RETURN_FLAG_ASSIST_TIMEOUT = 10;

	public static final int CTF_AUTO_FLAG_RETURN_TIMEOUT = 30; // number of seconds before dropped flag auto-returns

	public static final int CTF_TECH_TIMEOUT = 60; // seconds before techs spawn again

	public static final int CTF_GRAPPLE_SPEED = 650; // speed of grapple in flight
	public static final int CTF_GRAPPLE_PULL_SPEED = 650; // speed player is pulled at
	
	// match_t
	public static final int MATCH_NONE = 0;
	public static final int MATCH_SETUP = 1;
	public static final int MATCH_PREGAME = 2;
	public static final int MATCH_GAME = 3;
	public static final int MATCH_POST = 4;
	
	// elect_t
	public static final int ELECT_NONE = 0;
	public static final int ELECT_MATCH = 1;
	public static final int ELECT_ADMIN = 2;
	public static final int ELECT_MAP = 3;
	
	public static ctfgame_t ctfgame;
	public static cvar_t ctf = new cvar_t();
	public static cvar_t ctf_forcejoin = new cvar_t();

	public static cvar_t competition = new cvar_t();
	public static cvar_t matchlock = new cvar_t();
	public static cvar_t electpercentage = new cvar_t();
	public static cvar_t matchtime = new cvar_t();
	public static cvar_t matchsetuptime = new cvar_t();
	public static cvar_t matchstarttime = new cvar_t();
	public static cvar_t admin_password = new cvar_t();
	public static cvar_t warp_list = new cvar_t();
	
	public static final String ctf_statusbar =
		"yb	-24 " +

		// health
		"xv	0 " +
		"hnum " +
		"xv	50 " +
		"pic 0 " +

		// ammo
		"if 2 " +
		"	xv	100 " +
		"	anum " +
		"	xv	150 " +
		"	pic 2 " +
		"endif " +

		// armor
		"if 4 " +
		"	xv	200 " +
		"	rnum " +
		"	xv	250 " +
		"	pic 4 " +
		"endif " +

		// selected item
		"if 6 " +
		"	xv	296 " +
		"	pic 6 " +
		"endif " +

		"yb	-50 " +

		// picked up item
		"if 7 " +
		"	xv	0 " +
		"	pic 7 " +
		"	xv	26 " +
		"	yb	-42 " +
		"	stat_string 8 " +
		"	yb	-50 " +
		"endif " +

		// timer
		"if 9 " +
		  "xv 246 " +
		  "num 2 10 " +
		  "xv 296 " +
		  "pic 9 " +
		"endif " +

		//  help / weapon icon 
		"if 11 " +
		  "xv 148 " +
		  "pic 11 " +
		"endif " +

		//  frags
		"xr	-50 " +
		"yt 2 " +
		"num 3 14 " +

		//tech
		"yb -129 " +
		"if 26 " +
		  "xr -26 " +
		  "pic 26 " +
		"endif " +

		// red team
		"yb -102 " +
		"if 17 " +
		  "xr -26 " +
		  "pic 17 " +
		"endif " +
		"xr -62 " +
		"num 2 18 " +
		//joined overlay
		"if 22 " +
		  "yb -104 " +
		  "xr -28 " +
		  "pic 22 " +
		"endif " +

		// blue team
		"yb -75 " +
		"if 19 " +
		  "xr -26 " +
		  "pic 19 " +
		"endif " +
		"xr -62 " +
		"num 2 20 " +
		"if 23 " +
		  "yb -77 " +
		  "xr -28 " +
		  "pic 23 " +
		"endif " +

		// have flag graph
		"if 21 " +
		  "yt 26 " +
		  "xr -24 " +
		  "pic 21 " +
		"endif " +

		// id view state
		"if 27 " +
		  "xv 0 " +
		  "yb -58 " +
		  "string \"Viewing\" " +
		  "xv 64 " +
		  "stat_string 27 " +
		"endif " +

		"if 28 " +
		  "xl 0 " +
		  "yb -78 " +
		  "stat_string 28 " +
		"endif "
	;
	
	public static final String tnames[] = {
		"item_tech1", "item_tech2", "item_tech3", "item_tech4",
		null
	};
	
	public static void stuffcmd(edict_t ent, String s) {
		GameBase.gi.WriteByte(11);
		GameBase.gi.WriteString(s);
		GameBase.gi.unicast(ent, true);
	}
	
	/*--------------------------------------------------------------------------*/

	/*
	=================
	findradius

	Returns entities that have origins within a spherical area

	findradius (origin, radius)
	=================
	*/
	public static edict_t[] loc_findradius(edict_t from[], float[] org, float rad) {
		float[]	eorg = new float[3];
		int j;
		
		
		if (from == null)
			from = GameBase.g_edicts;
		for (int currentEdict = 0; currentEdict < GameBase.g_edicts.length; currentEdict++) {
			if (from[currentEdict].inuse)
				continue;
			for (j = 0; j < 3; j++)
				eorg[j] = org[j] - (from[currentEdict].s.origin[j] + (from[currentEdict].mins[j] + from[currentEdict].maxs[j]) * 0.5f);
			if (Math3D.VectorLength(eorg) > rad)
				continue;
			return from;
		}

		return null;
	}

	static void loc_buildboxpoints(float[][] p, float[] org, float[] mins, float[] maxs) {
		Math3D.VectorAdd(org, mins, p[0]);
		Math3D.VectorCopy(p[0], p[1]);
		p[1][0] -= mins[0];
		Math3D.VectorCopy(p[0], p[2]);
		p[2][1] -= mins[1];
		Math3D.VectorCopy(p[0], p[3]);
		p[3][0] -= mins[0];
		p[3][1] -= mins[1];
		Math3D.VectorAdd(org, maxs, p[4]);
		Math3D.VectorCopy(p[4], p[5]);
		p[5][0] -= maxs[0];
		Math3D.VectorCopy(p[0], p[6]);
		p[6][1] -= maxs[1];
		Math3D.VectorCopy(p[0], p[7]);
		p[7][0] -= maxs[0];
		p[7][1] -= maxs[1];
	}

	static boolean loc_CanSee (edict_t targ, edict_t inflictor) {
		trace_t	trace;
		float[][] targpoints = new float[8][3];
		int i;
		float[] viewpoint = new float[3];

		// bmodels need special checking because their origin is 0,0,0
		if (targ.movetype == Defines.MOVETYPE_PUSH)
			return false; // bmodels not supported

		loc_buildboxpoints(targpoints, targ.s.origin, targ.mins, targ.maxs);
		
		Math3D.VectorCopy(inflictor.s.origin, viewpoint);
		viewpoint[2] += inflictor.viewheight;

		for (i = 0; i < 8; i++) {
			trace = GameBase.gi.trace (viewpoint, Globals.vec3_origin, Globals.vec3_origin, targpoints[i], inflictor, Defines.MASK_SOLID);
			if (trace.fraction == 1.0)
				return true;
		}

		return false;
	}

	/*--------------------------------------------------------------------------*/
	
	public static gitem_t flag1_item;
	public static gitem_t flag2_item;
	
	public static void CTFInit() {
		if (flag1_item == null)
			flag1_item = GameItems.FindItemByClassname("item_flag_team1");
		if (flag2_item == null)
			flag2_item = GameItems.FindItemByClassname("item_flag_team2");
		//memset(&ctfgame, 0, sizeof(ctfgame));
		CTFSetupTechSpawn();

		if (competition.value > 1) {
			ctfgame.match = MATCH_SETUP;
			ctfgame.matchtime = GameBase.level.time + matchsetuptime.value * 60;
		}
	}
	
	public static void CTFSpawn() {
		ctf = GameBase.gi.cvar("ctf", "1", Defines.CVAR_SERVERINFO);
		ctf_forcejoin = GameBase.gi.cvar("ctf_forcejoin", "", 0);
		competition = GameBase.gi.cvar("competition", "0", Defines.CVAR_SERVERINFO);
		matchlock = GameBase.gi.cvar("matchlock", "1", Defines.CVAR_SERVERINFO);
		electpercentage = GameBase.gi.cvar("electpercentage", "66", 0);
		matchtime = GameBase.gi.cvar("matchtime", "20", Defines.CVAR_SERVERINFO);
		matchsetuptime = GameBase.gi.cvar("matchsetuptime", "10", 0);
		matchstarttime = GameBase.gi.cvar("matchstarttime", "20", 0);
		admin_password = GameBase.gi.cvar("admin_password", "", 0);
		warp_list = GameBase.gi.cvar("warp_list", "q2ctf1 q2ctf2 q2ctf3 q2ctf4 q2ctf5", 0);
	}

	/*--------------------------------------------------------------------------*/
	
	/*QUAKED info_player_team1 (1 0 0) (-16 -16 -24) (16 16 32)
	potential team1 spawning position for ctf games
	*/
	public static void SP_info_player_team1(edict_t self) {}
	/*QUAKED info_player_team2 (0 0 1) (-16 -16 -24) (16 16 32)
	potential team2 spawning position for ctf games
	*/
	public static void SP_info_player_team2(edict_t self) {}

	/*--------------------------------------------------------------------------*/
	
	public static String CTFTeamName(int team) {
		switch (team) {
		case CTF_TEAM1:
			return "RED";
		case CTF_TEAM2:
			return "BLUE";
		}
		return "UKNOWN";
	}
	
	public static String CTFOtherTeamName(int team) {
		switch (team) {
		case CTF_TEAM1:
			return "BLUE";
		case CTF_TEAM2:
			return "RED";
		}
		return "UKNOWN";
	}
	
	public static int CTFOtherTeam(int team) {
		switch (team) {
		case CTF_TEAM1:
			return CTF_TEAM2;
		case CTF_TEAM2:
			return CTF_TEAM1;
		}
		return -1; // invalid value
	}
	
	/*--------------------------------------------------------------------------*/
	
	public static void CTFAssignSkin(edict_t ent, String s) {
		int playernum = ent.index - GameBase.g_edicts.length - 1;

		switch (ent.client.resp.ctf_team) {
		case CTF_TEAM1:
			GameBase.gi.configstring(Globals.CS_PLAYERSKINS + playernum,
				ent.client.pers.netname + "\\" + s + CTF_TEAM1_SKIN);
			break;
		case CTF_TEAM2:
			GameBase.gi.configstring(Globals.CS_PLAYERSKINS + playernum,
				ent.client.pers.netname + "\\" + s + CTF_TEAM2_SKIN);
			break;
		default:
			GameBase.gi.configstring(Globals.CS_PLAYERSKINS + playernum, 
				ent.client.pers.netname + "\\" + s);
			break;
		}
		// gi.cprintf(ent, PRINT_HIGH, "You have been assigned to %s team.\n", ent.client.pers.netname);
	}
	
	public static void CTFAssignTeam(gclient_t who) {
		edict_t player;
		int i;
		int team1count = 0, team2count = 0;

		who.resp.ctf_state = 0;

		if (((int) GameBase.dmflags.value & DF_CTF_FORCEJOIN) == 0) {
			who.resp.ctf_team = CTF_NOTEAM;
			return;
		}

		for (i = 1; i <= GameBase.maxclients.value; i++) {
			player = GameBase.g_edicts[i];

			if (!player.inuse || player.client == who)
				continue;

			switch (player.client.resp.ctf_team) {
			case CTF_TEAM1:
				team1count++;
				break;
			case CTF_TEAM2:
				team2count++;
			}
		}
		if (team1count < team2count)
			who.resp.ctf_team = CTF_TEAM1;
		else if (team2count < team1count)
			who.resp.ctf_team = CTF_TEAM2;
		else if (Math.random() > 0.5)
			who.resp.ctf_team = CTF_TEAM1;
		else
			who.resp.ctf_team = CTF_TEAM2;
	}
	
	/*
	================
	SelectCTFSpawnPoint

	go to a ctf point, but NOT the two points closest
	to other players
	================
	*/
	public static edict_t SelectCTFSpawnPoint (edict_t ent) {
		EdictIterator spot;
		edict_t	spot1, spot2;
		int	count = 0;
		int	selection;
		float range, range1, range2;
		String cname;

		if (ent.client.resp.ctf_state != 0)
			if (((int) GameBase.dmflags.value & Defines.DF_SPAWN_FARTHEST) == 0)
				return PlayerClient.SelectFarthestDeathmatchSpawnPoint();
			else
				return PlayerClient.SelectRandomDeathmatchSpawnPoint();

		ent.client.resp.ctf_state++;

		switch (ent.client.resp.ctf_team) {
		case CTF_TEAM1:
			cname = "info_player_team1";
			break;
		case CTF_TEAM2:
			cname = "info_player_team2";
			break;
		default:
			return PlayerClient.SelectRandomDeathmatchSpawnPoint();
		}

		spot = null;
		range1 = range2 = 99999;
		spot1 = spot2 = null;

		while ((spot = GameBase.G_Find(spot, GameBase.findByClass, cname)) != null) {
			count++;
			range = PlayerClient.PlayersRangeFromSpot(spot.o);
			if (range < range1)
			{
				range1 = range;
				spot1 = spot.o;
			}
			else if (range < range2)
			{
				range2 = range;
				spot2 = spot.o;
			}
		}

		if (count == 0)
			return PlayerClient.SelectRandomDeathmatchSpawnPoint();

		if (count <= 2)
			spot1 = spot2 = null;
		else
			count -= 2;

		selection = (int) (Math.random() * count);

		spot = null;
		do {
			spot = GameBase.G_Find(spot, GameBase.findByClass, cname);
			if (spot.o == spot1 || spot.o == spot2)
				selection++;
		} while (selection-- != 0);

		return spot.o;
	}
	
	/*------------------------------------------------------------------------*/
	
	/*
	CTFFragBonuses

	Calculate the bonuses for flag defense, flag carrier defense, etc.
	Note that bonuses are not cumaltive.  You get one, they are in importance
	order.
	*/
	public static void CTFFragBonuses(edict_t targ, edict_t inflictor, edict_t attacker) {
		int i;
		edict_t ent;
		gitem_t flag_item, enemy_flag_item;
		int otherteam;
		EdictIterator flag;
		edict_t carrier = null;
		String c;
		float[] v1 = null, v2 = null;

		if (targ.client != null && attacker.client != null) {
			if (attacker.client.resp.ghost != null)
				if (attacker != targ)
					attacker.client.resp.ghost.kills++;
			if (targ.client.resp.ghost != null)
				targ.client.resp.ghost.deaths++;
		}

		// no bonus for fragging yourself
		if (targ.client == null || attacker.client == null || targ == attacker)
			return;

		otherteam = CTFOtherTeam(targ.client.resp.ctf_team);
		if (otherteam < 0)
			return; // whoever died isn't on a team

		// same team, if the flag at base, check to he has the enemy flag
		if (targ.client.resp.ctf_team == CTF_TEAM1) {
			flag_item = flag1_item;
			enemy_flag_item = flag2_item;
		} else {
			flag_item = flag2_item;
			enemy_flag_item = flag1_item;
		}

		// did the attacker frag the flag carrier?
		if (targ.client.pers.inventory[GameItems.ITEM_INDEX(enemy_flag_item)] != 0) {
			attacker.client.resp.ctf_lastfraggedcarrier = GameBase.level.time;
			attacker.client.resp.score += CTF_FRAG_CARRIER_BONUS;
			GameBase.gi.cprintf(attacker, Globals.PRINT_MEDIUM,
				"BONUS: " + CTF_FRAG_CARRIER_BONUS + "points for fragging enemy flag carrier.\n");

			// the target had the flag, clear the hurt carrier
			// field on the other team
			for (i = 1; i <= GameBase.maxclients.value; i++) {
				ent = GameBase.g_edicts[i];
				if (ent.inuse && ent.client.resp.ctf_team == otherteam)
					ent.client.resp.ctf_lasthurtcarrier = 0;
			}
			return;
		}

		if (targ.client.resp.ctf_lasthurtcarrier > 0.0f &&
			GameBase.level.time - targ.client.resp.ctf_lasthurtcarrier < CTF_CARRIER_DANGER_PROTECT_TIMEOUT &&
			attacker.client.pers.inventory[GameItems.ITEM_INDEX(flag_item)] == 0) {
			// attacker is on the same team as the flag carrier and
			// fragged a guy who hurt our flag carrier
			attacker.client.resp.score += CTF_CARRIER_DANGER_PROTECT_BONUS;
			GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
				attacker.client.pers.netname + " defends " +
				CTFTeamName(attacker.client.resp.ctf_team) + "'s flag carrier against an agressive enemy\n"
			);
			if (attacker.client.resp.ghost != null)
				attacker.client.resp.ghost.carrierdef++;
			return;
		}

		// flag and flag carrier area defense bonuses

		// we have to find the flag and carrier entities

		// find the flag
		switch (attacker.client.resp.ctf_team) {
		case CTF_TEAM1:
			c = "item_flag_team1";
			break;
		case CTF_TEAM2:
			c = "item_flag_team2";
			break;
		default:
			return;
		}

		flag = null;
		while ((flag = GameBase.G_Find(flag, GameBase.findByClass, c)) != null) {
			if ((flag.o.spawnflags & Defines.DROPPED_ITEM) == 0)
				break;
		}

		if (flag == null)
			return; // can't find attacker's flag

		// find attacker's team's flag carrier
		for (i = 1; i <= GameBase.maxclients.value; i++) {
			carrier = GameBase.g_edicts[i];
			if (carrier.inuse && 
				carrier.client.pers.inventory[GameItems.ITEM_INDEX(flag_item)] != 0)
				break;
			carrier = null;
		}

		// ok we have the attackers flag and a pointer to the carrier

		// check to see if we are defending the base's flag
		Math3D.VectorSubtract(targ.s.origin, flag.o.s.origin, v1);
		Math3D.VectorSubtract(attacker.s.origin, flag.o.s.origin, v2);

		if ((Math3D.VectorLength(v1) < CTF_TARGET_PROTECT_RADIUS ||
				Math3D.VectorLength(v2) < CTF_TARGET_PROTECT_RADIUS ||
				loc_CanSee(flag.o, targ) || loc_CanSee(flag.o, attacker)) &&
				attacker.client.resp.ctf_team != targ.client.resp.ctf_team) {
			// we defended the base flag
			attacker.client.resp.score += CTF_FLAG_DEFENSE_BONUS;
			if (flag.o.solid == Defines.SOLID_NOT)
				GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
					attacker.client.pers.netname + " defends the " +
					CTFTeamName(attacker.client.resp.ctf_team) + " base.\n"
				);
			else
				GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
					attacker.client.pers.netname + " defends the " +
					CTFTeamName(attacker.client.resp.ctf_team) + " flag.\n" 
				);
			if (attacker.client.resp.ghost != null)
				attacker.client.resp.ghost.basedef++;
			return;
		}

		if (carrier != null && carrier != attacker) {
			Math3D.VectorSubtract(targ.s.origin, carrier.s.origin, v1);
			Math3D.VectorSubtract(attacker.s.origin, carrier.s.origin, v1);

			if (Math3D.VectorLength(v1) < CTF_ATTACKER_PROTECT_RADIUS ||
					Math3D.VectorLength(v2) < CTF_ATTACKER_PROTECT_RADIUS ||
				loc_CanSee(carrier, targ) || loc_CanSee(carrier, attacker)) {
				attacker.client.resp.score += CTF_CARRIER_PROTECT_BONUS;
				GameBase.gi.bprintf(Globals.PRINT_MEDIUM,
					attacker.client.pers.netname + " defends the " + 
					CTFTeamName(attacker.client.resp.ctf_team) + "'s flag carrier.\n"
				);
				if (attacker.client.resp.ghost != null)
					attacker.client.resp.ghost.carrierdef++;
				return;
			}
		}
	}

	public static void CTFCheckHurtCarrier(edict_t targ, edict_t attacker) {
		gitem_t flag_item;

		if (targ.client == null || attacker.client == null)
			return;

		if (targ.client.resp.ctf_team == CTF_TEAM1)
			flag_item = flag2_item;
		else
			flag_item = flag1_item;

		if (targ.client.pers.inventory[GameItems.ITEM_INDEX(flag_item)] != 0 &&
			targ.client.resp.ctf_team != attacker.client.resp.ctf_team)
			attacker.client.resp.ctf_lasthurtcarrier = GameBase.level.time;
	}
	
	/*------------------------------------------------------------------------*/

	public static void CTFResetFlag(int ctf_team) {
		String c;
		EdictIterator ent;

		switch (ctf_team) {
		case CTF_TEAM1:
			c = "item_flag_team1";
			break;
		case CTF_TEAM2:
			c = "item_flag_team2";
			break;
		default:
			return;
		}

		ent = null;
		while ((ent = GameBase.G_Find(ent, GameBase.findByClass, c)) != null) {
			if ((ent.o.spawnflags & Defines.DROPPED_ITEM) != 0)
				GameUtil.G_FreeEdict(ent.o);
			else {
				ent.o.svflags &= ~Defines.SVF_NOCLIENT;
				ent.o.solid = Defines.SOLID_TRIGGER;
				GameBase.gi.linkentity(ent.o);
				ent.o.s.event = Defines.EV_ITEM_RESPAWN;
			}
		}
	}
	
	public static void CTFResetFlags() {
		CTFResetFlag(CTF_TEAM1);
		CTFResetFlag(CTF_TEAM2);
	}
	
	public static boolean CTFPickup_Flag(edict_t ent, edict_t other) {
		int ctf_team;
		int i;
		edict_t player;
		gitem_t flag_item, enemy_flag_item;

		// figure out what team this flag is
		if (ent.classname.equals("item_flag_team1"))
			ctf_team = CTF_TEAM1;
		else if (ent.classname.equals("item_flag_team2"))
			ctf_team = CTF_TEAM2;
		else {
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH, "Don't know what team the flag is on.\n");
			return false;
		}

		// same team, if the flag at base, check to he has the enemy flag
		if (ctf_team == CTF_TEAM1) {
			flag_item = flag1_item;
			enemy_flag_item = flag2_item;
		} else {
			flag_item = flag2_item;
			enemy_flag_item = flag1_item;
		}

		if (ctf_team == other.client.resp.ctf_team) {

			if ((ent.spawnflags & Globals.DROPPED_ITEM) == 0) {
				// the flag is at home base.  if the player has the enemy
				// flag, he's just won!
			
				if (other.client.pers.inventory[GameItems.ITEM_INDEX(enemy_flag_item)] != 0) {
					GameBase.gi.bprintf(Globals.PRINT_HIGH,
						other.client.pers.netname + " captured the " +
						CTFOtherTeamName(ctf_team) + " flag!\n"
					);
					other.client.pers.inventory[GameItems.ITEM_INDEX(enemy_flag_item)] = 0;

					ctfgame.last_flag_capture = GameBase.level.time;
					ctfgame.last_capture_team = ctf_team;
					if (ctf_team == CTF_TEAM1)
						ctfgame.team1++;
					else
						ctfgame.team2++;

					GameBase.gi.sound (ent,
						Globals.CHAN_RELIABLE + Globals.CHAN_NO_PHS_ADD + Globals.CHAN_VOICE,
						GameBase.gi.soundindex("ctf/flagcap.wav"), 1, Globals.ATTN_NONE, 0
					);

					// other gets another 10 frag bonus
					other.client.resp.score += CTF_CAPTURE_BONUS;
					if (other.client.resp.ghost != null)
						other.client.resp.ghost.caps++;

					// Ok, let's do the player loop, hand out the bonuses
					for (i = 1; i <= GameBase.maxclients.value; i++) {
						player = GameBase.g_edicts[i];
						if (!player.inuse)
							continue;

						if (player.client.resp.ctf_team != other.client.resp.ctf_team)
							player.client.resp.ctf_lasthurtcarrier = -5;
						else if (player.client.resp.ctf_team == other.client.resp.ctf_team) {
							if (player != other)
								player.client.resp.score += CTF_TEAM_BONUS;
							// award extra points for capture assists
							if (player.client.resp.ctf_lastreturnedflag + CTF_RETURN_FLAG_ASSIST_TIMEOUT > GameBase.level.time) {
								GameBase.gi.bprintf(Globals.PRINT_HIGH,
									player.client.pers.netname + " gets an assist for returning the flag!\n");
								player.client.resp.score += CTF_RETURN_FLAG_ASSIST_BONUS;
							}
							if (player.client.resp.ctf_lastfraggedcarrier + CTF_FRAG_CARRIER_ASSIST_TIMEOUT > GameBase.level.time) {
								GameBase.gi.bprintf(Globals.PRINT_HIGH,
									player.client.pers.netname + " gets an assist for fragging the flag carrier!\n");
								player.client.resp.score += CTF_FRAG_CARRIER_ASSIST_BONUS;
							}
						}
					}

					CTFResetFlags();
					return false;
				}
				return false; // its at home base already
			}	
			// hey, its not home.  return it by teleporting it back
			GameBase.gi.bprintf(Globals.PRINT_HIGH,
				other.client.pers.netname + " returned the " +
				CTFTeamName(ctf_team) + " flag!\n"
			);
			other.client.resp.score += CTF_RECOVERY_BONUS;
			other.client.resp.ctf_lastreturnedflag = GameBase.level.time;
			GameBase.gi.sound (ent,
				Globals.CHAN_RELIABLE + Globals.CHAN_NO_PHS_ADD + Globals.CHAN_VOICE,
				GameBase.gi.soundindex("ctf/flagret.wav"), 1, Globals.ATTN_NONE, 0
			);
			//CTFResetFlag will remove this entity!  We must return false
			CTFResetFlag(ctf_team);
			return false;
		}

		// hey, its not our flag, pick it up
		GameBase.gi.bprintf(Globals.PRINT_HIGH,
			other.client.pers.netname + " got the " +
			CTFTeamName(ctf_team) + " flag!\n"
		);
		other.client.resp.score += CTF_FLAG_BONUS;

		other.client.pers.inventory[GameItems.ITEM_INDEX(flag_item)] = 1;
		other.client.resp.ctf_flagsince = GameBase.level.time;

		// pick up the flag
		// if it's not a dropped flag, we just make is disappear
		// if it's dropped, it will be removed by the pickup caller
		if ((ent.spawnflags & Defines.DROPPED_ITEM) == 0) {
			ent.flags |= Defines.FL_RESPAWN;
			ent.svflags |= Defines.SVF_NOCLIENT;
			ent.solid = Defines.SOLID_NOT;
		}
		return true;
	}
	
	public static EntTouchAdapter CTFDropFlagTouch = new EntTouchAdapter() {
		public String getID() { return "CTFDropFlagTouch"; };
		public void touch(edict_t ent, edict_t other, cplane_t plane, csurface_t surf) {
			//owner (who dropped us) can't touch for two secs
			if (other == ent.owner && 
				ent.nextthink - GameBase.level.time > CTF_AUTO_FLAG_RETURN_TIMEOUT-2)
				return;
	
			GameItems.Touch_Item (ent, other, plane, surf);
		}
	};
	
	public static EntThinkAdapter CTFDropFlagThink = new EntThinkAdapter() {
		public String getID() { return "CTFDropFlagThink"; };
		public boolean think(edict_t ent) {
			// auto return the flag
			// reset flag will remove ourselves
			if (ent.classname.equals("item_flag_team1")) {
				CTFResetFlag(CTF_TEAM1);
				GameBase.gi.bprintf(Globals.PRINT_HIGH,
					"The " + CTFTeamName(CTF_TEAM1) + " flag has returned!\n"
				);
			} else if (ent.classname.equals("item_flag_team2")) {
				CTFResetFlag(CTF_TEAM2);
				GameBase.gi.bprintf(Globals.PRINT_HIGH,
					"The " + CTFTeamName(CTF_TEAM2) + " flag has returned!\n"
				);
			}
			return true;
		}
	};
	
	// Called from PlayerDie, to drop the flag from a dying player
	public static void CTFDeadDropFlag(edict_t self) {
		edict_t dropped = null;

		if (self.client.pers.inventory[GameItems.ITEM_INDEX(flag1_item)] != 0) {
			dropped = GameItems.Drop_Item(self, flag1_item);
			self.client.pers.inventory[GameItems.ITEM_INDEX(flag1_item)] = 0;
			GameBase.gi.bprintf(Globals.PRINT_HIGH,
				self.client.pers.netname + " lost the " +
				CTFTeamName(CTF_TEAM1) + " flag!\n"
			);
		} else if (self.client.pers.inventory[GameItems.ITEM_INDEX(flag2_item)] != 0) {
			dropped = GameItems.Drop_Item(self, flag2_item);
			self.client.pers.inventory[GameItems.ITEM_INDEX(flag2_item)] = 0;
			GameBase.gi.bprintf(Globals.PRINT_HIGH,
				self.client.pers.netname + " lost the " +
				CTFTeamName(CTF_TEAM2) + " flag!\n"
			);
		}

		if (dropped != null) {
			dropped.think = CTFDropFlagThink;
			dropped.nextthink = GameBase.level.time + CTF_AUTO_FLAG_RETURN_TIMEOUT;
			dropped.touch = CTFDropFlagTouch;
		}
	}
	
	public static boolean CTFDrop_Flag(edict_t ent, gitem_t item) {
		if (((int) (Math.random() * 32768) & 1) != 0) 
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH, "Only lusers drop flags.\n");
		else
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH, "Winners don't drop flags.\n");
		return false;
	}
	
	public static EntThinkAdapter CTFFlagThink = new EntThinkAdapter() {
		public String getID() { return "CTFFlagThink"; }
		public boolean think(edict_t ent) {
			if (ent.solid != Globals.SOLID_NOT)
				ent.s.frame = 173 + (((ent.s.frame - 173) + 1) % 16);
			ent.nextthink = GameBase.level.time + Globals.FRAMETIME;
			return true;
		}
	};

	public static void CTFFlagSetup (edict_t ent) {
		trace_t tr;
		float[] dest = null;
		float[] v;

		v = new float[] {-15, -15, -15};
		Math3D.VectorCopy (v, ent.mins);
		v = new float[] {15, 15, 15};
		Math3D.VectorCopy (v, ent.maxs);

		if (ent.model != null)
			GameBase.gi.setmodel (ent, ent.model);
		else
			GameBase.gi.setmodel (ent, ent.item.world_model);
		ent.solid = Defines.SOLID_TRIGGER;
		ent.movetype = Defines.MOVETYPE_TOSS;  
		ent.touch = GameItems.Touch_Item;

		v = new float[] {0, 0, -128};
		Math3D.VectorAdd(ent.s.origin, v, dest);

		tr = GameBase.gi.trace (ent.s.origin, ent.mins, ent.maxs, dest, ent, Globals.MASK_SOLID);
		if (tr.startsolid)
		{
			GameBase.gi.dprintf ("CTFFlagSetup: " +
					ent.classname + " startsolid at " + 
					ent.s.origin[0] + ',' + ent.s.origin[1] + ',' + ent.s.origin[2] + "\n"
			);
			GameUtil.G_FreeEdict(ent);
			return;
		}

		Math3D.VectorCopy(tr.endpos, ent.s.origin);

		GameBase.gi.linkentity (ent);

		ent.nextthink = GameBase.level.time + Globals.FRAMETIME;
		ent.think = CTFFlagThink;
	}
	
	public static void CTFEffects(edict_t player) {
		player.s.effects &= ~(Defines.EF_FLAG1 | Defines.EF_FLAG2);
		if (player.health > 0) {
			if (player.client.pers.inventory[GameItems.ITEM_INDEX(flag1_item)] != 0) {
				player.s.effects |= Defines.EF_FLAG1;
			}
			if (player.client.pers.inventory[GameItems.ITEM_INDEX(flag2_item)] != 0) {
				player.s.effects |= Defines.EF_FLAG2;
			}
		}

		if (player.client.pers.inventory[GameItems.ITEM_INDEX(flag1_item)] != 0)
			player.s.modelindex3 = GameBase.gi.modelindex("players/male/flag1.md2");
		else if (player.client.pers.inventory[GameItems.ITEM_INDEX(flag2_item)] != 0)
			player.s.modelindex3 = GameBase.gi.modelindex("players/male/flag2.md2");
		else
			player.s.modelindex3 = 0;
	}
	
	// called when we enter the intermission
	public static void CTFCalcScores() {
		int i;

		ctfgame.total1 = ctfgame.total2 = 0;
		for (i = 0; i < GameBase.maxclients.value; i++) {
			if (!GameBase.g_edicts[i+1].inuse)
				continue;
			if (GameBase.game.clients[i].resp.ctf_team == CTF_TEAM1)
				ctfgame.total1 += GameBase.game.clients[i].resp.score;
			else if (GameBase.game.clients[i].resp.ctf_team == CTF_TEAM2)
				ctfgame.total2 += GameBase.game.clients[i].resp.score;
		}
	}
	
	public static void CTFID_f (edict_t ent) {
		if (ent.client.resp.id_state) {
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH, "Disabling player identication display.\n");
			ent.client.resp.id_state = false;
		} else {
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH, "Activating player identication display.\n");
			ent.client.resp.id_state = true;
		}
	}
	
	public static void CTFSetIDView(edict_t ent) {
		float[] forward = null, dir = null;
		trace_t	tr;
		edict_t	who, best;
		float	bd = 0, d;
		int i;

		ent.client.ps.stats[STAT_CTF_ID_VIEW] = 0;

		Math3D.AngleVectors(ent.client.v_angle, forward, null, null);
		Math3D.VectorScale(forward, 1024, forward);
		Math3D.VectorAdd(ent.s.origin, forward, forward);
		tr = GameBase.gi.trace(ent.s.origin, null, null, forward, ent, Globals.MASK_SOLID);
		if (tr.fraction < 1 && tr.ent != null && tr.ent.client != null) {
			ent.client.ps.stats[STAT_CTF_ID_VIEW] = 
				(short) (Globals.CS_PLAYERSKINS + (ent.index));
			return;
		}

		Math3D.AngleVectors(ent.client.v_angle, forward, null, null);
		best = null;
		for (i = 1; i <= GameBase.maxclients.value; i++) {
			who = GameBase.g_edicts[i];
			if (!who.inuse || who.solid == Globals.SOLID_NOT)
				continue;
			Math3D.VectorSubtract(who.s.origin, ent.s.origin, dir);
			Math3D.VectorNormalize(dir);
			d = Math3D.DotProduct(forward, dir);
			if (d > bd && loc_CanSee(ent, who)) {
				bd = d;
				best = who;
			}
		}
		if (bd > 0.90)
			ent.client.ps.stats[STAT_CTF_ID_VIEW] = 
				(short) (Globals.CS_PLAYERSKINS + (best.index));
	}
	
	public static void SetCTFStats(edict_t ent) {
		gitem_t tech;
		int i;
		int p1, p2;
		EdictIterator e;

		if (ctfgame.match > MATCH_NONE)
			ent.client.ps.stats[STAT_CTF_MATCH] = CONFIG_CTF_MATCH;
		else
			ent.client.ps.stats[STAT_CTF_MATCH] = 0;

		//ghosting
		if (ent.client.resp.ghost != null) {
			ent.client.resp.ghost.score = ent.client.resp.score;
			ent.client.resp.ghost.netname = ent.client.pers.netname;
			ent.client.resp.ghost.number = ent.s.number;
		}

		// logo headers for the frag display
		ent.client.ps.stats[STAT_CTF_TEAM1_HEADER] = (short) GameBase.gi.imageindex ("ctfsb1");
		ent.client.ps.stats[STAT_CTF_TEAM2_HEADER] = (short) GameBase.gi.imageindex ("ctfsb2");

		// if during intermission, we must blink the team header of the winning team
		if (GameBase.level.intermissiontime != 0.0f && (GameBase.level.framenum & 8) != 0) { // blink 1/8th second
			// note that ctfgame.total[12] is set when we go to intermission
			if (ctfgame.team1 > ctfgame.team2)
				ent.client.ps.stats[STAT_CTF_TEAM1_HEADER] = 0;
			else if (ctfgame.team2 > ctfgame.team1)
				ent.client.ps.stats[STAT_CTF_TEAM2_HEADER] = 0;
			else if (ctfgame.total1 > ctfgame.total2) // frag tie breaker
				ent.client.ps.stats[STAT_CTF_TEAM1_HEADER] = 0;
			else if (ctfgame.total2 > ctfgame.total1) 
				ent.client.ps.stats[STAT_CTF_TEAM2_HEADER] = 0;
			else { // tie game!
				ent.client.ps.stats[STAT_CTF_TEAM1_HEADER] = 0;
				ent.client.ps.stats[STAT_CTF_TEAM2_HEADER] = 0;
			}
		}

		// tech icon
		i = 0;
		ent.client.ps.stats[STAT_CTF_TECH] = 0;
		while (tnames[i] != null) {
			if ((tech = GameItems.FindItemByClassname(tnames[i])) != null &&
				ent.client.pers.inventory[GameItems.ITEM_INDEX(tech)] != 0) {
				ent.client.ps.stats[STAT_CTF_TECH] = (short) GameBase.gi.imageindex(tech.icon);
				break;
			}
			i++;
		}

		// figure out what icon to display for team logos
		// three states:
		//   flag at base
		//   flag taken
		//   flag dropped
		p1 = GameBase.gi.imageindex ("i_ctf1");
		e = GameBase.G_Find(null, GameBase.findByClass, "item_flag_team1");
		if (e != null) {
			if (e.o.solid == Globals.SOLID_NOT) {
				int x;

				// not at base
				// check if on player
				p1 = GameBase.gi.imageindex ("i_ctf1d"); // default to dropped
				for (x = 1; x <= GameBase.maxclients.value; x++)
					if (GameBase.g_edicts[x].inuse &&
						GameBase.g_edicts[x].client.pers.inventory[GameItems.ITEM_INDEX(flag1_item)] != 0) {
						// enemy has it
						p1 = GameBase.gi.imageindex ("i_ctf1t");
						break;
					}
			} else if ((e.o.spawnflags & Globals.DROPPED_ITEM) != 0)
				p1 = GameBase.gi.imageindex ("i_ctf1d"); // must be dropped
		}
		p2 = GameBase.gi.imageindex ("i_ctf2");
		e = GameBase.G_Find(null, GameBase.findByClass, "item_flag_team2");
		if (e != null) {
			if (e.o.solid == Globals.SOLID_NOT) {
				int x;

				// not at base
				// check if on player
				p2 = GameBase.gi.imageindex ("i_ctf2d"); // default to dropped
				for (x = 1; i <= GameBase.maxclients.value; x++)
					if (GameBase.g_edicts[x].inuse &&
						GameBase.g_edicts[x].client.pers.inventory[GameItems.ITEM_INDEX(flag2_item)] != 0) {
						// enemy has it
						p2 = GameBase.gi.imageindex ("i_ctf2t");
						break;
					}
			} else if ((e.o.spawnflags & Globals.DROPPED_ITEM) != 0)
				p2 = GameBase.gi.imageindex ("i_ctf2d"); // must be dropped
		}


		ent.client.ps.stats[STAT_CTF_TEAM1_PIC] = (short) p1;
		ent.client.ps.stats[STAT_CTF_TEAM2_PIC] = (short) p2;

		if (ctfgame.last_flag_capture >= 0.0f && GameBase.level.time - ctfgame.last_flag_capture < 5) {
			if (ctfgame.last_capture_team == CTF_TEAM1)
				if ((GameBase.level.framenum & 8) != 0)
					ent.client.ps.stats[STAT_CTF_TEAM1_PIC] = (short) p1;
				else
					ent.client.ps.stats[STAT_CTF_TEAM1_PIC] = 0;
			else
				if ((GameBase.level.framenum & 8) != 0)
					ent.client.ps.stats[STAT_CTF_TEAM2_PIC] = (short) p2;
				else
					ent.client.ps.stats[STAT_CTF_TEAM2_PIC] = 0;
		}

		ent.client.ps.stats[STAT_CTF_TEAM1_CAPS] = (short) ctfgame.team1;
		ent.client.ps.stats[STAT_CTF_TEAM2_CAPS] = (short) ctfgame.team2;

		ent.client.ps.stats[STAT_CTF_FLAG_PIC] = 0;
		if (ent.client.resp.ctf_team == CTF_TEAM1 &&
			ent.client.pers.inventory[GameItems.ITEM_INDEX(flag2_item)] != 0 &&
			((GameBase.level.framenum & 8) != 0))
			ent.client.ps.stats[STAT_CTF_FLAG_PIC] = (short) GameBase.gi.imageindex ("i_ctf2");

		else if (ent.client.resp.ctf_team == CTF_TEAM2 &&
			ent.client.pers.inventory[GameItems.ITEM_INDEX(flag1_item)] != 0&&
			((GameBase.level.framenum & 8) != 0))
			ent.client.ps.stats[STAT_CTF_FLAG_PIC] = (short) GameBase.gi.imageindex ("i_ctf1");

		ent.client.ps.stats[STAT_CTF_JOINED_TEAM1_PIC] = 0;
		ent.client.ps.stats[STAT_CTF_JOINED_TEAM2_PIC] = 0;
		if (ent.client.resp.ctf_team == CTF_TEAM1)
			ent.client.ps.stats[STAT_CTF_JOINED_TEAM1_PIC] = (short) GameBase.gi.imageindex ("i_ctfj");
		else if (ent.client.resp.ctf_team == CTF_TEAM2)
			ent.client.ps.stats[STAT_CTF_JOINED_TEAM2_PIC] = (short) GameBase.gi.imageindex ("i_ctfj");

		ent.client.ps.stats[STAT_CTF_ID_VIEW] = 0;
		if (ent.client.resp.id_state)
			CTFSetIDView(ent);
	}
	
	/*
	==================
	CTFScoreboardMessage
	==================
	*/
	public static void CTFScoreboardMessage (edict_t ent, edict_t killer) {
		StringBuilder entry = new StringBuilder();
		StringBuilder string = new StringBuilder();
		int i, j, k; //, n;
		int sorted[][] = new int[2][Globals.MAX_CLIENTS];
		int sortedscores[][] = new int[2][Globals.MAX_CLIENTS];
		int score;
		int total[] = new int[2];
		int totalscore[] = new int [2];
		int last[] = new int[2];
		gclient_t cl;
		edict_t cl_ent;
		int team;
		// int maxsize = 1000;

		// sort the clients by team and score
		total[0] = total[1] = 0;
		last[0] = last[1] = 0;
		totalscore[0] = totalscore[1] = 0;
		for (i=0 ; i<GameBase.game.maxclients ; i++) {
			cl_ent = GameBase.g_edicts[1 + i];
			if (!cl_ent.inuse)
				continue;
			if (GameBase.game.clients[i].resp.ctf_team == CTF_TEAM1)
				team = 0;
			else if (GameBase.game.clients[i].resp.ctf_team == CTF_TEAM2)
				team = 1;
			else
				continue; // unknown team?

			score = GameBase.game.clients[i].resp.score;
			for (j=0 ; j<total[team] ; j++)
			{
				if (score > sortedscores[team][j])
					break;
			}
			for (k=total[team] ; k>j ; k--)
			{
				sorted[team][k] = sorted[team][k-1];
				sortedscores[team][k] = sortedscores[team][k-1];
			}
			sorted[team][j] = i;
			sortedscores[team][j] = score;
			totalscore[team] += score;
			total[team]++;
		}

		// print level name and exit rules
		// add the clients in sorted order

		// team one
		string.append(Com.sprintf("if 24 xv 8 yv 8 pic 24 endif " +
			"xv 40 yv 28 string \"%4d/%-3d\" " +
			"xv 98 yv 12 num 2 18 " +
			"if 25 xv 168 yv 8 pic 25 endif " +
			"xv 200 yv 28 string \"%4d/%-3d\" " +
			"xv 256 yv 12 num 2 20 ",
			new Vargs().add(totalscore[0]).add(total[0])
				.add(totalscore[1]).add(total[1]))
		);

		for (i=0 ; i<16 ; i++)
		{
			if (i >= total[0] && i >= total[1])
				break; // we're done

			// left side
			if (i < total[0]) {
				cl = GameBase.game.clients[sorted[0][i]];
				cl_ent = GameBase.g_edicts[1 + sorted[0][i]];

				entry.append(Com.sprintf(
					"ctf 0 %d %d %d %d ",
					new Vargs().add(42 + i * 8)
					.add(sorted[0][i])
					.add(cl.resp.score)
					.add(cl.ping > 999 ? 999 : cl.ping))
				);

				if (cl_ent.client.pers.inventory[GameItems.ITEM_INDEX(flag2_item)] != 0)
					entry.append(Com.sprintf("xv 56 yv %d picn sbfctf2 ",
						new Vargs(42 + i * 8))
					);

				string.append(entry.toString());
			}

			// right side
			if (i < total[1]) {
				cl = GameBase.game.clients[sorted[1][i]];
				cl_ent = GameBase.g_edicts[1 + sorted[1][i]];

				entry.append(Com.sprintf(
					"ctf 160 %d %d %d %d ",
					new Vargs().add(42 + i * 8)
					.add(sorted[1][i])
					.add(cl.resp.score)
					.add(cl.ping > 999 ? 999 : cl.ping))
				);
				if (cl_ent.client.pers.inventory[GameItems.ITEM_INDEX(flag1_item)] != 0)
					entry.append(Com.sprintf("xv 216 yv %d picn sbfctf1 ",
						new Vargs(42 + i * 8))
					);
				string.append(entry.toString());
			}
		}

		/* Nah, we don't need no spectators. - flibit
		// put in spectators if we have enough room
		if (last[0] > last[1])
			j = last[0];
		else
			j = last[1];
		j = (j + 2) * 8 + 42;

		k = n = 0;
		if (maxsize - len > 50) {
			for (i = 0; i < GameBase.maxclients.value; i++) {
				cl_ent = GameBase.g_edicts + 1 + i;
				cl = &game.clients[i];
				if (!cl_ent.inuse ||
					cl_ent.solid != SOLID_NOT ||
					cl_ent.client.resp.ctf_team != CTF_NOTEAM)
					continue;

				if (!k) {
					k = 1;
					sprintf(entry, "xv 0 yv %d string2 \"Spectators\" ", j);
					strcat(string, entry);
					len = strlen(string);
					j += 8;
				}

				sprintf(entry+strlen(entry),
					"ctf %d %d %d %d %d ",
					(n & 1) ? 160 : 0, // x
					j, // y
					i, // playernum
					cl.resp.score,
					cl.ping > 999 ? 999 : cl.ping);
				if (maxsize - len > strlen(entry)) {
					strcat(string, entry);
					len = strlen(string);
				}
				
				if (n & 1)
					j += 8;
				n++;
			}
		}

		if (total[0] - last[0] > 1) // couldn't fit everyone
			sprintf(string + strlen(string), "xv 8 yv %d string \"..and %d more\" ",
				42 + (last[0]+1)*8, total[0] - last[0] - 1);
		if (total[1] - last[1] > 1) // couldn't fit everyone
			sprintf(string + strlen(string), "xv 168 yv %d string \"..and %d more\" ",
				42 + (last[1]+1)*8, total[1] - last[1] - 1);
		*/

		GameBase.gi.WriteByte(Defines.svc_layout);
		GameBase.gi.WriteString(string.toString());
	}
	
	public static void CTFTeam_f (edict_t ent) {
		String t, s;
		int desired_team;

		t = GameBase.gi.args();
		if (t == null) {
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH,
				"You are on the " + CTFTeamName(ent.client.resp.ctf_team) + " team.\n"
			);
			return;
		}

		if (ctfgame.match > MATCH_SETUP) {
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH, "Can't change teams in a match.\n");
			return;
		}

		if (Lib.Q_stricmp(t, "red") == 0)
			desired_team = CTF_TEAM1;
		else if (Lib.Q_stricmp(t, "blue") == 0)
			desired_team = CTF_TEAM2;
		else {
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH,
				"Unknown team " + t + ".\n");
			return;
		}

		if (ent.client.resp.ctf_team == desired_team) {
			GameBase.gi.cprintf(ent, Globals.PRINT_HIGH,
				"You are already on the " + CTFTeamName(ent.client.resp.ctf_team) + " team.\n"
			);
			return;
		}

		ent.svflags = 0;
		ent.flags &= ~Globals.FL_GODMODE;
		ent.client.resp.ctf_team = desired_team;
		ent.client.resp.ctf_state = 0;
		s = Info.Info_ValueForKey (ent.client.pers.userinfo, "skin");
		CTFAssignSkin(ent, s);

		if (ent.solid == Globals.SOLID_NOT) { // spectator
			PlayerClient.PutClientInServer (ent);
			// add a teleportation effect
			ent.s.event = Globals.EV_PLAYER_TELEPORT;
			// hold in place briefly
			ent.client.ps.pmove.pm_flags = pmove_t.PMF_TIME_TELEPORT;
			ent.client.ps.pmove.pm_time = 14;
			GameBase.gi.bprintf(Globals.PRINT_HIGH,
				ent.client.pers.netname + " joined the " +
				CTFTeamName(desired_team) + " team.\n"
			);
			return;
		}

		ent.health = 0;
		PlayerClient.player_die.die(ent, ent, ent, 100000, Globals.vec3_origin);
		// don't even bother waiting for death frames
		ent.deadflag = Globals.DEAD_DEAD;
		PlayerClient.respawn (ent);

		ent.client.resp.score = 0;

		GameBase.gi.bprintf(Globals.PRINT_HIGH,
			ent.client.pers.netname + " changed to the " +
			CTFTeamName(desired_team) + " team.\n"
		);
	}
	
	/*
	======================================================================

	SAY_TEAM

	======================================================================
	*/

	// This array is in 'importance order', it indicates what items are
	// more important when reporting their names.
	private static class loc_name {
		public loc_name(String name, int rank) {
			classname = name;
			priority = rank;
		}
		public String classname;
		public int priority;
	}
	public static loc_name loc_names[] = new loc_name[] {
		new loc_name("item_flag_team1", 1),
		new loc_name("item_flag_team2", 1),
		new loc_name("item_quad", 2), 
		new loc_name("item_invulnerability", 2),
		new loc_name("weapon_bfg", 3),
		new loc_name("weapon_railgun", 4),
		new loc_name("weapon_rocketlauncher", 4),
		new loc_name("weapon_hyperblaster", 4),
		new loc_name("weapon_chaingun", 4),
		new loc_name("weapon_grenadelauncher", 4),
		new loc_name("weapon_machinegun", 4),
		new loc_name("weapon_supershotgun", 4),
		new loc_name("weapon_shotgun", 4),
		new loc_name("item_power_screen", 5),
		new loc_name("item_power_shield", 5),
		new loc_name("item_armor_body", 6),
		new loc_name("item_armor_combat", 6),
		new loc_name("item_armor_jacket", 6),
		new loc_name("item_silencer", 7),
		new loc_name("item_breather", 7),
		new loc_name("item_enviro", 7),
		new loc_name("item_adrenaline", 7),
		new loc_name("item_bandolier", 8),
		new loc_name("item_pack", 8),
		new loc_name(null, 0)
	};
	
	public static void CTFSay_Team(edict_t who, String msg);

	// GRAPPLE
	public static void CTFWeapon_Grapple (edict_t ent);
	public static void CTFPlayerResetGrapple(edict_t ent);
	public static void CTFGrapplePull(edict_t self);
	public static void CTFResetGrapple(edict_t self);

	//TECH
	public static gitem_t CTFWhat_Tech(edict_t ent);
	public static boolean CTFPickup_Tech (edict_t ent, edict_t other);
	public static void CTFDrop_Tech(edict_t ent, gitem_t item);
	public static void CTFDeadDropTech(edict_t ent);
	public static void CTFSetupTechSpawn();
	public static int CTFApplyResistance(edict_t ent, int dmg);
	public static int CTFApplyStrength(edict_t ent, int dmg);
	public static boolean CTFApplyStrengthSound(edict_t ent);
	public static boolean CTFApplyHaste(edict_t ent);
	public static void CTFApplyHasteSound(edict_t ent);
	public static void CTFApplyRegeneration(edict_t ent);
	public static boolean CTFHasRegeneration(edict_t ent);
	public static void CTFRespawnTech(edict_t ent);
	public static void CTFResetTech();

	public static void CTFOpenJoinMenu(edict_t ent);
	public static boolean CTFStartClient(edict_t ent);
	public static void CTFVoteYes(edict_t ent);
	public static void CTFVoteNo(edict_t ent);
	public static void CTFReady(edict_t ent);
	public static void CTFNotReady(edict_t ent);
	public static boolean CTFNextMap();
	public static boolean CTFMatchSetup();
	public static boolean CTFMatchOn();
	public static void CTFGhost(edict_t ent);
	public static void CTFAdmin(edict_t ent);
	public static boolean CTFInMatch();
	public static void CTFStats(edict_t ent);
	public static void CTFWarp(edict_t ent);
	public static void CTFBoot(edict_t ent);
	public static void CTFPlayerList(edict_t ent);

	public static boolean CTFCheckRules();

	public static void SP_misc_ctf_banner (edict_t ent);
	public static void SP_misc_ctf_small_banner (edict_t ent);

	public static void UpdateChaseCam(edict_t ent);
	public static void ChaseNext(edict_t ent);
	public static void ChasePrev(edict_t ent);

	public static void CTFObserver(edict_t ent);

	public static void SP_trigger_teleport (edict_t ent);
	public static void SP_info_teleport_destination (edict_t ent);
	
}