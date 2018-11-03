package ru.SemperAnte.TextParser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.UnsupportedEncodingException;


public class AppMain extends Application
{
    private AnchorPane root;
    private Scene scene;
    private Stage stage;
    private TextArea output;
    private TextArea input;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialog.fxml"));
        root = loader.load();
        scene = new Scene(root);
        this.stage = primaryStage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        initButtons();
        initTextAreas();
        stage.show();
    }

    private void initTextAreas()
    {
        input = (TextArea) root.lookup("#textInput");
        output = (TextArea) root.lookup("#textInput");

    }

    private double X;
    private double Y;
    private void initButtons()
    {
        Button collapse = (Button) root.lookup("#collButton");
        Button close = (Button) root.lookup("#closeButton");
        Button go = (Button) root.lookup("#goButton");
        Button copy = (Button) root.lookup("#copyButton");
        Button goWithout = (Button) root.lookup("#goWithoutEnc");
        Button removeWiki =  (Button) root.lookup("#removeWiki");
        Button removeFig =  (Button) root.lookup("#removeFig");
        Button allInOne = (Button) root.lookup("#allInOne");
        Button clearButton = (Button) root.lookup("#ClearButton");
        close.setOnAction(event -> Platform.exit());
        collapse.setOnAction((event) -> stage.setIconified(true));
        clearButton.setOnAction((event)->input.clear());
        copy.setOnAction((event) ->
        {
            ClipboardContent cn = new ClipboardContent();
            cn.clear();
            cn.putString(output.getText());
            Clipboard.getSystemClipboard().setContent(cn);
        });
        go.setOnAction((event) ->
        {
            String text = input.getText();
            if (text == null)
            { return; }

            try
            {
                output.setText(Parser.parseFromString(text, "Windows-1252", "Windows-1251"));
            } catch (UnsupportedEncodingException e)
            {
                output.setText("Какая-то ошибочка вышла =( \n" + e.getMessage());
            }
        });
        goWithout.setOnAction((event) ->
        {
            String text = input.getText();
            if (text == null)
            { return; }

            try
            {
                output.setText(Parser.parseFromString(text, "UTF-8", "UTF-8"));
            } catch (Exception e)
            {
                output.setText("Какая-то ошибочка вышла =( \n" + e.getMessage());
            }
        });

        removeWiki.setOnAction((event)->
        {
            String text = input.getText();
            if(text == null)
                return;
            output.setText(Parser.removeWiki(text));
        });
        removeFig.setOnAction((event)->
        {
            String text = input.getText();
            if(text ==null)
                return;
            output.setText(Parser.removeBraces(text));
        });
        allInOne.setOnAction((event)->
        {
            String text = input.getText();
            if(text == null)
                return;
            output.setText(Parser.toOneLineAll(text));
        });
        root.setOnMousePressed(event ->
        {
            X = stage.getX() - event.getScreenX();
            Y = stage.getY() - event.getScreenY();
        });
        root.setOnMouseDragged(event ->
                {
                    stage.setX(event.getScreenX() + X);
                    stage.setY(event.getScreenY() + Y);
                }
        );
    }

    public static void main(String... args)
    {
        launch(args);
    }
}
