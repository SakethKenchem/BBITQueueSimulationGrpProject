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
    
        private TextField createInputField(String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.setPrefWidth(90);
        return field;
    }

    private void addInputField(GridPane grid, String labelText, TextField field, int column) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        grid.add(label, column, 0);
        grid.add(field, column, 1);
    }

    private void setupResultsTable() {
        addTableColumn("Customer", "customerNumber");
        addTableColumn("IAT", "interArrivalTime");
        addTableColumn("Arrival Time", "arrivalTime");
        addTableColumn("Service Time", "serviceTime");
        addTableColumn("Service Starts", "serviceStartTime");
        addTableColumn("Service Ends", "serviceEndTime");
        addTableColumn("In System", "customersInSystem");
        addTableColumn("In Queue", "customersInQueue");
        addTableColumn("Waiting Time", "waitingTime");
        addTableColumn("Time in System", "totalTimeInSystem");
    }

    private void addTableColumn(String title, String property) {
        TableColumn<BankCustomer, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setStyle("-fx-alignment: CENTER;");

        final boolean isIatColumn = property.equals("interArrivalTime");

        col.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (isIatColumn && getIndex() == 0) {
                    // Customer 1 arrives at time 0, so it has no inter-arrival time
                    setText("-");
                } else if (item instanceof Double) {
                    setText(String.format(Locale.US, "%.2f", item));
                } else {
                    setText(item.toString());
                }
            }
        });
        resultsTable.getColumns().add(col);
    }

    private void startSimulation() {
        try {
            int numCustomers = Integer.parseInt(customerCountField.getText().trim());
            double minArr = Double.parseDouble(minArrivalField.getText().trim());
            double maxArr = Double.parseDouble(maxArrivalField.getText().trim());
            double minSrv = Double.parseDouble(minServiceField.getText().trim());
            double maxSrv = Double.parseDouble(maxServiceField.getText().trim());

            if (maxArr <= minArr || maxSrv <= minSrv || numCustomers < 1) {
                showError("Invalid input. Maximums must exceed minimums, and customers must be > 0.");
                return;
            }

            runQueueLogic(numCustomers, minArr, maxArr, minSrv, maxSrv);
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers in all fields.");
        }
    }

    private void runQueueLogic(int numCustomers, double minArr, double maxArr, double minSrv, double maxSrv) {
        customers.clear();
        double prevArrTime = 0;
        double prevSrvEnd = 0;

        // Accumulators for queue statistics
        double totalWait = 0;
        double totalSysTime = 0;
        double totalSrvTime = 0;
        double totalIat = 0;          // sum of IATs for customers 2..n
        int customersWaited = 0;

        int departed = 0;
        int startedSrv = 0;

        for (int i = 1; i <= numCustomers; i++) {
            // Customer 1 arrives at time 0 (no IAT). Others get a uniform IAT on (minArr, maxArr).
            double iat;
            double arrTime;
            if (i == 1) {
                iat = 0;
                arrTime = 0;
            } else {
                iat = minArr + randomGenerator.nextDouble() * (maxArr - minArr);
                arrTime = prevArrTime + iat;
                totalIat += iat;
            }

            // Uniformly distributed service time
            double srvTime = minSrv + randomGenerator.nextDouble() * (maxSrv - minSrv);

            // Queue formulas
            double srvStart = Math.max(arrTime, prevSrvEnd);
            double srvEnd = srvStart + srvTime;
            double waitTime = srvStart - arrTime;
            double timeInSys = srvEnd - arrTime;

            // Calculate current states for table representation
            while (departed < customers.size() && customers.get(departed).getServiceEndTime() <= arrTime) departed++;
            while (startedSrv < customers.size() && customers.get(startedSrv).getServiceStartTime() <= arrTime) startedSrv++;

            int inSystem = i - departed;
            int inQueue = i - 1 - startedSrv;

            if (waitTime > 0) {
                customersWaited++;
                inQueue++;
            }

            customers.add(new BankCustomer(i, iat, arrTime, srvTime, srvStart, srvEnd, inSystem, inQueue, waitTime, timeInSys));

            // Accumulate totals
            totalWait += waitTime;
            totalSysTime += timeInSys;
            totalSrvTime += srvTime;

            prevArrTime = arrTime;
            prevSrvEnd = srvEnd;
        }
        private void addStatLine(String title, String value) {
        Label label = new Label(title + value);
        label.setStyle("-fx-font-size: 14px; -fx-padding: 5 0 5 0;");
        statisticsPanel.getChildren().add(label);
    }

    private void clearResults() {
        customers.clear();
        resetStatisticsPanel();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static class BankCustomer {
        private final SimpleIntegerProperty customerNumber, customersInSystem, customersInQueue;
        private final SimpleDoubleProperty interArrivalTime, arrivalTime, serviceTime, serviceStartTime, serviceEndTime, waitingTime, totalTimeInSystem;

        public BankCustomer(int cNum, double iat, double arrTime, double srvTime, double srvStart, double srvEnd, int inSys, int inQ, double wait, double totSys) {
            this.customerNumber = new SimpleIntegerProperty(cNum);
            this.interArrivalTime = new SimpleDoubleProperty(iat);
            this.arrivalTime = new SimpleDoubleProperty(arrTime);
            this.serviceTime = new SimpleDoubleProperty(srvTime);
            this.serviceStartTime = new SimpleDoubleProperty(srvStart);
            this.serviceEndTime = new SimpleDoubleProperty(srvEnd);
            this.customersInSystem = new SimpleIntegerProperty(inSys);
            this.customersInQueue = new SimpleIntegerProperty(inQ);
            this.waitingTime = new SimpleDoubleProperty(wait);
            this.totalTimeInSystem = new SimpleDoubleProperty(totSys);
        }

        public int getCustomerNumber() { return customerNumber.get(); }
        public double getInterArrivalTime() { return interArrivalTime.get(); }
        public double getArrivalTime() { return arrivalTime.get(); }
        public double getServiceTime() { return serviceTime.get(); }
        public double getServiceStartTime() { return serviceStartTime.get(); }
        public double getServiceEndTime() { return serviceEndTime.get(); }
        public int getCustomersInSystem() { return customersInSystem.get(); }
        public int getCustomersInQueue() { return customersInQueue.get(); }
        public double getWaitingTime() { return waitingTime.get(); }
        public double getTotalTimeInSystem() { return totalTimeInSystem.get(); }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

