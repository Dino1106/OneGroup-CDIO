package vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

public class IdentifyEdges {
    public  int[][] boxCoordinates = new int[4][2],
            searchCoordinates  = new int [8][2];
    IdentifyCoordinates identifyCoordinates = new IdentifyCoordinates();
    public int[] test;


    public  int centerCross[] = new int[2];

    public IdentifyEdges(Mat picture) {

        identifyCoordinates.extractColor(picture,"red");
        searchCoordinates  = identifyCoordinates.getWallCorners(picture);
        boxCoordinates[0] = calculate_walls_edges(searchCoordinates[0],searchCoordinates[2],searchCoordinates[1],searchCoordinates[6]);
        boxCoordinates[1] = calculate_walls_edges(searchCoordinates[7],searchCoordinates[4],searchCoordinates[1],searchCoordinates[6]);
        boxCoordinates[2] = calculate_walls_edges(searchCoordinates[7],searchCoordinates[4],searchCoordinates[5],searchCoordinates[3]);
        boxCoordinates[3] = calculate_walls_edges(searchCoordinates[0],searchCoordinates[2],searchCoordinates[5],searchCoordinates[3]);


    }

    public int[] calculate_walls_edges(int l1p1[], int l1p2[], int l2p1[], int l2p2[]) {
        if (l1p1[0] > l1p2[0]) {
            int[] temp;
            temp = l1p1;
            l1p1 = l1p2;
            l1p2 = temp;
        }
        if (l2p1[0] > l2p2[0]) {
            int[] temp;
            temp = l2p1;
            l2p1 = l2p2;
            l2p2 = temp;
        }


        int[] to_out = new int[2];
        double l1a = (double) (l1p2[1] - l1p1[1]) / (double) (l1p2[0] - l1p1[0]);
        double l2a = (double) (l2p2[1] - l2p1[1]) / (double) (l2p2[0] - l2p1[0]);
        double l1b = (double) l1p1[1] - l1a * (double) l1p1[0];
        double l2b = (double) l2p1[1] - l2a * (double) l2p1[0];
        to_out[0] = (int) ((l2b - l1b) / (l1a - l2a));
        to_out[1] = (int) (l1a * ((l2b - l1b) / (l1a - l2a)) + l1b);
        {
            if (l1p1[0] > l1p2[0]) {
                int[] temp;
                temp = l1p1;
                l1p1 = l1p2;
                l1p2 = temp;
            }
            if (l2p1[0] > l2p2[0]) {
                int[] temp;
                temp = l2p1;
                l2p1 = l2p2;
                l2p2 = temp;
            }
            return to_out;
        }
    }
    public int[][] getArray() {
        return this.boxCoordinates;
    }


    public void drawAnchors(Mat drawIn,Scalar color){
        for(int i = 0; i < 4 ;i++)
            line(drawIn, new Point(0,0), new Point(boxCoordinates[i][0],boxCoordinates[i][1]),color);

        for(int i = 0; i < 8 ;i++)
            line(drawIn, new Point(0,0), new Point(searchCoordinates[i][0],searchCoordinates[i][1]),color);





    }}