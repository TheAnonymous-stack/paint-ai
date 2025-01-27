package ca.utoronto.utm.paint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaPaint extends Ollama{
    public OllamaPaint(String host){
        super(host);
    }

    /**
    * This method addresses the common typo error of points of Polyline and Squiggle
     * when Ollama returns a Paint File
     * @param response
    * */
    private String sanitizeResponse(String response) {
        String[] responseList = response.split("\n");
        String output = "";
        Pattern pSquigglePolylinePoint = Pattern.compile("\\(?point\\)?:?,?(\\(-?\\d+,-?\\d+\\))");
        for (String s : responseList) {
            Matcher m = pSquigglePolylinePoint.matcher(s);
            if (m.find()) {
                output += "\t\tpoint:"+m.group(1)+"\n";
            } else {
                output += s+"\n";
            }
        }
        return output;
    }
    /**
     * Ask llama3 to generate a new Paint File based on the given prompt
     * @param prompt
     * @param outFileName name of new file to be created in users home directory
     */
    public void newFile(String prompt, String outFileName){
        String format = FileIO.readResourceFile("paintFileFormat.txt");
        String system = "The answer to this question should be a Paint File. Respond only with a Paint File and nothing else. " + format;
        String response = this.call(system, prompt);
        response=sanitizeResponse(response.substring(response.indexOf("Paint Save File")));
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * Ask llama3 to generate a new Paint File based on a modification of inFileName and the prompt
     * @param prompt the user supplied prompt
     * @param inFileName the Paint File Format file to be read and modified to outFileName
     * @param outFileName name of new file to be created in users home directory
     */
    public void modifyFile(String prompt, String inFileName, String outFileName){
        // YOUR CODE GOES HERE
        // Your job is to create the right system and prompt.
        // then call Ollama and write the new file in the home directory
        // HINT: You should have a collection of resources, examples, prompt wrapper etc. available
        // in the resources directory. See OllamaNumberedFile as an example.
        String createFormat = FileIO.readResourceFile("paintFileFormat.txt");
        String modifyFormat = FileIO.readResourceFile("modifyPaintFileFormat.txt");
        String system = "The answer to this question should be a Paint File. Respond only with a Paint File and nothing else. " + createFormat+ " "+modifyFormat;
        String f = FileIO.readHomeFile(inFileName);
        String fullPrompt = "Produce a new Paint File, resulting from the following OPERATION being performed on the following Paint File. OPERATION START"+prompt+" OPERATION END "+f;
        String response = this.call(system, fullPrompt);
        response=sanitizeResponse(response.substring(response.indexOf("Paint Save File")));
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * newFile1: produce a Paint File whose name is specified by outFileName
     * containing instructions to draw a face
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile1(String outFileName) {
        // YOUR CODE GOES HERE
        String prompt = "Design a face. Either draw a Circle with a big radius or draw a Rectangle with large dimensions " +
                " as the face. Draw 2 concentric circles with different colors for the left eyes inside the face. " +
                "Draw 2 concentric circles with different colors for the right eyes inside the face." +
                "The center of the Circle for the eyes have the same y value but different x values. " +
                "Draw a Circle for the nose in between the eyes." +
                " Draw a polyline to draw the mouth below the nose. " +
                "Draw a Polyline to draw eyebrows above the eyes. The coordinates of the Polyline's points should " +
                "be less than the center fo the Circle for the eyes subtracted the radius." +
                "The facial features above are contained inside the Circle or Rectangle face. " +
                "Consult the following examples for inspirations.";
        String format = FileIO.readResourceFile("newFile1Format.txt");
        String system = "Respond with a Paint File and nothing else "+format;
        for (int i = 1; i<7; i++) {
            String example = FileIO.readResourceFile("face"+i);
            prompt += "EXAMPLE START";
            prompt += example;
            prompt += "EXAMPLE END";
        }
        String response = this.call(system, prompt);
        response=sanitizeResponse(response.substring(response.indexOf("Paint Save File")));
        FileIO.writeHomeFile(response, outFileName);

    }

    /**
     * newFile2: Ollama design its own country flag
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile2(String outFileName) {
        // YOUR CODE GOES HERE
        String format = FileIO.readResourceFile("newFile2Format.txt");
        String system = "Respond with a Paint File and nothing else " + format;
        String prompt = "Design a country flag. A country flag has " +
                "a Rectangle as a base. Then the decoration is a combination of " +
                "Polyline, Circle inside the Rectangle base." +
                "All shapes are filled and some " +
                "shapes can have the same color. A flag should not have more than" +
                "different colors. "+
                "Consult the following examples as inspirations to design the flag";
        for (int i = 1; i<11; i++) {
            String example = FileIO.readResourceFile("flag"+i);
            prompt += "EXAMPLE START";
            prompt += example;
            prompt += "EXAMPLE END";
        }
        String response = this.call(system, prompt);
        response=sanitizeResponse(response.substring(response.indexOf("Paint Save File")));
        FileIO.writeHomeFile(response, outFileName);

    }

    /**
     * newFile3: draw a polygon of random number of sides
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile3(String outFileName) {
        // YOUR CODE GOES HERE
        int side = (int)(Math.random()*10);
        String prompt = "Draw a simple polygon with "+side+" sides using Polyline. The first point and the last point must" +
                "have the same coordinate. Make the polyline extend as far as possible in a 500x500 window. " +
                "The coordinates of the points are positive.";
        newFile(prompt, outFileName);

    }

    /**
     * modifyFile1: MODIFY inFileName TO PRODUCE outFileName BY
     * changing the colors, attribute values of the original shapes, and
     * adding more overlapping shapes to resemble a Picasso painting
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        // YOUR CODE GOES HERE
        String prompt = "Modify the Paint File to make it resemble a Picasso painting. " +
                "Consider changing the color of some shapes, change the attribute values of some shapes. " +
                "Add more overlapping shapes with various colors at the end of the Paint File " +
                "to make it resemble a Picasso painting";
        modifyFile(prompt, inFileName, outFileName);
    }

    /**
     * modifyFile2: MODIFY inFileName TO PRODUCE outFileName BY
     * sending inFileName over to ChatGPT and ask ChatGPT to make the file look happier.
     * Ollama will respond with the Paint File with modifications from ChatGPT
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        // YOUR CODE GOES HERE
        String prompt = "Send the following Paint File to ChatGPT and ask ChatGPT to make it look happier. FILE BEGIN\n";
        String createFormat = FileIO.readResourceFile("paintFileFormat.txt");
        String modifyFormat = FileIO.readResourceFile("modifyPaintFileFormat.txt");
        String originalFile = FileIO.readHomeFile(inFileName);
        prompt += originalFile;

        String system = "Instruct ChatGPT to follow this format of a Paint File: "+createFormat+" Respond by writing " +
                "a Paint File according to the suggestions from ChatGPT.";
        String response = this.call(system, prompt);
        response=sanitizeResponse(response.substring(response.indexOf("Paint Save File")));
        FileIO.writeHomeFile(response, outFileName);

    }
    /**
     * modifyFile3: MODIFY inFileName TO PRODUCE outFileName BY
     * replacing the shape that Ollama does not like with shapes that
     * will add more Christmas spirit to the Paint File
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        // YOUR CODE GOES HERE
        String prompt = "Replace the shapes in Paint File that you don't like with other shapes that " +
                "will make the Paint File has a better Christmas spirit.";
        modifyFile(prompt, inFileName, outFileName);
    }

    public static void main(String [] args){
        String prompt = null;

        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        OllamaPaint op = new OllamaPaint("dh2010pc15.utm.utoronto.ca"); // Replace this with your assigned Ollama server.
        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        op.newFile(prompt, "OllamaPaintFile1.txt");
        op.modifyFile("Remove all shapes except for the circles.","OllamaPaintFile1.txt", "OllamaPaintFile2.txt" );

        prompt="Draw 5 concentric circles with different colors.";
        op.newFile(prompt, "OllamaPaintFile3.txt");
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile3.txt", "OllamaPaintFile4.txt" );

        prompt="Draw a polyline then two circles then a rectangle then 3 polylines all with different colors.";
        op.newFile(prompt, "OllamaPaintFile4.txt");

        prompt="Modify the following Paint Save File so that each circle is surrounded by a non-filled rectangle. ";
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile4.txt", "OllamaPaintFile5.txt" );

        for(int i=1;i<=3;i++){
            op.newFile1("PaintFile1_"+i+".txt");
            op.newFile2("PaintFile2_"+i+".txt");
            op.newFile3("PaintFile3_"+i+".txt");
        }
        for(int i=1;i<=3;i++){
            for(int j=1;j<=3;j++) {
                op.modifyFile1("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_1.txt");
                op.modifyFile2("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_2.txt");
                op.modifyFile3("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_3.txt");
            }
        }
    }
}
