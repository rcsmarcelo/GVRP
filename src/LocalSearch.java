import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class LocalSearch {
	private int	VehicleCapacity;
	
	private static ArrayList<ArrayList<Node>> GlobalSolution;
	
	
	public LocalSearch (int Capacity) {
		VehicleCapacity = Capacity;
	}
	
	public ArrayList<ArrayList<Node>> startSearch() {
		GlobalSolution = localSearch();
		
		return GlobalSolution;
	}
	
	private int evaluateSolution(ArrayList<ArrayList<Node>> Solution) {
		int Distance = 0;
		
		for (int c = 0; c < Solution.size(); c++) {
			for (int d = 0; d < Solution.get(c).size() - 1; d++)
				Distance += Instance.getDistance(Solution.get(c).get(d), Solution.get(c).get(d + 1));
		}
		
		return Distance;
	}
	
	private ArrayList<ArrayList<Node>> genStartingSolution() {
		ArrayList<ArrayList<Node>> Solution = new ArrayList<ArrayList<Node>>();
		int AccDemand = 0;
		int RouteNum = 0;
		int AccDist = 0;
		int AuxSize = 0;
		Node Depot = Node.getNode(0);
		
		for (int c = 0; c < Instance.getGroups().size(); c++) {
			Node CurrNode, MinDistNode;
			int MinDist;
			
			if (c == 0) {
				Solution.add(new ArrayList<Node>());
				Solution.get(RouteNum).add(Depot);
			}
			
			if(RouteNum == 0)
				CurrNode = Solution.get(RouteNum).get(c);
			else
				CurrNode = Solution.get(RouteNum).get(AuxSize - c);
			
			MinDist = Integer.MAX_VALUE;
			MinDistNode = null;
			
			for (int d = 0; d < Instance.getGroups().get(c).size(); d++) {
				if (Instance.getGroups().get(c).get(d).getDemand() + AccDemand > VehicleCapacity) {
					AccDemand = 0;
					Depot.setAccLength(AccDist);
					Solution.get(RouteNum).add(Depot);
					AccDist = 0;
					RouteNum++;
					Solution.add(new ArrayList<Node>());
					Solution.get(RouteNum).add(Depot);
					CurrNode = Solution.get(RouteNum).get(AuxSize - c);
				}
				if (!Instance.getGroups().get(c).get(d).isVisited() &&
						Instance.getDistance(CurrNode, Instance.getGroups().get(c).get(d)) <= MinDist) {
					MinDist = Instance.getDistance(CurrNode, Instance.getGroups().get(c).get(d));
					MinDistNode = Instance.getGroups().get(c).get(d);
				}
			}
			
			AccDist += MinDist;
			
			if (MinDistNode != null) {
				//MinDistNode.setAccLength(AccDist);
				Solution.get(RouteNum).add(MinDistNode);
				AuxSize++;
				MinDistNode.setVisited(true);
				AccDemand += MinDistNode.getDemand();
			}
		}
		
		return Solution;
	}
	
	private boolean checkSolution(ArrayList<ArrayList<Node>> Solution) {
		int acc;
		
		for (int c = 0; c < Solution.size(); c++) {
			acc = 0;
			for (int d = 0; d< Solution.get(c).size(); d++) {
				acc += Solution.get(c).get(d).getDemand();
				if (acc > VehicleCapacity)
					return false;
			}
		}
		return true;
	}
	
	private ArrayList<ArrayList<Node>> getNeighbor2(ArrayList<ArrayList<Node>> CurrSol) {
		int Sol1 = 0;
		int Sol2 = 0;
		int swapResult1 = 0;
		int swapResult2 = 0;
		
		for (int c = 0; c < CurrSol.size(); c++)
			Sol1 += CurrSol.get(c).get(0).getAccLength();
		
		Sol2 = Sol1;
		
		if (CurrSol.size() > 1) {
			int pos1 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(0).size() - 1);
			int pos2 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(1).size() - 1);
			
			swapResult1 = -Instance.getDistance(CurrSol.get(0).get(pos1),
				CurrSol.get(0).get(pos1 - 1)) - Instance.getDistance(CurrSol.get(0).get(pos1), CurrSol.get(0).get(pos1 + 1))
				+ Instance.getDistance(CurrSol.get(1).get(pos2), CurrSol.get(0).get(pos1 - 1)) + Instance.getDistance(
				CurrSol.get(1).get(pos2), CurrSol.get(0).get(pos1 + 1));
			swapResult2 = -Instance.getDistance(CurrSol.get(1).get(pos2),
					CurrSol.get(1).get(pos2 - 1)) - Instance.getDistance(CurrSol.get(1).get(pos2), CurrSol.get(1).get(pos2 + 1))
					+ Instance.getDistance(CurrSol.get(0).get(pos1), CurrSol.get(1).get(pos2 - 1)) + Instance.getDistance(
					CurrSol.get(0).get(pos1), CurrSol.get(1).get(pos2 + 1));
		}
		
		Sol2 -= (swapResult1 + swapResult2);
		
		return null;
	}
	
	private ArrayList<ArrayList<Node>> getNeighbor(ArrayList<ArrayList<Node>> CurrSol, int bestSol) {
		ArrayList<ArrayList<Node>> Memory = new ArrayList<ArrayList<Node>>();
		ArrayList<ArrayList<ArrayList<Node>>> Neighbors = new ArrayList<ArrayList<ArrayList<Node>>>();

		for (int c = 0; c < CurrSol.size(); c++) {
			Memory.add(new ArrayList<Node>());
			for (int d = 0; d < CurrSol.get(c).size(); d++)
				Memory.get(c).add(CurrSol.get(c).get(d));
		}
		
		for (int d = 0; d < 100; d++) {
			for (int c = 0; c < CurrSol.size(); c++) {
				int pos1 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c).size() - 1);
				int pos2 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c).size() - 1);
				Collections.swap(CurrSol.get(c), pos2, pos1);
				
				if (CurrSol.size() > c + 1)
					do {
						pos1 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c).size() - 1);
						pos2 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c + 1).size() - 1);
						Node Aux = CurrSol.get(c).get(pos1);
						CurrSol.get(c).set(pos1, CurrSol.get(c + 1).get(pos2));
						CurrSol.get(c + 1).set(pos2, Aux);
					} while (!checkSolution(CurrSol));
			}
			Neighbors.add(CurrSol);
		}
		
		for (int c = 0; c < Neighbors.size(); c++)
			if(evaluateSolution(Neighbors.get(c)) <= bestSol)
				return Neighbors.get(c);
		
		return Memory;
	}
	
	private ArrayList<ArrayList<Node>> localSearch() {
		ArrayList<ArrayList<Node>> Solution = genStartingSolution();
		int best = evaluateSolution(Solution);
		System.out.println(evaluateSolution(Solution));
		for (int c = 0, aux = 0; c < 1000000; c++) {
			int Sol = evaluateSolution(Solution);
			Solution = getNeighbor(Solution, Sol);
			System.out.println(Sol);
			if (Sol < best)
				best = Sol;
			if (Sol == evaluateSolution(Solution))
				aux++;
			if (aux >= 500) {
				aux = 0;
				Solution = perturbSolution(Solution);
			}
		}
		System.out.println(best);
		return Solution;
	}

	private ArrayList<ArrayList<Node>> perturbSolution(ArrayList<ArrayList<Node>> solution) {
		for (int c = 0; c < solution.size(); c++)
			for(int d = 0; d < solution.get(c).size(); d++) {
				Collections.shuffle(solution.get(c));
				if(!checkSolution(solution))
					System.exit(0);
			}
		return solution;
	}
}
