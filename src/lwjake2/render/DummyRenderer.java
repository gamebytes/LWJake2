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

import lwjake2.client.refdef_t;
import lwjake2.client.refexport_t;
import lwjake2.qcommon.xcommand_t;
import lwjake2.sys.KBD;

import java.awt.Dimension;
import java.awt.DisplayMode;

/**
 * DummyRenderer
 * 
 * @author cwei
 */
public class DummyRenderer implements refexport_t {

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#Init(int, int)
	 */
	public boolean Init(int vid_xpos, int vid_ypos) {
		return false;
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#Shutdown()
	 */
	public void Shutdown() {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#BeginRegistration(java.lang.String)
	 */
	public void BeginRegistration(String map) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#RegisterModel(java.lang.String)
	 */
	public model_t RegisterModel(String name) {
		return null;
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#RegisterSkin(java.lang.String)
	 */
	public image_t RegisterSkin(String name) {
		return null;
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#RegisterPic(java.lang.String)
	 */
	public image_t RegisterPic(String name) {
		return null;
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#SetSky(java.lang.String, float, float[])
	 */
	public void SetSky(String name, float rotate, float[] axis) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#EndRegistration()
	 */
	public void EndRegistration() {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#RenderFrame(jake2.client.refdef_t)
	 */
	public void RenderFrame(refdef_t fd) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawGetPicSize(java.awt.Dimension, java.lang.String)
	 */
	public void DrawGetPicSize(Dimension dim, String name) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawPic(int, int, java.lang.String)
	 */
	public void DrawPic(int x, int y, String name) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawStretchPic(int, int, int, int, java.lang.String)
	 */
	public void DrawStretchPic(int x, int y, int w, int h, String name) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawChar(int, int, int)
	 */
	public void DrawChar(int x, int y, int num) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawTileClear(int, int, int, int, java.lang.String)
	 */
	public void DrawTileClear(int x, int y, int w, int h, String name) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawFill(int, int, int, int, int)
	 */
	public void DrawFill(int x, int y, int w, int h, int c) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawFadeScreen()
	 */
	public void DrawFadeScreen() {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#DrawStretchRaw(int, int, int, int, int, int, byte[])
	 */
	public void DrawStretchRaw(int x, int y, int w, int h, int cols, int rows, byte[] data) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#CinematicSetPalette(byte[])
	 */
	public void CinematicSetPalette(byte[] palette) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#BeginFrame(float)
	 */
	public void BeginFrame(float camera_separation) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#EndFrame()
	 */
	public void EndFrame() {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#AppActivate(boolean)
	 */
	public void AppActivate(boolean activate) {
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#updateScreen(jake2.qcommon.xcommand_t)
	 */
	public void updateScreen(xcommand_t callback) {
	    callback.execute();
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#apiVersion()
	 */
	public int apiVersion() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#getModeList()
	 */
	public DisplayMode[] getModeList() {
		return null;
	}

	/* (non-Javadoc)
	 * @see jake2.client.refexport_t#getKeyboardHandler()
	 */
	public KBD getKeyboardHandler() {
		return null;
	}

}
