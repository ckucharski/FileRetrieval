import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Interface {

	JPanel panel;
	String currentDirectoryName;
	int nbFiles;
	int fileSelected;
	ArrayList<Element> elements = new ArrayList<>();
	ArrayList<String> oldDirectories = new ArrayList<>();
	int currentStep;
	
	BufferedImage baseBuffer;
	BufferedImage prevBuffer;
	BufferedImage nextBuffer;
	BufferedImage fileBuffer;
	BufferedImage directoryBuffer;
	
	public Interface() {	
		currentDirectoryName = "Dossier1";
		currentStep = 0;
		oldDirectories.add( currentDirectoryName );
		
		initBuffers();
		initFilesArray();
		initJPanel();
	}
	
	public void initBuffers() {
		try {
			baseBuffer = ImageIO.read( new File( getPath( "base.png" )));
			prevBuffer = ImageIO.read( new File( getPath( "prev.png" )));
			nextBuffer = ImageIO.read( new File( getPath( "next.png" )));
			fileBuffer = ImageIO.read( new File( getPath( "file.png" )));
			directoryBuffer = ImageIO.read( new File( getPath( "directory.png" )));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getPath( String imageName ) {
		String path = null;
		try {
			path = new File(".").getCanonicalPath() + "/resources/" + imageName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	
	void initFilesArray()
	{			
		try {
			FileReader input = new FileReader( getPath( "elements.txt" ) );
			BufferedReader buffer = new BufferedReader( input );
			String line = null;
			
			while( (line = buffer.readLine()) != null )
			{
				String[] elements = line.split(" ");
				
				if( elements[0].equals(currentDirectoryName) )
				{
					nbFiles = elements.length - 1;
					
					for( int i = 1; i <= nbFiles; i++ )
					{
						Element currentFile;
						
						if( elements[i].contains("/") )
						{	
							String[] str = elements[i].split("/");
							
							currentFile = new Element(i-1, false, str[0], Integer.parseInt(str[1])); 
							
						}
						else
							currentFile = new Element(i-1, true, elements[i], 0 );
						
						this.elements.add(currentFile);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void initJPanel() {
		
		panel = new JPanel() {		
			@Override
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
				
				g.drawImage( baseBuffer, 0, 0, this );
				g.drawImage( prevBuffer, 20, 20, this );
				g.drawImage( nextBuffer, 46, 20, this );
				
				g.setFont(new Font("Calibri", Font.PLAIN, 14));
				g.drawString( currentDirectoryName, 450, 18);
								
				for( Element element : elements )
				{
					// margin + i * (imageSize + offset)
					element.x = 65 + (element.id%8)*(65+65);
					element.y = 70 + (element.id/8)*(65+60);
						
					final BufferedImage bufferToUse;
						
					if( element.isADirectory )
						bufferToUse = directoryBuffer;
					else
						bufferToUse = fileBuffer;
							
					g.drawImage( bufferToUse, element.x, element.y, this);
						
					int textWidth = g.getFontMetrics().stringWidth( element.name );
					g.drawString( element.name, element.x + 65/2 - textWidth/2, element.y + 80);
				}
			}
		};
		
		
		panel.addMouseListener( new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if( e.getY() >= 20 && e.getY() <= 20+23 )
				{
					if( e.getX() >= 20 && e.getX() <= 20+26 && currentStep > 0 )
					{
						currentStep--;
						update( oldDirectories.get( currentStep ) );
					}
					else if( e.getX() >= 46 && e.getX() <= 46+26 && currentStep + 1 < oldDirectories.size() )
					{
						currentStep++;
						update( oldDirectories.get( currentStep ) );
					}
					return;
				}
				
				for( Element element : elements ) {
					
					if( e.getX() >= element.x && e.getX() <= element.x + 65 ) {
						if( e.getY() >= element.y && e.getY() <= element.y + 65 ) {
							
							if( fileSelected == element.id && element.isADirectory )
							{								
								while( oldDirectories.size() > currentStep + 1 )
									oldDirectories.remove( currentStep + 1 );
								
								oldDirectories.add( element.name );
								currentStep++;

								update( element.name );
							}
							else 
								fileSelected = element.id;
							
							break;
						}
					}
				}
				System.out.println(fileSelected);
			}
		});
	}
	
	public void update( String newCurrentDirectoryName )
	{
		fileSelected = -1;
		elements.clear();
		currentDirectoryName = newCurrentDirectoryName;
		initFilesArray();
		
		panel.repaint();
	}
}
