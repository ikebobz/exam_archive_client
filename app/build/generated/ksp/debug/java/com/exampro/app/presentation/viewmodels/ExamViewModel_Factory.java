package com.exampro.app.presentation.viewmodels;

import androidx.lifecycle.SavedStateHandle;
import com.exampro.app.data.repository.ExamRepository;
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
public final class ExamViewModel_Factory implements Factory<ExamViewModel> {
  private final Provider<ExamRepository> examRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ExamViewModel_Factory(Provider<ExamRepository> examRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.examRepositoryProvider = examRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ExamViewModel get() {
    return newInstance(examRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static ExamViewModel_Factory create(Provider<ExamRepository> examRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ExamViewModel_Factory(examRepositoryProvider, savedStateHandleProvider);
  }

  public static ExamViewModel newInstance(ExamRepository examRepository,
      SavedStateHandle savedStateHandle) {
    return new ExamViewModel(examRepository, savedStateHandle);
  }
}
