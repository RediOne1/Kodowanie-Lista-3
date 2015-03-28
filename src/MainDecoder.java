import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class MainDecoder {

	private static HashMap<String, String> dictionary = new HashMap<String, String>();

	public static void main(String[] args) {
		try {
			getDictionary(new File("test.dict"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File inputFile = new File("compressed.txt");
		try {
			decodeFile(inputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void getDictionary(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (String line; (line = br.readLine()) != null;) {
			String temp[] = line.split(" ");
			dictionary.put(temp[1], temp[0]);
		}
		br.close();
	}

	private static void decodeFile(File file) throws IOException {
		long fileLength = file.length();
		FileInputStream fis = new FileInputStream(file);
		String name[] = file.getName().split("\\.");
		//File outputFile = new File(name[0] + "." + name[1]);
		File outputFile = new File("decoded.txt");
		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedInputStream reader = new BufferedInputStream(fis);
		try {
			byte[] buffer = new byte[1024]; // == 1024
			int bufferLength = buffer.length;
			long bufferCount = fileLength / bufferLength;
			int index = 0;
			String tempKey = "";
			while (reader.read(buffer) > 0) {
				int n = bufferLength;
				if (index == bufferCount)
					n = (int) (fileLength % bufferLength);
				for (int i = 0; i < n; i++) {
					byte b = buffer[i];
					String tempBinary = toBinary(b);
					for (char bit : tempBinary.toCharArray()) {
						tempKey += bit;
						if (dictionary.containsKey(tempKey)) {
							int x = Integer.parseInt(dictionary.get(tempKey), 2);
							fos.write((char) (x));
							tempKey = "";
						}
					}
				}
				index++;
			}
			reader.close();
			fos.flush();
			fos.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String toBinary(byte _byte) {
		StringBuilder sb = new StringBuilder(Byte.SIZE);
		for (int i = 0; i < Byte.SIZE; i++)
			sb.append((_byte << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
		return sb.toString();
	}
}
