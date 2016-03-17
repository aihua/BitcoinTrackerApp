package com.example.dr.bitcointracker.test.common.injection.component;


import com.example.dr.bitcointracker.dagger.component.ApplicationComponent;
import com.example.dr.bitcointracker.test.common.injection.module.ApplicationTestModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
