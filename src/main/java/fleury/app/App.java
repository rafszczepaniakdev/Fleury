package fleury.app;

import javax.swing.JFrame;

public class App {
	
	public static void main(String[] args) {
		JFrame window = new JFrame("Fleury");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setContentPane(new AppMain());
        window.setResizable(false);
        window.pack();
        window.setVisible(true);
	}
	
}
