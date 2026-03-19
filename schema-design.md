# Schema Design – Smart Clinic System

## Overview
This system uses a hybrid database approach:
- MySQL for structured relational data (patients, doctors, appointments, admin)
- MongoDB for flexible, document-based data (prescriptions)

---

## MySQL Database Design

### 1. Patients Table
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- first_name (VARCHAR(50), NOT NULL)
- last_name (VARCHAR(50), NOT NULL)
- email (VARCHAR(100), UNIQUE, NOT NULL)
- phone (VARCHAR(15), NOT NULL)
- date_of_birth (DATE, NOT NULL)

### 2. Doctors Table
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- name (VARCHAR(100), NOT NULL)
- specialization (VARCHAR(100), NOT NULL)
- email (VARCHAR(100), UNIQUE, NOT NULL)
- phone (VARCHAR(15))

### 3. Appointments Table
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- patient_id (INT, NOT NULL, FOREIGN KEY REFERENCES Patients(id))
- doctor_id (INT, NOT NULL, FOREIGN KEY REFERENCES Doctors(id))
- appointment_date (DATETIME, NOT NULL)
- status (VARCHAR(50), NOT NULL)

### 4. Admin Table
- id (INT, PRIMARY KEY, AUTO_INCREMENT)
- username (VARCHAR(50), UNIQUE, NOT NULL)
- password (VARCHAR(255), NOT NULL)

---

## MongoDB Collection Design

### Collection: Prescriptions

```json
{
  "patientId": 1,
  "doctorId": 2,
  "date": "2026-03-19",
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Twice a day",
      "duration": "5 days"
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "Three times a day",
      "duration": "3 days"
    }
  ],
  "notes": "Take medication after meals",
  "followUpDate": "2026-03-26"
}