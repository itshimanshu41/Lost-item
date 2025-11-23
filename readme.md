# Lost & Found Portal (Java Console Application)

A simple, real-world Lost & Found management system built using **Core Java**, designed to help users report items they have **lost** or **found**, search for potential matches, and mark items as resolved.  
The application stores data in a lightweight CSV file without requiring any database setup.

---

## 🧭 Project Overview

Every day, students misplace items like wallets, phones, bags, ID cards, etc.  
This portal provides a simple way to:

- Report lost items  
- Report found items  
- Match found items to lost ones  
- Contact reporters  
- Maintain persistent records  

This project demonstrates:
- File handling (CSV)  
- OOP concepts  
- Modular design  
- Simple rule-based matching logic  
- Menu-driven console UI  

---

## 📁 Folder Structure
Lost-item/
├── src/
│   ├── Item.java
│   ├── Repository.java
│   ├── LostAndFoundService.java
│   └── Main.java
│
├── data/
│   └── items.csv
│
├── out/
│   └── (compiled .class files)
│
├── README.md
└── .gitignore

---

## 🛠️ Features

### ✔ Report Lost Item  
Enter title, description, location, date, your name & contact.

### ✔ Report Found Item  
Same details, but marked as "found".

### ✔ List All Items  
Shows every record stored in CSV.

### ✔ View Item by ID  
Quickly open any item using its UUID.

### ✔ Smart Match Finder  
Matches using:
- Type similarity  
- Description word overlap  
- Location token match  
- Date proximity (recent items get priority)

### ✔ Mark Item as Resolved  
Once the owner collects the item.

---

## 🧪 How to Compile & Run

### **Compile Java files**

javac -d out src/*.java

### **Run the application**
java -cp out Main

You will see a menu like:

	1.	Report LOST item
	2.	Report FOUND item
	3.	List all items
	4.	View item by ID
	5.	Find potential matches
	6.	Mark item as resolved
	7.	Exit

Example entry:
d3f0923f-b8be-4d91-9e28-5e8a26a1e2c9,Black Wallet,Wallet,leather wallet,Cafeteria,2025-11-24,Himanshu,9876543210,false,false

CSV automatically updates when you add/edit items through the program.

---

## 🧩 Project Design

### **Classes:**
- `Item` — Data model
- `Repository` — CSV read/write
- `LostAndFoundService` — Main business logic & matching
- `Main` — Console UI

### **Concepts Used**
- OOP (classes, objects)
- File I/O
- Tokenization & string matching
- Date handling
- Modular design
- Exception handling
- Collections (List)

---

## 🚀 Future Improvements
- JavaFX GUI  
- MySQL/PostgreSQL integration  
- Login system  
- Notification system  
- QR-code tagging for lost items  

---

## 📌 Author
Developed as part of a flipped-course project for academic submission.  
