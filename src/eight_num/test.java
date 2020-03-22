package eight_num;

import java.util.*;

import java.util.logging.*;

/**
 * 
 * @author 18069
 * ����8-puzzle
 * 
 * �ƶ��ո�
 * ������ �ӽڵ������һ���ո�ĺϷ��ƶ�
 * ��һά�������м�¼һ��״̬
 */
public class test {
	static void RUN(Treesearch search) {
		search.Search();
		System.out.println(
				"actions: "+search.retActSeq() + "\n" +
				"timecost: " +search.retTimeCost() + "\n" +
				"spacecost: "+search.retSpaceCost());
	}
	public static void main(String[] args) {
		RUN(new BFS());
		Logger logger = Logger.getLogger("logTest1");
		logger.info("Message 1"); 
	}
}

class Node{
	private final ArrayList<Integer> state;
	private Node parent = null;
	private String parentAct = null;
	private int cost = 1;

	//������Ӧ����
	private HashMap<String, Integer> actions = new HashMap<String, Integer>();
	
	public Node
	(final ArrayList<Integer> init, final Node parent, final String parentAct, final int cost) {
		state = new ArrayList<>(init);
		actions.put("up", -3);
		actions.put("down", 3);
		actions.put("right", 1);
		actions.put("left", -1);
		
		this.parent = parent;
		this.parentAct = parentAct;
		this.cost = cost;
	}
	
	public Node(Node node) {
		this.state = node.getState();
		this.parent = node.getParent();
		this.parentAct = node.parentAct();
		this.cost = node.getCost();
	}
	
	//����Ƿ�ﵽĿ��
	public boolean check(final ArrayList<Integer> goal) {
		return goal.equals(state);
	}
	
	public ArrayList<Integer> act(String action) {
		ArrayList<Integer> nextSta = new ArrayList<>(state);
		if (haveAccess(action)) {
			Collections.swap(nextSta, nextSta.indexOf(0), nextSta.indexOf(0) + actions.get(action));
			return nextSta;
		}
		else
		{
			return nextSta;
		}
	}
	
	public Node getChild(String action) {
		return new Node(this.act(action), this, action, cost+1);
	}
	
	public ArrayList<Integer> getState(){
		return state;
	}
	
	public String parentAct(){
		return parentAct;
	}
	
	public int getCost() {
		return cost;
	}
	
	public Node getParent() {
		return parent;
	}
	
	private boolean haveAccess(String action) {
		switch(action) {
		case "up":
			if (state.indexOf(0) < 3)
				return false;
			break;
		case "down":
			if (state.indexOf(0) > 5)
				return false;
			break;
		case "right":
			if (0 == state.get(2) || 0 == state.get(5) || 0 == state.get(8))
				return false;
			break;
		case "left":
			if (0 == state.get(0) || 0 == state.get(3) || 0 == state.get(6))
				return false;
			break;
		}
		return true;
	}
}

abstract class Treesearch{
	//��ʼ
	final ArrayList<Integer> initSeq = 
			new ArrayList<>(Arrays.asList(7, 2, 4, 5, 0, 6, 8, 3, 1));
//			new ArrayList<>(Arrays.asList(1, 0, 2, 3, 4, 5, 6, 7, 8)); test if work
	//Ŀ��
	final ArrayList<Integer> goalSeq = 
			new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
	//����
	final ArrayList<String> actions = 
			new ArrayList<>(Arrays.asList("up", "right", "down", "left"));
	
	private ArrayList<String> actSeq = new ArrayList<String>();
	protected int spaceCost = 1;
	private int timeCost = 0;
	
	abstract boolean Search();
	
	protected void getSolution(final Node goalNode) {
		Node node = new Node(goalNode);
		while(null != node.parentAct())
		{
//			System.out.println(node.parentAct());
			actSeq.add(node.parentAct());
			node = node.getParent();
		}
		timeCost = goalNode.getCost();
		Collections.reverse(actSeq);
	}
	
	public ArrayList<String> retActSeq(){
		return actSeq;
	}
	
	public int retTimeCost() {
		return timeCost;
	}
	
	public int retSpaceCost() {
		return spaceCost;
	}
}

class BFS extends Treesearch{
	private LinkedList<Node> frontier = new LinkedList<Node>();
	private LinkedList<ArrayList<Integer>> explored = new LinkedList<>();
	
	public boolean Search() 
	{
		Node node = new Node(initSeq, null, null, 0);
		frontier.push(node);
		
		if (node.check(goalSeq))
			return true;
		
		while(true) {
			//end
			if (frontier.isEmpty())
				return false;
			
			node = frontier.pop();
			explored.add(node.getState());
			
			for (String s : actions) 
			{
				Node child = node.getChild(s);
				//�����������ɽ���ʱ��������ԭ�ڵ���ͬ������
				if (child.getState().equals(node.getState()))
					continue;
				
				spaceCost++; /*����ռ临�Ӷ�*/
				
				if (!explored.contains(child.getState())) 
				{
					if (child.check(goalSeq)) 
					{
						getSolution(child);
						return true;
					}
					
					frontier.push(child);
				}
			}
		}
	}
}

//TODO
class DFS extends Treesearch{
	public boolean Search()
	{
		return false;
	}
}

//TODO
class Astar extends Treesearch{
	public boolean Search()
	{
		return false;
	}
}





