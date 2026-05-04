package com.exampro.app.di;

import com.exampro.app.data.api.SubjectApi;
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
public final class NetworkModule_ProvideSubjectApiFactory implements Factory<SubjectApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideSubjectApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SubjectApi get() {
    return provideSubjectApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideSubjectApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideSubjectApiFactory(retrofitProvider);
  }

  public static SubjectApi provideSubjectApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideSubjectApi(retrofit));
  }
}
