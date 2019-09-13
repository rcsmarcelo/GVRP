import java.util.ArrayList;

public class LocalSearch {
	private int	VehicleCapacity;
	
	private static ArrayList<ArrayList<Node>> GlobalSolution = new ArrayList<ArrayList<Node>>();
	
	
	public LocalSearch (int Capacity) {
		VehicleCapacity = Capacity;
	}
	
	public ArrayList<ArrayList<Node>> startSearch() {
		genStartingSolution();
		
		for (int line = 0; line < GlobalSolution.size(); line++) {
			for (int col = 0; col < GlobalSolution.get(line).size(); col++)
				System.out.printf("%d ", GlobalSolution.get(line).get(col).getNum());
			System.out.println();
		}
		
		return GlobalSolution;
	}
	
	private void genStartingSolution() {
		int AccDemand = 0;
		int RouteNum = 0;
		int AuxSize = 0;
		Node Depot = Node.getNode(0);
		
		for (int c = 0; c < Instance.getGroups().size(); c++) {
			Node CurrNode, MinDistNode;
			int MinDist;
			GlobalSolution.add(new ArrayList<Node>());
			
			if (c == 0)
				GlobalSolution.get(RouteNum).add(Depot);
			
			if(RouteNum == 0)
				CurrNode = GlobalSolution.get(RouteNum).get(c);
			else
				CurrNode = GlobalSolution.get(RouteNum).get(AuxSize - c);
			
			MinDist = Integer.MAX_VALUE;
			MinDistNode = null;
			
			for (int d = 0; d < Instance.getGroups().get(c).size(); d++) {
				if (Instance.getGroups().get(c).get(d).getDemand() + AccDemand > VehicleCapacity) {
					AccDemand = 0;
					RouteNum++;
					GlobalSolution.add(new ArrayList<Node>());
					GlobalSolution.get(RouteNum).add(Depot);
					CurrNode = GlobalSolution.get(RouteNum).get(AuxSize - c);
				}
				if (!Instance.getGroups().get(c).get(d).isVisited() &&
						Instance.getDistance(CurrNode, Instance.getGroups().get(c).get(d)) <= MinDist) {
					MinDist = Instance.getDistance(CurrNode, Instance.getGroups().get(c).get(d));
					MinDistNode = Instance.getGroups().get(c).get(d);
				}
			}
			
			if (MinDistNode != null) {
				GlobalSolution.get(RouteNum).add(MinDistNode);
				AuxSize++;
				MinDistNode.setVisited(true);
				AccDemand += MinDistNode.getDemand();
			}
		}
	}
}
