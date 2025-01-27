package ca.utoronto.utm.paint;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;

public class PolylineManipulatorStrategy extends ShapeManipulatorStrategy{
    PolylineManipulatorStrategy(PaintModel paintModel) { super(paintModel); }
    private PolylineCommand polylineCommand;
    private boolean isPolylineEnded = true;

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!isPolylineEnded) {
            if (this.polylineCommand.getPoints().size() > 1) {
                Point pointToBeRemoved = this.polylineCommand.getPoints().getLast();
                this.polylineCommand.remove(pointToBeRemoved);
            }
            this.polylineCommand.add(new Point((int)e.getX(), (int)e.getY()));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        MouseButton button = e.getButton();
        if (button == MouseButton.PRIMARY) {
            if (isPolylineEnded) {
                this.polylineCommand = new PolylineCommand();
                this.addCommand(polylineCommand);
                isPolylineEnded = false;
            }
            this.polylineCommand.add(new Point((int)e.getX(), (int)e.getY()));


        } else if (button == MouseButton.SECONDARY) {
            isPolylineEnded = true;

        }
    }
}
