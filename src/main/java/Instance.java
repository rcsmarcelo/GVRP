import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Collections;

public class Instance {
	private static String  InstanceName;
	private static int	   InstanceDimension;
	private static int	   NumOfVehicles;
	private static int	   VehicleCapacity;
	private static int	   NumOfGroups;
	private static String  Filename;
	
	private static LinkedList<LinkedList<Node>> 	 Groups;
	private static LinkedList<LinkedList<Node>> 	 Routes;
	private static LinkedList<LinkedList<Integer>> Distances;
	
	
	public static void main(String[] args)throws Exception {
	    //Filename = args[0];
		readInstance();
		preProcess();
		LocalSearch ls = new LocalSearch(VehicleCapacity);
		System.out.println(ls.startSearch());
	}
	
	private static void preProcess() {
		Distances = new LinkedList<LinkedList<Integer>>();
		for (int line = 0; line < InstanceDimension; line++) {
			Distances.add(new LinkedList<Integer>());
			for (int col = 0; col < InstanceDimension; col++) {
				if (line == col)
					Distances.get(line).add(0);
				else
					Distances.get(line).add(Node.getDistance(Node.getNode(line), Node.getNode(col)));
			}
		}
	}
	
	private static void readInstance() throws IOException {
		File file = new File("C:\\Users\\ramos\\Downloads\\GVRP3\\GVRP3\\A-n33-k6-C11-V2.gvrp");
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String st, split[];
		  
		//header section
		for (int i = 0; i < 8; i++) {
			st = br.readLine();
			split = st.split(" : ");
			switch (i) {
				case 0: InstanceName = split[1]; break;
			  	case 2: InstanceDimension = Integer.parseInt(split[1]); break;
			  	case 3: NumOfVehicles = Integer.parseInt(split[1]); break;
			  	case 4: NumOfGroups = Integer.parseInt(split[1]); break;
			  	case 5: VehicleCapacity = Integer.parseInt(split[1]); break;
			  	default : continue;	  		
			}
		}
		  
		//node coord section
		for (int i = 0; i < InstanceDimension; i++) {
		  st = br.readLine();
		  split = st.split(" ");
		  Node client = new Node();
		  client.setNum(Integer.parseInt(split[0]));
		  client.setXCoord(Integer.parseInt(split[1]));
		  client.setYCoord(Integer.parseInt(split[2]));
		  Node.addNode(client);
		}
		  
		//group section
		br.readLine();
		Groups = new LinkedList<LinkedList<Node>>();
		for (int i = 0; i < NumOfGroups; i++) {
		  st = br.readLine();
		  split = st.split(" ");
		  Groups.add(new LinkedList<Node>());
		  for (int j = 1; j < split.length - 1; j++)
			  Groups.get(i).add(Node.getNode(Integer.parseInt(split[j]) - 1));
		}
		  
		//demand section
		br.readLine();
		for (int i = 0; i < NumOfGroups; i++) {
			st = br.readLine();
			split = st.split(" ");
			for (int j = 0; j < Groups.get(i).size(); j++)
				Groups.get(i).get(j).setDemand(Integer.parseInt(split[1]));
		}
		  
		br.close();
	}
	
	public static int getDistance(Node a, Node b) {
		return Distances.get(a.getNum() - 1).get(b.getNum() - 1);
	}
	
	public static LinkedList<LinkedList<Node>> getGroups() {
		return Groups;
	}

	public static LinkedList<Node> getGroup(int gnum) { return Groups.get(gnum); }
	
}
