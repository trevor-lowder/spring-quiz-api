package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.exceptions.ResourceNotFoundException;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final QuizMapper quizMapper;
  private final QuestionMapper questionMapper;

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAll());
  }

  @Override
  public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {
    Quiz quiz = quizMapper.DtoToEntity(quizRequestDto);
    quizRepository.save(quiz);

    List<Question> questions = quiz.getQuestions();

    for (Question question : questions) {
      question.setQuiz(quiz);
      questionRepository.save(question);
      for (Answer answer : question.getAnswers()) {
        answer.setQuestion(question);
        answerRepository.save(answer);
      }
    }

    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuizResponseDto deleteQuizById(Long id) throws ResourceNotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    if (optionalQuiz.isPresent()) {
      Quiz quizToDelete = optionalQuiz.get();
      quizRepository.deleteById(id);
      return quizMapper.entityToDto(quizToDelete);
    } else {
      throw new ResourceNotFoundException("Quiz not found with id: " + id);
    }
  }

  @Override
  public QuizResponseDto renameQuiz(Long id, String newName) throws ResourceNotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);

    if (optionalQuiz.isEmpty()) {
      throw new ResourceNotFoundException(String.format("Quiz with id %d not found", id));
    }

    Quiz quiz = optionalQuiz.get();
    quiz.setName(newName);
    quizRepository.save(quiz);

    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuestionResponseDto getRandomQuestion(Long quizId) {
    Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);
    if (!optionalQuiz.isPresent()) {
      throw new ResourceNotFoundException(String.format("Quiz with id %d not found", quizId));
    }
    Quiz quiz = optionalQuiz.get();
    List<Question> questions = quiz.getQuestions();
    if (questions.isEmpty()) {
      throw new ResourceNotFoundException(String.format("No questions found for quiz with id %d", quizId));
    }
    Random random = new Random();
    Question randomQuestion = questions.get(random.nextInt(questions.size()));
    return questionMapper.entityToDto(randomQuestion);
  }

  @Override
  public QuizResponseDto addQuestionToQuiz(Long quizId, QuestionRequestDto questionRequestDto)
      throws ResourceNotFoundException {
    Quiz quiz = quizRepository.findById(quizId)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("Quiz with ID %d not found", quizId)));

    Question question = questionMapper.dtoToEntity(questionRequestDto);
    question.setQuiz(quiz);

    quiz.getQuestions().add(question);

    questionRepository.save(question);
    quizRepository.save(quiz);

    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuestionResponseDto deleteQuestionById(Long quizId, Long questionId) throws ResourceNotFoundException {
    Quiz quiz = quizRepository.findById(quizId)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("Quiz not found with id %d", quizId)));

    Optional<Question> questionToDelete = quiz.getQuestions().stream()
        .filter(question -> question.getId().equals(questionId))
        .findFirst();

    if (questionToDelete.isEmpty()) {
      throw new ResourceNotFoundException(
          String.format("Question not found with id %d in quiz with id %d", questionId, quizId));
    }

    Question deletedQuestion = questionToDelete.get();
    quiz.removeQuestion(deletedQuestion);
    quizRepository.save(quiz);

    return questionMapper.entityToDto(deletedQuestion);
  }

}
