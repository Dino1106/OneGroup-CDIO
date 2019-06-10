package vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

public class IdentifyWalls {
  private  int[][] BoxCoordinates = new int[2][4], coords = new int [2][4];
   public  int centerCross[] = new int[2];


    public IdentifyWalls(int[][] CrossBoxCoordinates) {
        this.BoxCoordinates = CrossBoxCoordinates;
        calculateWalls();
    }

    private void calculateWalls()
    {
    	// Proportion from edges of the cross to the edges of the walls 
    	int horizontalProportion = 9, verticalProportion = 6;
        
    	// Determine the vertical/ horizontal distance from center of cross to its edges
        int distCrossX, distCrossY;
        distCrossX = (BoxCoordinates[0][1] - BoxCoordinates[0][0])/2;
        distCrossY = (BoxCoordinates[1][2] - BoxCoordinates[1][1])/2;

        // Determine the distance to edges
        int distEdgeX, distEdgeY;
        distEdgeX = distCrossX * horizontalProportion;
        distEdgeY = distCrossY * verticalProportion;

        
        // Center of cross x and y
        centerCross[0] = (BoxCoordinates[0][0] + BoxCoordinates[0][1]) / 2;
        centerCross[1] = (BoxCoordinates[1][0] + BoxCoordinates[1][2]) / 2;
        
        // Calculate the position of the edge's corners 
        coords[0][0] = centerCross[0] - distEdgeX;
        coords[1][0] = centerCross[1] - distEdgeY;
        coords[0][1] = centerCross[0] + distEdgeX;
        coords[1][1] = centerCross[1] - distEdgeY;
        coords[0][2] = centerCross[0] + distEdgeX;
        coords[1][2] = centerCross[1] + distEdgeY;
        coords[0][3] = centerCross[0] - distEdgeX;
        coords[1][3] = centerCross[1] + distEdgeY;
    }

    public void drawWalls(Mat colorMap, Scalar BoxColor)
    {
        line(colorMap,new Point(coords[0][0],coords[1][0]),new Point(coords[0][1],coords[1][1]),BoxColor);
        line(colorMap,new Point(coords[0][1],coords[1][1]),new Point(coords[0][2],coords[1][2]),BoxColor);
        line(colorMap,new Point(coords[0][2],coords[1][2]),new Point(coords[0][3],coords[1][3]),BoxColor);
        line(colorMap,new Point(coords[0][3],coords[1][3]),new Point(coords[0][0],coords[1][0]),BoxColor);
    }

}
