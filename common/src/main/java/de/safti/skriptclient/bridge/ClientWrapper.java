package de.safti.skriptclient.bridge;

import java.time.Duration;

public interface ClientWrapper {
	
	void showErrorToast(String title, String message, Duration duration);
	
	void runOnStartup(Runnable runnable);


	
}
