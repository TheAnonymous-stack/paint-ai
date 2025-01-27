package ca.utoronto.utm.paint;

import java.io.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class View implements EventHandler<ActionEvent> {

	private PaintModel paintModel;
	private PaintPanel paintPanel;
	private ShapeChooserPanel shapeChooserPanel;
	private Stage stage;

	public View(PaintModel model, Stage stage) {
		this.stage = stage;
		this.paintModel = model;
		initUI(stage);
	}

	public PaintModel getPaintModel() {
		return this.paintModel;
	}

	public void setPaintModel(PaintModel paintModel) {
		this.paintModel=paintModel;
		this.paintPanel.setPaintModel(paintModel);
	}
	private void initUI(Stage stage) {

		this.paintPanel = new PaintPanel(this.paintModel);
		this.shapeChooserPanel = new ShapeChooserPanel(this);

		BorderPane root = new BorderPane();
		root.setTop(createMenuBar());
		root.setCenter(this.paintPanel);
		root.setLeft(this.shapeChooserPanel);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Paint");
		stage.show();
	}

	public PaintPanel getPaintPanel() {
		return paintPanel;
	}

	public ShapeChooserPanel getShapeChooserPanel() {
		return shapeChooserPanel;
	}

	private MenuBar createMenuBar() {

		MenuBar menuBar = new MenuBar();
		Menu menu;
		MenuItem menuItem;

		// A menu for File

		menu = new Menu("File");

		menuItem = new MenuItem("New");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Open");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Save");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menu.getItems().add(new SeparatorMenuItem());

		menuItem = new MenuItem("Exit");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		// Another menu for Edit

		menu = new Menu("Edit");

		menuItem = new MenuItem("Cut");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Copy");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Paste");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menu.getItems().add(new SeparatorMenuItem());

		menuItem = new MenuItem("Undo");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Redo");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		return menuBar;
	}

	public void setPaintPanelShapeManipulatorStrategy(ShapeManipulatorStrategy strategy) {
		this.paintPanel.setShapeManipulatorStrategy(strategy);
	}

	@Override
	public void handle(ActionEvent event) {
		System.out.println(((MenuItem) event.getSource()).getText());
		String command = ((MenuItem) event.getSource()).getText();
		if (command.equals("Open")) {
			FileChooser fc = new FileChooser();
			File file = fc.showOpenDialog(this.stage);

			if (file != null) {
				System.out.println("Opening: " + file.getName() + "." + "\n");
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()));
					PaintModel paintModel = new PaintModel();
					PaintFileParser parser = new PaintFileParser();
					if (parser.parse(bufferedReader, paintModel)){
						this.setPaintModel(paintModel);
					} else {
						System.out.println(parser.getErrorMessage());
					}

				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Open command cancelled by user." + "\n");
			}
		} else if (command.equals("Save")) {
			FileChooser fc = new FileChooser();
			File file = fc.showSaveDialog(this.stage);

			if (file != null) {
				// This is where a real application would open the file.
				System.out.println("Saving: " + file.getName() + "." + "\n");
				// Add something like the following...
				 try {
					 file.createNewFile();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
				 try {
					 PrintWriter writer = new PrintWriter(file);
					 View.save(writer, this.paintModel);
				 } catch (FileNotFoundException e) {
					 e.printStackTrace();
				}

			} else {
				System.out.println("Save command cancelled by user." + "\n");
			}
		} else if (command.equals("New")) {
			// this.paintModel.reset();
			this.setPaintModel(new PaintModel());
		} else if (command.equals("Exit")) {
			Platform.exit();
		} 
	}
	
	/**
	 * Save the given paintModel to the open file
	 * @param writer
	 * @param paintModel
	 */
	public static void save(PrintWriter writer, PaintModel paintModel) {
		writer.println("Paint Save File Version 1.0");
		for (PaintCommand c: paintModel.getCommands()) {
			String name = c.getCommandName();
			writer.println(name);
			writer.print(c.getPaintSaveFileString());
			writer.println("End "+name);

		}

		writer.println("End Paint Save File");
		writer.close();
		System.out.println("File saved successfully");
	}
}
