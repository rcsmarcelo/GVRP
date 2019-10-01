import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LocalSearch {
	private int	VehicleCapacity;
	
	private static LinkedList<LinkedList<Node>> GlobalSolution;
	
	
	public LocalSearch (int Capacity) {
		VehicleCapacity = Capacity;
	}
	
	public LinkedList<LinkedList<Node>> startSearch() {
		GlobalSolution = localSearch();
		
		return GlobalSolution;
	}
	
	private int evaluateSolution(LinkedList<LinkedList<Node>> Solution) {
		int Distance = 0;
		
		for (int c = 0; c < Solution.size(); c++) {
			for (int d = 0; d < Solution.get(c).size() - 1; d++)
				Distance += Instance.getDistance(Solution.get(c).get(d), Solution.get(c).get(d + 1));
		}
		
		return Distance;
	}
	
	private LinkedList<LinkedList<Node>> genStartingSolution() {
		LinkedList<LinkedList<Node>> Solution = new LinkedList<LinkedList<Node>>();
		int AccDemand = 0;
		int RouteNum = 0;
		int AccDist = 0;
		int AuxSize = 0;
		Node Depot = Node.getNode(0);
		
		for (int c = 0; c < Instance.getGroups().size(); c++) {
			Node CurrNode, MinDistNode;
			int MinDist;
			
			if (c == 0) {
				Solution.add(new LinkedList<Node>());
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
					Solution.add(new LinkedList<Node>());
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
		
		Solution.get(Solution.size() - 1).add(Depot);
		
		return Solution;
	}
	
	private boolean checkSolution(LinkedList<LinkedList<Node>> Solution) {
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
	
	private LinkedList<LinkedList<Node>> getNeighbor2(LinkedList<LinkedList<Node>> CurrSol) {
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
	
	private LinkedList<LinkedList<Node>> getNeighbor(LinkedList<LinkedList<Node>> CurrSol, int bestSol) {
		LinkedList<LinkedList<Node>> Memory = new LinkedList<LinkedList<Node>>();

		for (int c = 0; c < CurrSol.size(); c++) {
			Memory.add(new LinkedList<Node>());
			for (int d = 0; d < CurrSol.get(c).size(); d++)
				Memory.get(c).add(CurrSol.get(c).get(d));
		}
		
		for (int d = 0; d < 30; d++) {
			int c = ThreadLocalRandom.current().nextInt(0, CurrSol.size());
			int pos1 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c).size() - 1);
			int pos2 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c).size() - 1);
			Collections.swap(CurrSol.get(c), pos2, pos1);
			
			if (CurrSol.size() < c + 1) {
				pos1 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c).size() - 1);
				pos2 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c + 1).size() - 1);
				Node AuxNode = CurrSol.get(c).get(pos1);
				CurrSol.get(c).set(pos1, CurrSol.get(c + 1).get(pos2));
				CurrSol.get(c + 1).set(pos2, AuxNode);
			} else if (CurrSol.size() < c - 1) {
				pos1 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c).size() - 1);
				pos2 = ThreadLocalRandom.current().nextInt(1, CurrSol.get(c - 1).size() - 1);
				Node AuxNode = CurrSol.get(c).get(pos1);
				CurrSol.get(c).set(pos1, CurrSol.get(c - 1).get(pos2));
				CurrSol.get(c - 1).set(pos2, AuxNode);
			}
			
			/*for (int A = 0; A < CurrSol.size(); A++) {
				for (int B = 0; B < CurrSol.get(A).size(); B++)
					System.out.printf("%d ", CurrSol.get(A).get(B).getNum());
				System.out.println();
				
			}*/
			if (evaluateSolution(CurrSol) <= bestSol)
				return CurrSol;
			
			for (int e = 0; e < Memory.size(); e++) {
				for (int f = 0; f < Memory.get(e).size(); f++) {
					CurrSol.get(e).add(Memory.get(e).get(f));
				}
			}
		}
		
		return Memory;
	}
	
	private LinkedList<LinkedList<Node>> localSearch() {
		LinkedList<LinkedList<Node>> Solution = genStartingSolution();
		int best = evaluateSolution(Solution);
		System.out.println(evaluateSolution(Solution));
		for (int c = 0, aux = 0; c < 100000; c++) {
			int Sol = evaluateSolution(Solution);
			Solution = getNeighbor(Solution, Sol);
			System.out.println(Sol);
			if (Sol < best)
				best = Sol;
			if (Sol == evaluateSolution(Solution))
				aux++;
			if (aux >= 1000) {
				aux = 0;
				Solution = perturbSolution(Solution);
			}
		}
		System.out.println(best);
		return Solution;
	}

	private LinkedList<LinkedList<Node>> perturbSolution(LinkedList<LinkedList<Node>> solution) {
		LinkedList<LinkedList<Node>> Memory = new LinkedList<LinkedList<Node>>();

		for (int c = 0; c < solution.size(); c++) {
			Memory.add(new LinkedList<Node>());
			for (int d = 0; d < solution.get(c).size(); d++)
				Memory.get(c).add(solution.get(c).get(d));
		}
		
		for (int c = 0; c < 50; c++) {
			int pos1 = ThreadLocalRandom.current().nextInt(0, solution.size() - 1);
			int pos2 = ThreadLocalRandom.current().nextInt(0, solution.size() - 1);
			int aux = 0;
			
			if (pos1 == pos2)
				continue;
			if (solution.get(pos1).size() > solution.get(pos2).size()) 
				aux = solution.get(pos2).size();
			else
				aux = solution.get(pos1).size();
			
			for (int d = 1; d < aux/2; d++) {
				Node AuxNode = solution.get(pos1).get(d);
				solution.get(pos1).set(d, solution.get(pos2).get(d));
				solution.get(pos2).set(d, AuxNode);
			}
			
			Collections.shuffle(solution.get(pos1).subList(1, solution.get(pos1).size() - 1));
			Collections.shuffle(solution.get(pos2).subList(1, solution.get(pos2).size() - 1));
			
			if (checkSolution(solution))
				return solution;
		}
		return Memory;
	}
}
