package ca.utoronto.utm.paint;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.canvas.GraphicsContext;

public class PaintModel extends Observable implements Observer {

	public void save(PrintWriter writer) {
		
	}
	public void reset(){
		for(PaintCommand c: this.commands){
			c.deleteObserver(this);
		}
		this.commands.clear();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void addCommand(PaintCommand command){
		this.commands.add(command);
		command.addObserver(this);
		this.setChanged();
		this.notifyObservers();
	}
	
	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();

	public void executeAll(GraphicsContext g) {
		for(PaintCommand c: this.commands){
			c.execute(g);
		}
	}

	public ArrayList<PaintCommand> getCommands() { return commands; }

	public void checkCommandValidity() {
		// this function is meant to make sure that the user
		// created a valid shape before switching to a different mode
		// Recall that a PaintCommand is created when user presses mouse onto the PaintPanel
		// so if user immediately switches to a different mode afterwards, that PaintCommand
		// should be removed
		boolean commandRemoved = false;
		for(PaintCommand c: this.commands){
			if (!c.isValid()) {
				this.commands.remove(c);
				commandRemoved = true;
			}
		}
		if (commandRemoved) {
			this.setChanged();
			this.notifyObservers();
		}
	}
	/**
	 * We Observe our model components, the PaintCommands
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers();
	}
}
