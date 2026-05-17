package com.exampro.app.presentation.viewmodels;

import com.exampro.app.data.db.dao.ExamDao;
import com.exampro.app.data.db.dao.SubjectDao;
import com.exampro.app.data.repository.AuthRepository;
import com.exampro.app.data.repository.QuestionRepository;
import com.exampro.app.data.repository.SyncRepository;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<ExamDao> examDaoProvider;

  private final Provider<SubjectDao> subjectDaoProvider;

  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  public DashboardViewModel_Factory(Provider<ExamDao> examDaoProvider,
      Provider<SubjectDao> subjectDaoProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    this.examDaoProvider = examDaoProvider;
    this.subjectDaoProvider = subjectDaoProvider;
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(examDaoProvider.get(), subjectDaoProvider.get(), questionRepositoryProvider.get(), authRepositoryProvider.get(), syncRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<ExamDao> examDaoProvider,
      Provider<SubjectDao> subjectDaoProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    return new DashboardViewModel_Factory(examDaoProvider, subjectDaoProvider, questionRepositoryProvider, authRepositoryProvider, syncRepositoryProvider);
  }

  public static DashboardViewModel newInstance(ExamDao examDao, SubjectDao subjectDao,
      QuestionRepository questionRepository, AuthRepository authRepository,
      SyncRepository syncRepository) {
    return new DashboardViewModel(examDao, subjectDao, questionRepository, authRepository, syncRepository);
  }
}
