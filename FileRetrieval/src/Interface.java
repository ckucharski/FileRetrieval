import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
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
	
	BufferedImage baseBuffer;
	BufferedImage fileBuffer;
	BufferedImage directoryBuffer;
	
	public Interface() {	
		initBuffers();
		initFilesArray();
		updateJPanel();
	}
	
	public void initBuffers() {
		try {
			baseBuffer = ImageIO.read( new File( getImagePath( "base.png" )));
			fileBuffer = ImageIO.read( new File( getImagePath( "file.png" )));
			directoryBuffer = ImageIO.read( new File( getImagePath( "directory.png" )));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getImagePath( String imageName ) {
		String path = null;
		try {
			path = new File(".").getCanonicalPath() + "/resources/" + imageName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return path;
	}
	
	
	// Méthode à modifier pour récupérer les données de la base de données  
	void initFilesArray() {
		currentDirectoryName = "CurrentDirectoryName";
		fileSelected = -1;
		
		nbFiles = 20; // max : 40
		
		for(int i = 0; i < nbFiles; i++ ){	
			Element file = new Element( i, true, "aaaaaaaaaaaaaa"); // 14 letters max
			
			elements.add(file);
		}
	}
	
	
	public void updateJPanel() {
		
		panel = new JPanel() {		
			@Override
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
				
				g.drawImage( baseBuffer, 0, 0, this );
				
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
				
				for( Element element : elements ) {
					
					if( e.getX() >= element.x && e.getX() <= element.x + 65 ) {
						if( e.getY() >= element.y && e.getY() <= element.y + 65 ) {
							fileSelected = element.id;
							break;
						}
					}
				}
				
				System.out.println(fileSelected);
			}
		});
	}
}
