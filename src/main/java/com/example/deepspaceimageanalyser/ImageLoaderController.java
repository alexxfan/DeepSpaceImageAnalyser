package com.example.deepspaceimageanalyser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;

import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.*;

public class ImageLoaderController implements Initializable {
    FileChooser fileChooser = new FileChooser(); //file chooser


    public int[] pixelArray;
    private int width;
    private int height;
    private WritableImage newBlackAndWhiteImage;


    HashMap<Integer, ArrayList<Integer>> hashMap;

    @FXML
    public ImageView regularImage, blackAndWhiteImageView, adjustableImage, Label;

    @FXML
    public Label Stars;

    @FXML
    private Button Load, Exit, bW;

    @FXML
    private MenuButton options;

    @FXML
    private MenuItem randomColour, circles, circleAndLabel;

   @FXML
   public TextField thresh;

    @FXML
    public void getImage(ActionEvent actionEvent){
        File file = fileChooser.showOpenDialog(new Stage()); //open file explorer
        fileChooser.setInitialDirectory(new File("C:\\Users\\tinyf\\OneDrive - Waterford Institute of Technology\\Applied Computing Course\\year 2\\semester 2\\data structures & algorithms 2\\Deep Space Imagery Analyser\\space images"));
        if(file != null) {
            Image image = new Image(file.toURI().toString()); //make file into a URI
            regularImage.setImage(image); //open image in imageviewer

        }
        else{
            System.out.println("No Image Chosen");
        }

    }

    public WritableImage blackAndWhiteImg(ActionEvent actionEvent) {

        Image image = regularImage.getImage(); //get the chosen image from file explorer (regular imageview)
        blackAndWhiteImageView.setImage(image); //set black and white image to this
        PixelReader pixelReader = image.getPixelReader(); //initialize the pixel reader for the original image

        width = (int) image.getWidth(); //get width of image
        height = (int) image.getHeight(); //get height of image
//        if(width <= 0 || height <= 0){
//            System.out.println("Error in image");
//        }
        pixelArray = new int[(height * width)];
        hashMap = new HashMap<>();
//        Arrays.fill(pixelArray, -1); // initialize all pixels to -1

        WritableImage blackAndWhiteImage = new WritableImage(width, height); //create a new WritableImage object with the same dimensions as the original image
        PixelWriter pixelWriter = blackAndWhiteImage.getPixelWriter();

        //loop through each pixel of the original image
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {

                //get the red, green, and blue values of the current pixel
                double red = pixelReader.getColor(column, row).getRed();
                double green = pixelReader.getColor(column, row).getGreen();
                double blue = pixelReader.getColor(column, row).getBlue();

                //calculate the luminance of the current pixel
                double luminance = (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
                double luminanceThreshold = Double.parseDouble(thresh.getText());

                //determine whether the pixel should be white or black based on the luminance threshold
                if (luminance >= luminanceThreshold) {
                    blackAndWhiteImage.getPixelWriter().setColor(column, row, WHITE);
                    pixelArray[((row*width)+column)] = row*width + column;
                } else {
                    blackAndWhiteImage.getPixelWriter().setColor(column, row, BLACK);
                    pixelArray[((row*width)+column)] = -1;
                }


            }
        }
        for(int row = 0; row < height; row++){
            for(int column = 0; column < width; column++){
                int currentIndex = (row * width) + column; //calculate the index of the current pixel in the pixel array
                int currentPixel = pixelArray[currentIndex]; //value of current pixel

              if(currentPixel >= 0) { //if the pixel is not black
                  if(column + 1 < width && pixelArray[currentIndex + 1] >= 0 ) { //if pixel on right isn't black
                      int besideIndex = row * width + (column + 1); //calculate index of pixel
                      union(pixelArray,currentIndex, besideIndex); //union the pixels
                  }

                  if(row + 1 < height && pixelArray[currentIndex + width] >= 0){ //if pixel below isn't black
                      int belowIndex = (row + 1) * width + column; //calculate index of pixel
                      union(pixelArray,currentIndex, belowIndex); //union the pixels
                  }
              }
            }
        }

        blackAndWhiteImageView.setImage(blackAndWhiteImage); //set the blackAndWhiteImageView to display the new image
        newBlackAndWhiteImage = blackAndWhiteImage; //return the new image

        for(int row = 0; row < height; row++){
            for(int column = 0; column < width; column++){
                int colour = pixelArray[((row*width)+column)]; //value of pixel

                if(colour >= 0){ //if the pixel isn't black / -1
                    int index = (row * width) + column;
                    int root = find(pixelArray, index); //find root containing the pixel

                    if(!hashMap.containsKey(root)){ //if the root is not already saved into the hashMap
                        hashMap.put(root, new ArrayList<>()); //create an array list for this root
                    }
                    hashMap.get(root).add(index); //add the pixel to the array list made for the root
                }
            }
        }

        int largestRoot = Collections.max(hashMap.keySet(),(a, b)-> hashMap.get(a).size() - hashMap.get(b).size());
        List list = hashMap.get(largestRoot);

        countCelestialObjects();
//        displayPixelArray();
        return newBlackAndWhiteImage;

    }




//    public void displayPixelArray() {
//        for(int i=0;i<pixelArray.length;i++)
//            System.out.print(find(pixelArray,i) + ((i+1)%width==0 ? "\n" : " "));
//    }

    public void unionPixels(int a, int b) {
        if(find(pixelArray, a) < find(pixelArray, b))
            union(pixelArray, a, b);
        else union(pixelArray, b, a);
    }


    public int find(int[] a, int id) {
        if(a[id]==-1) return -1;
        return a[id] == id ? id : (a[id] = find(a, a[id])); //finding the root of id
    } //keeps recursively calling itself until the root is found

    public void union(int[] a, int p, int q) { //merges two groups by connecting the root element of one group to the root element of another group
        a[find(a, q)] = find(a, p); //set the root of the group containing q to the root of the group containing p
    }



    public void randomColorImg(ActionEvent actionEvent) {
        Image blackAndWhiteImage = blackAndWhiteImageView.getImage(); //get black and white image
        PixelReader pixelReader = blackAndWhiteImage.getPixelReader(); //initialize pixel reader

        WritableImage randomColorImage = new WritableImage(width, height); //WritableImage object with the same dimensions as the original image
        PixelWriter pixelWriter = randomColorImage.getPixelWriter();

        //loops through each pixel of the black and white image
        for (int row = 0; row < height; row++){
            for (int column = 0; column < width; column++){
                int index = (row * width) + column;
                int root = find(pixelArray, index); //gets the root index of current disjoint set

                if (!hashMap.containsKey(root)){
                    pixelWriter.setColor(column, row, BLACK); //if the hashmap does not contain the root, make the pixel black
                }
            }
        }

        for(int row = 0; row < height; row++){
            for(int column = 0; column < width; column++){
                int index = (row * width) + column;
                int root = find(pixelArray, index); //gets the root index of current disjoint set

                if (hashMap.containsKey(root)){

                    //gets a random color for the current disjoint set
                    int red = (int) (Math.random() * 256);
                    int green = (int) (Math.random() * 256);
                    int blue = (int) (Math.random() * 256);
                    Color color = Color.rgb(red, green, blue);

                    //colours all pixels in the current disjoint set with the same color
                    for (int pixelIndex : hashMap.get(root)) {
                        int pixelRow = pixelIndex / width;
                        int pixelColumn = pixelIndex % width;
                        pixelWriter.setColor(pixelColumn, pixelRow, color);


                    }
                }
            }
            adjustableImage.setImage(randomColorImage); //set the adjustableImage to display the new image
        }
    }

    public void circleDisjointSets(ActionEvent actionEvent) {
        //gets the black and white image from the previous method
        WritableImage blackAndWhiteImage = newBlackAndWhiteImage;

        //create a canvas from the black and white image
        Canvas canvas = new Canvas(blackAndWhiteImage.getWidth(), blackAndWhiteImage.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(blackAndWhiteImage, 0, 0);

        //find the disjoint sets of white pixels (stars)
        for(int row = 0; row < height; row++){
            for(int column = 0; column < width; column++){
                int colour = pixelArray[((row*width)+column)];

                if(colour >= 0){
                    int index = (row * width) + column;
                    int root = find(pixelArray, index);

                    if(!hashMap.containsKey(root)){
                        hashMap.put(root, new ArrayList<>());
                    }
                    hashMap.get(root).add(index);
                }
            }
        }


        //circle each star / disjoint set
        int starCount = 0;
        for (List<Integer> set : hashMap.values()) {
            graphicsContext.setStroke(Color.BLUE); //make circles blue
            graphicsContext.setLineWidth(2);
            graphicsContext.setFill(Color.TRANSPARENT); //make inside of circle transparent


            //calculate the radius of the circle based on the size of the set
            int radius = (int) Math.sqrt(set.size());

            //find the center of the set
            int centerX = 0, centerY = 0;
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                centerX += x;
                centerY += y;

            }
            centerX /= set.size();
            centerY /= set.size();

            //draw the circle at the center of the disjoint set
            graphicsContext.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            starCount++;
        }

        WritableImage outputImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, outputImage);
        Stars.setText("Number of Stars: " + Integer.toString(starCount));

        blackAndWhiteImageView.setImage(outputImage);

    }

    public void circleAndLableDisjointSets(ActionEvent actionEvent) {
        //gets the black and white image from the previous method
        WritableImage blackAndWhiteImage = newBlackAndWhiteImage;


        //create a canvas from the black and white image
        Canvas canvas = new Canvas(blackAndWhiteImage.getWidth(), blackAndWhiteImage.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(blackAndWhiteImage, 0, 0);

        //find the disjoint sets of white pixels (stars)
        for(int row = 0; row < height; row++){
            for(int column = 0; column < width; column++){
                int colour = pixelArray[((row*width)+column)];

                if(colour >= 0){
                    int index = (row * width) + column;
                    int root = find(pixelArray, index);

                    if(!hashMap.containsKey(root)){
                        hashMap.put(root, new ArrayList<>());
                    }
                    hashMap.get(root).add(index);
                }
            }
        }

        double scaleOfX = blackAndWhiteImageView.getBoundsInParent().getWidth() / blackAndWhiteImage.getWidth();
        double scaleOfY = blackAndWhiteImageView.getBoundsInParent().getHeight() / blackAndWhiteImage.getHeight();
        double Scale = Math.min(scaleOfX, scaleOfY);

        // Sort the disjoint sets by their size in decreasing order
        List<List<Integer>> sets = new ArrayList<>(hashMap.values());
        sets.sort((a, b) -> b.size() - a.size());

        //circle each star / disjoint set
        int starCount = 0;
        int count = 0;
        boolean[] setDrawn = new boolean[hashMap.size()]; //boolean array to keep track of which sets have been drawn

        for (List<Integer> set : sets) {
            graphicsContext.setStroke(Color.BLUE); //make circles blue
            graphicsContext.setLineWidth(2);
            graphicsContext.setFill(Color.TRANSPARENT); //make inside of circle transparent

            count++;

            //calculate the radius of the circle based on the size of the set
            int radius = (int) Math.sqrt(set.size());

            //find the center of the set
            int centerX = 0, centerY = 0;
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                centerX += x;
                centerY += y;
            }

            centerX /= set.size();
            centerY /= set.size();

            //draw the circle at the center of the disjoint set
            graphicsContext.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            graphicsContext.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 28)); //set font size to 28, style to times new roman
            graphicsContext.setStroke(RED); //make font red
            graphicsContext.setLineWidth(2);
            graphicsContext.strokeText(Integer.toString(count), centerX + radius, centerY);


            starCount++;
        }

        WritableImage labeledImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, labeledImage);

        Label.setImage(labeledImage);
    }



    public void countCelestialObjects() {
        //gets the black and white image from the previous method
        WritableImage blackAndWhiteImage = newBlackAndWhiteImage;

        //create a canvas from the black and white image
        Canvas canvas = new Canvas(blackAndWhiteImage.getWidth(), blackAndWhiteImage.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(blackAndWhiteImage, 0, 0);

        //find the disjoint sets of white pixels
        int[] pixelArray = new int[(int) (blackAndWhiteImage.getWidth() * blackAndWhiteImage.getHeight())];
        blackAndWhiteImage.getPixelReader().getPixels(0, 0, (int) blackAndWhiteImage.getWidth(), (int) blackAndWhiteImage.getHeight(), PixelFormat.getIntArgbInstance(), pixelArray, 0, (int) blackAndWhiteImage.getWidth());
        int width = (int) blackAndWhiteImage.getWidth();
        int height = (int) blackAndWhiteImage.getHeight();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int colour = pixelArray[((row * width) + column)];
                if (colour >= 0) { //if colour isn't black
                    int index = (row * width) + column;
                    int root = find(pixelArray, index);
                    if (!hashMap.containsKey(root)) {
                        hashMap.put(root, new ArrayList<>());
                    }
                    hashMap.get(root).add(index);
                }
            }
        }

        //this creates a list to save the sets of white pixels in the hashMap created at the start
        List<List<Integer>> setsList = new ArrayList<>(hashMap.values());

        //this sort the list of sets by size from biggest star to smallest
        setsList.sort((set1, set2) -> Integer.compare(set2.size(), set1.size()));

        //this labels and prints each disjoint/star set along with its size in pixel units in the console
        int i = 1;
        for (List<Integer> set : setsList) {
            int size = set.size();
            System.out.println("Size of disjoint set " + i + ": " + size + " pixels");
            i++;
        }

    }


    public void exit(ActionEvent actionEvent){
        //exits app
        Platform.exit();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    fileChooser.setInitialDirectory(new File("C:\\Users\\tinyf\\OneDrive - Waterford Institute of Technology\\Applied Computing Course\\year 2\\semester 2\\data structures & algorithms 2\\Deep Space Imagery Analyser\\space images"));
    }
}