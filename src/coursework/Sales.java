package coursework;

public class Sales {
    
    private Integer Year;
    private String QTR;
    private String Region;
    private String Vehicle;
    private Integer Quantity;
    
    public Sales(Integer year, String QTR, String region, String vehicle, Integer quantity) {
        this.Year = year;
        this.QTR = QTR;
        this.Region = region;
        this.Vehicle = vehicle;
        this.Quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("Year: %d, QTR: %d, Region: %s, Vehicle: %s, Quantity: %d",
                             Year, QTR, Region, Vehicle, Quantity);
    }

    // Getter methods
    public Integer getYear() {
        return Year;
    }

    public String getQTR() {
        return QTR;
    }

    public String getRegion() {
        return Region;
    }

    public String getVehicle() {
        return Vehicle;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public Integer setQuantity(Integer newValue) {
        return Quantity;
    }
}