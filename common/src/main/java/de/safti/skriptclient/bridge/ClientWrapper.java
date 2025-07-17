package de.safti.skriptclient.bridge;

import java.time.Duration;

public interface ClientWrapper {
	
	void showErrorToast(String message, Duration duration);
	
}
