package com.studentmanagement;

import java.util.List;

/**
 * Representa um aluno acompanhado da lista de nomes dos cursos em que esta matriculado.
 */
public record StudentDetails(int id, String name, String registrationNumber, List<String> courses) {
}