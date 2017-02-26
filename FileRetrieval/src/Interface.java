import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Interface implements KeyListener {

	JPanel panel;
	String currentDirectoryName;
	int nbFiles;
	Element fileSelected;
	ArrayList<Element> elements = new ArrayList<>();
	ArrayList<String> oldDirectories = new ArrayList<>();
	int currentStep;
	JTextField tfSearch;
	String currentSearch;
	
	BufferedImage baseBuffer;
	BufferedImage prevBuffer;
	BufferedImage nextBuffer;
	BufferedImage fileBuffer;
	BufferedImage directoryBuffer;
	BufferedImage highlightBuffer;
	
	
	String saveDirectory;
	ArrayList<Element> saveElements = new ArrayList<>();
	ArrayList<String> directoryProbable = new ArrayList<>();
	
	boolean openHoverMenu;
	
	public enum mode{ STANDARD, ICON_HIGHLIGHTS, HOVER_MENUS, SDN };
	mode currentMode;
	
	public Interface() {
		currentMode = mode.STANDARD;		
		initBuffers();
		initJPanel();
		panel.addKeyListener(this);
		init();
	}
	
	public void init() {

		currentDirectoryName = "Dossier1";
		nbFiles = 0;
		fileSelected = null;
		elements.clear();
		oldDirectories.clear();
		oldDirectories.add( currentDirectoryName );
		currentStep = 0;
		currentSearch = "";
		saveDirectory = "";
		saveElements.clear();
		directoryProbable.clear();
		openHoverMenu = false;
		
		initFilesArray(false);
		
		for( int i = 0; i < 3 && currentMode == mode.ICON_HIGHLIGHTS; i++ )
			checkDirectory(currentDirectoryName);
		
		panel.repaint();
	}
	
	public void initBuffers() {
		try {
			baseBuffer = ImageIO.read( new File( getPath( "base.png" )));
			prevBuffer = ImageIO.read( new File( getPath( "prev.png" )));
			nextBuffer = ImageIO.read( new File( getPath( "next.png" )));
			fileBuffer = ImageIO.read( new File( getPath( "file.png" )));
			directoryBuffer = ImageIO.read( new File( getPath( "directory.png" )));
			highlightBuffer = ImageIO.read( new File( getPath( "highlight.png" )));
			
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
	
	
	void initFilesArray( boolean itIsASearch )
	{			
		try {
			FileReader input = new FileReader( getPath( "elements.txt" ) );
			BufferedReader buffer = new BufferedReader( input );
			String line = null;
			
			int pos = 0;
			
			while( (line = buffer.readLine()) != null )
			{
				String[] elements = line.split(" ");
				if( elements[0].equals(currentDirectoryName) || (itIsASearch && currentSearch != "") )
				{
					nbFiles = elements.length - 1;
					
					for( int i = 1; i <= nbFiles; i++ )
					{
						Element currentFile;
							
						if( elements[i].contains("/") )
						{	
							String[] str = elements[i].split("/");
							
							if( itIsASearch )
							{
								if( !str[0].toLowerCase().contains(currentSearch.toLowerCase()) )
									continue;
								else
								{
									currentFile = new Element(pos, false, str[0], Integer.parseInt(str[1]));
									pos++;
								}							
							}
							else	
								currentFile = new Element(i-1, false, str[0], Integer.parseInt(str[1]));
								
						}
						else
						{
							if( itIsASearch )
							{
								if( !elements[i].toLowerCase().contains(currentSearch.toLowerCase()) )
									continue;
								else
								{
									currentFile = new Element(pos, true, elements[i], 0 );
									pos++;
								}
							}
							else
								currentFile = new Element(i-1, true, elements[i], 0 );
						}
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
	
	
	public void checkDirectory( String directory )
	{
		String line = null;
		
		FileReader input;
		try {
			input = new FileReader( getPath( "elements.txt" ) );
			BufferedReader buffer = new BufferedReader( input );
				
			while( (line = buffer.readLine()) != null )
			{
				String[] elements = line.split(" ");
					
				if( elements[0].equals(directory) )
				{
					for( int i = 1; i < elements.length; i++ )
					{
						if( elements[i].contains("/") )
						{
							String[] str = elements[i].split("/");
								
							if( currentMode == mode.SDN && str[0].toLowerCase().contains(currentSearch.toLowerCase()) && !directoryProbable.contains(str[0]))
							{
								directoryProbable.add(str[0]);
								directoryProbable.add(directory);
								return;
							}
							
							if( ( currentMode == mode.ICON_HIGHLIGHTS || currentMode == mode.HOVER_MENUS ) && Integer.parseInt(str[1]) > 50 && !directoryProbable.contains(directory) )
							{
								directoryProbable.add(str[0]);
								directoryProbable.add(directory);
								return;
							}
						}
						else
						{
							if( !directoryProbable.contains(directory) && directoryProbable.contains( elements[i] ))
							{
								directoryProbable.add(directory);
								return;
							}
								
							checkDirectory( elements[i] );
						}	
					}
				}
			}
		} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
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
				
				String drawMode;
				
				if( currentMode == mode.STANDARD )
					drawMode = "Standard";
				else if( currentMode == mode.ICON_HIGHLIGHTS )
					drawMode = "Icon Highlights";
				else if( currentMode == mode.HOVER_MENUS )
					drawMode = "Hover Menus";
				else
					drawMode = "SDN";
				
				g.drawString( drawMode, 10, 15);
				
								
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
					
					if( currentMode == mode.ICON_HIGHLIGHTS || currentMode == mode.SDN )
					{
						if( directoryProbable.contains(element.name) )
							g.drawImage( highlightBuffer, element.x, element.y, this );
					}
					
					g.drawImage( bufferToUse, element.x, element.y, this);
						
					int textWidth = g.getFontMetrics().stringWidth( element.name );
					g.drawString( element.name, element.x + 65/2 - textWidth/2, element.y + 80);
					
					if( currentMode == mode.HOVER_MENUS && openHoverMenu && element.name.equals(fileSelected.name))
					{
						int compteur = 0;
						g.drawRect(element.x, element.y + 90, 150, 150);
						
						for( String str : directoryProbable )
						{
							if( compteur < 5 && !str.equals(fileSelected.name) )
							{
								g.drawRect(element.x, element.y + 90 + (150/5) * compteur, 150, 30);
								g.drawString( str, element.x + 5, 20 + element.y + 90 + (150/5) * compteur );
								compteur++;
							}
							
						}
					}
				}
	
				boolean searchWasNull = false;
				if( tfSearch == null )
				{
					tfSearch = new JTextField();
					searchWasNull = true;
				}
				
				tfSearch.setText(currentSearch);
				tfSearch.setLocation(810, 25);
				tfSearch.setSize(280,20);
				tfSearch.getDocument().addDocumentListener( new DocumentListener() {

					@Override
					public void insertUpdate(DocumentEvent e) {
						// TODO Auto-generated method stub
						if( currentDirectoryName != "" && currentMode != mode.SDN )
						{
							saveDirectory = currentDirectoryName;
							saveElements = elements;
						}
						currentSearch = tfSearch.getText();
						
						if( currentMode == mode.SDN )
						{
							directoryProbable.clear();
							for( int i = 0; i < 3; i++ )
								checkDirectory(currentDirectoryName);
						}
						else
						{
							currentDirectoryName = "";
							elements.clear();
							initFilesArray(true);
						}
						panel.repaint();
						
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						// TODO Auto-generated method stub
							
						currentSearch = tfSearch.getText();
						
						if( currentSearch.equals("") )
						{
							
							if( currentMode != mode.SDN )
							{
								currentDirectoryName = saveDirectory;
								elements.clear();
								elements = saveElements;
								initFilesArray(false);
							}
							else
								directoryProbable.clear();
						}
						else
						{
							
							if( currentMode == mode.SDN )
							{
								directoryProbable.clear();
								for( int i = 0; i < 3; i++ )
									checkDirectory(currentDirectoryName);
							}
							else
							{
								currentDirectoryName = "";
								elements.clear();
								initFilesArray(true);
							}
						}		
						panel.repaint();
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						System.out.println("change");
						// TODO Auto-generated method stub
					}
					
				});
			
				if( searchWasNull )
					panel.add(tfSearch);
			}
		};
		
		
		panel.addMouseListener( new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if( e.getX() >= 810 && e.getX() <= 810 + 280 && e.getY() >= 25 && e.getY() <= 25 + 20 )
					tfSearch.requestFocus();
				else
					panel.requestFocus();
				
				
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
				
				boolean found = false;
				
				if( currentMode == mode.HOVER_MENUS && e.getButton() == MouseEvent.BUTTON1 && openHoverMenu )
				{
					if( e.getX() >= fileSelected.x && e.getX() <= fileSelected.x + 150 && 
							e.getY() >= fileSelected.y && e.getY() <= fileSelected.y + 90 + 150 )
					{
						int compteur = 0;
						
						for( String str : directoryProbable )
						{
							if( compteur < 5 && !str.equals(fileSelected.name) )
							{
								if( e.getY() >= fileSelected.y + 90 + 30 * compteur && e.getY() <= fileSelected.y + 90 + 30 * compteur + 30 )
								{	
									oldDirectories.add(fileSelected.name);
									directoryProbable.clear();
									openHoverMenu = false;
									currentStep++;
									update(str);
									break;
								}
								compteur++;
							}
							
						}
					}
					openHoverMenu = false;
				}
				
				for( Element element : elements ) {
					
					element.x = 65 + (element.id%8)*(65+65);
					element.y = 70 + (element.id/8)*(65+60);
					
					if( e.getX() >= element.x && e.getX() <= element.x + 65 ) {
						if( e.getY() >= element.y && e.getY() <= element.y + 65 ) {
							
							if( fileSelected != null && fileSelected.id == element.id && element.isADirectory && e.getButton() == MouseEvent.BUTTON1 )
							{
								while( oldDirectories.size() > currentStep + 1 )
									oldDirectories.remove( currentStep + 1 );
								
								oldDirectories.add( element.name );
								currentStep++;
								currentDirectoryName = element.name;

								if( currentSearch != "" && currentMode != mode.SDN )
								{
									currentSearch = "";
									tfSearch.setText("");
								}
									
								update( element.name );
							}
							else
							{
								fileSelected = new Element(element.id, element.isADirectory, element.name, element.nbOpen);
								fileSelected.x = element.x;
								fileSelected.y = element.y;
								
								if( currentMode == mode.HOVER_MENUS && e.getButton() == MouseEvent.BUTTON3 && element.isADirectory )
								{
									openHoverMenu = true;
									directoryProbable.clear();
									
									for( int i = 0; i < 3; i++ )
										checkDirectory(element.name);
									
									panel.repaint();
								}
							}
								
							found = true;
							break;
						}
					}
				}
				

				if( !found )
				{
					fileSelected = null;
					openHoverMenu = false;
					panel.repaint();
				}
			}
		});
	}
	
	public void update( String newCurrentDirectoryName )
	{
		fileSelected = null;
		elements.clear();
		currentDirectoryName = newCurrentDirectoryName;
		initFilesArray(false);
		panel.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if( e.getKeyCode() == KeyEvent.VK_ALT_GRAPH )
		{
			if( currentMode.ordinal() < 3)
				currentMode = mode.values()[currentMode.ordinal() + 1];
			else
				currentMode = mode.values()[0];
			
			init();
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		// TODO Auto-generated method stub
		
	}
}
