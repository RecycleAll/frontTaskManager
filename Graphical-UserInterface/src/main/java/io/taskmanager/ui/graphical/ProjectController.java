package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import io.taskmanager.test.Dev;
import io.taskmanager.test.DevStatus;
import io.taskmanager.test.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.Optional;

public class ProjectController extends Tab {

    private static final String FXML_FILE = "ProjectController.fxml";

    @FXML
    public VBox devsVBox;
    @FXML
    public Label projectTitle;
    @FXML
    public HBox columnHBox;
    @FXML
    public ScrollPane columnScrollPane;
    @FXML
    public BorderPane borderPane;

    private Project project;
    private int loggedDevId;

    public ProjectController(Project project, int loggedDevId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.loggedDevId = loggedDevId;
        setProject(project);

    }

    private boolean isProjectDevControllerEditable(Dev dev){
        if( dev.getId() == loggedDevId){
            return true;
        }else if( project.getDevStatus(dev) == DevStatus.OWNER){
            return  true;
        }

        return false;
    }

    public void setProject(Project newProject) throws IOException {
        if( newProject == null){
            this.project = new Project();
        }else {
            this.project = newProject;
        }

        this.setText(project.getName());
        projectTitle.setText(project.getName());

        for (Dev dev:project.getDevs()) {
            devsVBox.getChildren().add( new ProjectDevController(this, dev, isProjectDevControllerEditable(dev)) );
        }

        for (Column col:project.getColumns()) {
            columnHBox.getChildren().add( new ProjectColumnController(this, col));
        }

    }

    public Project getProject() {
        return project;
    }

    public void removeColumn(ProjectColumnController projectColumnController){
        project.removeColumn(projectColumnController.getColumn());
        columnHBox.getChildren().remove(projectColumnController);
    }

    private void addColumn(Column column) throws IOException {
        project.addColumn(column);

        System.out.println("max min" + this.getTabPane().getMinWidth() + " / " + this.getTabPane().getMaxWidth());
        //System.out.println("borderPane width" + borderPane.minWidthProperty().get() + " < " + borderPane.getWidth() +" < " +borderPane.getMinWidth());

        System.out.println("added column " + borderPane.getWidth() + " / " + this.getTabPane().getWidth());
        columnHBox.getChildren().add( new ProjectColumnController(this, column));
        //this.getTabPane().setPrefWidth( borderPane.getWidth());
        System.out.println("added column " + borderPane.getWidth() + " / " + this.getTabPane().getWidth());
        System.out.println("added column " + borderPane.getWidth() + " / " + this.getTabPane().getWidth());
    }

    public void removeDev( ProjectDevController dev){
        project.removeDev(dev.getDev());
        devsVBox.getChildren().remove(dev);
    }

    private void addDev(Dev dev) throws IOException {
        project.addDev(dev);
        devsVBox.getChildren().add( new ProjectDevController(this, dev, isProjectDevControllerEditable(dev) ) );
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddColumn(ActionEvent actionEvent) throws IOException {
        ColumnEditorDialog dialog = new ColumnEditorDialog();
        Optional<Column> res = dialog.showAndWait();
        if(res.isPresent()){
            addColumn(res.get());
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddDev(ActionEvent actionEvent) throws IOException {
        DevEditorDialog devEditorDialog = new DevEditorDialog();
        Optional<Dev> res = devEditorDialog.showAndWait();
        if(res.isPresent()){
            this.addDev(res.get());
        }
    }
}

