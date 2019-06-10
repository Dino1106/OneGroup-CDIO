package vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

public class IdentifyWalls {
  private  int[][] Box_Coordinates = new int[2][4], coords = new int [2][4];
   public   int center_cross[] = new int[2];


    public IdentifyWalls(int[][] CrossBoxCoordinates) {
        this.Box_Coordinates = CrossBoxCoordinates;
        Calculate_Walls();
    }





    private void Calculate_Walls()
    {
    	// Proportion from edges of the cross to the edges of the walls 
    	int horizontal_proportion = 9, vertical_proportion = 6;
        
    	// Determine the vertical/ horizontal distance from center of cross to its edges
        int dist_cross_x, dist_cross_y;
        dist_cross_x = (Box_Coordinates[0][1] - Box_Coordinates[0][0])/2;
        dist_cross_y = (Box_Coordinates[1][2] - Box_Coordinates[1][1])/2;

        // Determine the distance to edges
        int dist_edge_x, dist_edge_y;
        dist_edge_x = dist_cross_x * horizontal_proportion;
        dist_edge_y = dist_cross_y * vertical_proportion;

        
        // Center of cross x and y
        center_cross[0] = (Box_Coordinates[0][0] + Box_Coordinates[0][1]) / 2;
        center_cross[1] = (Box_Coordinates[1][0] + Box_Coordinates[1][2]) / 2;
        
        // Calculate the position of the edge's corners 
        coords[0][0] = center_cross[0] - dist_edge_x;
        coords[1][0] = center_cross[1] - dist_edge_y;
        coords[0][1] = center_cross[0] + dist_edge_x;
        coords[1][1] = center_cross[1] - dist_edge_y;
        coords[0][2] = center_cross[0] + dist_edge_x;
        coords[1][2] = center_cross[1] + dist_edge_y;
        coords[0][3] = center_cross[0] - dist_edge_x;
        coords[1][3] = center_cross[1] + dist_edge_y;


    }

    public void draw_Walls(Mat color_map, Scalar BoxColor)
    {
        line(color_map,new Point(coords[0][0],coords[1][0]),new Point(coords[0][1],coords[1][1]),BoxColor);
        line(color_map,new Point(coords[0][1],coords[1][1]),new Point(coords[0][2],coords[1][2]),BoxColor);
        line(color_map,new Point(coords[0][2],coords[1][2]),new Point(coords[0][3],coords[1][3]),BoxColor);
        line(color_map,new Point(coords[0][3],coords[1][3]),new Point(coords[0][0],coords[1][0]),BoxColor);
    }





}
