package com.studentmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Acesso a tabela 'students' e a associativa 'enrollments'.
 */
public class StudentDAO {

    /** Insere um aluno e retorna o ID gerado pelo banco. */
    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, registration_number) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, student.name());
            statement.setString(2, student.registrationNumber());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    /** Vincula um aluno a um curso na tabela associativa enrollments. */
    public void enroll(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        }
    }

    /**
     * Busca um aluno por sua matricula acompanhado de seus cursos.
     */
    public Optional<StudentDetails> findByRegistrationNumber(String regNum) throws SQLException {
        String sql = """
                SELECT s.id, s.name, s.registration_number, c.name AS course_name
                FROM students s
                LEFT JOIN enrollments e ON s.id = e.student_id
                LEFT JOIN courses c ON e.course_id = c.id
                WHERE s.registration_number = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, regNum);
            try (ResultSet result = statement.executeQuery()) {
                int id = 0;
                String name = null;
                String registrationNumber = null;
                List<String> courses = new ArrayList<>();

                while (result.next()) {
                    if (name == null) {
                        id = result.getInt("id");
                        name = result.getString("name");
                        registrationNumber = result.getString("registration_number");
                    }
                    String courseName = result.getString("course_name");
                    if (courseName != null) {
                        courses.add(courseName);
                    }
                }

                if (name == null) {
                    return Optional.empty();
                }

                return Optional.of(new StudentDetails(id, name, registrationNumber, courses));
            }
        }
    }

    /**
     * Lista todos os alunos agrupando seus cursos via duplo JOIN.
     */
    public List<StudentDetails> findAllWithCourses() throws SQLException {
        String sql = """
                SELECT s.id, s.name, s.registration_number, c.name AS course_name
                FROM students s
                LEFT JOIN enrollments e ON s.id = e.student_id
                LEFT JOIN courses c ON e.course_id = c.id
                ORDER BY s.id
                """;

        Map<Integer, StudentDetails> map = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                String reg = result.getString("registration_number");
                String courseName = result.getString("course_name");

                StudentDetails details = map.computeIfAbsent(id,
                        k -> new StudentDetails(k, name, reg, new ArrayList<>()));

                if (courseName != null) {
                    details.courses().add(courseName);
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * Lista todos os alunos pertencentes a um curso especifico.
     */
    public List<Student> findByCourseId(int courseId) throws SQLException {
        String sql = """
                SELECT s.id, s.name, s.registration_number
                FROM students s
                JOIN enrollments e ON s.id = e.student_id
                WHERE e.course_id = ?
                ORDER BY s.name
                """;

        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    students.add(new Student(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getString("registration_number")));
                }
            }
        }
        return students;
    }
}
