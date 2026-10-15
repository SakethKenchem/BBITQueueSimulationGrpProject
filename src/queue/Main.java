package queue;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Random;

public class Main extends Application {

    private final Random randomGenerator = new Random();
    private final ObservableList<BankCustomer> customers = FXCollections.observableArrayList();

    private TableView<BankCustomer> resultsTable;
    private VBox statisticsPanel;

    private TextField customerCountField;
    private TextField minArrivalField;
    private TextField maxArrivalField;
    private TextField minServiceField;
    private TextField maxServiceField;

    @Override
    public void start(Stage stage) {
        // 1. Header & Group Members
        Label title = new Label("BANK QUEUE SIMULATION");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label BBITGroup = new Label("BBIT Group D");
        BBITGroup.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        BBITGroup.setStyle("-fx-text-fill: #1D4E89;");

        Label groupMembers = new Label(
                "Andrew Mutero - 150736  |  Saketh Kenchem - 152324  |  " +
                "Kumaylali Murji Viqarhussein - 193642  |  Mark Kipruto - 169386  |  Lewis Kamunjeru - 155669"
        );
        groupMembers.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        VBox header = new VBox(5, title, BBITGroup, groupMembers);
        header.setPadding(new Insets(10, 0, 10, 0));

        // 2. Input Panel
        VBox inputPanel = createInputPanel();

        // 3. Results Table (Left Side)
        resultsTable = new TableView<>();
        resultsTable.setItems(customers);
        resultsTable.setPlaceholder(new Label("Run the simulation to display data."));
        setupResultsTable();
        HBox.setHgrow(resultsTable, Priority.ALWAYS); // Ensures table gets maximum space

        // 4. Statistics Panel (Right Side, Simple Text)
        statisticsPanel = new VBox(10);
        statisticsPanel.setMinWidth(300);
        statisticsPanel.setPadding(new Insets(0, 0, 0, 15));
        resetStatisticsPanel();

        // Layout holding Table and Stats side-by-side
        HBox tableAndStats = new HBox(10, resultsTable, statisticsPanel);
        VBox.setVgrow(tableAndStats, Priority.ALWAYS);

        // Main Layout
        VBox mainLayout = new VBox(15, header, inputPanel, tableAndStats);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #F5F7FA;");

        Scene scene = new Scene(mainLayout, 1250, 780);
        stage.setTitle("Bank Queue Simulation");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createInputPanel() {
        customerCountField = createInputField("20");
        minArrivalField = createInputField("1");
        maxArrivalField = createInputField("7");
        minServiceField = createInputField("1");
        maxServiceField = createInputField("6");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(5);

        addInputField(grid, "No. of Customers:", customerCountField, 0);
        addInputField(grid, "Min Arrival Time:", minArrivalField, 1);
        addInputField(grid, "Max Arrival Time:", maxArrivalField, 2);
        addInputField(grid, "Min Service Time:", minServiceField, 3);
        addInputField(grid, "Max Service Time:", maxServiceField, 4);

        Button runButton = new Button("Run Simulation");
        runButton.setStyle("-fx-background-color: #1D4E89; -fx-text-fill: white; -fx-font-weight: bold;");
        runButton.setOnAction(e -> startSimulation());

        Button clearButton = new Button("Clear Results");
        clearButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white; -fx-font-weight: bold;");
        clearButton.setOnAction(e -> clearResults());

        HBox buttons = new HBox(10, runButton, clearButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox panel = new VBox(10, grid, buttons);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        return panel;
    }
