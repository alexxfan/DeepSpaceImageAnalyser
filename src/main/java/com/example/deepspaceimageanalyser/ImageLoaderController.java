package com.example.deepspaceimageanalyser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;

import javafx.scene.paint.Color;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

public class ImageLoaderController implements Initializable {
    FileChooser fileChooser = new FileChooser(); //file chooser


    private int[] pixelArray;
    private int width;
    private int height;
    private Color white, black;
    private WritableImage newBlackAndWhiteImage;

    HashMap<Integer, ArrayList<Integer>> hashMap;

    @FXML
    public ImageView regularImage, blackAndWhiteImageView, redImage, greenImage, blueImage, adjustableImage;

    @FXML
    public Label Hue, Brightness, Saturation;

    @FXML
    private Button Load, Exit, bW;

    @FXML
    private MenuButton colourChoices;

    @FXML
    private MenuItem blackAndWhite, Red, Green, Blue;

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
                int currentIndex = (row * width) + column;
                int currentPixel = pixelArray[currentIndex]; //(row*column)];

              if(currentPixel >= 0) {
                  if(column + 1 < width && pixelArray[currentIndex + 1] >= 0 ) {
                      int besideIndex = row * width + (column + 1);
                      union(pixelArray,currentIndex, besideIndex);
                  }

                  if(row + 1 < height && pixelArray[currentIndex + width] >= 0){
                      int belowIndex = (row + 1) * width + column;
                      union(pixelArray,currentIndex, belowIndex);
                  }
              }
            }
        }

        blackAndWhiteImageView.setImage(blackAndWhiteImage); //set the blackAndWhiteImageView to display the new image
        newBlackAndWhiteImage = blackAndWhiteImage; //return the new image

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

        int largestRoot = Collections.max(hashMap.keySet(),(a, b)-> hashMap.get(a).size() - hashMap.get(b).size());
        List list = hashMap.get(largestRoot);

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
        Image blackAndWhiteImage = blackAndWhiteImageView.getImage(); // get black and white image
        PixelReader pixelReader = blackAndWhiteImage.getPixelReader(); // initialize pixel reader

        WritableImage randomColorImage = new WritableImage(width, height); // create a new WritableImage object with the same dimensions as the original image
        PixelWriter pixelWriter = randomColorImage.getPixelWriter();

        //loops through each pixel of the black and white image
        for (int row = 0; row < height; row++){
            for (int column = 0; column < width; column++){
                int index = (row * width) + column;
                int root = find(pixelArray, index); //gets the root index of current disjoint set

                if (!hashMap.containsKey(root)){
                    pixelWriter.setColor(column, row, BLACK);
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
            adjustableImage.setImage(randomColorImage); // set the adjustableImage to display the new image
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