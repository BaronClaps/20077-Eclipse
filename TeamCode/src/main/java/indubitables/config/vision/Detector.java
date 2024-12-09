package indubitables.config.vision;

import android.graphics.Color;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class
Detector extends OpenCvPipeline {

    /*
     * These are our variables that will be
     * modifiable from the variable tuner.
     */
    public Scalar lowerOrange = new Scalar(5, 50, 50); // Lower HSV bounds for orange (adjust as needed)
    public Scalar upperOrange = new Scalar(15, 255, 255); // Upper HSV bounds for orange (adjust as needed)
    public ColorSpace colorSpace = ColorSpace.HSV; // Set to HSV for accurate color thresholding

    /*
     * A good practice when typing EOCV pipelines is
     * declaring the Mats you will use here at the top
     * of your pipeline, to reuse the same buffers every
     * time. This removes the need to call mat.release()
     * with every Mat you create on the processFrame method,
     * and therefore, reducing the possibility of getting a
     * memory leak and causing the app to crash due to an
     * "Out of Memory" error.
     */
    private Mat ycrcbMat = new Mat();
    private Mat
            hsvMat = new Mat();
    // New Mat for HSV conversion
    private Mat binaryMat = new Mat();
    private Mat maskedInputMat = new Mat();
    private Mat colorMat = new Mat();
    private Mat edges = new Mat(); // New Mat for storing edges

    private Telemetry telemetry = null;
    private int largestContourArea = 0; // Variable to store the area of the largest contour
    private final int MIN_CONTOUR_AREA = 100; // Adjust as needed

    /**
     * Enum to choose which color space to choose
     * with the live variable tuner instead of
     * hardcoding it.
     */
    enum ColorSpace {
        RGB(Imgproc.COLOR_RGBA2RGB),
        HSV(Imgproc.COLOR_RGB2HSV),
        YCrCb(Imgproc.COLOR_RGB2YCrCb),
        Lab(Imgproc.COLOR_RGB2Lab);


        //store cvtCode in a public var
        public int cvtCode = 0;

        //constructor to be used by enum declarations above
        ColorSpace(int cvtCode) {
            this.cvtCode = cvtCode;
        }
    }

    public
    Detector(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    @Override
    public Mat processFrame(Mat input) {
        /*
         * Converts our input mat from RGB to
         * specified color space by the enum.
         * EOCV ALWAYS returns RGB mats, so you'd
         * always convert from RGB to the color
         * space you want to use.
         *
         * Takes our "input" mat
 as an input, and outputs
         * to a separate Mat buffer "ycrcbMat"
 (or hsvMat depending on colorSpace)
         */
        Imgproc.cvtColor(input, hsvMat, colorSpace.cvtCode);

        Imgproc.stackBlur(hsvMat, binaryMat, new Size(5, 5));

        /*
         * Create a mask for orange using the defined HSV range
         */
        //Core.inRange(colorMat, lowerOrange, upperOrange, binaryMat);

        /*
         * Apply the mask to the original image
         */
        //maskedInputMat.release();
        //Core.bitwise_and(input, input, maskedInputMat, binaryMat);

        // Edge detection
        Imgproc.Canny(binaryMat, edges, 500, 500); // Adjust threshold values as needed

        // Contour detection
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filter
        List<MatOfPoint> filteredContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            Scalar scalar = new Scalar(0.5,0.1,0.25);
            Imgproc.drawContours(input, Collections.unmodifiableList((List<MatOfPoint>) contour), 10, new Scalar(1,1,1));
            if (area > 50) {
                filteredContours.add(contour);

            }
        }

        // Process each contour
        List<Rect> allBoundingBoxes = new ArrayList<>();  // Store all bounding boxes
        for (MatOfPoint contour : filteredContours) {
            Rect rect = Imgproc.boundingRect(contour);
            allBoundingBoxes.add(rect);
            Imgproc.rectangle(binaryMat, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);

            // Calculate center point of the rectangle
            int xPos = rect.x + rect.width / 2;
            int yPos = rect.y + rect.height / 2;

            // Do something with xPos and yPos (e.g., display them on the screen)
            telemetry.addData("xPos", xPos);
            telemetry.addData("yPos", yPos);
        }

        // Handle overlapping bounding boxes (optional)
        List<Rect> nonOverlappingBoxes = mergeOverlappingBoxes(allBoundingBoxes);

        // Process non-overlapping boxes (e.g., calculate center points, etc.)
        for (Rect rect : nonOverlappingBoxes) {
            // Calculate center point of the rectangle
            int xPos = rect.x + rect.width / 2;
            int yPos = rect.y + rect.height / 2;

            // Do something with xPos and yPos (e.g., display them on the screen)
            telemetry.addData("nonOverlappingXPos", xPos);
            telemetry.addData("nonOverlappingYPos", yPos);
        }

        /**
         * Add some nice and informative telemetry messages
         */
        telemetry.addData("[>]", "Change HSV bounds in tuner menu");
        telemetry.addData("[Color Space]", colorSpace.name());
        telemetry.addData("[Lower Orange]", lowerOrange);
        telemetry.addData("[Upper Orange]", upperOrange);
        telemetry.update();

        /*
         * The Mat returned from this method is the
         * one displayed on the viewport.
         *
         * To visualize our threshold, we'll return
         * the "masked input mat" which shows the
         * pixel from the input Mat that were inside
         * the threshold range.
         */
        return binaryMat;
    }

    // Function to merge overlapping bounding boxes (optional)
    private List<Rect> mergeOverlappingBoxes(List<Rect> boxes) {
        List<Rect> nonOverlappingBoxes = new ArrayList<>();
        boolean[] merged = new boolean[boxes.size()]; // Flag to track merged boxes

        for (int i = 0; i < boxes.size(); i++) {
            if (merged[i]) continue;  // Skip if already merged

            Rect box1 = boxes.get(i);
            nonOverlappingBoxes.add(box1);
            merged[i] = true;

            for (int j = i + 1; j < boxes.size(); j++) {
                Rect box2 = boxes.get(j);
                // Check for significant overlap (adjust thresholds as needed)
                if (isRectanglesOverlapping(box1, box2, 0.5f, 0.5f)) {
                    merged[j] = true;  // Mark box2 as merged
                    box1 = mergeRectangles(box1, box2);  // Merge overlapping regions
                }
            }
        }

        return nonOverlappingBoxes;
    }

    // Function to check for rectangle overlap (optional)
    private boolean isRectanglesOverlapping(Rect rect1, Rect rect2, float xOverlapThreshold, float yOverlapThreshold) {
        float xOverlap = Math.max(0.0f, Math.min(rect1.x + rect1.width, rect2.x + rect2.width) - Math.max(rect1.x, rect2.x));
        float yOverlap = Math.max(0.0f, Math.min(rect1.y + rect1.height, rect2.y + rect2.height) - Math.max(rect1.y, rect2.y));
        float areaOverlap = xOverlap * yOverlap;
        float area1 = rect1.width * rect1.height;
        float area2 = rect2.width * rect2.height;
        return (areaOverlap / (area1 + area2 - areaOverlap) > xOverlapThreshold * yOverlapThreshold);
    }

    // Function to merge two rectangles (optional)
    private Rect mergeRectangles(Rect rect1, Rect rect2) {
        int x = Math.min(rect1.x, rect2.x);
        int y = Math.min(rect1.y, rect2.y);
        int width = Math.max(rect1.x + rect1.width, rect2.x + rect2.width) - x;
        int height = Math.max(rect1.y + rect1.height, rect2.y + rect2.height) - y;
        return new Rect(x, y, width, height);
    }
}
