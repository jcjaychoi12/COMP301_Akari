package com.comp301.a09akari.model;

public class PuzzleImpl implements Puzzle {

  private int[][] board;

  public PuzzleImpl(int[][] board) {
    this.board = board.clone();
  }

  @Override
  public int getWidth() {
    return this.board[0].length;
  }

  @Override
  public int getHeight() {
    return this.board.length;
  }

  @Override
  public CellType getCellType(int r, int c) {
    if ((r < 0) || (c < 0) || (r >= this.getHeight()) || (c >= this.getWidth())) {
      throw new IndexOutOfBoundsException();
    }

    int cell = this.board[r][c];

    if ((cell >= 0) && (cell <= 4)) {
      return CellType.CLUE;
    } else if (cell == 5) {
      return CellType.WALL;
    } else {
      return CellType.CORRIDOR;
    }
  }

  @Override
  public int getClue(int r, int c) {
    if ((r < 0) || (c < 0) || (r >= this.getHeight()) || (c >= this.getWidth())) {
      throw new IndexOutOfBoundsException();
    }

    int cell = this.board[r][c];

    if ((cell < 0) || (cell > 4)) {
      throw new IllegalArgumentException();
    } else {
      return cell;
    }
  }
}
