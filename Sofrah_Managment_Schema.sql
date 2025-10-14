
-- -----------------------------------------------------
-- Core & Independent Tables
-- -----------------------------------------------------
CREATE TABLE Location (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    City VARCHAR(100) NOT NULL,
    Street VARCHAR(255) NOT NULL
);

CREATE TABLE JobTitle (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100) NOT NULL,
    Description TEXT,
    PayRate DECIMAL(10, 2) NOT NULL
);

CREATE TABLE OrderStatus (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE OrderType (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE MenuItem (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    itemName VARCHAR(150) NOT NULL,
    description TEXT,
    isAvailable BOOLEAN DEFAULT TRUE,
    Price DECIMAL(10, 2) NOT NULL -- Added price as it's needed for queries
);

CREATE TABLE Customer (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(150) NOT NULL,
    PhoneNumber VARCHAR(20) UNIQUE
);

CREATE TABLE Supplier (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(150) NOT NULL,
    phoneNum VARCHAR(20)
);

CREATE TABLE Employee (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    jobTitleID INT,
    DateOfBirth DATE NOT NULL,
    Name VARCHAR(150) NOT NULL,
    FOREIGN KEY (jobTitleID) REFERENCES JobTitle(ID)
);

CREATE TABLE InventoryItem (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(150) NOT NULL,
    Stock INT NOT NULL DEFAULT 0
);

CREATE TABLE InventoryOrder (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierID INT, -- Added to know which supplier the order is for
    OrderDate DATETIME NOT NULL,
    FOREIGN KEY (SupplierID) REFERENCES Supplier(ID)
);

-- -----------------------------------------------------
-- Junction (Many-to-Many) Tables
-- -----------------------------------------------------
CREATE TABLE CustomerLocation (
    CustomerID INT ,
    LocationID INT,
    PRIMARY KEY (CustomerID, LocationID),
    FOREIGN KEY (CustomerID) REFERENCES Customer(ID) ON DELETE CASCADE,
    FOREIGN KEY (LocationID) REFERENCES Location(ID) ON DELETE CASCADE
);

-- NEW table for the many-to-many relationship
CREATE TABLE SupplierInventoryItem (
    SupplierID INT,
    InventoryItemID INT,
    PRIMARY KEY (SupplierID, InventoryItemID),
    FOREIGN KEY (SupplierID) REFERENCES Supplier(ID) ON DELETE CASCADE,
    FOREIGN KEY (InventoryItemID) REFERENCES InventoryItem(ID) ON DELETE CASCADE
);

CREATE TABLE MenuIngredient (
    MenuItemID INT,
    InventoryItemID INT,
    quantity DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (MenuItemID, InventoryItemID),
    FOREIGN KEY (MenuItemID) REFERENCES MenuItem(ID),
    FOREIGN KEY (InventoryItemID) REFERENCES InventoryItem(ID)
);

CREATE TABLE InventoryOrderItem (
    InventoryOrderID INT,
    InventoryItemID INT,
    Price DECIMAL(10, 2) NOT NULL,
    Stock INT NOT NULL, -- This is the quantity being ordered
    PRIMARY KEY (InventoryOrderID, InventoryItemID),
    FOREIGN KEY (InventoryOrderID) REFERENCES InventoryOrder(ID),
    FOREIGN KEY (InventoryItemID) REFERENCES InventoryItem(ID)
);

-- -----------------------------------------------------
-- Other Dependent Tables
-- -----------------------------------------------------
CREATE TABLE Shifts (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    EmployeeID INT,
    expectedCheckIn DATETIME NOT NULL, -- Corrected typo
    expectedCheckOut DATETIME NOT NULL, -- Corrected typo
    CheckIn DATETIME,
    CheckOut DATETIME,
    hasWorked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (EmployeeID) REFERENCES Employee(ID)
);

CREATE TABLE Orders (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    customerID INT,
    EmployeeID INT,
    orderStatusID INT,
    orderTypeID INT,
    Date DATETIME NOT NULL,
    FOREIGN KEY (customerID) REFERENCES Customer(ID),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(ID),
    FOREIGN KEY (orderStatusID) REFERENCES OrderStatus(ID),
    FOREIGN KEY (orderTypeID) REFERENCES OrderType(ID)
);

CREATE TABLE OrderMenuItem (
    orderID INT,
    menuItemID INT,
    quantity INT NOT NULL,
    PRIMARY KEY (orderID, menuItemID),
    FOREIGN KEY (menuItemID) REFERENCES MenuItem(ID),
    FOREIGN KEY (orderID) REFERENCES Orders(ID)
);

CREATE TABLE Payment (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    orderID INT UNIQUE, -- Fixed: UNIQUE constraint enforces one-to-one
    quantity DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES Orders(ID)
);