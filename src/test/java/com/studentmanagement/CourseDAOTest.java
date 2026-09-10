package com.studentmanagement;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class CourseDAOTest {

    @Test
    void insertReturnsGeneratedIdWhenInsertSucceeds() throws Exception {
        CourseDAO dao = new CourseDAO();
        Course course = new Course("Matematica", 8);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);

        when(connection.prepareStatement("INSERT INTO courses (name, duration_semesters) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);
        when(statement.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getInt(1)).thenReturn(10);

        try (MockedStatic<DatabaseConnection> databaseConnection = mockStatic(DatabaseConnection.class)) {
            databaseConnection.when(DatabaseConnection::getConnection).thenReturn(connection);

            int id = dao.insert(course);

            assertEquals(10, id);
        }
    }

    @Test
    void insertThrowsDuplicateCourseNameExceptionForDuplicateName() throws Exception {
        CourseDAO dao = new CourseDAO();
        Course course = new Course("Fisica", 8);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        SQLException duplicateSql = new SQLException("duplicate key value violates unique constraint \"uq_courses_name\"",
                "23505");

        when(connection.prepareStatement("INSERT INTO courses (name, duration_semesters) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(duplicateSql);

        try (MockedStatic<DatabaseConnection> databaseConnection = mockStatic(DatabaseConnection.class)) {
            databaseConnection.when(DatabaseConnection::getConnection).thenReturn(connection);

            DuplicateCourseNameException exception = assertThrows(DuplicateCourseNameException.class,
                    () -> dao.insert(course));
            assertEquals(duplicateSql, exception.getCause());
        }
    }
}
