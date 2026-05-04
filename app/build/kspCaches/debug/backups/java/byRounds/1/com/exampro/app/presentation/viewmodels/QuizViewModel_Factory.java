package com.exampro.app.presentation.viewmodels;

import com.exampro.app.data.repository.QuestionRepository;
import com.exampro.app.data.repository.StudyProgressRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class QuizViewModel_Factory implements Factory<QuizViewModel> {
  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<StudyProgressRepository> studyProgressRepositoryProvider;

  public QuizViewModel_Factory(Provider<QuestionRepository> questionRepositoryProvider,
      Provider<StudyProgressRepository> studyProgressRepositoryProvider) {
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.studyProgressRepositoryProvider = studyProgressRepositoryProvider;
  }

  @Override
  public QuizViewModel get() {
    return newInstance(questionRepositoryProvider.get(), studyProgressRepositoryProvider.get());
  }

  public static QuizViewModel_Factory create(
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<StudyProgressRepository> studyProgressRepositoryProvider) {
    return new QuizViewModel_Factory(questionRepositoryProvider, studyProgressRepositoryProvider);
  }

  public static QuizViewModel newInstance(QuestionRepository questionRepository,
      StudyProgressRepository studyProgressRepository) {
    return new QuizViewModel(questionRepository, studyProgressRepository);
  }
}
