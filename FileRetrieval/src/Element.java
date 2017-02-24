public class Element {

	int id;
	boolean isADirectory;
	String name;
	int x, y;
	
	public Element( int id, boolean isADirectory, String name )
	{
		this.id = id;
		this.isADirectory = isADirectory; 
		this.name = name;
	}
}
