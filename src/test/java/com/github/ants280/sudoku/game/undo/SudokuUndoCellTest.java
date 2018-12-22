package com.github.ants280.sudoku.game.undo;

import com.github.ants280.sudoku.game.SudokuCell;
import com.github.ants280.sudoku.game.SudokuValue;
import java.util.function.BiConsumer;
import org.junit.Assert;
import org.junit.Test;

public class SudokuUndoCellTest
{
	@Test
	public void testEquals_sameObject()
	{
		SudokuUndoCell sudokuCell1 = new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuUndoCell sudokuCell2 = sudokuCell1;

		boolean equals = sudokuCell1.equals(sudokuCell2);

		Assert.assertTrue(equals);
	}

	@Test
	public void testEquals_wrongObjectClass()
	{
		SudokuCell sudokuCell1 = new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuCell sudokuCell2 = new SudokuCell(1, 2, 3, SudokuValue.VALUE_4, true);

		boolean equals = sudokuCell1.equals(sudokuCell2);

		Assert.assertFalse(equals);
	}

	@Test
	public void testEquals_null()
	{
		SudokuUndoCell sudokuCell1 = new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuUndoCell sudokuCell2 = null;

		boolean equals = sudokuCell1.equals(sudokuCell2);

		Assert.assertFalse(equals);
	}

	@Test
	public void testEquals_eq()
	{
		SudokuUndoCell sudokuCell1
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuUndoCell sudokuCell2
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		BiConsumer<Boolean, Boolean> undoRedoEmptyConsumer = (a, b) ->
		{
		};
		CommandHistory<SudokuUndoCellCommand> commandHistory = new CommandHistory<>(undoRedoEmptyConsumer);
		sudokuCell1.setCommandHistory(commandHistory);
		sudokuCell2.setCommandHistory(commandHistory);

		boolean equals = sudokuCell1.equals(sudokuCell2);

		Assert.assertTrue(equals);
	}

	@Test
	public void testEquals_differentCommandHistory()
	{
		SudokuUndoCell sudokuCell1
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuUndoCell sudokuCell2
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		BiConsumer<Boolean, Boolean> undoRedoEmptyConsumer1 = (a, b) ->
		{
		};
		BiConsumer<Boolean, Boolean> undoRedoEmptyConsumer2 = (a, b) ->
		{
		};
		sudokuCell1.setCommandHistory(new CommandHistory<>(undoRedoEmptyConsumer1));
		sudokuCell2.setCommandHistory(new CommandHistory<>(undoRedoEmptyConsumer2));

		boolean equals = sudokuCell1.equals(sudokuCell2);

		Assert.assertFalse(equals);
		Assert.assertNotEquals(undoRedoEmptyConsumer1, undoRedoEmptyConsumer2);
	}

	@Test
	public void testEquals_differentData()
	{
		SudokuUndoCell sudokuCell1
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuUndoCell sudokuCell2
				= new SudokuUndoCell(5, 6, 7, SudokuValue.VALUE_8, true);

		boolean equals = sudokuCell1.equals(sudokuCell2);

		Assert.assertFalse(equals);
	}

	@Test
	public void testHashCode_sameDataAndCommandHistory()
	{
		SudokuUndoCell sudokuCell1
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuUndoCell sudokuCell2
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		CommandHistory<SudokuUndoCellCommand> commandHistory
				= new CommandHistory<>(null);
		sudokuCell1.setCommandHistory(commandHistory);
		sudokuCell2.setCommandHistory(commandHistory);

		int hashCode1 = sudokuCell1.hashCode();
		int hashCode2 = sudokuCell2.hashCode();

		Assert.assertEquals(hashCode2, hashCode1);
	}

	@Test
	public void testHashCode_differentData()
	{
		SudokuUndoCell sudokuCell1
				= new SudokuUndoCell(1, 2, 3, SudokuValue.VALUE_4, true);
		SudokuUndoCell sudokuCell2
				= new SudokuUndoCell(5, 6, 7, SudokuValue.VALUE_8, true);

		int hashCode1 = sudokuCell1.hashCode();
		int hashCode2 = sudokuCell2.hashCode();

		Assert.assertNotEquals(hashCode2, hashCode1);
	}

	@Test
	public void testTogglePossibleValue()
	{
		SudokuUndoCell sudokuCell = new SudokuUndoCell(0, 0, 0, null, false);
		CommandHistory<SudokuUndoCellCommand> commandHistory
				= new CommandHistory<>((a, b) ->
				{
				});
		sudokuCell.setCommandHistory(commandHistory);
		sudokuCell.togglePossibleValue(SudokuValue.VALUE_1);

		Assert.assertTrue(sudokuCell.hasPossibleValue(SudokuValue.VALUE_1));
		Assert.assertSame(1, commandHistory.getUndoCount());
	}
}
