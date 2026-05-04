package com.exampro.app.presentation.viewmodels;

import androidx.lifecycle.SavedStateHandle;
import com.exampro.app.data.repository.ExamRepository;
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
public final class SubjectViewModel_Factory implements Factory<SubjectViewModel> {
  private final Provider<SubjectRepository> subjectRepositoryProvider;

  private final Provider<ExamRepository> examRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public SubjectViewModel_Factory(Provider<SubjectRepository> subjectRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.subjectRepositoryProvider = subjectRepositoryProvider;
    this.examRepositoryProvider = examRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public SubjectViewModel get() {
    return newInstance(subjectRepositoryProvider.get(), examRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static SubjectViewModel_Factory create(
      Provider<SubjectRepository> subjectRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new SubjectViewModel_Factory(subjectRepositoryProvider, examRepositoryProvider, savedStateHandleProvider);
  }

  public static SubjectViewModel newInstance(SubjectRepository subjectRepository,
      ExamRepository examRepository, SavedStateHandle savedStateHandle) {
    return new SubjectViewModel(subjectRepository, examRepository, savedStateHandle);
  }
}
