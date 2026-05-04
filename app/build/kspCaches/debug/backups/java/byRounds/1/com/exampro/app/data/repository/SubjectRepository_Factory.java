package com.exampro.app.data.repository;

import com.exampro.app.data.db.dao.SubjectDao;
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
public final class SubjectRepository_Factory implements Factory<SubjectRepository> {
  private final Provider<SubjectDao> subjectDaoProvider;

  public SubjectRepository_Factory(Provider<SubjectDao> subjectDaoProvider) {
    this.subjectDaoProvider = subjectDaoProvider;
  }

  @Override
  public SubjectRepository get() {
    return newInstance(subjectDaoProvider.get());
  }

  public static SubjectRepository_Factory create(Provider<SubjectDao> subjectDaoProvider) {
    return new SubjectRepository_Factory(subjectDaoProvider);
  }

  public static SubjectRepository newInstance(SubjectDao subjectDao) {
    return new SubjectRepository(subjectDao);
  }
}
