import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window {

	JFrame window;
	ArrayList<FileOrDirectory> files = new ArrayList<>();
	String currentDirectoryName;
	int nbFiles;
	int fileSelected;
	
	
	public Window()
	{	
		window = new JFrame();
		window.setTitle("Improving navigation-based file retrieval");
		window.setSize(1116, 677);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel base = drawImages();
		window.add( base );

		window.setVisible(true);
	}
	
	public String getImagePath( String imageName )
	{
		String path = null;
		try {
			path = new File(".").getCanonicalPath() + "/resources/" + imageName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return path;
	}
	
	
	public JPanel drawImages()
	{	
		initFilesArray();
		JPanel panel = null;
		
		try {
			final BufferedImage baseBuffer = ImageIO.read( new File( getImagePath( "base.png" )));
			final BufferedImage fileBuffer = ImageIO.read( new File( getImagePath( "file.png" )));
			final BufferedImage directoryBuffer = ImageIO.read( new File( getImagePath( "directory.png" )));
			
			panel = new JPanel() {
				
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage( baseBuffer, 0, 0, this );
					g.setFont(new Font("Calibri", Font.PLAIN, 14));
					g.drawString( currentDirectoryName, 450, 18);
					
					for( int i = 0; i < nbFiles; i++ )
					{
						// margin + i * (imageSize + offset)
						FileOrDirectory file = files.get(i);
						file.x = 65 + (i%8)*(65+65);
						file.y = 70 + (i/8)*(65+60);
						
						final BufferedImage bufferToUse;
						
						if( file.isADirectory )
							bufferToUse = directoryBuffer;
						else
							bufferToUse = fileBuffer;
							
						g.drawImage( bufferToUse, file.x, file.y, this);
						
						int textWidth = g.getFontMetrics().stringWidth( file.name );
						g.drawString( file.name, file.x + 65/2 - textWidth/2, file.y + 80);
					}
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		panel.addMouseListener( new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				for( int i = 0; i < files.size(); i++ )
				{
					if( e.getX() >= files.get(i).x && e.getX() <= files.get(i).x + 65 )
					{
						if( e.getY() >= files.get(i).y && e.getY() <= files.get(i).y + 65 )
						{
							fileSelected = i;
							break;
						}
					}
				}
				System.out.println(fileSelected);
			}
		});
		return panel;
	}
	
	
	void initFilesArray()
	{
		currentDirectoryName = "CurrentDirectoryName";
		fileSelected = -1;
		
		nbFiles = 20; // max : 40
		
		for(int i = 0; i < nbFiles; i++ )
		{	
			FileOrDirectory file = new FileOrDirectory( i, true, "aaaaaaaaaaaaaa"); // 14 letters max
			
			files.add(file);
		}
		
		
	}
}
