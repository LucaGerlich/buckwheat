package com.danilkinkin.buckwheat.capture.parser

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {
    /**
     * Declares the (currently empty) set of bank parsers. Concrete parsers will be bound
     * here with `@Binds @IntoSet` once their notification format is known.
     */
    @Multibinds
    abstract fun bankNotificationParsers(): Set<BankNotificationParser>
}
