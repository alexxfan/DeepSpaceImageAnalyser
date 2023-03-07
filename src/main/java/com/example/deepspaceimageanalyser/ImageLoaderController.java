package com.example.deepspaceimageanalyser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ImageLoaderController implements Initializable {
    FileChooser fileChooser = new FileChooser(); // file chooser


    private int[] imageArray;
    private int width;
    private int height;

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
        blackAndWhiteImageView.setImage(image); //set grayscale image to this
        PixelReader pixelReader = image.getPixelReader(); //initialize the pixel reader for the original image

        int width = (int) image.getWidth(); //get width of image
        int height = (int) image.getHeight(); //get height of image

        WritableImage blackAndWhiteImage = new WritableImage(width, height); //create a new WritableImage object with the same dimensions as the original image
        PixelWriter pixelWriter = blackAndWhiteImage.getPixelWriter();

        //loop through each pixel of the original image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                //get the red, green, and blue values of the current pixel
                double red = pixelReader.getColor(x, y).getRed();
                double green = pixelReader.getColor(x, y).getGreen();
                double blue = pixelReader.getColor(x, y).getBlue();

                //calculate the luminance of the current pixel
                double luminance = (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
//                double luminanceThreshold = 0.5;
                double luminanceThreshold = Double.parseDouble(thresh.getText());

                //determine whether the pixel should be white or black based on the luminance threshold
                if (luminance >= luminanceThreshold) {
                    blackAndWhiteImage.getPixelWriter().setColor(x, y, Color.WHITE);
                } else {
                    blackAndWhiteImage.getPixelWriter().setColor(x, y, Color.BLACK);
                }

            }
        }
        blackAndWhiteImageView.setImage(blackAndWhiteImage); //set the grayImageView to display the new grayscale image
        return blackAndWhiteImage; //return the new grayscale image
    }


    public void joinPixelsInArray(){ //joins adjacent pixels in an image array, using the union method
        for (int i = 0; i < imageArray.length; i++){  //loop through each pixel in the image array
            if (imageArray[i] >= 0) {  //check if the pixel is not already part of a group
                if ((i + 1) < imageArray.length && imageArray[i + 1] >= 0 && (i + 1) % width != 0){ //checks if the pixel to the right is also part of a group and not at the edge of the image
                    union(imageArray, i, i + 1); //join the pixels using the union method
                }
                if ((i + width) < imageArray.length && imageArray[i + width] >= 0){ //checks if the pixel below is also part of a group
                    union(imageArray, i, i + width); //join the pixels using the union method
                }
            }
        }
    }

    public int find(int[] a, int id) {
        return a[id] == id ? id : (a[id] = find(a, a[id])); //finding the root of id
    }

    public void union(int[] a, int p, int q) { //merges two groups by connecting the root element of one group to the root element of another group
        a[find(a, q)] = find(a, p); //set the root of the group containing q to the root of the group containing p
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