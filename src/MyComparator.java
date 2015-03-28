import java.util.Comparator;

public class MyComparator implements Comparator<Node> {
	@Override
	public int compare(Node node1, Node node2) {
		double result = node1.root.probability	- node2.root.probability;
		if (result < 0.0)
			return -1;
		else if (result > 0.0)
			return 1;
		else
			return 0;
	}
}
