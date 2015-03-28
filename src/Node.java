public class Node {
	public Pair<String, Double> root;
	public Node left = null;
	public Node right = null;

	Node(String symbol, double probability) {
		root = new Pair<String, Double>(symbol, probability);
	}
}
