package ca.utoronto.utm.paint;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class PolylineCommand extends PaintCommand {
    private ArrayList<Point> points = new ArrayList<Point>();
    public String getCommandName() {return "Polyline";}
    public void add(Point p){
        this.points.add(p);
        this.setChanged();
        this.notifyObservers();
    }
    public void remove(Point p){
        this.points.remove(p);
        this.setChanged();
        this.notifyObservers();
    }
    public ArrayList<Point> getPoints() { return this.points; }

    @Override
    public String getPaintSaveFileString() {
        int r = (int)(this.getColor().getRed()*256);
        int g = (int)(this.getColor().getGreen()*256);
        int b = (int)(this.getColor().getBlue()*256);

        String s = "";
        s +="\tcolor:"+r+","+g+","+b+"\n";
        s +="\tfilled:"+this.isFill()+"\n";
        s += "\tpoints\n";
        for (Point p : this.points) {
            s += "\t\tpoint:("+p.x+","+p.y+")\n";
        }
        s += "\tend points\n";
        return s;
    }
    @Override
    public boolean isValid() {
        return !this.points.isEmpty();
    }
    @Override
    public void execute(GraphicsContext g) {
        ArrayList<Point> points = this.getPoints();
        g.setStroke(this.getColor());
        if (points.size() == 1) {
            Point startingPoint = points.getLast();
            g.strokeLine(startingPoint.x, startingPoint.y, startingPoint.x, startingPoint.y);
        } else {
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g.strokeLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
}
