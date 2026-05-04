package com.exampro.app.di;

import com.exampro.app.data.api.ExamApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideExamApiFactory implements Factory<ExamApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideExamApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public ExamApi get() {
    return provideExamApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideExamApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideExamApiFactory(retrofitProvider);
  }

  public static ExamApi provideExamApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideExamApi(retrofit));
  }
}
