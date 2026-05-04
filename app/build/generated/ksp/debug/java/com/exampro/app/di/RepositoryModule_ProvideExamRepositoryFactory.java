package com.exampro.app.di;

import com.exampro.app.data.db.dao.ExamDao;
import com.exampro.app.data.repository.ExamRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class RepositoryModule_ProvideExamRepositoryFactory implements Factory<ExamRepository> {
  private final Provider<ExamDao> examDaoProvider;

  public RepositoryModule_ProvideExamRepositoryFactory(Provider<ExamDao> examDaoProvider) {
    this.examDaoProvider = examDaoProvider;
  }

  @Override
  public ExamRepository get() {
    return provideExamRepository(examDaoProvider.get());
  }

  public static RepositoryModule_ProvideExamRepositoryFactory create(
      Provider<ExamDao> examDaoProvider) {
    return new RepositoryModule_ProvideExamRepositoryFactory(examDaoProvider);
  }

  public static ExamRepository provideExamRepository(ExamDao examDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideExamRepository(examDao));
  }
}
