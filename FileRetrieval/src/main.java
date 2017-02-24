import javax.swing.JFrame;

public class main {

	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		window.setTitle("Improving navigation-based file retrieval");
		window.setSize(1116, 677);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Interface inter = new Interface();
		window.add( inter.panel );

		window.setVisible(true);
	}
}
