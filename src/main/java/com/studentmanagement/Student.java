package com.studentmanagement;

/** Um aluno matriculado. Os cursos dele ficam na tabela 'enrollments', nao aqui. */
public record Student(int id, String name, String registrationNumber) {

    public Student {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome do aluno e obrigatorio.");
        }
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new IllegalArgumentException("Matricula e obrigatoria.");
        }
    }

    /** Usado antes da insercao: o id ainda sera gerado pelo banco. */
    public Student(String name, String registrationNumber) {
        this(0, name, registrationNumber);
    }
}