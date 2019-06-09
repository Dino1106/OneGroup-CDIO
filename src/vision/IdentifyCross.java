package vision;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;

import static org.bytedeco.opencv.global.opencv_imgproc.*;



public class IdentifyCross {

   private CanvasFrame vid_color = new CanvasFrame("color");
    private int coords[][] = new int [2][4];


    int highier_left_boundary[] = {280,150};
    int lower_left_boundary[] =   {280,300};
    int lower_right_boundary[] =  {430,300};
    int highier_right_boundary[] =  {430,150};




    public IdentifyCross(Mat to_transform)
    {

         coords = calculate_edges(to_transform);


    }





public int[][] calculate_edges(Mat to_transform)
{


    int out[][] = new int[2][4];
    BytePointer p;

    Mat color_map = extract_color(to_transform);
    p = color_map.data();




 d21:
    for( int y = highier_left_boundary[1]; y <= lower_left_boundary[1]; y++ )
    for( int x = highier_left_boundary[0]; x < highier_right_boundary[0]; x++ ){

        System.out.print( p.get((y*color_map.arrayWidth())+x));
        if(p.get((y*color_map.arrayWidth())+x) == -1){


            coords[1][0] = y;
            coords[1][1] = y;


            break d21;
        }}
d22:
    for( int y = lower_left_boundary[1]; y >= highier_left_boundary[1]; y-- )
        for( int x = highier_left_boundary[0]; x < highier_right_boundary[0]; x++ ){

            if(p.get((y*color_map.arrayWidth())+x) == -1){


                coords[1][3] = y;
                coords[1][2] = y;


                break d22;
            }}

d23:
    for( int x = lower_left_boundary[0]; x <= lower_right_boundary[0]; x++ )
        for( int y = highier_left_boundary[1]; y <= lower_right_boundary[1]; y++ ){

            if(p.get((y*color_map.arrayWidth())+x) == -1){

                coords[0][0] = x;
                coords[0][3] = x;



                break d23;
            }}


    d24:
    for( int x = lower_right_boundary[0]; x >= lower_left_boundary[0]; x-- )
        for( int y = highier_left_boundary[1]; y <= lower_right_boundary[1]; y++ ){

            if(p.get((y*color_map.arrayWidth())+x) == -1){

                coords[0][1] = x;
                coords[0][2] = x;


                break d24;
            }}

return coords;
}


    /**
     *
     * Returns xy Coordinates of a square around the cross
     *
     * x = 0 , y = 1
     *  position = 0 <--- high-left corner
     *  position = 1 <--- high-right corner
     *  position = 2 <--- low-right corner
     *  position = 3 <--- low-left corner
     *
     */
    int get_coords(int x_y,int position)
{
    return coords[x_y][position];
}


public void draw_box(Mat color_map, Scalar BoxColor)
{


    line(color_map,new Point(coords[0][0],coords[1][0]),new Point(coords[0][1],coords[1][1]),BoxColor);
    line(color_map,new Point(coords[0][1],coords[1][1]),new Point(coords[0][2],coords[1][2]),BoxColor);
    line(color_map,new Point(coords[0][2],coords[1][2]),new Point(coords[0][3],coords[1][3]),BoxColor);
    line(color_map,new Point(coords[0][3],coords[1][3]),new Point(coords[0][0],coords[1][0]),BoxColor);



}

public  void draw_render_space(Mat color_map, Scalar BoxColor)
{
     line(color_map,new Point(highier_left_boundary[0],highier_left_boundary[1]),new Point(lower_left_boundary[0],lower_left_boundary[1]),Scalar.RED);
     line(color_map,new Point(lower_left_boundary[0],lower_left_boundary[1]),new Point(lower_right_boundary[0],lower_right_boundary[1]),Scalar.RED);
     line(color_map,new Point(lower_right_boundary[0],lower_right_boundary[1]),new Point(highier_right_boundary[0],highier_right_boundary[1]),Scalar.RED);
     line(color_map,new Point(highier_right_boundary[0],highier_right_boundary[1]),new Point(highier_left_boundary[0],highier_left_boundary[1]),Scalar.RED);



}



    public Mat extract_color(Mat picture) {

        Mat to_out = new Mat();

        // Range of red color of cross
        int b_min = 0, 		b_max = 111;
        int g_min = 27, 	g_max = 136;
        int r_min = 151,	r_max = 255;


        // Create Mat's based of the colors for the inRange function
        Mat min_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(b_min, g_min, r_min, 0));
        Mat max_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(b_max, g_max, r_max, 0));

        // Remove any other color than in the range of min and max
        opencv_core.inRange(picture, min_Mat, max_Mat, to_out);


        return to_out;

    }


}

