package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.exceptions.ResourceNotFoundException;

public interface QuizService {

  List<QuizResponseDto> getAllQuizzes();

  QuestionResponseDto getRandomQuestion(Long quizId) throws ResourceNotFoundException;

  QuizResponseDto createQuiz(QuizRequestDto quizRequestDto);

  QuizResponseDto deleteQuizById(Long id);

  QuizResponseDto renameQuiz(Long id, String newName) throws ResourceNotFoundException;

  QuizResponseDto addQuestionToQuiz(Long quizId, QuestionRequestDto questionRequestDto);

  QuestionResponseDto deleteQuestionById(Long quizId, Long questionId);
}
