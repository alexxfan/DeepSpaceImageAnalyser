module com.example.deepspaceimageanalyser {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.internal.le;


    opens com.example.deepspaceimageanalyser to javafx.fxml;
    exports com.example.deepspaceimageanalyser;
}