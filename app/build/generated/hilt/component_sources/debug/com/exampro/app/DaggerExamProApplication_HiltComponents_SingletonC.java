package com.exampro.app;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.exampro.app.data.api.AuthApi;
import com.exampro.app.data.api.DeviceApi;
import com.exampro.app.data.api.DynamicBaseUrlInterceptor;
import com.exampro.app.data.api.ExamApi;
import com.exampro.app.data.api.QuestionApi;
import com.exampro.app.data.api.SessionCookieInterceptor;
import com.exampro.app.data.api.SubjectApi;
import com.exampro.app.data.db.AppDatabase;
import com.exampro.app.data.db.dao.BookmarkDao;
import com.exampro.app.data.db.dao.ExamDao;
import com.exampro.app.data.db.dao.QuestionDao;
import com.exampro.app.data.db.dao.StudyProgressDao;
import com.exampro.app.data.db.dao.SubjectDao;
import com.exampro.app.data.repository.AuthRepository;
import com.exampro.app.data.repository.ExamRepository;
import com.exampro.app.data.repository.QuestionRepository;
import com.exampro.app.data.repository.SettingsRepository;
import com.exampro.app.data.repository.StudyProgressRepository;
import com.exampro.app.data.repository.SubjectRepository;
import com.exampro.app.data.repository.SyncRepository;
import com.exampro.app.data.services.MyFirebaseMessagingService;
import com.exampro.app.data.services.MyFirebaseMessagingService_MembersInjector;
import com.exampro.app.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.exampro.app.di.DatabaseModule_ProvideBookmarkDaoFactory;
import com.exampro.app.di.DatabaseModule_ProvideExamDaoFactory;
import com.exampro.app.di.DatabaseModule_ProvideQuestionDaoFactory;
import com.exampro.app.di.DatabaseModule_ProvideStudyProgressDaoFactory;
import com.exampro.app.di.DatabaseModule_ProvideSubjectDaoFactory;
import com.exampro.app.di.NetworkModule_ProvideAuthApiFactory;
import com.exampro.app.di.NetworkModule_ProvideAuthPreferencesFactory;
import com.exampro.app.di.NetworkModule_ProvideCookiePreferencesFactory;
import com.exampro.app.di.NetworkModule_ProvideDeviceApiFactory;
import com.exampro.app.di.NetworkModule_ProvideExamApiFactory;
import com.exampro.app.di.NetworkModule_ProvideLoggingInterceptorFactory;
import com.exampro.app.di.NetworkModule_ProvideOkHttpClientFactory;
import com.exampro.app.di.NetworkModule_ProvideQuestionApiFactory;
import com.exampro.app.di.NetworkModule_ProvideRetrofitFactory;
import com.exampro.app.di.NetworkModule_ProvideSessionCookieInterceptorFactory;
import com.exampro.app.di.NetworkModule_ProvideSubjectApiFactory;
import com.exampro.app.di.RepositoryModule_ProvideAuthRepositoryFactory;
import com.exampro.app.di.RepositoryModule_ProvideExamRepositoryFactory;
import com.exampro.app.di.RepositoryModule_ProvideQuestionRepositoryFactory;
import com.exampro.app.di.RepositoryModule_ProvideStudyProgressRepositoryFactory;
import com.exampro.app.di.RepositoryModule_ProvideSubjectRepositoryFactory;
import com.exampro.app.presentation.viewmodels.AuthViewModel;
import com.exampro.app.presentation.viewmodels.AuthViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.DashboardViewModel;
import com.exampro.app.presentation.viewmodels.DashboardViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.ExamViewModel;
import com.exampro.app.presentation.viewmodels.ExamViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.MainViewModel;
import com.exampro.app.presentation.viewmodels.MainViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.QuestionDetailViewModel;
import com.exampro.app.presentation.viewmodels.QuestionDetailViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.QuestionViewModel;
import com.exampro.app.presentation.viewmodels.QuestionViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.QuizViewModel;
import com.exampro.app.presentation.viewmodels.QuizViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.SettingsViewModel;
import com.exampro.app.presentation.viewmodels.SettingsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.exampro.app.presentation.viewmodels.SubjectViewModel;
import com.exampro.app.presentation.viewmodels.SubjectViewModel_HiltModules_KeyModule_ProvideFactory;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SetBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

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
public final class DaggerExamProApplication_HiltComponents_SingletonC {
  private DaggerExamProApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public ExamProApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements ExamProApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public ExamProApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements ExamProApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public ExamProApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements ExamProApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public ExamProApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements ExamProApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ExamProApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements ExamProApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ExamProApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements ExamProApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public ExamProApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements ExamProApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public ExamProApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends ExamProApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends ExamProApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends ExamProApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends ExamProApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Set<String> getViewModelKeys() {
      return SetBuilder.<String>newSetBuilder(9).add(AuthViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(DashboardViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ExamViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(MainViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(QuestionDetailViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(QuestionViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(QuizViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SettingsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SubjectViewModel_HiltModules_KeyModule_ProvideFactory.provide()).build();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends ExamProApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<ExamViewModel> examViewModelProvider;

    private Provider<MainViewModel> mainViewModelProvider;

    private Provider<QuestionDetailViewModel> questionDetailViewModelProvider;

    private Provider<QuestionViewModel> questionViewModelProvider;

    private Provider<QuizViewModel> quizViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SubjectViewModel> subjectViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.examViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.questionDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.questionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.quizViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.subjectViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<String, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9).put("com.exampro.app.presentation.viewmodels.AuthViewModel", ((Provider) authViewModelProvider)).put("com.exampro.app.presentation.viewmodels.DashboardViewModel", ((Provider) dashboardViewModelProvider)).put("com.exampro.app.presentation.viewmodels.ExamViewModel", ((Provider) examViewModelProvider)).put("com.exampro.app.presentation.viewmodels.MainViewModel", ((Provider) mainViewModelProvider)).put("com.exampro.app.presentation.viewmodels.QuestionDetailViewModel", ((Provider) questionDetailViewModelProvider)).put("com.exampro.app.presentation.viewmodels.QuestionViewModel", ((Provider) questionViewModelProvider)).put("com.exampro.app.presentation.viewmodels.QuizViewModel", ((Provider) quizViewModelProvider)).put("com.exampro.app.presentation.viewmodels.SettingsViewModel", ((Provider) settingsViewModelProvider)).put("com.exampro.app.presentation.viewmodels.SubjectViewModel", ((Provider) subjectViewModelProvider)).build();
    }

    @Override
    public Map<String, Object> getHiltViewModelAssistedMap() {
      return Collections.<String, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.exampro.app.presentation.viewmodels.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.provideAuthRepositoryProvider.get());

          case 1: // com.exampro.app.presentation.viewmodels.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.provideExamDaoProvider.get(), singletonCImpl.provideSubjectDaoProvider.get(), singletonCImpl.provideQuestionRepositoryProvider.get(), singletonCImpl.provideAuthRepositoryProvider.get());

          case 2: // com.exampro.app.presentation.viewmodels.ExamViewModel 
          return (T) new ExamViewModel(singletonCImpl.provideExamRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 3: // com.exampro.app.presentation.viewmodels.MainViewModel 
          return (T) new MainViewModel(singletonCImpl.syncRepositoryProvider.get());

          case 4: // com.exampro.app.presentation.viewmodels.QuestionDetailViewModel 
          return (T) new QuestionDetailViewModel(singletonCImpl.provideQuestionRepositoryProvider.get(), singletonCImpl.provideSubjectRepositoryProvider.get(), singletonCImpl.provideExamRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 5: // com.exampro.app.presentation.viewmodels.QuestionViewModel 
          return (T) new QuestionViewModel(singletonCImpl.provideQuestionRepositoryProvider.get(), singletonCImpl.provideSubjectRepositoryProvider.get(), singletonCImpl.provideExamRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 6: // com.exampro.app.presentation.viewmodels.QuizViewModel 
          return (T) new QuizViewModel(singletonCImpl.provideQuestionRepositoryProvider.get(), singletonCImpl.provideStudyProgressRepositoryProvider.get());

          case 7: // com.exampro.app.presentation.viewmodels.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.settingsRepositoryProvider.get());

          case 8: // com.exampro.app.presentation.viewmodels.SubjectViewModel 
          return (T) new SubjectViewModel(singletonCImpl.provideSubjectRepositoryProvider.get(), singletonCImpl.provideExamRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends ExamProApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends ExamProApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectMyFirebaseMessagingService(
        MyFirebaseMessagingService myFirebaseMessagingService) {
      injectMyFirebaseMessagingService2(myFirebaseMessagingService);
    }

    @CanIgnoreReturnValue
    private MyFirebaseMessagingService injectMyFirebaseMessagingService2(
        MyFirebaseMessagingService instance) {
      MyFirebaseMessagingService_MembersInjector.injectDeviceApi(instance, singletonCImpl.provideDeviceApiProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends ExamProApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<SharedPreferences> provideCookiePreferencesProvider;

    private Provider<SessionCookieInterceptor> provideSessionCookieInterceptorProvider;

    private Provider<HttpLoggingInterceptor> provideLoggingInterceptorProvider;

    private Provider<SettingsRepository> settingsRepositoryProvider;

    private Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptorProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<AuthApi> provideAuthApiProvider;

    private Provider<SharedPreferences> provideAuthPreferencesProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<AuthRepository> provideAuthRepositoryProvider;

    private Provider<ExamDao> provideExamDaoProvider;

    private Provider<SubjectDao> provideSubjectDaoProvider;

    private Provider<QuestionDao> provideQuestionDaoProvider;

    private Provider<BookmarkDao> provideBookmarkDaoProvider;

    private Provider<QuestionRepository> provideQuestionRepositoryProvider;

    private Provider<ExamRepository> provideExamRepositoryProvider;

    private Provider<ExamApi> provideExamApiProvider;

    private Provider<SubjectApi> provideSubjectApiProvider;

    private Provider<QuestionApi> provideQuestionApiProvider;

    private Provider<SyncRepository> syncRepositoryProvider;

    private Provider<SubjectRepository> provideSubjectRepositoryProvider;

    private Provider<StudyProgressDao> provideStudyProgressDaoProvider;

    private Provider<StudyProgressRepository> provideStudyProgressRepositoryProvider;

    private Provider<DeviceApi> provideDeviceApiProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideCookiePreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SharedPreferences>(singletonCImpl, 5));
      this.provideSessionCookieInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<SessionCookieInterceptor>(singletonCImpl, 4));
      this.provideLoggingInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<HttpLoggingInterceptor>(singletonCImpl, 6));
      this.settingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepository>(singletonCImpl, 8));
      this.dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<DynamicBaseUrlInterceptor>(singletonCImpl, 7));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 3));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 2));
      this.provideAuthApiProvider = DoubleCheck.provider(new SwitchingProvider<AuthApi>(singletonCImpl, 1));
      this.provideAuthPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SharedPreferences>(singletonCImpl, 9));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 10));
      this.provideAuthRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 0));
      this.provideExamDaoProvider = DoubleCheck.provider(new SwitchingProvider<ExamDao>(singletonCImpl, 11));
      this.provideSubjectDaoProvider = DoubleCheck.provider(new SwitchingProvider<SubjectDao>(singletonCImpl, 12));
      this.provideQuestionDaoProvider = DoubleCheck.provider(new SwitchingProvider<QuestionDao>(singletonCImpl, 14));
      this.provideBookmarkDaoProvider = DoubleCheck.provider(new SwitchingProvider<BookmarkDao>(singletonCImpl, 15));
      this.provideQuestionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<QuestionRepository>(singletonCImpl, 13));
      this.provideExamRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ExamRepository>(singletonCImpl, 16));
      this.provideExamApiProvider = DoubleCheck.provider(new SwitchingProvider<ExamApi>(singletonCImpl, 18));
      this.provideSubjectApiProvider = DoubleCheck.provider(new SwitchingProvider<SubjectApi>(singletonCImpl, 19));
      this.provideQuestionApiProvider = DoubleCheck.provider(new SwitchingProvider<QuestionApi>(singletonCImpl, 20));
      this.syncRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SyncRepository>(singletonCImpl, 17));
      this.provideSubjectRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SubjectRepository>(singletonCImpl, 21));
      this.provideStudyProgressDaoProvider = DoubleCheck.provider(new SwitchingProvider<StudyProgressDao>(singletonCImpl, 23));
      this.provideStudyProgressRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<StudyProgressRepository>(singletonCImpl, 22));
      this.provideDeviceApiProvider = DoubleCheck.provider(new SwitchingProvider<DeviceApi>(singletonCImpl, 24));
    }

    @Override
    public void injectExamProApplication(ExamProApplication examProApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.exampro.app.data.repository.AuthRepository 
          return (T) RepositoryModule_ProvideAuthRepositoryFactory.provideAuthRepository(singletonCImpl.provideAuthApiProvider.get(), singletonCImpl.provideAuthPreferencesProvider.get(), singletonCImpl.provideAppDatabaseProvider.get());

          case 1: // com.exampro.app.data.api.AuthApi 
          return (T) NetworkModule_ProvideAuthApiFactory.provideAuthApi(singletonCImpl.provideRetrofitProvider.get());

          case 2: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 3: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.provideSessionCookieInterceptorProvider.get(), singletonCImpl.provideLoggingInterceptorProvider.get(), singletonCImpl.dynamicBaseUrlInterceptorProvider.get());

          case 4: // com.exampro.app.data.api.SessionCookieInterceptor 
          return (T) NetworkModule_ProvideSessionCookieInterceptorFactory.provideSessionCookieInterceptor(singletonCImpl.provideCookiePreferencesProvider.get());

          case 5: // @com.exampro.app.di.CookiePrefs android.content.SharedPreferences 
          return (T) NetworkModule_ProvideCookiePreferencesFactory.provideCookiePreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // okhttp3.logging.HttpLoggingInterceptor 
          return (T) NetworkModule_ProvideLoggingInterceptorFactory.provideLoggingInterceptor();

          case 7: // com.exampro.app.data.api.DynamicBaseUrlInterceptor 
          return (T) new DynamicBaseUrlInterceptor(singletonCImpl.settingsRepositoryProvider.get());

          case 8: // com.exampro.app.data.repository.SettingsRepository 
          return (T) new SettingsRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // @com.exampro.app.di.AuthPrefs android.content.SharedPreferences 
          return (T) NetworkModule_ProvideAuthPreferencesFactory.provideAuthPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.exampro.app.data.db.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 11: // com.exampro.app.data.db.dao.ExamDao 
          return (T) DatabaseModule_ProvideExamDaoFactory.provideExamDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 12: // com.exampro.app.data.db.dao.SubjectDao 
          return (T) DatabaseModule_ProvideSubjectDaoFactory.provideSubjectDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 13: // com.exampro.app.data.repository.QuestionRepository 
          return (T) RepositoryModule_ProvideQuestionRepositoryFactory.provideQuestionRepository(singletonCImpl.provideQuestionDaoProvider.get(), singletonCImpl.provideBookmarkDaoProvider.get(), singletonCImpl.provideAuthRepositoryProvider.get());

          case 14: // com.exampro.app.data.db.dao.QuestionDao 
          return (T) DatabaseModule_ProvideQuestionDaoFactory.provideQuestionDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 15: // com.exampro.app.data.db.dao.BookmarkDao 
          return (T) DatabaseModule_ProvideBookmarkDaoFactory.provideBookmarkDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 16: // com.exampro.app.data.repository.ExamRepository 
          return (T) RepositoryModule_ProvideExamRepositoryFactory.provideExamRepository(singletonCImpl.provideExamDaoProvider.get());

          case 17: // com.exampro.app.data.repository.SyncRepository 
          return (T) new SyncRepository(singletonCImpl.provideExamApiProvider.get(), singletonCImpl.provideSubjectApiProvider.get(), singletonCImpl.provideQuestionApiProvider.get(), singletonCImpl.provideExamDaoProvider.get(), singletonCImpl.provideSubjectDaoProvider.get(), singletonCImpl.provideQuestionDaoProvider.get(), singletonCImpl.provideBookmarkDaoProvider.get(), singletonCImpl.provideAuthRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          case 18: // com.exampro.app.data.api.ExamApi 
          return (T) NetworkModule_ProvideExamApiFactory.provideExamApi(singletonCImpl.provideRetrofitProvider.get());

          case 19: // com.exampro.app.data.api.SubjectApi 
          return (T) NetworkModule_ProvideSubjectApiFactory.provideSubjectApi(singletonCImpl.provideRetrofitProvider.get());

          case 20: // com.exampro.app.data.api.QuestionApi 
          return (T) NetworkModule_ProvideQuestionApiFactory.provideQuestionApi(singletonCImpl.provideRetrofitProvider.get());

          case 21: // com.exampro.app.data.repository.SubjectRepository 
          return (T) RepositoryModule_ProvideSubjectRepositoryFactory.provideSubjectRepository(singletonCImpl.provideSubjectDaoProvider.get());

          case 22: // com.exampro.app.data.repository.StudyProgressRepository 
          return (T) RepositoryModule_ProvideStudyProgressRepositoryFactory.provideStudyProgressRepository(singletonCImpl.provideStudyProgressDaoProvider.get());

          case 23: // com.exampro.app.data.db.dao.StudyProgressDao 
          return (T) DatabaseModule_ProvideStudyProgressDaoFactory.provideStudyProgressDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 24: // com.exampro.app.data.api.DeviceApi 
          return (T) NetworkModule_ProvideDeviceApiFactory.provideDeviceApi(singletonCImpl.provideRetrofitProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
