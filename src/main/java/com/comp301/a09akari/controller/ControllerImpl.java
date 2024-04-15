package com.comp301.a09akari.controller;

import com.comp301.a09akari.model.CellType;
import com.comp301.a09akari.model.Model;
import com.comp301.a09akari.model.Puzzle;

import java.util.*;

public class ControllerImpl implements ClassicMvcController {

  private Model model;

  public ControllerImpl(Model model) {
    if (model == null) {
      throw new IllegalArgumentException();
    }

    this.model = model;
  }

  @Override
  public void clickNextPuzzle() {
    int size = this.model.getPuzzleLibrarySize();
    int currentIndex = this.model.getActivePuzzleIndex();

    if (currentIndex + 1 >= size) {
      this.model.setActivePuzzleIndex(size - 1);
    } else {
      this.model.setActivePuzzleIndex(currentIndex + 1);
    }
  }

  @Override
  public void clickPrevPuzzle() {
    int currentIndex = this.model.getActivePuzzleIndex();

    if (currentIndex - 1 < 0) {
      this.model.setActivePuzzleIndex(0);
    } else {
      this.model.setActivePuzzleIndex(currentIndex - 1);
    }
  }

  @Override
  public void clickRandPuzzle() {
    int size = this.model.getPuzzleLibrarySize();
    int current = this.model.getActivePuzzleIndex();
    Random rand = new Random();
    int randInt = rand.nextInt(size);

    if (randInt == current) {
      this.clickRandPuzzle();
    } else {
      this.model.setActivePuzzleIndex(randInt);
    }
  }

  @Override
  public void clickResetPuzzle() {
    this.model.resetPuzzle();
  }

  @Override
  public void clickCell(int r, int c) {
    Puzzle currentPuzzle = this.model.getActivePuzzle();

    if (currentPuzzle.getCellType(r, c) == CellType.CORRIDOR) {
      if (this.model.isLamp(r, c)) {
        this.model.removeLamp(r, c);
      } else {
        this.model.addLamp(r, c);
      }
    }
  }
}
