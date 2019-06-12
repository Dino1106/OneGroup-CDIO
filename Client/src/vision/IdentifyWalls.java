package vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

public class IdentifyWalls {
  private  int[][] BoxCoordinates = new int[4][2],
                   SearchCoordinates  = new int [4][2];

   public  int centerCross[] = new int[2];

    public IdentifyWalls(int[][] CrossBoxCoordinates) {
        this.BoxCoordinates = CrossBoxCoordinates;
       // calculateWalls();
    }
/*
    private void calculateWalls()
    {
        int scale = 2;
    	// Proportion from edges of the cross to the edges of the walls
    	int horizontalProportion = 8/scale, verticalProportion = 6/scale;
        
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
        SearchCoordinates[0][0] = centerCross[0] - distEdgeX;
        SearchCoordinates[0][1] = centerCross[1] - distEdgeY;
        SearchCoordinates[1][0] = centerCross[0] + distEdgeX;
        SearchCoordinates[1][1] = centerCross[1] - distEdgeY;
        SearchCoordinates[2][0] = centerCross[0] + distEdgeX;
        SearchCoordinates[2][1] = centerCross[1] + distEdgeY;
        SearchCoordinates[3][0] = centerCross[0] - distEdgeX;
        SearchCoordinates[3][1] = centerCross[1] + distEdgeY;
    */

    public int[][] getArray(){
        return BoxCoordinates;
    }

	public void draw(Mat colorMap, Scalar BoxColor)
	{
        line(colorMap,new Point(0,0),new Point(colorMap.cols()/2,colorMap.rows()/2),BoxColor);

        line(colorMap,new Point(SearchCoordinates[0][0],SearchCoordinates[1][0]),new Point(SearchCoordinates[0][1],SearchCoordinates[1][1]),BoxColor);
	//	line(colorMap,new Point(SearchCoordinates[0][2],SearchCoordinates[1][2]),new Point(SearchCoordinates[0][3],SearchCoordinates[1][3]),BoxColor);
	//	line(colorMap,new Point(SearchCoordinates[0][3],SearchCoordinates[1][3]),new Point(SearchCoordinates[0][0],SearchCoordinates[1][0]),BoxColor);
	}


}
