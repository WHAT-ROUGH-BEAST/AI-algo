package eight_num;

import java.util.*;
import java.util.logging.*;

/**
 * 
 * @author 18069
 * 描述8-puzzle
 * 
 * 移动空格
 * 树—— 子节点就是下一步空格的合法移动
 * 用一维数组序列记录一个状态
 */
interface Log{
	Logger logger = Logger.getLogger("logTest1");
}

public class test implements Log {
	static void RUN(Treesearch search) {
		search.Search();
		System.out.println(
				search.getClass().toString()+ "\n" +
				"actions: "+search.retActSeq() + "\n" +
				"timecost: " +search.retTimeCost() + "\n" +
				"spacecost: "+search.retSpaceCost());
	}
	public static void main(String[] args) {
		RUN(new BFS());
		RUN(new DFS(20));
		RUN(new Astar());
	}
}

class Node implements Comparable<Node>{ // implements Comparable to use PriorityQueue
	private final ArrayList<Integer> state;
	private Node parent = null;
	private String parentAct = null;
	private int cost = 1;

	//动作对应步数
	private HashMap<String, Integer> actions = new HashMap<String, Integer>();
	
	//Constructor
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
	
	//检测是否达到目标
	public boolean check(final ArrayList<Integer> goal) {
		return goal.equals(state);
	}
	
	public ArrayList<Integer> act(String action) {
		if (haveAccess(action)) {
			ArrayList<Integer> nextSta = new ArrayList<>(state);
			
			Collections.swap(nextSta, nextSta.indexOf(0), nextSta.indexOf(0) + actions.get(action));
			return nextSta;
		}
		else
		{
			return null; //本操作不可行
		}
	}
	
	public Node getChild(String action) {
		if (null == this.act(action))
			return null; //本操作不可行
		
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

	// Comparable : compareTo
	// Astar : lower f_price
	@Override
	public int compareTo(Node o) {
		if (f_price(this) > f_price(o))
			return 1;
		else if (f_price(this) == f_price(o))
			return 0;
		
		return -1;
	}
	
	private int f_price(Node node)
	{
		int price = node.getCost();
		for (int i = 0; i < node.getState().size(); i++)
		{
			price += Math.abs(i - Treesearch.goalSeq.indexOf(node.getState().get(i)));
		}
		return price;
	}
}

abstract class Treesearch implements Log {
	//初始
	final static ArrayList<Integer> initSeq = 
//			new ArrayList<>(Arrays.asList(7, 2, 4, 5, 0, 6, 8, 3, 1));
			new ArrayList<>(Arrays.asList(3, 1, 2, 4, 7, 5, 0, 6, 8)); //test if work
	//目标
	final static ArrayList<Integer> goalSeq = 
			new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
	//动作
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
		Node node = new Node(initSeq, null, null, 1);
		
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
				
				//当本操作不可进行时，返回与原节点相同的序列
				if (null == child)
					continue;
				spaceCost++; /*计算空间复杂度*/
							
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
	private int limit;	
	final static boolean CUTOFF = false;
	
	public DFS(int limit) {
		this.limit = limit;
	}
	
//	public boolean Search()
//	{
//		return recursive(new Node(initSeq, null, null, 1), limit);
//	}
	
	// iterative deepending search
	public boolean Search()
	{
		int currentLimit = 1;
		while (!recursive(new Node(initSeq, null, null, 1), currentLimit))
		{
			currentLimit++;
			
			if (currentLimit > limit)
				return false;
		}
		return true;
	}
	
	public boolean recursive(Node node, int limit)
	{
		boolean coOccure = false;
		
		if (node.check(goalSeq))
		{
			getSolution(node);
			return true;
		}
		else if (0 == limit)
		{
//			System.out.println("limit");
			return CUTOFF; // 应该走向其他的分支
		}
		else
		{
			coOccure = false;
			
			for (String s : actions)
			{
				Node child;
				if (node.getChild(s) != null) {
					child = node.getChild(s);
//					logger.info(child.getState().toString());
					spaceCost++;
				} else continue;
				
				if (CUTOFF == recursive(child, limit-1)) // 向上一层
				{
					coOccure = true;
				}
				else
				{
					return true;
				}
			}
			
			if (coOccure)
				return CUTOFF;
			else
				return false;
		}
	}
}

//TODO
class Astar extends Treesearch{
	private PriorityQueue<Node> frontier = new PriorityQueue<Node>();
	private LinkedList<ArrayList<Integer>> explored = new LinkedList<>();
	
	public boolean Search()
	{
		Node node = new Node(initSeq, null, null, 1);
		frontier.offer(node);
		
		while (!frontier.isEmpty())
		{
			node = frontier.poll();
			
			if (node.check(goalSeq))
			{
				getSolution(node);
				return true;
			}
			
			explored.add(node.getState());
			for (String s : actions)
			{
				Node child;
				if (node.getChild(s) != null)
				{
					child = node.getChild(s);
//					logger.info(child.getState().toString());
					spaceCost++;
				}
				else
					continue;
				
				if (!frontier.contains(child) || !explored.contains(child.getState()))
				{
					frontier.offer(child);
				}
				else if (frontier.contains(child) && child.getCost() > node.getCost())
				{
					frontier.remove(node);
					frontier.offer(child);
				}
			}
		}
		
		return false;
	}
}





