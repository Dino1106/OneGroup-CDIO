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
    private int coords[] = new int[5];




    public IdentifyCross(Mat to_transform)
    {

         coords = calculate_edges(to_transform);


    }





public int[] calculate_edges(Mat to_transform)
{
    OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
    int temp_x_stop= 0,temp_y = 0, temp_x_start=0;

    int highier_left_boundary[] = {280,150};
    int lower_left_boundary[] =   {280,300};
    int lower_right_boundary[] =  {430,300};
    int highier_right_boundary[] =  {430,150};


    int move_right = 80;
    int move_down = -50;
    int ite = 0;

    BytePointer p;
    Mat color_map = extract_color(to_transform);

    p = color_map.data();

int  cords_pre[][] = new int[2][4];


 d21:
    for( int y = highier_left_boundary[1]; y <= lower_left_boundary[1]; y++ )
    for( int x = highier_left_boundary[0]; x < highier_right_boundary[0]; x++ ){

        System.out.print( p.get((y*color_map.arrayWidth())+x));
        if(p.get((y*color_map.arrayWidth())+x) == -1){

            cords_pre[0][0] = x;
            cords_pre[1][0] = y;

            break d21;
        }}
d22:
    for( int y = lower_left_boundary[1]; y >= highier_left_boundary[1]; y-- )
        for( int x = highier_left_boundary[0]; x < highier_right_boundary[0]; x++ ){

            if(p.get((y*color_map.arrayWidth())+x) == -1){

                cords_pre[0][1] = x;
                cords_pre[1][1] = y;


                break d22;
            }}

d23:
    for( int x = lower_left_boundary[0]; x <= lower_right_boundary[0]; x++ )
        for( int y = highier_left_boundary[1]; y <= lower_right_boundary[1]; y++ ){

            if(p.get((y*color_map.arrayWidth())+x) == -1){

                cords_pre[0][2] = x;
                cords_pre[1][2] = y;


                break d23;
            }}


    d23:
    for( int x = lower_right_boundary[0]; x >= lower_left_boundary[0]; x-- )
        for( int y = highier_left_boundary[1]; y <= lower_right_boundary[1]; y++ ){

            if(p.get((y*color_map.arrayWidth())+x) == -1){

                cords_pre[0][3] = x;
                cords_pre[1][3] = y;


                break d23;
            }}












//
//    for (int x = temp_x_start  ; x < highier_right_boundary[0];x++)
//    {
//        if((p.get((temp_y*color_map.arrayWidth())+x)) == 0){
//           temp_x_stop = x;
//            break;}





    cvtColor(color_map,color_map,COLOR_GRAY2BGR);




    line(color_map,new Point(cords_pre[0][3],cords_pre[1][0]),new Point(cords_pre[0][2],cords_pre[1][0]),Scalar.GREEN);
    line(color_map,new Point(cords_pre[0][2],cords_pre[1][0]),new Point(cords_pre[0][2],cords_pre[1][1]),Scalar.GREEN);
    line(color_map,new Point(cords_pre[0][2],cords_pre[1][1]),new Point(cords_pre[0][3],cords_pre[1][1]),Scalar.GREEN);
    line(color_map,new Point(cords_pre[0][3],cords_pre[1][1]),new Point(cords_pre[0][3],cords_pre[1][0]),Scalar.GREEN);








    line(color_map,new Point(highier_left_boundary[0],highier_left_boundary[1]),new Point(lower_left_boundary[0],lower_left_boundary[1]),Scalar.RED);
    line(color_map,new Point(lower_left_boundary[0],lower_left_boundary[1]),new Point(lower_right_boundary[0],lower_right_boundary[1]),Scalar.RED);
    line(color_map,new Point(lower_right_boundary[0],lower_right_boundary[1]),new Point(highier_right_boundary[0],highier_right_boundary[1]),Scalar.RED);
    line(color_map,new Point(highier_right_boundary[0],highier_right_boundary[1]),new Point(highier_left_boundary[0],highier_left_boundary[1]),Scalar.RED);





    vid_color.showImage(converter.convert(color_map));

int[] edges = {1,2,3,4};


return edges;
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

