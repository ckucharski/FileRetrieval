public class Element {

	int id;
	boolean isADirectory;
	String name;
	int nbOpen;
	int x, y;
	
	public Element( int id, boolean isADirectory, String name, int nbOpen )
	{
		this.id = id;
		this.isADirectory = isADirectory; 
		this.name = name;
		this.nbOpen = nbOpen;
	}
}
