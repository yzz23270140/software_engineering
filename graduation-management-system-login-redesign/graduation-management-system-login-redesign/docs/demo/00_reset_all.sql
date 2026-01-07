USE graduation_management_system;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE Notice_recipient;
TRUNCATE TABLE Notice;
TRUNCATE TABLE Objection;
TRUNCATE TABLE Defense_record;
TRUNCATE TABLE Second_instance_opinion;
TRUNCATE TABLE First_instance_opinion;
TRUNCATE TABLE Paper;
TRUNCATE TABLE Guidance_record;
TRUNCATE TABLE Mid_term_report;
TRUNCATE TABLE Thesis_proposal;
TRUNCATE TABLE Topic_selection_application;
TRUNCATE TABLE Topic;
TRUNCATE TABLE Student;
TRUNCATE TABLE Teacher;
TRUNCATE TABLE Judge;
TRUNCATE TABLE Instructor;
TRUNCATE TABLE Office;

SET FOREIGN_KEY_CHECKS = 1;
