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

package lwjake2.server;

import lwjake2.Defines;
import lwjake2.game.cmodel_t;
import lwjake2.game.entity_state_t;
import lwjake2.qcommon.sizebuf_t;

import java.io.RandomAccessFile;

public class server_t {

    public server_t() {
        models = new cmodel_t[Defines.MAX_MODELS];
        for (int n = 0; n < Defines.MAX_MODELS; n++)
            models[n] = new cmodel_t();

        for (int n = 0; n < Defines.MAX_EDICTS; n++)
            baselines[n] = new entity_state_t(null);
    }

    int state; // precache commands are only valid during load

    boolean attractloop; // running cinematics and demos for the local system
                         // only

    boolean loadgame; // client begins should reuse existing entity

    int time; // always sv.framenum * 100 msec

    int framenum;

    String name = ""; // map name, or cinematic name

    cmodel_t models[];

    String configstrings[] = new String[Defines.MAX_CONFIGSTRINGS];

    entity_state_t baselines[] = new entity_state_t[Defines.MAX_EDICTS];

    // the multicast buffer is used to send a message to a set of clients
    // it is only used to marshall data until SV_Multicast is called
    sizebuf_t multicast = new sizebuf_t();

    byte multicast_buf[] = new byte[Defines.MAX_MSGLEN];

    // demo server information
    RandomAccessFile demofile;

    boolean timedemo; // don't time sync
}