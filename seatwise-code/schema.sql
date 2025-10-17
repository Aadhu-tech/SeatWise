-- SmartExamSeat schema
CREATE DATABASE IF NOT EXISTS mydatabase;
USE mydatabase;

-- Students
CREATE TABLE IF NOT EXISTS Student (
  student_id VARCHAR(50) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  branch VARCHAR(50) NOT NULL
);

-- Rooms
CREATE TABLE IF NOT EXISTS Room (
  room_id VARCHAR(50) PRIMARY KEY,
  capacity INT NOT NULL CHECK (capacity >= 0),
  is_backup BOOLEAN NOT NULL DEFAULT FALSE
);

-- Exam slots
CREATE TABLE IF NOT EXISTS ExamSlot (
  exam_slot_id VARCHAR(50) PRIMARY KEY,
  subject VARCHAR(100) NOT NULL,
  date VARCHAR(20) NOT NULL
);

-- Allocations
CREATE TABLE IF NOT EXISTS Allocation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id VARCHAR(50) NOT NULL,
  room_id VARCHAR(50) NOT NULL,
  seat_no INT NOT NULL,
  exam_slot_id VARCHAR(50) NOT NULL,
  CONSTRAINT fk_alloc_student FOREIGN KEY (student_id) REFERENCES Student(student_id) ON DELETE CASCADE,
  CONSTRAINT fk_alloc_room FOREIGN KEY (room_id) REFERENCES Room(room_id) ON DELETE CASCADE,
  CONSTRAINT fk_alloc_exam FOREIGN KEY (exam_slot_id) REFERENCES ExamSlot(exam_slot_id) ON DELETE CASCADE,
  CONSTRAINT uq_alloc UNIQUE (student_id, exam_slot_id),
  CONSTRAINT uq_room_seat UNIQUE (room_id, exam_slot_id, seat_no)
);

-- Admin warnings
CREATE TABLE IF NOT EXISTS AdminWarning (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  warning_text VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed sample data (optional)
INSERT INTO Room (room_id, capacity, is_backup) VALUES
  ('R101', 30, FALSE),
  ('R102', 30, FALSE),
  ('R201', 20, TRUE)
ON DUPLICATE KEY UPDATE capacity=VALUES(capacity), is_backup=VALUES(is_backup);

INSERT INTO ExamSlot (exam_slot_id, subject, date) VALUES
  ('EXAM1', 'Mathematics', '2025-10-15')
ON DUPLICATE KEY UPDATE subject=VALUES(subject), date=VALUES(date);
