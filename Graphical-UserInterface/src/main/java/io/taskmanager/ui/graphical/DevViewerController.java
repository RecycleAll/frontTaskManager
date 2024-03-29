package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import io.taskmanager.core.DevStatus;
import io.taskmanager.core.Project;
import io.taskmanager.core.repository.RepositoryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DevViewerController extends TabPane {

    private static final String FXML_FILE = "DevViewerController.fxml";

    @FXML
    public Label firstNameLabel;
    @FXML
    public Label lastNameLabel;
    @FXML
    public VBox projectVBox;
    @FXML
    public Tab overviewTab;

    private final RepositoryManager repositoryManager;

    private Dev dev;

    public DevViewerController(RepositoryManager repo, Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repositoryManager = repo;
        setDev(dev);

        this.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            if (t1 == overviewTab) {
                try {
                    updateUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    }

    public DevViewerController(RepositoryManager repo) throws IOException {
        this(repo, null);
    }

    public void setDev(Dev dev) throws IOException {
        this.dev = dev;
        updateUI();
    }

    private void addProjectViewer(Project project) throws IOException, ExecutionException, InterruptedException {
        Optional<Tab> optionalTab = this.getTabs().stream().filter(tab -> {
            if (tab.getContent() instanceof ProjectController myTab) {
                return project == myTab.getProject();
            } else {
                return false;
            }
        }).findFirst();

        if (optionalTab.isPresent()) {
            this.getSelectionModel().select(optionalTab.get());
        } else {
            ProjectController projectController = new ProjectController(repositoryManager, project, dev.getId());
            Tab tab = new Tab(projectController.getProject().getName());
            tab.setContent(projectController);
            this.getTabs().add(tab);
            this.getSelectionModel().select(tab);
            this.getScene().getWindow().sizeToScene();
        }
    }


    private void updateUI() throws IOException {
        projectVBox.getChildren().clear();

        if (dev == null) {
            firstNameLabel.setText("No Dev");
            lastNameLabel.setText("");

        } else {
            firstNameLabel.setText(dev.getFirstname());
            lastNameLabel.setText(dev.getLastname().toUpperCase(Locale.ROOT));

            for (Project project : dev.getProjects()) {
                System.out.println("p: " + project.getName());
                addProject(project);
            }
        }
    }

    private void addProject(Project project) throws IOException {
        DevProjectController devProjectController = new DevProjectController(this, project, dev);
        devProjectController.setOnMouseClicked(mouseEvent -> {
            try {
                addProjectViewer(project);
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        projectVBox.getChildren().add(devProjectController);
    }

    public void removeProject(DevProjectController controller) {
        projectVBox.getChildren().remove(controller);
    }

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    @FXML
    @SuppressWarnings("unused") // used by FXML
    public void OnEditDev(ActionEvent actionEvent) throws IOException {
        DevEditorDialog dialog = new DevEditorDialog(dev, false);
        Optional<Dev> dev = dialog.showAndWait();
        if (dev.isPresent()) {
            updateUI();
        }
    }

    @FXML
    @SuppressWarnings("unused") // used by FXML
    public void onAddProjectButton(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        ProjectEditorDialog dialog = new ProjectEditorDialog(repositoryManager);
        Optional<Project> res = dialog.showAndWait();
        if (res.isPresent()) {
            Project project = res.get();
            project.addDev(dev, DevStatus.OWNER);

            project.setRepositoryManager(repositoryManager);
            project.postToRepo();

            addProject(project);
        }
    }

    public Dev getDev() {
        return dev;
    }
}

