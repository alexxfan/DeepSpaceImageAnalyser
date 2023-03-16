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

import static jdk.internal.org.jline.utils.AttributedStyle.BLACK;
import static jdk.internal.org.jline.utils.AttributedStyle.WHITE;

public class ImageLoaderController implements Initializable {
    FileChooser fileChooser = new FileChooser(); // file chooser


    private int[][] pixelArray;
    public int size;
    public int[] sizeOfSet;
    public int[] id;
    public int sets;
    private int width;
    private int height;

    Hashtable<Integer, ArrayList<Integer>> hashTable;

    @FXML
    public ImageView regularImage, blackAndWhiteImageView, redImage, greenImage, blueImage, adjustableImage;

    @FXML
    public Label Hue, Brightness, Saturation;

    @FXML
    private Button Load, Exit, Threshold;

    @FXML
    private MenuButton choices;

    @FXML
    private MenuItem Random, Red, Green, Blue;

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

//    public int find(int[] a, int id) {
//        return a[id] == id ? id : (a[id] = find(a, a[id])); //finding the root of id
//    } // keeps recursively calling itself until the root is found

    public int find(int p){
        int parent = p;
        while(parent != id[p]) parent = id[parent];

        while (p != parent){
            int goNext = id[p];
            id[p] = parent;
            p = goNext;

        }
        return parent;
    }

//    public void union(int[] a, int p, int q) { //merges two groups by connecting the root element of one group to the root element of another group
//        a[find(a, q)] = find(a, p); //set the root of the group containing q to the root of the group containing p
//    }

    public void unionFind(int size){
        if (size<=0) throw new IllegalArgumentException();

        this.size = sets = size;
        sizeOfSet = new int[size];
        id = new int[size];

        for(int i = 0; i < size; i++){
            id[i] = i;
            sizeOfSet[i] = 1;
        }
    }

    public void unify(int a, int b){
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) return;

        if(sizeOfSet[rootA] < sizeOfSet[rootB]){
            sizeOfSet[rootB] += sizeOfSet[rootA];
            id[rootA] = id[rootB];
        }
        else{
            sizeOfSet[rootA] += sizeOfSet[rootB];
            id[rootB] += id[rootA];
        }
        sets--;
    }

//    public void unionPixels(int a, int b) {
//        if(find(pixelArray, a) < find(pixelArray, b))
//            union(pixelArray, a, b);
//        else union(pixelArray, b, a);
//    }

    public WritableImage blackAndWhiteImg(ActionEvent actionEvent) {

        Image image = regularImage.getImage(); //get the chosen image from file explorer (regular imageview)
        blackAndWhiteImageView.setImage(image); //set black and white image to this
        PixelReader pixelReader = image.getPixelReader(); //initialize the pixel reader for the original image

        width = (int) image.getWidth(); //get width of image
        height = (int) image.getHeight(); //get height of image
        pixelArray = new int [height][width];

        WritableImage blackAndWhiteImage = new WritableImage(width, height); //create a new WritableImage object with the same dimensions as the original image

        //loop through each pixel of the original image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                //get the red, green, and blue values of the current pixel
                double red = pixelReader.getColor(x, y).getRed();
                double green = pixelReader.getColor(x, y).getGreen();
                double blue = pixelReader.getColor(x, y).getBlue();

                //calculate the luminance of the current pixel
                double luminance = (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
                double luminanceThreshold = Double.parseDouble(thresh.getText());

                //determine whether the pixel should be white or black based on the luminance threshold
                if (luminance >= luminanceThreshold) {
                    blackAndWhiteImage.getPixelWriter().setColor(x, y, Color.WHITE);
                    pixelArray[y][x] = WHITE;
                } else {
                    blackAndWhiteImage.getPixelWriter().setColor(x, y, Color.BLACK);
                    pixelArray[y][x] = BLACK;
                }

            }
        }
        unionFind(height*width);

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int currentPixel = pixelArray[y][x];
                int currentIndex = y * width + x;

                if (currentPixel == WHITE){
                    if(x+1 < width && pixelArray[y][x + 1] == WHITE){
                        int besideIndex = y * width * (x + 1);
                        unify(currentIndex, besideIndex);
                    }
                    if (y + 1 > height && pixelArray[y + 1][x] == WHITE){
                        int belowIndex = (y + 1) * width + x;
                        unify(currentIndex, belowIndex);
                    }
                }
            }
        }

        blackAndWhiteImageView.setImage(blackAndWhiteImage); //set the grayImageView to display the new grayscale image
        return blackAndWhiteImage; //return the new grayscale image
    }


    public WritableImage randomColorImg(ActionEvent actionEvent) {

        Image image = blackAndWhiteImageView.getImage(); //get the chosen image from the black image view
        PixelReader pixelReader = image.getPixelReader(); //initialize the pixel reader for the original image

        width = (int) image.getWidth(); //get width of image
        height = (int) image.getHeight(); //get height of image
        pixelArray = new int[height][width];

        WritableImage randomColorImage = new WritableImage(width, height); //create a new WritableImage object with the same dimensions as the original image

        //loop through each pixel of the original image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                //get the red, green, and blue values of the current pixel
                double red = pixelReader.getColor(x, y).getRed();
                double green = pixelReader.getColor(x, y).getGreen();
                double blue = pixelReader.getColor(x, y).getBlue();

                //calculate the luminance of the current pixel
                double luminance = (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
                double luminanceThreshold = Double.parseDouble(thresh.getText());

                //determine whether the pixel should be coloured or black based on the luminance threshold
                if (luminance >= luminanceThreshold) {
                    //generate a random color for the current pixel
                    Color randomColor = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
                    randomColorImage.getPixelWriter().setColor(x, y, randomColor);
                } else {
                    randomColorImage.getPixelWriter().setColor(x, y, Color.BLACK);
                }

            }
        }
        blackAndWhiteImageView.setImage(randomColorImage); //set the grayImageView to display the new grayscale image
        return randomColorImage; //return the new colored image
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