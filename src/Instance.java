import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Instance {
	private static String InstanceName;
	private static int	  InstanceDimension;
	private static int	  NumOfVehicles;
	private static int	  VehicleCapacity;
	private static int	  NumOfGroups;
	
	private static ArrayList<ArrayList<Node>> Groups;
	
	public static void main(String[] args)throws Exception {  
	  File file = new File("C:\\Users\\tchel\\Documents\\P-n45-k5-C15-V2.GVRP"); 
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
	  Groups = new ArrayList<ArrayList<Node>>(NumOfGroups);
	  for (int i = 0; i < NumOfGroups; i++) {
		  st = br.readLine();
		  split = st.split(" ");
		  Groups.add(new ArrayList<Node>());
		  for (int j = 1; j < split.length - 1; j++)
			  Groups.get(i).add(Node.getNode(Integer.parseInt(split[j]) - 1));
	  }
	  
	  //demand section
	  br.readLine();
	  for (int i = 0; i < NumOfGroups; i++) {
		  st = br.readLine();
		  split = st.split(" ");
		  Node.getNode(Integer.parseInt(split[1]) - 1).setDemand(Integer.parseInt(split[0]));
	  }
	  
	  br.close();
	}
	
}
