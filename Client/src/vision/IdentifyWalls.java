package vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

public class IdentifyWalls {
  private  int[][] Box_Coordinates = new int[2][4], coords = new int [2][4];
   public   int centrum[] = new int[2];


    public IdentifyWalls(int[][] CrossBoxCoordinates) {
        this.Box_Coordinates = CrossBoxCoordinates;
        Calculate_Walls();
    }





    private void Calculate_Walls()
    {
         int magnitude[] = {9,6};



        int diff_x,diff_y;
        centrum[1] = (Box_Coordinates[1][0] + Box_Coordinates[1][2]) / 2;
        centrum[0] = (Box_Coordinates[0][0] + Box_Coordinates[0][1]) / 2;

        diff_x = (Box_Coordinates[0][1] - Box_Coordinates[0][0])/2;
        diff_y = (Box_Coordinates[1][2] - Box_Coordinates[1][1])/2;

        diff_x = diff_x * magnitude[0];
        diff_y = diff_y * magnitude[1];

        coords[0][0] = centrum[0] - diff_x;
        coords[1][0] = centrum[1] - diff_y;
        coords[0][1] = centrum[0] + diff_x;
        coords[1][1] = centrum[1] - diff_y;
        coords[0][2] = centrum[0] + diff_x;
        coords[1][2] = centrum[1] + diff_y;
        coords[0][3] = centrum[0] - diff_x;
        coords[1][3] = centrum[1] + diff_y;





    }

    public void draw_Walls(Mat color_map, Scalar BoxColor)
    {
        line(color_map,new Point(coords[0][0],coords[1][0]),new Point(coords[0][1],coords[1][1]),BoxColor);
        line(color_map,new Point(coords[0][1],coords[1][1]),new Point(coords[0][2],coords[1][2]),BoxColor);
        line(color_map,new Point(coords[0][2],coords[1][2]),new Point(coords[0][3],coords[1][3]),BoxColor);
        line(color_map,new Point(coords[0][3],coords[1][3]),new Point(coords[0][0],coords[1][0]),BoxColor);
    }





}
