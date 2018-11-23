package com.github.ants280.sudoku.ui;

import com.github.ants280.sudoku.game.SectionType;
import com.github.ants280.sudoku.game.SudokuBoard;
import com.github.ants280.sudoku.game.SudokuCell;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class SudokuUiManager implements ActionListener
{
	private static final String FILE_M = "File";
	private static final String RESTART_MI = "Restart";
	private static final String LOAD_MI = "Load Game...";
	private static final String EXPORT_MI = "Export Game...";
	private static final String EXIT_MI = "Exit";
	private static final String ACTION_M = "Action";
	private static final String SET_VALUE_MI = "Set value...";
	private static final String SET_POSSIBLE_VALUE_MI = "Set possible value...";
	private static final String SOLVE_MI = "Solve...";
	private static final String HELP_M = "Help";
	private static final String HELP_MI = "Help";
	private static final String ABOUT_MI = "About";

	private final JFrame frame;
	private final SudokuDisplayComponent sudokuDisplayComponent;
	private final SudokuBoard board;
	private final Collection<JMenuItem> selectedCellMenuItems;
	private final SudokuBoard initialBoard;
	private final Map<String, Runnable> actionCommands;
	private final SudokuMouseListener mouseListener;
	private final SudokuKeyListener keyListener;
	private boolean listenersAdded;

	private SudokuUiManager(
			JFrame frame,
			SudokuDisplayComponent sudokuDisplayComponent,
			SudokuBoard board,
			Collection<JMenuItem> selectedCellMenuItems)
	{

		this.frame = frame;
		this.sudokuDisplayComponent = sudokuDisplayComponent;
		this.board = board;
		this.initialBoard = new SudokuBoard(board.toString());
		this.selectedCellMenuItems = selectedCellMenuItems;
		this.actionCommands = this.createActionCommands();
		this.mouseListener = new SudokuMouseListener(
				this::selectCell,
				this::setValue,
				this::setPossibleValue);
		this.keyListener = new SudokuKeyListener(
				this::setSelectedCellValue,
				this::moveSelectedCell);
		this.listenersAdded = false;
	}

	public static void manage(
			SudokuFrame frame,
			SudokuDisplayComponent sudokuDisplayComponent,
			SudokuBoard board,
			JMenu fileMenu,
			JMenuItem restartMenuItem,
			JMenuItem loadMenuItem,
			JMenuItem exportMenuItem,
			JMenuItem exitMenuItem,
			JMenu actionMenu,
			JMenuItem setValueMenuItem,
			JMenuItem setPossibleValueMenuItem,
			JMenuItem solveMenuItem,
			JMenu helpMenu,
			JMenuItem helpMenuItem,
			JMenuItem aboutMenuItem)
	{
		SudokuUiManager sudokuActionListener
				= new SudokuUiManager(
						frame,
						sudokuDisplayComponent,
						board,
						Arrays.asList(
								setValueMenuItem,
								setPossibleValueMenuItem));

		sudokuActionListener.initMenu(
				fileMenu,
				restartMenuItem,
				loadMenuItem,
				exportMenuItem,
				exitMenuItem,
				actionMenu,
				setValueMenuItem,
				setPossibleValueMenuItem,
				solveMenuItem,
				helpMenu,
				helpMenuItem,
				aboutMenuItem);

		sudokuActionListener.addListeners();
	}

	private Map<String, Runnable> createActionCommands()
	{
		Map<String, Runnable> tempActionCommands = new HashMap<>();
		tempActionCommands.put(RESTART_MI, this::restart);
		tempActionCommands.put(LOAD_MI, this::load);
		tempActionCommands.put(EXPORT_MI, this::export);
		tempActionCommands.put(EXIT_MI, this::exit);
		tempActionCommands.put(SET_VALUE_MI, this::setValue);
		tempActionCommands.put(SET_POSSIBLE_VALUE_MI, this::setPossibleValue);
		tempActionCommands.put(SOLVE_MI, this::solve);
		tempActionCommands.put(HELP_MI, this::help);
		tempActionCommands.put(ABOUT_MI, this::about);
		return tempActionCommands;
	}

	private void initMenu(
			JMenu fileMenu,
			JMenuItem restartMenuItem,
			JMenuItem loadMenuItem,
			JMenuItem exportMenuItem,
			JMenuItem exitMenuItem,
			JMenu actionMenu,
			JMenuItem setValueMenuItem,
			JMenuItem setPossibleValueMenuItem,
			JMenuItem solveMenuItem,
			JMenu helpMenu,
			JMenuItem helpMenuItem,
			JMenuItem aboutMenuItem)
	{
		fileMenu.setText(FILE_M);
		restartMenuItem.setText(RESTART_MI);
		loadMenuItem.setText(LOAD_MI);
		exportMenuItem.setText(EXPORT_MI);
		exitMenuItem.setText(EXIT_MI);
		actionMenu.setText(ACTION_M);
		setValueMenuItem.setText(SET_VALUE_MI);
		setPossibleValueMenuItem.setText(SET_POSSIBLE_VALUE_MI);
		solveMenuItem.setText(SOLVE_MI);
		helpMenu.setText(HELP_M);
		helpMenuItem.setText(HELP_MI);
		aboutMenuItem.setText(ABOUT_MI);

		restartMenuItem.addActionListener(this);
		loadMenuItem.addActionListener(this);
		exportMenuItem.addActionListener(this);
		exitMenuItem.addActionListener(this);
		setValueMenuItem.addActionListener(this);
		setPossibleValueMenuItem.addActionListener(this);
		solveMenuItem.addActionListener(this);
		helpMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
	}

	private void addListeners()
	{
		if (!listenersAdded)
		{
			listenersAdded = true;
			frame.addKeyListener(keyListener);
			sudokuDisplayComponent.addMouseListener(mouseListener);
			selectedCellMenuItems
					.forEach(menuItem -> menuItem.setEnabled(false));
		}
	}

	private void removeListeners()
	{
		if (listenersAdded)
		{
			listenersAdded = false;
			frame.removeKeyListener(keyListener);
			sudokuDisplayComponent.removeMouseListener(mouseListener);
		}
	}

	private void startGame()
	{
		this.addListeners();
		selectedCellMenuItems
				.forEach(menuItem -> menuItem.setEnabled(false));
		sudokuDisplayComponent.removeSelectedCell();
	}

	private void endGame()
	{
		this.removeListeners();
		selectedCellMenuItems.forEach(menuItem -> menuItem.setEnabled(false));
		sudokuDisplayComponent.removeSelectedCell();
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent)
	{
		actionCommands.get(actionEvent.getActionCommand()).run();
	}

	private void restart()
	{
		board.resetFrom(initialBoard);
		sudokuDisplayComponent.repaint();

		this.startGame();
	}

	private void exit()
	{
		Runtime.getRuntime().exit(0);
	}

	private void help()
	{
		JOptionPane.showMessageDialog(
				frame,
				"Complete the grid,"
				+ "\nso that every row,"
				+ "\ncolumn, and 3x3 box"
				+ "\ncontains every"
				+ "\ndigit from 1 to 9"
				+ "\ninclusively.",
				"Help for " + frame.getTitle(),
				JOptionPane.QUESTION_MESSAGE);
	}

	private void about()
	{
		JOptionPane.showMessageDialog(
				frame,
				"(c) 2017 Jacob Patterson"
				+ "\n"
				+ "\nDescription taken from my newspaper,"
				+ "\n(c) Universal Uclick",
				"About " + frame.getTitle(),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void selectCell(int x, int y)
	{
		sudokuDisplayComponent.selectCellFromCoordinates(x, y);
		selectedCellMenuItems.forEach(menuItem -> menuItem.setEnabled(true));
	}

	private void moveSelectedCell(MoveDirection moveDirection)
	{
		switch (moveDirection)
		{
			case UP:
				sudokuDisplayComponent.incrementSelectedRow(-1);
				break;
			case DOWN:
				sudokuDisplayComponent.incrementSelectedRow(1);
				break;
			case LEFT:
				sudokuDisplayComponent.incrementSelectedCol(-1);
				break;
			case RIGHT:
				sudokuDisplayComponent.incrementSelectedCol(1);
				break;
			default:
				throw new IllegalArgumentException(
						"Unknown moveDirection: " + moveDirection);
		}
	}

	private void setValue()
	{
		this.showValueDialog(
				"[value]",
				"Select cell value",
				sudokuCell -> true,
				(selectedSudokuCell, v)
				-> v.equals(selectedSudokuCell.getValue()),
				this::setSudokuCellValue,
				true);
	}

	private void setPossibleValue()
	{
		this.showValueDialog(
				"[possible values]",
				"Select possible\n"
				+ "cell values",
				sudokuCell -> sudokuCell.getValue() == null,
				(selectedSudokuCell, v)
				-> selectedSudokuCell.getPossibleValues().contains(v),
				this::toggleSudokuCellPossibleValue,
				false);
	}

	private void showValueDialog(
			String title,
			String message,
			Predicate<SudokuCell> canSetValuePredicate,
			BiPredicate<SudokuCell, Integer> buttonSelectedFunction,
			BiConsumer<SudokuCell, Integer> valueClickConsumer,
			boolean closeOnDialogOnButtonClick)
	{
		Integer r = sudokuDisplayComponent.getSelectedRow();
		Integer c = sudokuDisplayComponent.getSelectedCol();
		if (r != null && c != null
				&& canSetValuePredicate.test(
						board.getSudokuCells(SectionType.ROW, r).get(c)))
		{
			SelectSudokuCellDialog selectSudokuCellDialog
					= new SelectSudokuCellDialog(
							frame,
							title,
							message,
							buttonSelectedFunction,
							valueClickConsumer,
							closeOnDialogOnButtonClick,
							board.getSudokuCells(SectionType.ROW, r).get(c));

			selectSudokuCellDialog.setVisible(true);
		}
	}

	private void setSudokuCellValue(SudokuCell sudokuCell, Integer v)
	{
		boolean valueChanged = sudokuCell.setValue(v);

		if (valueChanged)
		{
			sudokuDisplayComponent.repaint();

			if (board.isSolved())
			{
				this.endGame();
			}
		}
	}

	private void toggleSudokuCellPossibleValue(
			SudokuCell sudokuCell,
			Integer v)
	{
		boolean possibleValueChanged
				= sudokuCell.getPossibleValues().contains(v)
				? sudokuCell.removePossibleValue(v)
				: sudokuCell.addPossibleValue(v);

		if (possibleValueChanged)
		{
			sudokuDisplayComponent.repaint();
		}
	}

	private void setSelectedCellValue(Integer cellValue)
	{
		SudokuCell selectedSudokuCell = board.getSudokuCells(
				SectionType.ROW,
				sudokuDisplayComponent.getSelectedRow())
				.get(sudokuDisplayComponent.getSelectedCol());

		this.setSudokuCellValue(selectedSudokuCell, cellValue);
	}

	private void solve()
	{
		SudokuSolverPopup sudokuSolverPopup
				= new SudokuSolverPopup(
						frame,
						board,
						sudokuDisplayComponent::repaint);

		sudokuSolverPopup.setVisible(true);
	}

	private void load()
	{
		Object boardToLoad = JOptionPane.showInputDialog(
				frame,
				"Enter a saved game to load.",
				"Load " + frame.getTitle(),
				JOptionPane.INFORMATION_MESSAGE,
				null, // Icon
				null, // selectionValues (null implies textbox
				null); // initialSelectionValue

		if (boardToLoad != null)
		{
			if (SudokuBoard.isValidSavedBoard(boardToLoad.toString()))
			{
				SudokuBoard loadedBoard
						= new SudokuBoard(boardToLoad.toString());
				board.resetFrom(loadedBoard);
				initialBoard.resetFrom(board);

				sudokuDisplayComponent.repaint();

				this.addListeners();
			}
			else
			{
				JOptionPane.showMessageDialog(
						frame,
						"Error loading board.\n"
						+ "It should be something like '{<81 digits>}' "
						+ "(without quotes).",
						"Invalid Board for " + frame.getTitle(),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void export()
	{
		JOptionPane.showInputDialog(
				frame,
				"Copy the game state to load later.\n"
				+ "Warning: this does not save possible values.",
				"Export " + frame.getTitle(),
				JOptionPane.INFORMATION_MESSAGE,
				null, // Icon
				null, // selectionValues (null implies textbox
				board.toString());
	}
}
