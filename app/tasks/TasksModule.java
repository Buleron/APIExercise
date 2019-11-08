package tasks;

import actor.MyActorTask;
import com.google.inject.AbstractModule;

public class TasksModule extends AbstractModule {

    @Override
    protected void configure() {
        //running on start
      //  bind(MyActorTask.class).asEagerSingleton();
    }
}