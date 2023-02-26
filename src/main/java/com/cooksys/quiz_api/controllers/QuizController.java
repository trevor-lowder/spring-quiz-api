package com.cooksys.quiz_api.controllers;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.exceptions.ResourceNotFoundException;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final QuizService quizService;

  @GetMapping
  public ResponseEntity<List<QuizResponseDto>> getAllQuizzes() {
    return ResponseEntity.ok(quizService.getAllQuizzes());
  }

  @GetMapping("/{id}/random")
  public ResponseEntity<QuestionResponseDto> getRandomQuestion(@PathVariable Long id) throws ResourceNotFoundException {
    return ResponseEntity.ok(quizService.getRandomQuestion(id));
  }

  @PostMapping
  public ResponseEntity<QuizResponseDto> createQuiz(@RequestBody QuizRequestDto quizRequestDto) {
    return ResponseEntity.ok(quizService.createQuiz(quizRequestDto));
  }

  @PatchMapping("/{id}/rename/{newName}")
  public ResponseEntity<QuizResponseDto> renameQuiz(@PathVariable Long id, @PathVariable String newName) {
    return ResponseEntity.ok(quizService.renameQuiz(id, newName));
  }

  @PatchMapping("/{id}/add")
  public ResponseEntity<QuizResponseDto> addQuestionToQuiz(@PathVariable Long id,
      @RequestBody QuestionRequestDto questionRequestDto) throws ResourceNotFoundException {
    QuizResponseDto quizResponseDto = quizService.addQuestionToQuiz(id, questionRequestDto);
    return ResponseEntity.ok(quizResponseDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<QuizResponseDto> deleteQuizById(@PathVariable Long id) {
    QuizResponseDto quiz = quizService.deleteQuizById(id);
    return ResponseEntity.ok(quiz);
  }

  @DeleteMapping("/{quizId}/delete/{questionId}")
  public ResponseEntity<QuestionResponseDto> deleteQuestionFromQuiz(@PathVariable Long quizId,
      @PathVariable Long questionId) throws ResourceNotFoundException {
    QuestionResponseDto deletedQuestion = quizService.deleteQuestionById(quizId, questionId);
    return ResponseEntity.ok(deletedQuestion);
  }

}
