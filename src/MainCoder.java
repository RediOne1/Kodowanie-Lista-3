import static java.lang.Math.log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainCoder {

	private static HashMap<String, Integer> bytesCount = new HashMap<String, Integer>();
	private static List<Node> treeList = new ArrayList<>();
	private static HashMap<String, String> dictionary = new HashMap<String, String>();
	private static long fileLength;
	private static FileOutputStream fos;
	private static double avgLength = 0.0;

	public static void main(String[] args) {
		File inputFile = new File("test.txt");
		fileLength = inputFile.length();

		try {
			readFile(inputFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fillTreeList();
		treeList.sort(new MyComparator());
		// for (Node n : treeList) {
		// System.out.println(n.root.symbol + "  " + n.root.probability);
		// }
		huffman();
		try {
			String name[] = inputFile.getName().split("\\.");
			fos = new FileOutputStream(new File(name[0] + ".dict"));
			writeDictionary("", treeList.get(0));
			System.out.println("Œrednia d³ugoœæ kodowania: " + avgLength);
			System.out.println("Entropia: " + entropia());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			encodeFile(inputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void readFile(File file) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream reader = new BufferedInputStream(fis);
		try {
			byte[] buffer = new byte[1024]; // == 1024
			int bufferLength = buffer.length;
			long bufferCount = fileLength / bufferLength;
			int index = 0;
			while (reader.read(buffer) > 0) {
				int n = bufferLength;
				if (index == bufferCount)
					n = (int) (fileLength % bufferLength);
				for (int i = 0; i < n; i++) {
					byte b = buffer[i];
					String symbol = toBinary(b);
					if (!bytesCount.containsKey(symbol))
						bytesCount.put(symbol, 0);
					bytesCount.put(symbol, bytesCount.get(symbol) + 1);
				}
				index++;
			}
			reader.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void fillTreeList() {
		for (String symbol : bytesCount.keySet()) {
			double probability = (double) bytesCount.get(symbol)
					/ (double) fileLength;
			treeList.add(new Node(symbol, probability));
		}
	}

	private static void huffman() {
		while (treeList.size() > 1) {
			Node node1 = treeList.remove(0);
			Node node2 = treeList.remove(0);
			Node newNode = new Node(null, node1.root.probability
					+ node2.root.probability);
			newNode.left = node1;
			newNode.right = node2;
			addNodeToSortedList(newNode);
		}
	}

	private static void addNodeToSortedList(Node n) {
		int i = 0;
		for (Node node : treeList) {
			if (node.root.probability >= n.root.probability)
				break;
			i++;
		}
		treeList.add(i, n);
	}

	static boolean first = true;

	private static void writeDictionary(String result, Node node) {
		if (node.root.symbol != null) {
			try {
				if (!first) {
					fos.write("\n".getBytes());
				}
				first = false;

				fos.write((node.root.symbol + " " + result).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dictionary.put(node.root.symbol, result);
			avgLength += result.length() * node.root.probability;
			// System.out.println(node.root.symbol + " " + result);
		} else {
			writeDictionary(result + "0", node.left);
			writeDictionary(result + "1", node.right);
		}
	}

	private static double entropia() {
		double ent = 0;
		for (String b : bytesCount.keySet()) {
			double p = (double) bytesCount.get(b) / fileLength;
			if (p > 0)
				ent -= (float) p * (log(p) / log(2));
		}
		return ent;
	}

	private static void encodeFile(File file) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream("compressed.txt");
		BufferedInputStream reader = new BufferedInputStream(fis);
		try {
			byte[] buffer = new byte[1024]; // == 1024
			int bufferLength = buffer.length;
			long bufferCount = fileLength / bufferLength;
			int index = 0;
			String binaryToWrite = "";
			while (reader.read(buffer) > 0) {
				int n = bufferLength;
				if (index == bufferCount)
					n = (int) (fileLength % bufferLength);
				for (int i = 0; i < n; i++) {
					byte b = buffer[i];
					String symbol = toBinary(b);
					String encodedSymbol = dictionary.get(symbol);
					binaryToWrite += encodedSymbol;
					if (binaryToWrite.length() >= 8) {
						fos.write((byte) Integer.parseInt(
								binaryToWrite.substring(0, 8), 2));
						binaryToWrite = binaryToWrite.substring(8,
								binaryToWrite.length());
					}
				}
				index++;
			}
			int x = 8 - binaryToWrite.length();
			if (x > 0) {
				for (int i = x; i > 0; i--)
					binaryToWrite += "0";
				fos.write((byte) Integer.parseInt(binaryToWrite, 2));
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
