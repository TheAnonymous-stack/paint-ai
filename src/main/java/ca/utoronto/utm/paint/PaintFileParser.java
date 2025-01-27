package ca.utoronto.utm.paint;

import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel; 
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");

	private Pattern pShapeColor = Pattern.compile("^color:(0*[1-9]?[0-9]?|0*1[0-9][0-9]|0*2[0-4][0-9]|0*25[0-5]),(0*[1-9]?[0-9]?|0*1[0-9][0-9]|0*2[0-4][0-9]|0*25[0-5]),(0*[1-9]?[0-9]?|0*1[0-9][0-9]|0*2[0-4][0-9]|0*25[0-5])");
	private Pattern pShapeIsFilled = Pattern.compile("^filled:(true|false)$");

	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pCircleCenter = Pattern.compile("^center:[(](-?\\d+),(-?\\d+)[)]$");
	private Pattern pCircleRadius = Pattern.compile("^radius:(\\d+)$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");

	private Pattern pRectangleStart=Pattern.compile("^Rectangle$");
	private Pattern pRectangleP1 = Pattern.compile("^p1:[(](-?\\d+),(-?\\d+)[)]$");
	private Pattern pRectangleP2 = Pattern.compile("^p2:[(](-?\\d+),(-?\\d+)[)]$");
	private Pattern pRectangleEnd=Pattern.compile("^EndRectangle$");

	private Pattern pSquiggleStart=Pattern.compile("^Squiggle$");
	private Pattern pSquigglePointsStart = Pattern.compile("^points$");
	private Pattern pSquigglePoint = Pattern.compile("^point:[(](-?\\d+),(-?\\d+)[)]$");
	private Pattern pSquigglePointsEnd = Pattern.compile("^endpoints$");
	private Pattern pSquiggleEnd=Pattern.compile("^EndSquiggle$");

	private Pattern pPolylineStart=Pattern.compile("^Polyline$");
	private Pattern pPolylinePointsStart = Pattern.compile("^points$");
	private Pattern pPolylinePoint = Pattern.compile("^point:[(](-?\\d+),(-?\\d+)[)]$");
	private Pattern pPolylinePointsEnd = Pattern.compile("^endpoints$");
	private Pattern pPolylineEnd=Pattern.compile("^EndPolyline$");
	// ADD MORE!!
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}
	
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}

	/**
	 * Parse the specified file
	 * @param fileName
	 * @return
	 */
	public boolean parse(String fileName){
		boolean retVal = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			PaintModel pm = new PaintModel();
			retVal = this.parse(br, pm);
		} catch (FileNotFoundException e) {
			error("File Not Found: "+fileName);
		} finally {
			try { br.close(); } catch (Exception e){};
		}
		return retVal;
	}

	/**
	 * Parse the specified inputStream as a Paint Save File Format file.
	 * @param inputStream
	 * @return
	 */
	public boolean parse(BufferedReader inputStream){
		PaintModel pm = new PaintModel();
		return this.parse(inputStream, pm);
	}

	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following commands. As we parse the file, we modify 
		// the appropriate command.
		
		CircleCommand circleCommand = null; 
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
		PolylineCommand polylineCommand = null;
	
		try {	
			int state=0; Matcher m; String l;
			boolean filled = false;
			int red = 0, green = 0, blue = 0;
			Point center = new Point(0,0);
			int radius = 0;
			ArrayList<Point> points = new ArrayList<Point>();
			Point p1 = new Point(0,0);
			Point p2 = new Point(0,0);
			int attributeState = 0;
			String currentShape = "";
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				this.lineNumber++;
				l = l.replaceAll("\\s","");
				if (l.isEmpty()) continue;
				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: // Looking for the start of a new object or end of the save file
						m=pCircleStart.matcher(l);
						if(m.matches()){
							// ADD CODE!!!
							attributeState = 4;
							currentShape = "Circle";
							state=2;
							break;
						}
						m=pRectangleStart.matcher(l);
						if (m.matches()) {
							attributeState = 7;
							currentShape = "Rectangle";
							state=2;
							break;
						}
						m=pSquiggleStart.matcher(l);
						if (m.matches()) {
							attributeState=10;
							currentShape = "Squiggle";
							state = 2;
							squiggleCommand = new SquiggleCommand();
							break;
						}

						m = pPolylineStart.matcher(l);
						if (m.matches()) {
							attributeState = 13;
							currentShape = "Polyline";
							state = 2;
							polylineCommand = new PolylineCommand();
							break;
						}
						m = pFileEnd.matcher(l);
						if (m.matches()) {
							state=16;
							break;
						}
						error("Expected Start of Shape or End Paint Save File");
						return false;
					case 2: // get Shape color
						// ADD CODE
						m=pShapeColor.matcher(l);
						if (m.matches()) {
							red = Integer.parseInt(m.group(1));
							blue = Integer.parseInt(m.group(2));
							green = Integer.parseInt(m.group(3));
							state = 3;
							break;
						}

						error("Expected valid "+currentShape+" color");
						return false;
					case 3: // get Shape filled
						m=pShapeIsFilled.matcher(l);
						if (m.matches()) {
							filled = m.group(1).equals("true");
							state = attributeState;
							break;
						}

						error("Expected "+currentShape+" filled");
						return false;
					case 4:
						m=pCircleCenter.matcher(l);
						if (m.matches()) {
							center = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							state = 5;
							break;
						}

						error("Expected Circle center");
						return false;
					case 5:
						m=pCircleRadius.matcher(l);
						if (m.matches()) {
							radius = Integer.parseInt(m.group(1));
							state = 6;
							break;
						}

						error("Expected Circle Radius");
						return false;
					case 6:
						m=pCircleEnd.matcher(l);
						if (m.matches()) {
							circleCommand = new CircleCommand(center, radius);
							circleCommand.setColor(Color.rgb(red, blue, green));
							circleCommand.setFill(filled);
							this.paintModel.addCommand(circleCommand);
							state=1; // restart the checking shape process
							break;
						}

						error("Expected End Circle");
						return false;

					case 7:
						m=pRectangleP1.matcher(l);
						if (m.matches()) {
							p1 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							state = 8;
							break;
						}

						error("Expected Rectangle p1");
						return false;
					case 8:
						m=pRectangleP2.matcher(l);
						if (m.matches()) {
							p2 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							state = 9;
							break;
						}

						error("Expected Rectangle p2");
						return false;
					case 9:
						m=pRectangleEnd.matcher(l);
						if (m.matches()) {
							rectangleCommand = new RectangleCommand(p1, p2);
							rectangleCommand.setColor(Color.rgb(red, blue, green));
							rectangleCommand.setFill(filled);
							this.paintModel.addCommand(rectangleCommand);
							state=1;
							break;
						}

						error("Expected End Rectangle");
						return false;
					case 10:
						m=pSquigglePointsStart.matcher(l);
						if (m.matches()) {
							state = 11;
							break;
						}

						error("Expected Squiggle points");
						return false;
					case 11:
						m=pSquigglePoint.matcher(l);
						if (m.matches()) {
							squiggleCommand.add(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
							break;
						}
						m = pSquigglePointsEnd.matcher(l);
						if (m.matches()){
							state = 12;
							break;
						}

						error("Expected Squiggle point or end points");
						return false;
					case 12:
						m=pSquiggleEnd.matcher(l);
						if (m.matches()) {
							squiggleCommand.setColor(Color.rgb(red, blue, green));
							squiggleCommand.setFill(filled);
							this.paintModel.addCommand(squiggleCommand);
							state = 1;
							break;
						}

						error("Expected End Squiggle");
						return false;
					case 13:
						m=pPolylinePointsStart.matcher(l);
						if (m.matches()) {
							state = 14;
							break;
						}

						error("Expected Polyline points");
						return false;
					case 14:
						m=pPolylinePoint.matcher(l);
						if (m.matches()) {
							polylineCommand.add(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
							break;
						}
						m = pPolylinePointsEnd.matcher(l);
						if (m.matches()){
							state = 15;
							break;
						}

						error("Expected Polyline point or end points");
						return false;
					case 15:
						m = pPolylineEnd.matcher(l);
						if (m.matches()) {
							polylineCommand.setColor(Color.rgb(red, blue, green));
							polylineCommand.setFill(filled);
							this.paintModel.addCommand(polylineCommand);
							state=1;
							break;
						}

						error("Expected End Polyline");
						return false;
					case 16:
						error("Extra content after End of File");
						return false;

					/**
					 * I have around 20+/-5 cases in my FSM. If you have too many
					 * more or less, you are doing something wrong. Too few, and I bet I can find
					 * a bad file that you will say is good. Too many and you are not capturing the right concepts.
					 *
					 * Here are the errors I catch. All of these should be in your code.
					 *
					 	error("Expected Start of Paint Save File");
						error("Expected Start of Shape or End Paint Save File");
						error("Expected Circle color");
						error("Expected Circle filled");
						error("Expected Circle center");
						error("Expected Circle Radius");
						error("Expected End Circle");
						error("Expected Rectangle color");
						error("Expected Rectangle filled");
						error("Expected Rectangle p1");
						error("Expected Rectangle p2");
						error("Expected End Rectangle");
						error("Expected Squiggle color");
						error("Expected Squiggle filled");
						error("Expected Squiggle points");
						error("Expected Squiggle point or end points");
						error("Expected End Squiggle");
						error("Expected Polyline color");
						error("Expected Polyline filled");
						error("Expected Polyline points");
						error("Expected Polyline point or end points");
						error("Expected End Polyline");
						error("Extra content after End of File");
						error("Unexpected end of file");
					 */
				}
			}
			if (state != 16){
				error("Unexpected end of file");
				return false;
			}
		}  catch (Exception e){
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}
}
