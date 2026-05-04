package com.exampro.app.data.repository;

import com.exampro.app.data.db.dao.ExamDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ExamRepository_Factory implements Factory<ExamRepository> {
  private final Provider<ExamDao> examDaoProvider;

  public ExamRepository_Factory(Provider<ExamDao> examDaoProvider) {
    this.examDaoProvider = examDaoProvider;
  }

  @Override
  public ExamRepository get() {
    return newInstance(examDaoProvider.get());
  }

  public static ExamRepository_Factory create(Provider<ExamDao> examDaoProvider) {
    return new ExamRepository_Factory(examDaoProvider);
  }

  public static ExamRepository newInstance(ExamDao examDao) {
    return new ExamRepository(examDao);
  }
}
