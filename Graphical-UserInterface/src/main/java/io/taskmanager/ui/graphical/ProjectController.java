package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.Optional;

public class ProjectController {

    private static final String FXML_FILE = "ProjectViewer.fxml";

    public static ProjectController loadNew(Project project) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.load();
        ProjectController projectController = fxmlLoader.getController();
        projectController.setProject(project);
        return projectController;
    }
    public static ProjectController loadNew() throws IOException {
        return loadNew(null);
    }

    @FXML
    public VBox devsVBox;
    @FXML
    public Label projectTitle;
    @FXML
    public HBox columnHBox;
    @FXML
    public BorderPane borderPane;

    private Project project;

    public void setProject(Project newProject) throws IOException {
        if( newProject == null){
            this.project = new Project();
        }else {
            this.project = newProject;
        }

        projectTitle.setText(project.getName());

        for (Dev dev:project.getDevs()) {
            devsVBox.getChildren().add( ProjectDevController.loadNew(this, dev).anchorPane );
        }

        for (Column col:project.getColumns()) {
            columnHBox.getChildren().add( ColumnViewer.loadNew(this, col).scrollPane);
        }

    }

    public void removeColumn(ColumnViewer columnViewer){
        project.removeColumn(columnViewer.getColumn());
        columnHBox.getChildren().remove(columnViewer.scrollPane);
    }

    public void removeDev( ProjectDevController dev){
        project.removeDev(dev.getDev());
        devsVBox.getChildren().remove(dev.anchorPane);
    }

    private void addDev(Dev dev) throws IOException {
        project.addDev(dev);
        devsVBox.getChildren().add( ProjectDevController.loadNew(this, dev).anchorPane );
    }

    private void removeDev(DevViewer devViewer){

    }

    @FXML
    public void OnAddColumn(ActionEvent actionEvent) {
    }


    public void OnAddDev(ActionEvent actionEvent) throws IOException {
        DevDialog devDialog = new DevDialog();
        Optional<Dev> res = devDialog.showAndWait();
        if(res.isPresent()){
            this.addDev(res.get());
        }
    }


    private static class DevViewer extends GridPane {

        public DevViewer(ProjectController projectController, Dev dev) {
            super();
            this.add( new Label(dev.getFirstName()), 0, 0);
            this.add( new Label(dev.getLastName()), 0, 1);
            this.setBorder( new Border( new BorderStroke(Paint.valueOf("black"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1))));
            this.setBackground( new Background( new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            Button button = new Button("view");
            button.setOnAction(actionEvent -> {

            });
            AnchorPane pane = new AnchorPane();
            AnchorPane.setRightAnchor( button, 0d);
            pane.getChildren().add( button);
            this.add( pane, 1, 0);
            this.prefWidth(USE_COMPUTED_SIZE);
            this.maxWidth(USE_COMPUTED_SIZE);
        }
    }
}

