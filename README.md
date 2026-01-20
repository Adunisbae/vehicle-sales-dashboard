# Vehicle Sales Dashboard

A JavaFX desktop application that visualizes vehicle sales data through interactive charts and tables. Built as part of university coursework demonstrating proficiency in Java, REST API integration, and data visualization.

![Java](https://img.shields.io/badge/Java-8+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-8-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## Features

### Data Visualization
- **Bar Chart**: Compare vehicle sales across different regions for a selected year
- **Pie Chart**: View sales distribution by region for specific vehicle types
- **Line Chart**: Track quarterly sales trends across regions
- **Interactive Table**: Browse, filter, and analyze detailed sales records

### Core Functionality
- **REST API Integration**: Fetches real-time sales data from a remote server
- **Offline Support**: Automatic JSON backup/restore when network is unavailable
- **Dynamic Filtering**: Filter data by year and vehicle type using dropdown selectors
- **Multi-Row Selection**: Select multiple table rows to calculate total sales
- **Live Clock**: Real-time date and time display
- **JSON Viewer**: View raw and filtered JSON data for debugging/analysis

### Technical Highlights
- **MVC Architecture**: Clean separation using FXML for UI and Java controllers
- **Asynchronous Data Loading**: Non-blocking API calls with progress indicator
- **Stream API**: Efficient data processing using Java 8 Streams and Collectors
- **Gson Integration**: JSON serialization/deserialization for data handling
- **CSS Styling**: Custom stylesheet for professional appearance

## Screenshots

*Application screenshots can be added here*

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 8+ | Core programming language |
| JavaFX | Desktop UI framework |
| FXML | Declarative UI layout |
| Gson | JSON parsing library |
| CSS | UI styling |

## Project Structure

```
src/coursework/
├── Coursework.java          # Application entry point
├── CourseworkController.java # Main controller with business logic
├── Sales.java               # Data model for sales records
├── BackUp.java              # Backup/restore utility class
├── Coursework.fxml          # UI layout definition
└── coursework.css           # Custom styles
```

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- JavaFX SDK (included in JDK 8, separate download for JDK 11+)
- NetBeans IDE (recommended) or any Java IDE

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Adunisbae/vehicle-sales-dashboard.git
   cd vehicle-sales-dashboard
   ```

2. **Open in NetBeans**
   - File > Open Project
   - Navigate to the cloned directory
   - Select the project folder

3. **Run the application**
   - Right-click the project > Run
   - Or press F6

### Login Credentials
- Password: `APPDS24`

## Architecture

### Data Flow
```
REST API → DashService → JSON Parsing → Sales Objects → UI Components
                ↓
           BackUp.java (offline cache)
```

### Key Components

**DashService** (Inner Class)
- Extends JavaFX `Service` for async operations
- Handles HTTP GET requests to the sales API
- Manages success/failure callbacks

**BackUp Class**
- File-based JSON caching
- Automatic restore on network failure
- Clean backup management

## API Reference

The application connects to:
```
https://webteach.ljmu.ac.uk/DashService/SGetSales
```

### Response Format
```json
[
  {
    "Year": 2023,
    "QTR": "Q1",
    "Region": "Europe",
    "Vehicle": "SUV",
    "Quantity": 150
  }
]
```

## Skills Demonstrated

- **Object-Oriented Programming**: Encapsulation, inheritance, polymorphism
- **Design Patterns**: MVC, Service pattern for async operations
- **Java 8 Features**: Streams, Lambdas, Optional, Collectors
- **API Integration**: HTTP connections, JSON handling
- **UI Development**: JavaFX components, FXML, CSS styling
- **Error Handling**: Graceful degradation with offline support
- **Data Structures**: Lists, Sets, Maps for data organization

## Future Improvements

- [ ] Add data export functionality (CSV, Excel)
- [ ] Implement user authentication with database
- [ ] Add more chart types (Area, Scatter)
- [ ] Create unit tests for core functionality
- [ ] Add data caching with timestamp validation

## Author

**Adunoluwa Oguntuga**

- GitHub: [@Adunisbae](https://github.com/Adunisbae)
- LinkedIn: [Adun Oguntuga](https://linkedin.com/in/adun-oguntuga-779b00254)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Liverpool John Moores University for the coursework requirements
- JavaFX community for excellent documentation
- Google Gson library for JSON processing
