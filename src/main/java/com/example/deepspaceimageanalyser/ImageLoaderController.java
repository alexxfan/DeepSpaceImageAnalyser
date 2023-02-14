package com.example.deepspaceimageanalyser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageLoaderController implements Initializable {
    FileChooser fileChooser = new FileChooser(); // file chooser


    private Color sampleColour;

    static Image image;
    private int[] imageArray;
    private int width;
    private int height;
    private WritableImage newBWImage;
    private WritableImage newPCBImage;

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
        File file = fileChooser.showOpenDialog(new Stage()); // open file explorer
        fileChooser.setInitialDirectory(new File("C:\\Users\\tinyf\\OneDrive - Waterford Institute of Technology\\Applied Computing Course\\year 2\\semester 2\\data structures & algorithms 2\\Deep Space Imagery Analyser\\space images"));
        if(file != null) {
            Image image = new Image(file.toURI().toString()); // make file into a URI
            regularImage.setImage(image); // open image in imageviewer

        }
        else{
            System.out.println("No Image Chosen");
        }

    }

    public WritableImage blackAndWhiteImg(ActionEvent actionEvent) {

        Image image = regularImage.getImage(); // get the chosen image from file explorer (regular imageview)
        blackAndWhiteImageView.setImage(image); // set grayscale image to this
        PixelReader pixelReader = image.getPixelReader(); // initialize the pixel reader for the original image

        int width = (int) image.getWidth(); // get width of image
        int height = (int) image.getHeight(); // get height of image

        WritableImage blackAndWhiteImage = new WritableImage(width, height); // create a new WritableImage object with the same dimensions as the original image

        // loop through each pixel of the original image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // get the red, green, and blue values of the current pixel
                double red = pixelReader.getColor(x, y).getRed();
                double green = pixelReader.getColor(x, y).getGreen();
                double blue = pixelReader.getColor(x, y).getBlue();

                // calculate the luminance of the current pixel
                double luminance = (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
//                double luminanceThreshold = 0.5;
                double luminanceThreshold = Double.parseDouble(thresh.getText());

                // determine whether the pixel should be white or black based on the luminance threshold
                if (luminance >= luminanceThreshold) {
                    blackAndWhiteImage.getPixelWriter().setColor(x, y, Color.WHITE);
                } else {
                    blackAndWhiteImage.getPixelWriter().setColor(x, y, Color.BLACK);
                }

            }
        }
        blackAndWhiteImageView.setImage(blackAndWhiteImage); // set the grayImageView to display the new grayscale image
        return blackAndWhiteImage; // return the new grayscale image
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