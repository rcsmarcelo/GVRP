import java.util.ArrayList;

public class Node {
	private int 	Num;
	private int		Group;
	private int 	XCoord;
	private int 	YCoord;
	private int 	Demand;
	private boolean Visited = false;
	private boolean Included = false;
	private int 	AccLength;
	private int		AccDemand;
	
	private static ArrayList<Node> Nodes = new ArrayList<Node>();
	
	
	public Node() {}
	
	public int getNum() {
		return Num;
	}
	
	public void setNum(int num) {
		Num = num;
	}
	
	public int getGroup() {
		return Group;
	}
	
	public void setGroup(int group) {
		Group = group;
	}
	
	public int getXCoord() {
		return XCoord;
	}
	
	public void setXCoord(int xCoord) {
		XCoord = xCoord;
	}
	
	public int getYCoord() {
		return YCoord;
	}
	
	public void setYCoord(int yCoord) {
		YCoord = yCoord;
	}
	
	public int getDemand() {
		return Demand;
	}
	
	public void setDemand(int demand) {
		Demand = demand;
	}
	
	public void setAccLength(int acclength) {
		AccLength = acclength;
	}
	
	public int getAccLength() {
		return AccLength;
	}
	
	public static ArrayList<Node> getNodes() {
		return Nodes;
	}
	
	public static Node getNode(int index) {
		return Nodes.get(index);
	}
	
	public static void addNode(Node n) {
		Nodes.add(n);
	}
	
	public static int getDistance(Node a, Node b) {
		return (int) Math.round(Math.sqrt((Math.pow(a.getXCoord() - b.getXCoord(), 2) + 
				(Math.pow(a.getYCoord() - b.getYCoord(), 2)))));
	}

	public boolean isVisited() {
		return Visited;
	}

	public void setVisited(boolean b) {
		Visited = b;
	}

	public int getAccDemand() {
		return AccDemand;
	}
	
	public void setAccDemand(int demand) {
		AccDemand = demand;
	}

	public void setIncluded(boolean included) { Included = included; }

	public boolean isIncluded() { return Included; }
}
