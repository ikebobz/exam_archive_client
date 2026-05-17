package com.exampro.app.presentation.viewmodels;

import com.exampro.app.data.api.DeviceApi;
import com.exampro.app.data.repository.SyncRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<DeviceApi> deviceApiProvider;

  public MainViewModel_Factory(Provider<SyncRepository> syncRepositoryProvider,
      Provider<DeviceApi> deviceApiProvider) {
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.deviceApiProvider = deviceApiProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(syncRepositoryProvider.get(), deviceApiProvider.get());
  }

  public static MainViewModel_Factory create(Provider<SyncRepository> syncRepositoryProvider,
      Provider<DeviceApi> deviceApiProvider) {
    return new MainViewModel_Factory(syncRepositoryProvider, deviceApiProvider);
  }

  public static MainViewModel newInstance(SyncRepository syncRepository, DeviceApi deviceApi) {
    return new MainViewModel(syncRepository, deviceApi);
  }
}
