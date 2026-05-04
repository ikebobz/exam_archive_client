package com.exampro.app.di;

import com.exampro.app.data.api.DynamicBaseUrlInterceptor;
import com.exampro.app.data.api.SessionCookieInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<SessionCookieInterceptor> sessionCookieInterceptorProvider;

  private final Provider<HttpLoggingInterceptor> loggingInterceptorProvider;

  private final Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptorProvider;

  public NetworkModule_ProvideOkHttpClientFactory(
      Provider<SessionCookieInterceptor> sessionCookieInterceptorProvider,
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider,
      Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptorProvider) {
    this.sessionCookieInterceptorProvider = sessionCookieInterceptorProvider;
    this.loggingInterceptorProvider = loggingInterceptorProvider;
    this.dynamicBaseUrlInterceptorProvider = dynamicBaseUrlInterceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(sessionCookieInterceptorProvider.get(), loggingInterceptorProvider.get(), dynamicBaseUrlInterceptorProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<SessionCookieInterceptor> sessionCookieInterceptorProvider,
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider,
      Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptorProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(sessionCookieInterceptorProvider, loggingInterceptorProvider, dynamicBaseUrlInterceptorProvider);
  }

  public static OkHttpClient provideOkHttpClient(SessionCookieInterceptor sessionCookieInterceptor,
      HttpLoggingInterceptor loggingInterceptor,
      DynamicBaseUrlInterceptor dynamicBaseUrlInterceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(sessionCookieInterceptor, loggingInterceptor, dynamicBaseUrlInterceptor));
  }
}
