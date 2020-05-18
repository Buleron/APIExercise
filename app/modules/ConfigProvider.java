package modules;

import com.typesafe.config.Config;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ConfigProvider implements Provider<Config> {

  private final Config configuration;

  @Inject
  public ConfigProvider(Config configuration) {
    this.configuration = configuration;
  }

  @Override
  public Config get() {
    return this.configuration;
  }

}