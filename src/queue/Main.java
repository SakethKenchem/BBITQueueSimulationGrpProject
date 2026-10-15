package queue;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Main extends Application {

    private static final String BLUE = "#1D4E89";
    private static final String RED = "#C62828";

    private final TextField iatMinField = new TextField("1");
    private final TextField iatMaxField = new TextField("7");
    private final TextField serviceMinField = new TextField("1");
    private final TextField serviceMaxField = new TextField("6");
    private final TextField customersField = new TextField("20");

    private final TableView<Customer> table = new TableView<>();
    private final TextArea statisticsArea = new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Bank Queue Simulation");

        // Main heading
        Label heading = new Label("BANK QUEUE SIMULATION");
        heading.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + BLUE + ";"
        );

        // Group members
        Label groupMembers = new Label(
                "Andrew Mutero - 150736, "
                + "Saketh Kenchem - 152324, "
                + "Murji Viqarhussein - 193642, "
                + "Mark Kipruto - 169386, "
                + "Lewis Kamunjeru - 155669"
        );
        groupMembers.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: " + RED + ";"
                + "-fx-font-weight: bold;"
        );

        // Input fields
        GridPane inputs = new GridPane();
        inputs.setHgap(12);
        inputs.setVgap(10);
        inputs.setPadding(new Insets(12));

        addInput(inputs, 0, "IAT Minimum (min):", iatMinField);
        addInput(inputs, 1, "IAT Maximum (min):", iatMaxField);
        addInput(inputs, 2, "Service Minimum (min):", serviceMinField);
        addInput(inputs, 3, "Service Maximum (min):", serviceMaxField);
        addInput(inputs, 4, "Number of Customers:", customersField);

        Button simulateButton = new Button("Run Simulation");
        Button clearButton = new Button("Clear");

        simulateButton.setDefaultButton(true);
        simulateButton.setStyle(
                "-fx-background-color: " + BLUE + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
        );

        clearButton.setStyle(
                "-fx-background-color: " + RED + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
        );

        simulateButton.setOnAction(e -> {
            try {
                double iatMin
                        = readPositive(iatMinField, "IAT minimum");
                double iatMax
                        = readPositive(iatMaxField, "IAT maximum");
                double serviceMin
                        = readPositive(serviceMinField, "Service minimum");
                double serviceMax
                        = readPositive(serviceMaxField, "Service maximum");

                int n = Integer.parseInt(
                        customersField.getText().trim());

                if (iatMax <= iatMin) {
                    throw new IllegalArgumentException(
                            "IAT maximum must be greater than IAT minimum.");
                }

                if (serviceMax <= serviceMin) {
                    throw new IllegalArgumentException(
                            "Service maximum must be greater than service minimum.");
                }

                if (n < 1 || n > 1000000) {
                    throw new IllegalArgumentException(
                            "Customers must be between 1 and 1,000,000.");
                }

                simulate(iatMin, iatMax, serviceMin, serviceMax, n);

            } catch (NumberFormatException ex) {
                showError("Enter valid numeric values in all fields.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        clearButton.setOnAction(e -> {
            table.getItems().clear();
            statisticsArea.clear();
        });

        HBox buttons = new HBox(10, simulateButton, clearButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox inputPanel = new VBox(
                10, new Label("Simulation Inputs"), inputs, buttons);
        inputPanel.setPadding(new Insets(10));
        inputPanel.setStyle(
                "-fx-background-color: #EAF1FA;"
                + "-fx-border-color: " + BLUE + ";"
                + "-fx-border-radius: 5;"
        );

        // Simulation results table
        configureTable();

        Label tableHeading = new Label("Simulation Results");
        tableHeading.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + BLUE + ";"
        );
