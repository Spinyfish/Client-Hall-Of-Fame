package dev.elysium.client.scripting.api.components;

import java.util.ArrayList;
import java.util.List;

import dev.elysium.client.scripting.Script;

public class APIComponent {

	public List<APIComponent> childrens = new ArrayList<APIComponent>();
	public Script script;
	public APIComponent parent;
	
	private int posX = 0;
	private int posY = 0;
	public int renderPosX;
	public int renderPosY;
	
	public int GetPosX() {
		return posX;
	}

	public void SetPosX(int posX) {
		this.posX = posX;
	}

	public int GetPosY() {
		return posY;
	}

	public void SetPosY(int posY) {
		this.posY = posY;
	}

	public APIComponent(Script script) {
		this.script = script;
	}
	
	public void Child(APIComponent comp) {
		comp.parent = this;
		this.childrens.add(comp);
	}
	
	public void SetPos(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
}
