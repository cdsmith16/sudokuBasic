package assign3;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


 public class SudokuFrame extends JFrame implements MouseListener,ItemListener{
	 
	JTextArea source;
	JTextArea solution;
	private BorderLayout borderLayout;
	private Box box;
	JButton check;
	JCheckBox auto;
	private Sudoku sudoku;
	private String sudokuString;
	Document doc;
	
	public SudokuFrame() {
		super("Sudoku Solver");
		sudokuString = "";
		box = new Box(BoxLayout.LINE_AXIS);
		
		auto = new JCheckBox("auto");
		auto.addItemListener(this);
		box.add(auto, BoxLayout.X_AXIS);
		
		check = new JButton("check");
		check.addMouseListener(this);
		
		box.add(check, BoxLayout.X_AXIS);
		
		borderLayout = new BorderLayout(4,4);
		setLayout(borderLayout);
		
		 source = new JTextArea(15,20);
		 source.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				return;
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				doc = arg0.getDocument();
				return;
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				return;
			}
			 
		 });
		 
		 source.setBorder(new TitledBorder("source"));
		 
		 solution = new JTextArea(15,20);
		 
		 solution.setBorder(new TitledBorder("solution"));
		 
		 add(source, borderLayout.WEST);
		 add(solution, borderLayout.EAST);
		 add(box, borderLayout.SOUTH);
		// Could do this:
		setLocationByPlatform(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			Document doc = this.source.getDocument();
			sudokuString = doc.getText(0, doc.getLength());
		} catch (BadLocationException e1) {
			System.out.println(":(");
		}
		sudoku = new Sudoku(Sudoku.textToGrid(sudokuString));
		sudoku.solve();
		solution.setText(sudoku.getSolutionText() 
				+ "\n" + "Solutions:" + sudoku.count()
				+ "\n" + "Elapsed:" + sudoku.getElapsed() + "ms");
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		return;
	}
	@Override
	public void mouseExited(MouseEvent e) {
		return;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		return;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		return;
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		if(auto.isSelected()){
			try {
				Document doc = this.source.getDocument();
				sudokuString = doc.getText(0, doc.getLength());
			} catch (BadLocationException e1) {
				System.out.println(":(");
			}
			sudoku = new Sudoku(Sudoku.textToGrid(sudokuString));
			sudoku.solve();
			solution.setText(sudoku.getSolutionText() 
					+ "\n" + "Solutions:" + sudoku.count()
					+ "\n" + "Elapsed:" + sudoku.getElapsed() + "ms");
		}
		return;
	}

}
