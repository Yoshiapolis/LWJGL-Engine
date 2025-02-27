package files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextFile {
	
	BufferedReader reader = null;
	String path;
	
	public TextFile(String path) {
		this.path = path;
		this.createReader();
	}
	
	private void createReader() {
		try {
			System.out.println("loading text file: " + path);
			this.reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceStream(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String readLine() {
		try {
			return this.reader.readLine();
		} catch (IOException e) {
			System.err.println("Error reading file");
		}
		return null;
	}

	public String read() {
		
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while((line = this.reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			System.err.println("Error reading file as string");
		}
		
		return sb.toString();
		
	}
	
	public void close() {
		try {
			this.reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
