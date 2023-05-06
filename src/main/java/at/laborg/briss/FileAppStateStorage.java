package at.laborg.briss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileAppStateStorage implements AppStateStorage {
	private File stateFile;

	public FileAppStateStorage(File stateFile) {
		this.stateFile = stateFile;
	}

	@Override
	public void save(AppState appState) throws IOException {
		if (!stateFile.exists()) {
			stateFile.createNewFile();
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(stateFile);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(appState);
		}
	}

	@Override
	public AppState restore() throws IOException {
		if (!stateFile.exists()) {
			return null;
		}

		try (FileInputStream fileInputStream = new FileInputStream(stateFile);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
			final Object readObject = objectInputStream.readObject();

			if (readObject instanceof AppState) {
				return (AppState) readObject;
			}

			throwUnknownAppState();
		} catch (Exception e) {
			throwUnknownAppState();
		}

		return null;
	}

	private void throwUnknownAppState() throws IOException {
		throw new IOException("Unexpected app state object at " + stateFile.getAbsolutePath());
	}
}
