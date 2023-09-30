package me.wavelength.baseclient.module;

public enum AntiCheat {

	VANILLA(false), NCP(true), SPARTAN(false), VERUS(false), AAC(true), HYPIXEL(false), HYPIXELLOW(false), HYPIXELFAST(false), HYPIXELZOOM(false),VULCAN(false), MINEMORA(false),MMC(false), GHOSTLY(false), SIGMA(false), HENTAI(false), HENTAI1(false), HENTAI2(false), HENTAI3(false), HENTAI4(false), HENTAI5(false), HENTAI6(false), HENTAI7(false), MOON(false), HAZEL(false), MATRIX(false), HYPIXELBOOST(false),ZYTH(false), ZYTH2(false), LUNAR(false), BLOCKDROP(false), ZYTH3(false), ZYTH4(false), RUB(false), ORGASM(false), WANK(false), MINECRAFT(false), GLOW(false), TICKS(false), MORGAN(false), VCLIP(false);

	private boolean capital;

	AntiCheat(boolean capital) {
		this.capital = capital;
	}

	public boolean isCapital() {
		return capital;
	}

}