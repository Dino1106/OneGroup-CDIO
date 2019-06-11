package vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

public class IdentifyWalls {
  private  int[][] BoxCoordinates = new int[4][2], coords = new int [4][2];
   public  int centerCross[] = new int[2];

    public IdentifyWalls(int[][] CrossBoxCoordinates) {
        this.BoxCoordinates = CrossBoxCoordinates;
        calculateWalls();
    }

    private void calculateWalls()
    {
    	// Proportion from edges of the cross to the edges of the walls 
    	int horizontalProportion = 8, verticalProportion = 6;
        
    	// Determine the vertical/ horizontal distance from center of cross to its edges
        int distCrossX, distCrossY;
        distCrossX = (BoxCoordinates[1][0] - BoxCoordinates[0][0])/2;
        distCrossY = (BoxCoordinates[2][1] - BoxCoordinates[1][1])/2;

        // Determine the distance to edges
        int distEdgeX, distEdgeY;
        distEdgeX = distCrossX * horizontalProportion;
        distEdgeY = distCrossY * verticalProportion;

        
        // Center of cross x and y
        centerCross[0] = (BoxCoordinates[0][0] + BoxCoordinates[1][0]) / 2;
        centerCross[1] = (BoxCoordinates[0][1] + BoxCoordinates[2][1]) / 2;
        
        // Calculate the position of the edge's corners 
        coords[0][0] = centerCross[0] - distEdgeX;
        coords[0][1] = centerCross[1] - distEdgeY;
        coords[1][0] = centerCross[0] + distEdgeX;
        coords[1][1] = centerCross[1] - distEdgeY;
        coords[2][0] = centerCross[0] + distEdgeX;
        coords[2][1] = centerCross[1] + distEdgeY;
        coords[3][0] = centerCross[0] - distEdgeX;
        coords[3][1] = centerCross[1] + distEdgeY;
    }

    public void draw(Mat colorMap, Scalar BoxColor)
    {
        
		line(colorMap,new Point(coords[0][0],coords[0][1]),new Point(coords[1][0],coords[1][1]),BoxColor);
		line(colorMap,new Point(coords[1][0],coords[1][1]),new Point(coords[2][0],coords[2][1]),BoxColor);
		line(colorMap,new Point(coords[2][0],coords[2][1]),new Point(coords[3][0],coords[3][1]),BoxColor);
		line(colorMap,new Point(coords[3][0],coords[3][1]),new Point(coords[0][0],coords[0][1]),BoxColor);
    }

}
