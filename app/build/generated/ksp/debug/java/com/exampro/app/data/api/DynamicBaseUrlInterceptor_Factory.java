package com.exampro.app.data.api;

import com.exampro.app.data.repository.SettingsRepository;
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
public final class DynamicBaseUrlInterceptor_Factory implements Factory<DynamicBaseUrlInterceptor> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public DynamicBaseUrlInterceptor_Factory(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public DynamicBaseUrlInterceptor get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static DynamicBaseUrlInterceptor_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new DynamicBaseUrlInterceptor_Factory(settingsRepositoryProvider);
  }

  public static DynamicBaseUrlInterceptor newInstance(SettingsRepository settingsRepository) {
    return new DynamicBaseUrlInterceptor(settingsRepository);
  }
}
