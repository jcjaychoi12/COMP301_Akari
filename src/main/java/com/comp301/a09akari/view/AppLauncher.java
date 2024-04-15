package com.comp301.a09akari.view;

import com.comp301.a09akari.controller.ControllerImpl;
import com.comp301.a09akari.model.CellType;
import com.comp301.a09akari.model.ModelImpl;
import com.comp301.a09akari.SamplePuzzles;
import com.comp301.a09akari.model.Puzzle;
import com.comp301.a09akari.model.PuzzleImpl;
import com.comp301.a09akari.model.PuzzleLibrary;
import com.comp301.a09akari.model.PuzzleLibraryImpl;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AppLauncher extends Application {
  @Override
  public void start(Stage stage) {
    stage.setTitle("Play Akari!");

    Puzzle puzzle1 = new PuzzleImpl(SamplePuzzles.PUZZLE_01);
    Puzzle puzzle2 = new PuzzleImpl(SamplePuzzles.PUZZLE_02);
    Puzzle puzzle3 = new PuzzleImpl(SamplePuzzles.PUZZLE_03);
    Puzzle puzzle4 = new PuzzleImpl(SamplePuzzles.PUZZLE_04);
    Puzzle puzzle5 = new PuzzleImpl(SamplePuzzles.PUZZLE_05);

    PuzzleLibrary library = new PuzzleLibraryImpl();
    library.addPuzzle(puzzle1);
    library.addPuzzle(puzzle2);
    library.addPuzzle(puzzle3);
    library.addPuzzle(puzzle4);
    library.addPuzzle(puzzle5);

    ModelImpl model = new ModelImpl(library);

    ControllerImpl controller = new ControllerImpl(model);

    View game = new View(controller, model);

    Scene scene = new Scene(game.render(), 900, 900);
    scene.getStylesheets().add("main.css");
    stage.setScene(scene);

    model.addObserver((ModelObserver) -> {
      scene.setRoot(game.render());
    });

    stage.show();
  }
}


class View implements FXComponent {
  private ControllerImpl controller;
  private ModelImpl model;

  public View(ControllerImpl controller, ModelImpl model) {
    this.controller = controller;
    this.model = model;
  }

  @Override
  public Parent render() {
    Pane game = new VBox();

    Title title = new Title(controller, model);
    game.getChildren().add(title.render());

    Complete complete = new Complete(controller, model);
    game.getChildren().add(complete.render());

    Grid grid = new Grid(controller, model);
    game.getChildren().add(grid.render());

    Navigation navi = new Navigation(controller, model);
    game.getChildren().add(navi.render());

    game.getStyleClass().add("view");

    return game;
  }
}


class Title implements FXComponent {
  private ControllerImpl controller;
  private ModelImpl model;

  public Title(ControllerImpl controller, ModelImpl model) {
    this.controller = controller;
    this.model = model;
  }

  @Override
  public Parent render() {
    Pane box = new HBox();

    String librarySize = String.valueOf(this.model.getPuzzleLibrarySize());
    String current = String.valueOf(this.model.getActivePuzzleIndex() + 1);
    String titleStr = "AKARI - " + current + "/" + librarySize;

    Label title = new Label(titleStr);
    title.getStyleClass().add("title");
    box.getChildren().add(title);

    return box;
  }
}


class Grid implements FXComponent {
  private ControllerImpl controller;
  private ModelImpl model;

  public Grid(ControllerImpl controller, ModelImpl model) {
    this.controller = controller;
    this.model = model;
  }

  @Override
  public Parent render() {
    Puzzle puzzle = this.model.getActivePuzzle();
    int height = puzzle.getHeight();
    int width = puzzle.getWidth();

    GridPane grid = new GridPane();
    grid.setHgap(5);
    grid.setVgap(5);

    Label tile;

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        tile = makeTile(puzzle, r, c);
        grid.add(tile, c, r);
      }
    }

    return grid;
  }

  private Label makeTile(Puzzle puzzle, int r, int c) {
    CellType type = puzzle.getCellType(r, c);

    Label label;

    if (type == CellType.CLUE) {
      int clue = puzzle.getClue(r, c);
      label = new Label(String.valueOf(clue));
      label.getStyleClass().add("tile-clue");
      if (this.model.isClueSatisfied(r, c)) {
        label.getStyleClass().add("tile-clue-solved");
      }
    } else if (type == CellType.CORRIDOR) {
      label = new Label();
      if (this.model.isLit(r, c)) {
        label.getStyleClass().add("tile-corridor-lit");
        if (this.model.isLamp(r, c)) {
          Image image = new Image("light-bulb.png");
          ImageView imageView = new ImageView(image);
          imageView.setFitHeight(50);
          imageView.setPreserveRatio(true);
          label.setGraphic(imageView);
          label.getStyleClass().add("tile-bulb");
          if (this.model.isLampIllegal(r, c)) {
            label.getStyleClass().add("tile-bulb-illegal");
          }
        }
      } else {
        label.getStyleClass().add("tile-corridor-unlit");
      }
    } else {
      label = new Label();
      label.getStyleClass().add("tile-wall");
    }

    label.getStyleClass().add("tile");

    label.setOnMouseClicked((mouseEvent) -> {
      if (type == CellType.CORRIDOR) {
        this.controller.clickCell(r, c);
      }
    });


    return label;
  }
}


class Complete implements FXComponent {
  private ControllerImpl controller;
  private ModelImpl model;

  public Complete(ControllerImpl controller, ModelImpl model) {
    this.controller = controller;
    this.model = model;
  }

  @Override
  public Parent render() {
    Pane box = new HBox();
    Label label;

    if (this.model.isSolved()) {
      label = new Label("Solved");
    } else {
      label = new Label();
    }

    label.getStyleClass().add("solved");
    box.getChildren().add(label);

    return box;
  }
}


class Navigation implements FXComponent {
  private ControllerImpl controller;
  private ModelImpl model;

  public Navigation(ControllerImpl controller, ModelImpl model) {
    this.controller = controller;
    this.model = model;
  }
  @Override
  public Parent render() {
    Pane main = new HBox();
    Label prev = new Label("Previous");
    Label rand = new Label("Random");
    Label reset = new Label("Reset");
    Label next = new Label("Next");

    main.getChildren().add(prev);
    main.getChildren().add(rand);
    main.getChildren().add(reset);
    main.getChildren().add(next);

    prev.getStyleClass().add("navigation");
    rand.getStyleClass().add("navigation");
    reset.getStyleClass().add("navigation");
    next.getStyleClass().add("navigation");

    prev.setOnMouseClicked((mouseEvent) -> {
      this.controller.clickPrevPuzzle();
    });

    rand.setOnMouseClicked((mouseEvent) -> {
      this.controller.clickRandPuzzle();
    });

    reset.setOnMouseClicked((mouseEvent) -> {
      this.controller.clickResetPuzzle();
    });

    next.setOnMouseClicked((mouseEvent) -> {
      this.controller.clickNextPuzzle();
    });

    return main;
  }
}
