package at.laborg.briss;

import java.io.IOException;

public interface AppStateStorage {
	void save(AppState appState) throws IOException;

	AppState restore() throws IOException;
}
