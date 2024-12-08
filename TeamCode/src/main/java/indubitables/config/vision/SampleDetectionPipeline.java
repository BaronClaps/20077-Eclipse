package indubitables.config.vision;

import org.openftc.easyopencv.OpenCvPipeline;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public class SampleDetectionPipeline extends OpenCvPipeline
{

    public SampleDetectionPipeline(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    /*
     * Working image buffers
     */
    Mat ycrcbMat = new Mat();
    Mat crMat = new Mat();
    Mat cbMat = new Mat();

    Mat blueThresholdMat = new Mat();
    Mat redThresholdMat = new Mat();
    Mat yellowThresholdMat = new Mat();

    Mat morphedBlueThreshold = new Mat();
    Mat morphedRedThreshold = new Mat();
    Mat morphedYellowThreshold = new Mat();

    Mat contoursOnPlainImageMat = new Mat();

    /*
     * Threshold values
     */
    static final int YELLOW_MASK_THRESHOLD = 57;
    static final int BLUE_MASK_THRESHOLD = 150;
    static final int RED_MASK_THRESHOLD = 198;

    /*
     * Elements for noise reduction
     */
    Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3.5, 3.5));
    Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3.5, 3.5));

    /*
     * Colors
     */
    static final Scalar RED = new Scalar(255, 0, 0);
    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final Scalar YELLOW = new Scalar(255, 255, 0);

    // Variable to specify which color to detect
    public String selectedColor = "Red";  // Default color to detect (you can change this to "Blue" or "Yellow")
    private AnalyzedStone selectedStone;


    Telemetry telemetry = null;

    static final int CONTOUR_LINE_THICKNESS = 2;

    static class AnalyzedStone
    {
        double angle;
        String color;
    }

    ArrayList<AnalyzedStone> internalStoneList = new ArrayList<>();
    volatile ArrayList<AnalyzedStone> clientStoneList = new ArrayList<>();

    /*
     * Viewport stages
     */
    enum Stage
    {
        FINAL,
        YCrCb,
        MASKS,
        MASKS_NR,
        CONTOURS;
    }

    Stage[] stages = Stage.values();
    int stageNum = 0;


    @Override
    public void onViewportTapped()
    {
        int nextStageNum = stageNum + 1;

        if(nextStageNum >= stages.length)
        {
            nextStageNum = 0;
        }

        stageNum = nextStageNum;
    }

    @Override
    public Mat processFrame(Mat input)
    {
        internalStoneList.clear();

        // Run the image processing to find contours
        findContours(input);

        clientStoneList = new ArrayList<>(internalStoneList);

        // Find the contour with the highest y (closest to the bottom) and closest to the center x
        if (!internalStoneList.isEmpty()) {
            selectedStone = findBestStone(input.width(), input.height());

            // Telemetry output the angle of the selected stone
            if (selectedStone != null) {
                // Send the angle to telemetry
                telemetry.addData("Detected Angle", selectedStone.angle);
                telemetry.update();
            }
        }

        // Decide which buffer to send to the viewport
        switch (stages[stageNum])
        {
            case YCrCb:
            {
                return ycrcbMat;
            }

            case FINAL:
            {
                return input;
            }

            case MASKS:
            {
                Mat masks = new Mat();
                Core.addWeighted(yellowThresholdMat, 1.0, redThresholdMat, 1.0, 0.0, masks);
                Core.addWeighted(masks, 1.0, blueThresholdMat, 1.0, 0.0, masks);
                return masks;
            }

            case MASKS_NR:
            {
                Mat masksNR = new Mat();
                Core.addWeighted(morphedYellowThreshold, 1.0, morphedRedThreshold, 1.0, 0.0, masksNR);
                Core.addWeighted(masksNR, 1.0, morphedBlueThreshold, 1.0, 0.0, masksNR);
                return masksNR;
            }

            case CONTOURS:
            {
                return contoursOnPlainImageMat;
            }

            default:
            {
                return input;
            }
        }
    }

    public AnalyzedStone findBestStone(int imageWidth, int imageHeight)
    {
        double centerX = imageWidth / 2.0;
        double highestY = -1; // Initialize to a low value
        double closestX = Double.MAX_VALUE;
        AnalyzedStone bestStone = null;

        // Iterate over the internal list of stones and check if their color matches the selected color
        for (AnalyzedStone stone : internalStoneList) {
            // Only consider stones of the selected color
            if (stone.color.equalsIgnoreCase(selectedColor)) {
                double stoneX = stone.angle; // Assuming angle is used for X-coordinate in this case

                // Check if the stone is closer to the bottom (highest y) and closest to the center
                if (stone.angle > highestY || (stone.angle == highestY && Math.abs(stoneX - centerX) < closestX)) {
                    highestY = stone.angle;
                    closestX = Math.abs(stoneX - centerX);
                    bestStone = stone;
                }
            }
        }

        return bestStone;
    }



    public ArrayList<AnalyzedStone> getDetectedStones()
    {
        return clientStoneList;
    }

    void findContours(Mat input)
    {
        // Convert the input image to YCrCb color space
        Imgproc.cvtColor(input, ycrcbMat, Imgproc.COLOR_RGB2YCrCb);

        // Extract the Cb and Cr channels
        Core.extractChannel(ycrcbMat, cbMat, 2); // Cb channel index is 2
        Core.extractChannel(ycrcbMat, crMat, 1); // Cr channel index is 1

        // Threshold the channels to form masks for all colors
        Imgproc.threshold(cbMat, blueThresholdMat, BLUE_MASK_THRESHOLD, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(crMat, redThresholdMat, RED_MASK_THRESHOLD, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(cbMat, yellowThresholdMat, YELLOW_MASK_THRESHOLD, 255, Imgproc.THRESH_BINARY_INV);

        // Apply morphology to the masks
        morphMask(blueThresholdMat, morphedBlueThreshold);
        morphMask(redThresholdMat, morphedRedThreshold);
        morphMask(yellowThresholdMat, morphedYellowThreshold);

        // Create a plain image for drawing contours
        contoursOnPlainImageMat = Mat.zeros(input.size(), input.type());

        // Process only the selected color
        if ("Blue".equalsIgnoreCase(selectedColor)) {
            ArrayList<MatOfPoint> blueContoursList = new ArrayList<>();
            Imgproc.findContours(morphedBlueThreshold, blueContoursList, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
            for(MatOfPoint contour : blueContoursList) {
                analyzeContour(contour, input, "Blue");
            }
            selectedStone = findBestStone(input.width(), input.height());
        } else if ("Red".equalsIgnoreCase(selectedColor)) {
            ArrayList<MatOfPoint> redContoursList = new ArrayList<>();
            Imgproc.findContours(morphedRedThreshold, redContoursList, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
            for(MatOfPoint contour : redContoursList) {
                analyzeContour(contour, input, "Red");
            }
            selectedStone = findBestStone(input.width(), input.height());
        } else if ("Yellow".equalsIgnoreCase(selectedColor)) {
            ArrayList<MatOfPoint> yellowContoursList = new ArrayList<>();
            Imgproc.findContours(morphedYellowThreshold, yellowContoursList, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
            for(MatOfPoint contour : yellowContoursList) {
                analyzeContour(contour, input, "Yellow");
            }
            selectedStone = findBestStone(input.width(), input.height());
        }
    }


    void morphMask(Mat input, Mat output)
    {
        /*
         * Apply erosion and dilation for noise reduction
         */
        Imgproc.erode(input, output, erodeElement);
        Imgproc.erode(output, output, erodeElement);

        Imgproc.dilate(output, output, dilateElement);
        Imgproc.dilate(output, output, dilateElement);
    }

    void analyzeContour(MatOfPoint contour, Mat input, String color)
    {
        // Transform the contour to a different format
        Point[] points = contour.toArray();
        MatOfPoint2f contour2f = new MatOfPoint2f(points);

        // Fit a rotated rectangle to the contour
        RotatedRect rotatedRectFitToContour = Imgproc.minAreaRect(contour2f);
        drawRotatedRect(rotatedRectFitToContour, input, color);
        drawRotatedRect(rotatedRectFitToContour, contoursOnPlainImageMat, color);

        // Adjust the angle based on rectangle dimensions and normalize to -90 to 90
        double angle = rotatedRectFitToContour.angle;
        if (rotatedRectFitToContour.size.width < rotatedRectFitToContour.size.height)
        {
            angle += 90; // Rotate by 90 degrees if the rectangle is taller than wide
        }

        // Normalize angle to range -90 to 90
        if (angle > 90)
        {
            angle -= 180;
        }

        // Draw the angle tag
        drawTagText(rotatedRectFitToContour, Integer.toString((int) Math.round(angle)) + " deg", input, color);

        // Store the detected stone information
        AnalyzedStone analyzedStone = new AnalyzedStone();
        analyzedStone.angle = angle;
        analyzedStone.color = color;
        internalStoneList.add(analyzedStone);
    }


    static void drawTagText(RotatedRect rect, String text, Mat mat, String color)
    {
        Scalar colorScalar = getColorScalar(color);

        Imgproc.putText(
                mat, // The buffer we're drawing on
                text, // The text we're drawing
                new Point( // The anchor point for the text
                        rect.center.x - 50,  // x anchor point
                        rect.center.y + 25), // y anchor point
                Imgproc.FONT_HERSHEY_PLAIN, // Font
                1, // Font size
                colorScalar, // Font color
                1); // Font thickness
    }

    static void drawRotatedRect(RotatedRect rect, Mat drawOn, String color)
    {
        /*
         * Draws a rotated rectangle by drawing each of the 4 lines individually
         */
        Point[] points = new Point[4];
        rect.points(points);

        Scalar colorScalar = getColorScalar(color);

        for (int i = 0; i < 4; ++i)
        {
            Imgproc.line(drawOn, points[i], points[(i + 1) % 4], colorScalar, 2);
        }
    }

    static Scalar getColorScalar(String color)
    {
        switch (color)
        {
            case "Blue":
                return BLUE;
            case "Yellow":
                return YELLOW;
            default:
                return RED;
        }
    }
}
