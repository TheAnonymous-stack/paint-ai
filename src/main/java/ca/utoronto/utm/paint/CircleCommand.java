package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;

public class CircleCommand extends PaintCommand {
	private Point centre;
	private int radius;

	public String getCommandName() {return "Circle";}
	public CircleCommand(Point centre, int radius){
		this.centre = centre;
		this.radius = radius;
	}
	public Point getCentre() { return centre; }
	public void setCentre(Point centre) { 
		this.centre = centre; 
		this.setChanged();
		this.notifyObservers();
	}
	public int getRadius() { return radius; }
	public void setRadius(int radius) { 
		this.radius = radius; 
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public String getPaintSaveFileString() {
		int r = (int)(this.getColor().getRed()*256);
		int g = (int)(this.getColor().getGreen()*256);
		int b = (int)(this.getColor().getBlue()*256);

		String s = "";
		s +="\tcolor:"+r+","+g+","+b+"\n";
		s +="\tfilled:"+this.isFill()+"\n";
		s += "\tcenter: ("+centre.x+","+centre.y+")\n"+
				"\tradius:"+radius+"\n";
		return s;
	}

	@Override
	public boolean isValid() {
		return radius > 0;
	}

	@Override
	public void execute(GraphicsContext g){
		int x = this.getCentre().x;
		int y = this.getCentre().y;
		int radius = this.getRadius();
		if(this.isFill()){
			g.setFill(this.getColor());
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		} else {
			g.setStroke(this.getColor());
			g.strokeOval(x-radius, y-radius, 2*radius, 2*radius);
		}
	}
}
