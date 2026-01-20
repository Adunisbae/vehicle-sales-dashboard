package coursework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.animation.Animation;
import javafx.animation.KeyFrame; 
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.Duration;

import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CourseworkController implements Initializable {
    private static String markup;
    private static List<Sales> sales;
    private static final String Password = "APPDS24";

    private BackUp backUp;
    private DashService dashService;
    
    @FXML
    private AnchorPane AnchorPane1;
    
    @FXML
    private BarChart<String, Number> BarChart;
    
    @FXML
    private CategoryAxis barChartXAxis;
    
    @FXML
    private NumberAxis barChartYAxis;
    
    @FXML
    private LineChart<String, Number> LineChart;
    
    @FXML
    private CategoryAxis lineChartXAxis;

    @FXML
    private NumberAxis lineChartYAxis; 
    
    @FXML
    private PieChart PieChart;
        
    @FXML
    private TableView<Sales> salesTable;
    
    @FXML
    private TableColumn<Sales, Integer> yearColumn;
    
    @FXML
    private TableColumn<Sales, Integer> quantityColumn;
    
    @FXML
    private TableColumn<Sales, Integer> quarterColumn;
    
    @FXML
    private TableColumn<Sales, String> vehicleColumn;
    
    @FXML
    private TableColumn<Sales, String> regionColumn;
    
    @FXML
    private ComboBox<Integer> yearComboBox;
    
    @FXML
    private ComboBox<String> vehicleComboBox;
    
    @FXML
    private ProgressIndicator ProgressIndicator;
    
    @FXML
    private Label dateTimeLabel;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button clearDataButton;

    @FXML
    private Button viewJsonButton;
    
    @FXML
    private TextField TotalSales;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        backUp = new BackUp("SGetSales.JSON");

        dashService = new DashService();
        dashService.setAddress("https://webteach.ljmu.ac.uk/DashService/SGetSales");
        
        // Handles successful data fetch
        dashService.setOnSucceeded((WorkerStateEvent e) -> {
            markup = e.getSource().getValue().toString();
            backUp.backUp(markup);
            
             // Clears the backup data after successfully backing uo
            backUp.clearBackUp();
            
            deSerialize();
            setupControls();
            updateCharts(); // updates the charts with the data
        });
        
        // Handles failed data fetch
        dashService.setOnCancelled((WorkerStateEvent e) -> {
            if (backUp.exists()) {
                markup = backUp.restore();

                deSerialize();
                setupControls();
                updateCharts();
                showAlert(Alert.AlertType.WARNING, 
                        "Using Cached Data", 
                        "BackUp JSON - Using cached data. Please check your internet connection.");
            } else {
                showAlert(Alert.AlertType.ERROR, 
                        "No Data Available", 
                        "No JSON - No data available. Please check your internet connection and try again.");
            }
        });
        
        // Binds the visibility of the progress indicator to the service's running state
        ProgressIndicator.visibleProperty().bind(dashService.runningProperty());
        
        setupTable();
        setupRefreshButton();
        setupClearDataButton();
        setupViewJsonButton();
        showLoginDialog();

        dashService.start();
        
        // Sets up a timeline to update the data and time every second
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy, HH:mm:ss a")); 
            // EEEE - Full day of the week, dd - day of the month, MMM - abbreviated month, yyyy - Year, Hour, Minute, Seconds, a - Time with AM/PM
            dateTimeLabel.setText(formattedDateTime);
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void deSerialize() {
        //Deserialize JSON data into Sales List
        sales = (new Gson()).fromJson(markup, new TypeToken<LinkedList<Sales>>() {}.getType());
        
    }

    private void setupControls() {
        // Set items for ComboBoxes
        Set<Integer> years = sales.stream().map(Sales::getYear).collect(Collectors.toSet());
        Set<String> vehicles = sales.stream().map(Sales::getVehicle).collect(Collectors.toSet());

        yearComboBox.setItems(FXCollections.observableArrayList(years));
        vehicleComboBox.setItems(FXCollections.observableArrayList(vehicles));
        
        //Add listeners to ComboBoxes for updating charts on selection change
        yearComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateCharts());
        vehicleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateCharts());

        if (!years.isEmpty()) yearComboBox.getSelectionModel().selectFirst();
        if (!vehicles.isEmpty()) vehicleComboBox.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("Year"));
        quarterColumn.setCellValueFactory(new PropertyValueFactory<>("QTR"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("Region"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("Vehicle"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        
        quantityColumn.setOnEditCommit(event -> {
            Sales salesItem = event.getRowValue();
            salesItem.setQuantity(event.getNewValue());
            updateCharts();
        });
        
        // Enable multiple row selection in the table
        // When selecting rows in the dashbaord, select multiple using the shift/control/command button
        salesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Listen to selection changes and update total quantity
        salesTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Sales>) change -> {
            updateTotalSales();
        });
        
        // Constrain the column resizing
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       
    }
    
    private void updateTotalSales() {
        // Calculate the sum of quantities of selected rows and update it
        int sum = 0;
        for (Sales selectedRow : salesTable.getSelectionModel().getSelectedItems()) {
            sum += selectedRow.getQuantity();
        }
        
        // Update the TextField to display the sum
        TotalSales.setText("Total Sales: " + sum);
    }
    
    private void showAlert(AlertType type, String title, String content) {
        // method to show alerts
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText("Adunoluwa's Dashboard");  // No header text
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void setupClearDataButton() {
    clearDataButton.setOnAction(event -> {
        sales.clear();
        
        // Clear the charts data
        BarChart.getData().clear();
        LineChart.getData().clear();
        PieChart.getData().clear();
        
        // Clear chart titles
        BarChart.setTitle(" ");
        LineChart.setTitle(" ");
        PieChart.setTitle(" ");
        
        // Clear axis labels (for Bar and Line charts)
        barChartXAxis.setLabel("");
        barChartYAxis.setLabel("");
        lineChartXAxis.setLabel("");
        lineChartYAxis.setLabel("");
        
        // Clear the table
        salesTable.getItems().clear();
        
        // Update the total sales field to "Total Sales: 0"
        TotalSales.setText("Total Sales: 0");
        
        updateCharts();
    });
    
}

    private void setupRefreshButton() {
        // Refresh button to restart the data fetching process
        refreshButton.setOnAction((javafx.event.ActionEvent event) -> {
            if (dashService.isRunning()) {
                dashService.cancel(); // Cancel existing request if running
            }
            dashService.reset();
            dashService.start();
        });
    }
    
    private void setupViewJsonButton() {
        viewJsonButton.setOnAction(event -> {
            // Get the selected year and vehicle from the ComboBoxes
            Integer selectedYear = yearComboBox.getValue();
            String selectedVehicle = vehicleComboBox.getValue();
            
            // Filter the sales data based on the selected year and vehicle
            List<Sales> filteredSales = sales.stream()
                .filter(o -> o.getYear().equals(selectedYear))
                .filter(o -> o.getVehicle().equals(selectedVehicle))
                .collect(Collectors.toList());
            
            // Convert the full sales data into JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String fullJsonOutput = gson.toJson(sales);
            
            // Convert the filtered sales data into JSON
            String filteredJsonOutput = gson.toJson(filteredSales);

            // Create a TextArea to display the full JSON data
            TextArea fullTextArea = new TextArea(fullJsonOutput);
            fullTextArea.setEditable(false);
            fullTextArea.setWrapText(true);
            fullTextArea.getStyleClass().add("json-text-area");
            
            // Create a TextArea to display the filtered JSON data
            TextArea filteredTextArea = new TextArea(filteredJsonOutput);
            filteredTextArea.setEditable(false);
            filteredTextArea.setWrapText(true);
            filteredTextArea.getStyleClass().add("json-text-area");

            // Create a dialog to display the raw JSON
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Sales Data - Full & Filtered");
            dialog.setHeaderText(null);
            
            // Add both full and filtered JSON content
            VBox content = new VBox();
        
            // Add Full Data Section
            Label fullDataLabel = new Label("Full Sales Data:");
            fullDataLabel.getStyleClass().add("json-section-label"); // Add CSS class for styling

            content.getChildren().addAll(fullDataLabel, fullTextArea);

            // Add Filtered Data Section
            Label filteredDataLabel = new Label("Filtered Sales Data (Year: " + selectedYear + ", Vehicle: " + selectedVehicle + "):");
            filteredDataLabel.getStyleClass().add("json-section-label"); // Add CSS class for styling

            content.getChildren().addAll(filteredDataLabel, filteredTextArea);
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        });
        
    }
    
    private void showLoginDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Login");
        dialog.setHeaderText("Enter password 'APPDS24' to access the dashboard");
        dialog.setContentText("Password:");
        
        // tried to add an image to the login dialog, but it didn't allow the dashboard to run
        //Load image from resources
        //Image image = new Image(getClass().getResourceAsStream("/resources/login-image.png"));
        //ImageView imageView = new ImageView(image);
        //imageView.setFitHeight(50); 
       // imageView.setFitWidth(50);  
        
        // Set the window icon
        //Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        //stage.getIcons().add(image);  // Add the image as the stage icon

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            if (!password.equals("APPDS24")) {
                showAlert(AlertType.ERROR, "Login Failed", "Incorrect password. The application will now close.");
                Stage stageToClose = (Stage) AnchorPane1.getScene().getWindow();
                stageToClose.close();
            }
        });
    }

    private void updateCharts() {
        // Update charts based on selected year and vehicle
        Integer selectedYear = yearComboBox.getValue();
        String selectedVehicle = vehicleComboBox.getValue();

        if (selectedYear == null || selectedVehicle == null) return;

        updateBarChart(selectedYear);
        updatePieChart(selectedYear, selectedVehicle);
        updateTable(selectedYear, selectedVehicle);
        updateLineChart(selectedYear, selectedVehicle);
    }

    private void updateBarChart(Integer year) {
    BarChart.getData().clear();

    // Group sales by vehicle and region for the bar chart
    sales.stream()
            .filter(o-> o.getYear().equals(year))
            .collect(Collectors.groupingBy(Sales::getVehicle, Collectors.groupingBy(Sales::getRegion, Collectors.summingInt(Sales::getQuantity))))
            .forEach((vehicle, regionData) -> {
                XYChart.Series<String, Number> s = new XYChart.Series<>();
                s.setName(vehicle);
        
                regionData.forEach((region, quantity) -> {
                    s.getData().add(new XYChart.Data<>(region, quantity));
                });

                BarChart.getData().add(s);
            });

    BarChart.setTitle("Vehicle Sales Comparison by Region in " + year);
}
    
    private void updateLineChart(Integer year, String vehicle) {
        LineChart.getData().clear();
        
        sales.stream()
            .filter(o -> o.getYear().equals(year))
            .filter(o -> o.getVehicle().equals(vehicle))
            .collect(Collectors.groupingBy(Sales::getRegion, Collectors.mapping(Sales::getQuantity, Collectors.toList())))
            .forEach((regionName, quantities) -> {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(regionName);

                // Add data points for each quarter
                if (quantities.size() > 0) series.getData().add(new XYChart.Data<>("Q1", quantities.size() > 0 ? quantities.get(0) : 0));
                if (quantities.size() > 1) series.getData().add(new XYChart.Data<>("Q2", quantities.size() > 1 ? quantities.get(1) : 0));
                if (quantities.size() > 2) series.getData().add(new XYChart.Data<>("Q3", quantities.size() > 2 ? quantities.get(2) : 0));
                if (quantities.size() > 3) series.getData().add(new XYChart.Data<>("Q4", quantities.size() > 3 ? quantities.get(3) : 0));

                LineChart.getData().add(series);
            });

        LineChart.setTitle(vehicle + " Sales in " + year);
    }

    private void updatePieChart(Integer year, String vehicle) {
        ObservableList<PieChart.Data> oL = FXCollections.observableArrayList();
        
        // Create pie chart data based on total quantity by region
        sales.stream()
                .filter(o -> o.getYear().equals(year))
                .filter(o -> o.getVehicle().equals(vehicle))
                .collect(Collectors.groupingBy(Sales::getRegion, Collectors.reducing(0, Sales::getQuantity, Integer::sum)))
                .entrySet().forEach(o -> {
                    oL.add(new PieChart.Data(o.getKey(), o.getValue()));
            });

        PieChart.setData(oL);
        PieChart.setTitle("Total " + vehicle + " Sales Distribution in " + year);
    }

    private void updateTable(Integer year, String vehicle) {
        // Filters and updates the sales data in the table
        ObservableList<Sales> filteredSales = FXCollections.observableArrayList(
                sales.stream()
                        .filter(o -> o.getYear().equals(year))
                        .filter(o -> o.getVehicle().equals(vehicle))
                        .collect(Collectors.toList())
        );
        salesTable.setItems(filteredSales);
    }

    private static class DashService extends Service<String> {
        private StringProperty address = new SimpleStringProperty();

        public final void setAddress(String address) {
            this.address.set(address); // Set service address for fetching data
        }

        public final String getAddress() {
            return address.get();
        }

        public final StringProperty addressProperty() {
           return address;
        }

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                private HttpURLConnection httpuc;
                private String markup;

                @Override
                protected String call() {
                    try {
                        httpuc = (HttpURLConnection)(new URL(getAddress())).openConnection();
                        httpuc.setRequestMethod("GET");
                        httpuc.setRequestProperty("Accept", "application/json");
                        httpuc.setRequestProperty("Content-Type", "application/json");                        

                        markup = (new BufferedReader(new InputStreamReader(httpuc.getInputStream()))).readLine();
                    }
                    catch (Exception e) {
                        // e.printStackTrace();
                        this.cancel(); // Cancel task on failure
                    }
                    finally {
                        if (httpuc != null) {
                            httpuc.disconnect(); // Close the connection
                        }
                    }

                    return markup; // Return the fetched JSON data
                }
            };
        }
    }
}
