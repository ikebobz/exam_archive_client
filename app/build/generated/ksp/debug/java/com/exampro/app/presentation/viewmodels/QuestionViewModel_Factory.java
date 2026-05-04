package com.exampro.app.presentation.viewmodels;

import androidx.lifecycle.SavedStateHandle;
import com.exampro.app.data.repository.ExamRepository;
import com.exampro.app.data.repository.QuestionRepository;
import com.exampro.app.data.repository.SubjectRepository;
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
public final class QuestionViewModel_Factory implements Factory<QuestionViewModel> {
  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<SubjectRepository> subjectRepositoryProvider;

  private final Provider<ExamRepository> examRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public QuestionViewModel_Factory(Provider<QuestionRepository> questionRepositoryProvider,
      Provider<SubjectRepository> subjectRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.subjectRepositoryProvider = subjectRepositoryProvider;
    this.examRepositoryProvider = examRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public QuestionViewModel get() {
    return newInstance(questionRepositoryProvider.get(), subjectRepositoryProvider.get(), examRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static QuestionViewModel_Factory create(
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<SubjectRepository> subjectRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new QuestionViewModel_Factory(questionRepositoryProvider, subjectRepositoryProvider, examRepositoryProvider, savedStateHandleProvider);
  }

  public static QuestionViewModel newInstance(QuestionRepository questionRepository,
      SubjectRepository subjectRepository, ExamRepository examRepository,
      SavedStateHandle savedStateHandle) {
    return new QuestionViewModel(questionRepository, subjectRepository, examRepository, savedStateHandle);
  }
}
