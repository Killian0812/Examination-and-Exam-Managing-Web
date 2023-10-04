package com.killian.SpringBoot.ExamApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.killian.SpringBoot.ExamApp.models.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByText(String text);

    @Query("SELECT DISTINCT q.subject FROM Question q")
    List<String> findDistinctSubjects();

    @Query("SELECT DISTINCT q.difficulty FROM Question q")
    List<String> findDistinctDifficuties();

    @Query("SELECT q FROM Question q WHERE q.subject = :subject")
    List<Question> findBySubject(@Param("subject") String subject);

    @Query("SELECT q FROM Question q WHERE q.subject = :subject AND q.difficulty = :difficulty")
    List<Question> findBySubjectAndDifficulty(@Param("subject") String subject,
            @Param("difficulty") String difficulty);

    @Query("SELECT q FROM Question q WHERE q.difficulty = 'Easy' ORDER BY RAND() LIMIT :numberOfQuestions")
    List<Question> findRandomEasyQuestions(int numberOfQuestions);

    @Query("SELECT q FROM Question q WHERE q.difficulty = 'Medium' ORDER BY RAND() LIMIT :numberOfQuestions")
    List<Question> findRandomMediumQuestions(int numberOfQuestions);

    @Query("SELECT q FROM Question q WHERE q.difficulty = 'Hard' ORDER BY RAND() LIMIT :numberOfQuestions")
    List<Question> findRandomHardQuestions(int numberOfQuestions);
}
