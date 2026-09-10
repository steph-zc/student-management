package com.studentmanagement;

/** Um curso da instituicao. Espelha a tabela 'courses'. */
public record Course(int id, String name, int durationSemesters) {

    public Course {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome do curso e obrigatorio.");
        }
        if (durationSemesters <= 0) {
            throw new IllegalArgumentException("Duracao deve ser um numero positivo de semestres.");
        }
    }

    /** Usado antes da insercao: o id ainda sera gerado pelo banco. */
    public Course(String name, int durationSemesters) {
        this(0, name, durationSemesters);
    }
}