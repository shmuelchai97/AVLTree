
//an iterative AVLTree that accesses the lowest and highest values in O(1)

public class AVL <E extends Comparable<E>> {
          
	private Node<E> root;
	private Node<E> current; //the current node is used so that remove the tree doesn't need to be iterated over multiple times
	private Node<E> low; //so that the lowest value can be accessed in O(1) time
	private Node<E> high; //so that the highest value can be accessed in O(1) time 
	private int size; 
	
	public E root() {
		return root.key;
	}
	
	public int height() {
		return height(root);
	}
	
	public int size() {
		return size;
	}
	
	public boolean contains(E data) {
		
		if (root == null)
			return false;
                                
		current = root;
		while (true) {
			if (data.compareTo(current.key) < 0) {
				if (current.left == null)
					return false;
				current = current.left;
			}
			
			else if (data.compareTo(current.key) > 0) {
				if (current.right == null)
					return false;
				current = current.right;
			}
                                                
			else 
				return true;
		}
	}
                
	public boolean insert(E data) {
		if (contains(data))
			return false;
                                
		if (root == null) {
			root = new Node<E>(data,2,null,null,null);
			current = root;
			high = root;
			low = root;
			size = 1;
			return true;
		}
                                
		if (data.compareTo(current.key) < 0) {
			current.left = new Node<>(data, 0, null, null, current);
			if (current == low)
				low = current.left;
		}
		else {
			current.right = new Node<>(data, 1, null, null, current);
			if (current == high)
				high = current.right;
		}
		
		size++;
                                
		if (current.height == (1 + max(height(current.left), height(current.right))))
			return true;        
                                
		balance(current);
		return true;
                                                
	}
    
	public boolean remove(E data) {
		if (!contains(data))
			return false;
		//if it does contain, then current is the node containing the data that you are trying to remove
		
		//start the natural removal of a AVLTree
		
		size--;
		if (current.right == null && current.left == null) {
			
			if (current.side == 2) {
				root = null;
				low = null;
				high = null;
				current = null;
			}
			
			else if (current.side == 0) {
				if (current == low)
					low = current.parent;
				current = current.parent;
				current.left = null;
			}
			
			else {
				if (current == high)
					high = current.parent;
				current = current.parent;
				current.right = null;
				
			}
			
			balance(current);
				
		}
		
		else if (current.right == null && current.left != null) {
			
			if (current.side == 2) {
				root = current.left;
				root.side = 2;
				root.parent = null;
				current = root;
			}
			
			else if (current.side == 0) {
				current.parent.left = current.left;
				current.left.parent = current.parent;
				current.left.side = 0;
				current = current.parent;
			}
			
			else {
				if (high == current)
					high = current.left;
				current.parent.right = current.left;
				current.left.parent = current.parent;
				current.left.side = 1;
				current = current.parent;
			}
			
			balance(current);
		}
		
		else if (current.right != null && current.left == null) {
			
			if (current.side == 2) {
				root = current.right;
				root.side = 2;
				root.parent = null;
				current = root;
			}
			
			else if (current.side == 0) {
				if (current == low)
					low = current.right;
				current.parent.left = current.right;
				current.right.parent = current.parent;
				current.right.side = 0;
				current = current.parent;
			}
			
			else {
				current.parent.right = current.right;
				current.right.parent = current.parent;
				current.right.side = 1;
				current = current.parent;
			}
			
			balance(current);
			
		}
		
		else {
			
			Node<E> minNode = current.right;
			if (minNode.left == null) {
				current.key = minNode.key;
				current.right = null;
			} else {
				while (minNode.left != null) {
					minNode = minNode.left;
				}
				current.key = minNode.key;
				current = minNode.parent;
				current.left = null;
			}
			
			balance(current);
		}
		
		return true;
		
	}
	
	public E peekLow() {
		if (root == null)
			return null;
		return low.key;
	}
	
	public E peekHigh() {
		if (root == null)
			return null;
		return high.key;
	}
	
	public E pollLow() {
		if (root == null)
			return null;
			
		size--;
		E data = low.key;
		
		if (size == 0) {
			current = null;
			high = null;
			low = null;
			root = null;
			return data;
		}
		
		if (low.right != null) {
			low.parent.left = low.right;
			low.right.parent = low.parent;
			low = low.right;
			low.side = 0;
			current = low;
		}
		else {
			low = low.parent;
			low.left = null;
			current = low;
		}
		balance(current);
		return data;
	}
	
	public E pollHigh() {
		if (root == null)
			return null;
		
		E data = high.key;
		size--;
		
		if (size == 0) {
			current = null;
			root = null;
			high = null;
			low = null;
			return data;
		}
		
		if (high.left != null) {
			high.parent.right = high.left;
			high.left.parent = high.parent;
			high = high.left;
			high.side = 1;
			current = high;
		}
		
		else {
			high = high.parent;
			high.right = null;
			current = high;
		}
		
		balance(current);
		return data;
	}
	
	private void balance(Node<E> n) {
		while (n != null) {
			
			//readjust the height so that you can find the accurate balance
			n.height = 1 + max(height(n.left), height(n.right));
                                                
			//check the balance to see if anything needs to be done
			int balance = balanceFactor(n);
			
			//if nothing needs to be done, just skip to the next part of the loop
			if (balance <= 1 && balance >= -1) {
				n = n.parent;
				continue;
			}
                                                
			if (balance > 1 && balanceFactor(n.left) >= 0)
				n = rightRotate(n);
                	
			// Left Right Case
			else if (balance > 1 && balanceFactor(n.left) < 0) {
				root.left = leftRotate(root.left);
				n = rightRotate(n);
			}
                
			// Right Right Case
			else if (balance < -1 && balanceFactor(n.right) <= 0)
				n =  leftRotate(n);
                	
			// Right Left Case
			else if (balance < -1 && balanceFactor(n.right) > 0) {
				n.right = rightRotate(n.right);
				n = leftRotate(n);
			}
			
			n = n.parent;
		}
	}
	
	
                
	public Node<E> rightRotate(Node<E> n) {
		
		Node<E> newRoot = n.left;
		Node<E> newRight = n;
                                
		//each node has a height, a side, a parent, a right, a left, the key
		//what was on the right of the newRoot goes on the left of the newRight
		
		newRoot.parent = newRight.parent;
		newRoot.side = newRight.side;
		
		newRight.left = newRoot.right;
		if (newRight.left != null)
			newRight.left.side = 0;
		newRoot.right = newRight;
		newRight.side = 1;
		
		if (newRoot.side == 1)
			newRoot.parent.right = newRoot;
		else if (newRoot.side == 0)
			newRoot.parent.left = newRoot;
		else 
			root = newRoot;
		
		newRight.parent = newRoot;
		
		newRight.height = 1 + max(height(newRight.left),height(newRight.right));
		newRoot.height = 1 + max(height(newRoot.left), height(newRoot.right));
		
		return newRoot;
	}
	
	public Node<E> leftRotate(Node<E> n) {
		
		Node<E> newRoot = n.right;
		Node<E> newLeft = n;
		
		newRoot.parent = newLeft.parent;
		newRoot.side = newLeft.side;
		
		newLeft.right = newRoot.left;
		if (newLeft.right != null)
			newLeft.right.side = 1;
		newRoot.left = newLeft;
		newLeft.side = 0;
		
		if (newRoot.side == 1)
			newRoot.parent.right = newRoot;
		else if (newRoot.side == 0)
			newRoot.parent.left = newRoot;
		else 
			root = newRoot;
		
		newLeft.parent = newRoot;
		
		
		
		newLeft.height = 1 + max(height(newLeft.right),height(newLeft.left));
		newRoot.height = 1 + max(height(newRoot.right),height(newRoot.left));
		
		return newRoot;
	}
	
	private int balanceFactor(Node<E> n) {
		if (n == null)
			return 0;
		return (height(n.left) - height(n.right));
	}
	
	private int height(Node<E> n) {
		if (n == null)
			return 0;
		return n.height;
	}
	
	private int max(int a, int b) {
		return (a > b) ? a : b;
	}
	
	public void preOrder() {
		preOrder(root);
	}
	
	private void preOrder(Node<E> n) {
		if (n != null) {
			if (n.side == 2)
				System.out.print(n.key + ":root ");
			else if (n.side == 1)
				System.out.print(n.key+":right ");
			else
				System.out.print(n.key+":left ");
			preOrder(n.left);
			preOrder(n.right);
		}
	}
	
	public class Node<E> {
		E key;
		int height, side;
		Node<E> left, right, parent;
		
		public Node(E k, int s, Node<E> l, Node<E> r, Node<E> p) {
			key = k;
			side = s;
			left = l;
			right = r;
			parent = p;
			height = 1;
		}
	}
}	
