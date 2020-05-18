
/**
 *
 * @author Heinemann
 */

public class GameMain extends javax.swing.JFrame{
	GameMain(String title){
        super(title);
        setContentPane(new GamePanel());
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(510,510);
        setPreferredSize(new java.awt.Dimension(500,500));
        setLocationRelativeTo(null);
        setLayout(null);
    }
    public static void main(String[] args){
        new GameMain("Block Breaker").setVisible(true);
    }
}
