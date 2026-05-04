package com.exampro.app.di;

import com.exampro.app.data.api.QuestionApi;
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
public final class NetworkModule_ProvideQuestionApiFactory implements Factory<QuestionApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideQuestionApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public QuestionApi get() {
    return provideQuestionApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideQuestionApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideQuestionApiFactory(retrofitProvider);
  }

  public static QuestionApi provideQuestionApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideQuestionApi(retrofit));
  }
}
