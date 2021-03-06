package com.github.ants280.sudoku.ui;

import com.github.ants280.sudoku.game.SudokuBoard;
import com.github.ants280.sudoku.game.SudokuCell;
import com.github.ants280.sudoku.game.SudokuEvent;
import com.github.ants280.sudoku.game.SudokuValue;
import com.github.ants280.sudoku.game.undo.CommandHistory;
import com.github.ants280.sudoku.game.undo.SudokuCellChangeType;
import com.github.ants280.sudoku.game.undo.SudokuCellUndoCommand;
import static com.github.ants280.sudoku.ui.SudokuUiManager.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

public class SudokuFrame
{
	private final JFrame frame;
	private final JMenuItem undoMenuItem;
	private final JMenuItem redoMenuItem;
	private final JMenu setValueMenu;
	private final JMenu setPossibleValueMenu;
	private final JMenuItem hintMenuItem;
	private final CommandHistory<SudokuCellUndoCommand> commandHistory;

	public SudokuFrame()
	{
		this.frame = new JFrame("Sudoku");
		this.undoMenuItem = new JMenuItem();
		this.redoMenuItem = new JMenuItem();
		this.setValueMenu = new JMenu();
		this.setPossibleValueMenu = new JMenu();
		this.hintMenuItem = new JMenuItem();
		this.commandHistory = new CommandHistory<>();

		this.init();
	}

	private void init()
	{
		JLabel messageLabel = new BorderedLabel();
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(Box.createGlue());
		topPanel.add(messageLabel);
		topPanel.add(Box.createGlue());

		SudokuBoard board = new SudokuBoard();
		SudokuDisplayComponent sudokuDisplayComponent
				= new SudokuDisplayComponent(board);

		SudokuUiManager sudokuUiManager
				= new SudokuUiManager(
						frame,
						sudokuDisplayComponent,
						board,
						messageLabel,
						commandHistory,
						setValueMenu,
						setPossibleValueMenu);
		board.addSolvedChangedConsumer(this::handleSolvedChangedConsumer);
		board.addCellValueChangedConsumer(this::handleCellValueChanged);
		board.addCellPossibleValueChangedConsumer(
				this::handleCellPossibleValueChanged);
		sudokuDisplayComponent.addSelectedCellChangedConsumer(
				this::handleSelectedCellChanged);
		commandHistory.addUndoEmptyChangedConsumer(
				this::handleUndoEmptyChangedConsumer);
		commandHistory.addRedoEmptyChangedConsumer(
				this::handleRedoEmptyChangedConsumer);

		undoMenuItem.setEnabled(false);
		redoMenuItem.setEnabled(false);
		undoMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(
						KeyEvent.VK_Z,
						KeyEvent.CTRL_DOWN_MASK));
		redoMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(
						KeyEvent.VK_Y,
						KeyEvent.CTRL_DOWN_MASK));

		frame.setJMenuBar(createMenu(sudokuUiManager));
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(sudokuDisplayComponent.getComponent());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JMenuBar createMenu(ActionListener actionListener)
	{
		JMenu fileMenu = new JMenu(FILE_M);
		JMenuItem restartMenuItem = new JMenuItem(RESTART_MI);
		JMenuItem loadMenuItem = new JMenuItem(LOAD_MI);
		JMenuItem exportMenuItem = new JMenuItem(EXPORT_MI);
		JMenuItem exitMenuItem = new JMenuItem(EXIT_MI);
		JMenu actionMenu = new JMenu(ACTION_M);
		undoMenuItem.setText(UNDO_MI);
		redoMenuItem.setText(REDO_MI);
		setValueMenu.setText(SET_VALUE_MI);
		setPossibleValueMenu.setText(SET_POSSIBLE_VALUE_MI);
		hintMenuItem.setText(HINT_MI);
		JMenuItem clearPossibleValuesMenuItem
				= new JMenuItem(CLEAR_POSSIBLE_VALUES_MI);
		JMenuItem clearCellsMenuItem = new JMenuItem(CLEAR_CELLS_MI);
		JMenuItem lockCellsMenuItem = new JMenuItem(LOCK_CELLS_MI);
		JMenuItem unLockCellsMenuItem = new JMenuItem(UNLOCK_CELLS_MI);
		JMenuItem solveLogicMenuItem = new JMenuItem(SOLVE_LOGIC_MI);
		JMenuItem solveBruteForceMenuItem = new JMenuItem(SOLVE_BRUTE_FORCE_MI);
		JMenu helpMenu = new JMenu(HELP_M);
		JMenuItem helpMenuItem = new JMenuItem(HELP_MI);
		JMenuItem aboutMenuItem = new JMenuItem(ABOUT_MI);

		fileMenu.add(restartMenuItem);
		fileMenu.add(loadMenuItem);
		fileMenu.add(exportMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		actionMenu.add(undoMenuItem);
		actionMenu.add(redoMenuItem);
		actionMenu.add(setValueMenu);
		actionMenu.add(setPossibleValueMenu);
		actionMenu.add(clearPossibleValuesMenuItem);
		actionMenu.addSeparator();
		actionMenu.add(hintMenuItem);
		actionMenu.addSeparator();
		actionMenu.add(clearCellsMenuItem);
		actionMenu.add(lockCellsMenuItem);
		actionMenu.add(unLockCellsMenuItem);
		actionMenu.addSeparator();
		actionMenu.add(solveLogicMenuItem);
		actionMenu.add(solveBruteForceMenuItem);
		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);

		JMenuBar menuBar = new JMenuBar();

		menuBar.add(fileMenu);
		menuBar.add(actionMenu);
		menuBar.add(helpMenu);

		for (int index = 0; index < menuBar.getMenuCount(); index++)
		{
			JMenu menu = menuBar.getMenu(index);
			Arrays.stream(menu.getMenuComponents())
					.filter(component -> component instanceof JMenuItem)
					.forEach(jMenuItem -> ((JMenuItem) jMenuItem)
					.addActionListener(actionListener));
		}

		return menuBar;
	}

	public JFrame getFrame()
	{
		return frame;
	}

	private static class BorderedLabel extends JLabel
	{
		private static final Border EMPTY_BORDER
				= BorderFactory.createEmptyBorder(0, 0, 0, 0);
		private static final Border TOP_BOTTOM_BORDER
				= BorderFactory.createEmptyBorder(10, 0, 10, 0);
		private static final long serialVersionUID = 1L;

		@Override
		public void setText(String text)
		{
			super.setText(text);
			this.setBorder(text == null || text.isEmpty()
					? EMPTY_BORDER
					: TOP_BOTTOM_BORDER);
		}
	}

	private void handleSolvedChangedConsumer(
			SudokuEvent<SudokuBoard, Boolean> solvedChangedEvent)
	{
		hintMenuItem.setEnabled(!solvedChangedEvent.getNewValue());
	}

	private void handleCellValueChanged(
			SudokuEvent<SudokuCell, SudokuValue> cellValueChangedEvent)
	{
		setValueMenu.setEnabled(
				Objects.equals(
						cellValueChangedEvent.getOldValue(),
						cellValueChangedEvent.getNewValue()));

		SudokuCellUndoCommand sudokuCellUndoCommand
				= new SudokuCellUndoCommand(
						cellValueChangedEvent,
						SudokuCellChangeType.SET_VALUE);
		commandHistory.addCommand(sudokuCellUndoCommand);
	}

	private void handleCellPossibleValueChanged(
			SudokuEvent<SudokuCell, SudokuValue> cellPossibleValueChangedEvent)
	{
		setPossibleValueMenu.setEnabled(
				Objects.equals(
						cellPossibleValueChangedEvent.getOldValue(),
						cellPossibleValueChangedEvent.getNewValue()));

		SudokuCellUndoCommand sudokuCellUndoCommand
				= new SudokuCellUndoCommand(
						cellPossibleValueChangedEvent,
						SudokuCellChangeType.TOGGLE_POSSIBLE_VALUE);
		commandHistory.addCommand(sudokuCellUndoCommand);
	}

	private void handleSelectedCellChanged(
			SudokuEvent<?, SudokuCell> selectedCellChangedEvent)
	{
		SudokuCell selectedCell = selectedCellChangedEvent.getNewValue();
		setValueMenu.setEnabled(selectedCell != null);
		setPossibleValueMenu.setEnabled(
				selectedCell != null && selectedCell.getValue() == null);
	}

	private void handleUndoEmptyChangedConsumer(
			SudokuEvent<?, Boolean> undoEmptyChangedEvent)
	{
		undoMenuItem.setEnabled(!undoEmptyChangedEvent.getNewValue());
	}

	private void handleRedoEmptyChangedConsumer(
			SudokuEvent<?, Boolean> redoEmptyChangedEvent)
	{
		redoMenuItem.setEnabled(!redoEmptyChangedEvent.getNewValue());
	}
}
