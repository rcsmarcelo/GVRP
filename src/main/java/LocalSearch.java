import java.util.LinkedList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Date;

public class LocalSearch {
	private int	 VehicleCapacity;
	private long Time;

	public LocalSearch (int Capacity) {
		VehicleCapacity = Capacity;
	}
	
	public int startSearch() {
		int Solution = localSearch();

		return Solution;
	}
	
	private void printSolution(LinkedList<LinkedList<Node>> Solution) {
		for (int c = 0; c < Solution.size(); c++) {
			for (int d = 0; d < Solution.get(c).size(); d++)
				System.out.printf("%d ", Solution.get(c).get(d).getNum());
			System.out.println();
		}
	}

    private int evaluateSolution(LinkedList<LinkedList<Node>> Solution) {
        int Distance = 0;

        for (int c = 0; c < Solution.size(); c++) {
            int AccDemand = 0;
            int AccDist = 0;
            for (int d = 1; d < Solution.get(c).size(); d++) {
                int auxDist = Instance.getDistance(Solution.get(c).get(d - 1), Solution.get(c).get(d));
                if (Solution.get(c).get(d).getNum() != 1) {
                    LinkedList<Node> Grps = Instance.getGroup(Solution.get(c).get(d).getGroup());
                    Node Best = Solution.get(c).get(d);
                    for (int e = 0; e < Grps.size(); e++)
                        if (!Solution.get(c).get(d).isIncluded()
                                &&	Grps.get(e).getNum() != Solution.get(c).get(d).getNum()
                                &&	Instance.getDistance(Grps.get(e), Solution.get(c).get(d - 1)) < auxDist) {
                            Best = Grps.get(e);
                        }
                    Solution.get(c).get(d).setIncluded(false);
                    Solution.get(c).set(d, Best);
                    Solution.get(c).get(d).setIncluded(true);
                }

                AccDemand += Solution.get(c).get(d).getDemand();
                Solution.get(c).get(d).setAccDemand(AccDemand);
                AccDist += Instance.getDistance(Solution.get(c).get(d - 1), Solution.get(c).get(d));
                Distance += Instance.getDistance(Solution.get(c).get(d - 1), Solution.get(c).get(d));
                Solution.get(c).get(d).setAccLength(AccDist);
            }
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
		Depot.setGroup(-1);
		Depot.setDemand(0);
		
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
					Depot.setAccDemand(AccDemand);
					Depot.setAccLength(AccDist);
                    Solution.get(RouteNum).add(Depot);
					Depot = new Node();
					Depot.setXCoord(Solution.get(0).get(0).getXCoord());
					Depot.setYCoord(Solution.get(0).get(0).getYCoord());
					Depot.setNum(Solution.get(0).get(0).getNum());
					Depot.setGroup(-1);
					Depot.setDemand(0);

					AccDemand = 0;
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
				MinDistNode.setGroup(c);
                AccDemand += MinDistNode.getDemand();
                MinDistNode.setAccDemand(AccDemand);
				MinDistNode.setAccLength(AccDist);
				Solution.get(RouteNum).add(MinDistNode);
				AuxSize++;
				MinDistNode.setVisited(true);
			}
		}
		
		Depot = new Node();
		Depot.setXCoord(Solution.get(0).get(0).getXCoord());
		Depot.setYCoord(Solution.get(0).get(0).getYCoord());
		Depot.setNum(Solution.get(0).get(0).getNum());
		Depot.setGroup(-1);
		Depot.setAccDemand(AccDemand);
		Depot.setDemand(0);
		Solution.get(Solution.size() - 1).add(Depot);

		for (int c = 0; c < Solution.size(); c++)
			for (int d = 0; d < Solution.get(c).size(); d++)
				Solution.get(c).get(d).setVisited(false);

		return Solution;
	}
	
	private boolean checkSolution(LinkedList<LinkedList<Node>> Solution) {
		int acc = 0;
		
		for (int c = 0; c < Solution.size(); c++) {
			acc = 0;
			for (int d = 0; d < Solution.get(c).size(); d++) {
				acc += Solution.get(c).get(d).getDemand();
				if (acc > VehicleCapacity) {
                    return false;
                }
			}
		}
		return true;
	}
	
	private boolean checkRelocate(LinkedList<LinkedList<Node>> CurrSol, int pos1, int pos2, int r1, int r2) {
		int Sol1 = 0;
		int Sol2 = 0;
		int Demand = 0;

		for (int c = 0; c < CurrSol.size(); c++)
			Sol1 += CurrSol.get(c).get(CurrSol.get(c).size() - 1).getAccLength();
		Sol2 = Sol1;
		
		Sol2 -= Instance.getDistance(CurrSol.get(r1).get(pos1), CurrSol.get(r1).get(pos1 - 1));
		Sol2 -= Instance.getDistance(CurrSol.get(r1).get(pos1), CurrSol.get(r1).get(pos1 + 1));
		Sol2 += Instance.getDistance(CurrSol.get(r1).get(pos1 - 1), CurrSol.get(r1).get(pos1 + 1));

		Sol2 += Instance.getDistance(CurrSol.get(r1).get(pos1), CurrSol.get(r2).get(pos2));
		Sol2 -= Instance.getDistance(CurrSol.get(r2).get(pos2), CurrSol.get(r2).get(pos2 - 1));
		Sol2 += Instance.getDistance(CurrSol.get(r1).get(pos1), CurrSol.get(r2).get(pos2 - 1));
		
		if (r1 != r2) {
			Demand = CurrSol.get(r2).get(CurrSol.get(r2).size() - 1).getAccDemand();
			Demand += CurrSol.get(r2).get(CurrSol.get(r2).size() - 1).getDemand();
			Demand += CurrSol.get(r1).get(pos1).getDemand();
            if (Demand > VehicleCapacity)
				return false;
		}

		if (Sol2 <= Sol1)
			return true;
		return false;
	}

	private boolean checkSwap(LinkedList<LinkedList<Node>> CurrSol, int pos1, int pos2, int r1, int r2){
	    int Sol1 = 0, Sol2 = 0;
        int Demand1 = 0, Demand2 = 0;

	    for (int c = 0; c < CurrSol.size(); c++)
	        Sol1 += CurrSol.get(c).get(CurrSol.get(c).size() - 1).getAccLength();
	    Sol2 = Sol2;

	    Sol2 -= Instance.getDistance(CurrSol.get(r1).get(pos1), CurrSol.get(r1).get(pos1 - 1));
	    Sol2 -= Instance.getDistance(CurrSol.get(r1).get(pos1), CurrSol.get(r1).get(pos1 + 1));
	    Sol2 += Instance.getDistance(CurrSol.get(r2).get(pos2), CurrSol.get(r1).get(pos1 - 1));
	    Sol2 += Instance.getDistance(CurrSol.get(r2).get(pos2), CurrSol.get(r1).get(pos1 + 1));
	    Sol2 -= Instance.getDistance(CurrSol.get(r2).get(pos2), CurrSol.get(r2).get(pos2 - 1));
	    Sol2 -= Instance.getDistance(CurrSol.get(r2).get(pos2), CurrSol.get(r2).get(pos2 + 1));
	    Sol2 += Instance.getDistance(CurrSol.get(r2).get(pos2 - 1), CurrSol.get(r1).get(pos1));
	    Sol2 += Instance.getDistance(CurrSol.get(r2).get(pos2 + 1), CurrSol.get(r1).get(pos1));

	    if (r1 != r2) {
	        Demand1 = CurrSol.get(r1).get(CurrSol.get(r1).size() - 1).getAccDemand();
	        Demand1 -= CurrSol.get(r1).get(pos1).getDemand();
	        Demand1 += CurrSol.get(r2).get(pos2).getDemand();
	        Demand2 = CurrSol.get(r2).get(CurrSol.get(r2).size() - 1).getAccDemand();
	        Demand2 -= CurrSol.get(r2).get(pos2).getDemand();
	        Demand2 += CurrSol.get(r1).get(pos1).getAccDemand();

	        if (Demand1 > VehicleCapacity || Demand2 > VehicleCapacity)
	            return false;
        }

	    if (Sol2 < Sol1)
	        return true;
	    return false;
    }

	private LinkedList<LinkedList<Node>> getNeighbor(LinkedList<LinkedList<Node>> CurrSol, int bestSol) {
		/* Swap */
		for (int c = 0; c < CurrSol.size(); c++) {
			int pos = ThreadLocalRandom.current().nextInt(0, CurrSol.size());
			for (int d = 1; d < CurrSol.get(c).size() - 1 && d < CurrSol.get(pos).size(); d++)
				for (int e = 1; e < CurrSol.get(pos).size() - 1 && e < CurrSol.get(c).size(); e++)
					if(d != e && checkSwap(CurrSol, d, e, c, pos)) {
						Node Aux = CurrSol.get(c).get(d);
						CurrSol.get(c).set(d, CurrSol.get(pos).get(e));
						CurrSol.get(pos).set(e, Aux);
						return CurrSol;
					}
		}
		
		/* Relocate */
		for(int c = 0; c < CurrSol.size(); c++) {
		int pos1 = ThreadLocalRandom.current().nextInt(0, CurrSol.size());
		int pos2 = ThreadLocalRandom.current().nextInt(0, CurrSol.size());
			for (int d = 1; d < CurrSol.get(pos1).size() - 1; d++)
				for (int e = 1; e < CurrSol.get(pos2).size() - 1; e++) {
					if (d != e && checkRelocate(CurrSol, d, e, pos1, pos2)) {
						if (CurrSol.get(pos1).size() > 3) {
							CurrSol.get(pos2).add(e, CurrSol.get(pos1).get(d));
							CurrSol.get(pos1).remove(d);
                            return CurrSol;
						}
					}
				}
			}

		return CurrSol;
	}
	
	private int localSearch() {
		LinkedList<LinkedList<Node>> Solution = genStartingSolution();
		int best = evaluateSolution(Solution);
		int aux = 0;
		int Sol = best;
		Time = new Date().getTime();
		while (true) {
			Solution = getNeighbor(Solution, Sol);
			Sol = evaluateSolution(Solution);
            System.out.printf("%d - %d\n", best, Sol);
            if(!checkSolution(Solution))
                System.exit(1);
			if (Sol < best)
				best = Sol;
			else
				aux++;
			if (aux == 300) {
				aux = 0;
				Collections.shuffle(Instance.getGroups());
				Solution = genStartingSolution();
			}
			if (new Date().getTime() - Time >= 120000)
				break;
		}
		return best;
	}

	private LinkedList<LinkedList<Node>> perturbSolution(LinkedList<LinkedList<Node>> solution) {
		LinkedList<LinkedList<Node>> Memory = new LinkedList<LinkedList<Node>>();

		for (int c = 0; c < solution.size(); c++) {
			Memory.add(new LinkedList<Node>());
			for (int d = 0; d < solution.get(c).size(); d++)
				Memory.get(c).add(solution.get(c).get(d));
		}
		
		for (int c = 0; c < solution.size(); c++) {
			if (solution.size() == 1) {
				Collections.shuffle(solution.get(0).subList(1, solution.get(0).size() - 1));
			} else {
			int pos1 = ThreadLocalRandom.current().nextInt(0, solution.size() - 1);
			int pos2 = ThreadLocalRandom.current().nextInt(0, solution.size() - 1);
			int aux = 0;
			
			if (solution.get(pos1).size() > solution.get(pos2).size())
				aux = solution.get(pos2).size();
			else
				aux = solution.get(pos1).size();
			
			for (int d = 1; d < aux/2; d++) {
				Node AuxNode = solution.get(pos1).get(d);
				solution.get(pos1).set(d, solution.get(pos2).get(d));
				solution.get(pos2).set(d, AuxNode);
			}

			Collections.shuffle(solution.get(c).subList(1, solution.get(c).size() - 1));

			if (checkSolution(solution))
				return solution;
			}
		}
		return Memory;
	}
}
