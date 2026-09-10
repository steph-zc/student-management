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


public class CourseDAO {

    // Insere um curso e retorna a chave primária do mesmo (no caso do banco não retornar a chave, o método retorna -1).
    public int insert(Course course) throws SQLException {
        String sql = "INSERT INTO courses (name, duration_semesters) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, course.name());
            statement.setInt(2, course.durationSemesters());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    //Retorna todos os cursos em uma lista.
    public List<Course> findAll() throws SQLException {
        String sql = "SELECT id, name, duration_semesters FROM courses ORDER BY id";
        List<Course> courses = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                courses.add(new Course(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getInt("duration_semesters")));
            }
        }
        return courses;
    }

    //Busca um curso pelo id dele (o optional serve pra poder retornar empty em caso do curso não existir).
    public Optional<Course> findById(int id) throws SQLException {
        String sql = "SELECT id, name, duration_semesters FROM courses WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Course(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getInt("duration_semesters")));
            }
        }
    }

    //Retorna um mapa com o nome do curso e quantidade de alunos matriculados nele (essa query agrupa os cursos e conta a quantidade de alunos em cada um).
    public Map<String, Integer> countStudentsPerCourse() throws SQLException {
        String sql = """
                SELECT c.name, COUNT(e.student_id) AS total
                  FROM courses c
                  LEFT JOIN enrollments e ON e.course_id = c.id
                 GROUP BY c.name
                 ORDER BY total DESC, c.name
                """;

        Map<String, Integer> report = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                report.put(result.getString("name"), result.getInt("total"));
            }
        }
        return report;
    }
}
