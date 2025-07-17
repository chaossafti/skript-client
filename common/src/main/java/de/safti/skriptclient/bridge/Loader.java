package de.safti.skriptclient.bridge;

public enum Loader {
	FABRIC(true, true),
	FORGE(false, false),
	NEO_FORGE(true, false),
	QUILT(true, false),
	OTHER(false, false);
	
	
	private final boolean isSupported;
	private final boolean isImplemented;
	
	Loader(boolean isSupported, boolean isImplemented) {
		this.isSupported = isSupported;
		this.isImplemented = isImplemented;
	}
	
	public boolean isImplemented() {
		return isImplemented;
	}
	
	public boolean isSupported() {
		return isSupported;
	}
}
