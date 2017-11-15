package com.jpatterson.school.sudoku2.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SudokuMouseListener extends MouseAdapter
{
	private final SudokuCanvas canvas;
	private final SudokuActionListener actionListener;

	public SudokuMouseListener(
		SudokuCanvas canvas, SudokuActionListener actionListener)
	{
		this.canvas = canvas;
		this.actionListener = actionListener;
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		switch (event.getButton())
		{
			case MouseEvent.BUTTON1: // left mouse button
				canvas.selectCellFromCoordinates(event.getX(), event.getY());
				if (event.getClickCount() == 2)
				{
					actionListener.setValue(null);
				}
				break;
			case MouseEvent.BUTTON3: // right mouse button
				canvas.selectCellFromCoordinates(event.getX(), event.getY());
				actionListener.setPossibleValue(null);
				break;
			default:
				break;
		}
	}
}
