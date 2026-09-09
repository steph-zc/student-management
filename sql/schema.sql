DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;

CREATE TABLE courses (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	duration_semesters INTEGER NOT NULL CHECK (duration_semesters > 0)
);

CREATE TABLE students (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	registration_number VARCHAR(20) NOT NULL UNIQUE
);	

--Relação N:N
CREATE TABLE enrollments (
	student_id INTEGER NOT NULL 
        REFERENCES students(id) ON DELETE CASCADE,
	course_id INTEGER NOT NULL 
        REFERENCES courses(id) ON DELETE RESTRICT,

	PRIMARY KEY(student_id, course_id)
);

CREATE INDEX idx_enrollments_course ON enrollments(course_id);
