package com.exampro.app.data.repository;

import com.exampro.app.data.api.ExamApi;
import com.exampro.app.data.api.QuestionApi;
import com.exampro.app.data.api.SubjectApi;
import com.exampro.app.data.db.dao.BookmarkDao;
import com.exampro.app.data.db.dao.ExamDao;
import com.exampro.app.data.db.dao.QuestionDao;
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
public final class SyncRepository_Factory implements Factory<SyncRepository> {
  private final Provider<ExamApi> examApiProvider;

  private final Provider<SubjectApi> subjectApiProvider;

  private final Provider<QuestionApi> questionApiProvider;

  private final Provider<ExamDao> examDaoProvider;

  private final Provider<SubjectDao> subjectDaoProvider;

  private final Provider<QuestionDao> questionDaoProvider;

  private final Provider<BookmarkDao> bookmarkDaoProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public SyncRepository_Factory(Provider<ExamApi> examApiProvider,
      Provider<SubjectApi> subjectApiProvider, Provider<QuestionApi> questionApiProvider,
      Provider<ExamDao> examDaoProvider, Provider<SubjectDao> subjectDaoProvider,
      Provider<QuestionDao> questionDaoProvider, Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.examApiProvider = examApiProvider;
    this.subjectApiProvider = subjectApiProvider;
    this.questionApiProvider = questionApiProvider;
    this.examDaoProvider = examDaoProvider;
    this.subjectDaoProvider = subjectDaoProvider;
    this.questionDaoProvider = questionDaoProvider;
    this.bookmarkDaoProvider = bookmarkDaoProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public SyncRepository get() {
    return newInstance(examApiProvider.get(), subjectApiProvider.get(), questionApiProvider.get(), examDaoProvider.get(), subjectDaoProvider.get(), questionDaoProvider.get(), bookmarkDaoProvider.get(), authRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static SyncRepository_Factory create(Provider<ExamApi> examApiProvider,
      Provider<SubjectApi> subjectApiProvider, Provider<QuestionApi> questionApiProvider,
      Provider<ExamDao> examDaoProvider, Provider<SubjectDao> subjectDaoProvider,
      Provider<QuestionDao> questionDaoProvider, Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new SyncRepository_Factory(examApiProvider, subjectApiProvider, questionApiProvider, examDaoProvider, subjectDaoProvider, questionDaoProvider, bookmarkDaoProvider, authRepositoryProvider, settingsRepositoryProvider);
  }

  public static SyncRepository newInstance(ExamApi examApi, SubjectApi subjectApi,
      QuestionApi questionApi, ExamDao examDao, SubjectDao subjectDao, QuestionDao questionDao,
      BookmarkDao bookmarkDao, AuthRepository authRepository,
      SettingsRepository settingsRepository) {
    return new SyncRepository(examApi, subjectApi, questionApi, examDao, subjectDao, questionDao, bookmarkDao, authRepository, settingsRepository);
  }
}
